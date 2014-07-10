package org.squbs.unicomplex

import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._
import scala.concurrent.duration._
import org.scalatest.concurrent.AsyncAssertions
import scala.io.Source
import akka.actor.ActorSystem
import scala.util.Try
import org.squbs.unicomplex.dummyextensions.DummyExtension
import com.typesafe.config.ConfigFactory
import scala.util.Failure
import scala.util.Success
import org.squbs.lifecycle.GracefulStop
import spray.can.Http
import akka.io.IO
import spray.http._
import spray.http.HttpRequest
import spray.http.HttpResponse

/**
 * Created by zhuwang on 2/21/14.
 */
object UnicomplexSpec {

  val dummyJarsDir = "unicomplex/src/test/resources/classpaths"

  val classPaths = Array(
    "DummyCube",
    "DummyCubeSvc",
    "DummySvc",
    "DummyExtensions.jar"
  ) map (dummyJarsDir + "/" + _)

  import collection.JavaConversions._

  val mapConfig = ConfigFactory.parseMap(
    Map(
      "squbs.actorsystem-name"    -> "unicomplexSpec",
      "squbs." + JMX.prefixConfig -> Boolean.box(true)
    )
  )

  val boot = UnicomplexBoot(mapConfig)
    .createUsing {(name, config) => ActorSystem(name, config)}
    .scanComponents(classPaths)
    .initExtensions.start()

}

class UnicomplexSpec extends TestKit(UnicomplexSpec.boot.actorSystem) with ImplicitSender
                             with WordSpecLike with Matchers with BeforeAndAfterAll
                             with AsyncAssertions {

  import UnicomplexSpec._

  implicit val timeout: akka.util.Timeout =
    Try(System.getProperty("test.timeout").toLong) map { millis =>
      akka.util.Timeout(millis, TimeUnit.MILLISECONDS)
    } getOrElse (10 seconds)

  val port = system.settings.config getInt "default-listener.bind-port"

  implicit val executionContext = system.dispatcher
  
  override def afterAll() {
    Unicomplex(system).uniActor ! GracefulStop
  }

  "UnicomplexBoot" must {

    "start all cube actors" in {
      val w = new Waiter

      system.actorSelection("/user/DummyCube").resolveOne().onComplete(result => {
        w {assert(result.isSuccess)}
        w.dismiss()
      })
      w.await()

      system.actorSelection("/user/DummyCubeSvc").resolveOne().onComplete(result => {
        w {assert(result.isSuccess)}
        w.dismiss()
      })
      w.await()

      system.actorSelection("/user/DummyCube/Appender").resolveOne().onComplete(result => {
        w {assert(result.isSuccess)}
        w.dismiss()
      })
      w.await()

      system.actorSelection("/user/DummyCube/Prepender").resolveOne().onComplete(result => {
        w {assert(result.isSuccess)}
        w.dismiss()
      })
      w.await()

      system.actorSelection("/user/DummyCubeSvc/PingPongPlayer").resolveOne().onComplete(result => {
        w {assert(result.isSuccess)}
        w.dismiss()
      })
      w.await()
    }

    "start all services" in {
      val services = boot.cubes flatMap { cube => cube.components.getOrElse(StartupType.SERVICES, Seq.empty) }
      assert(services.size == 2)
      (IO(Http) ! HttpRequest(HttpMethods.GET, Uri(s"http://127.0.0.1:$port/dummysvc/msg/hello")))
      within(timeout.duration) {
        val response = expectMsgType[HttpResponse]
        response.status should be(StatusCodes.OK)
        response.entity.asString should be("^hello$")
      }

      (IO(Http) ! HttpRequest(HttpMethods.GET, Uri(s"http://127.0.0.1:$port/pingpongsvc/ping")))
      within(timeout.duration) {
        val response = expectMsgType[HttpResponse]
        response.status should be(StatusCodes.OK)
        response.entity.asString should be("Pong")
      }

      (IO(Http) ! HttpRequest(HttpMethods.GET, Uri(s"http://127.0.0.1:$port/pingpongsvc/pong")))
      within(timeout.duration) {
        val response = expectMsgType[HttpResponse]
        response.status should be(StatusCodes.OK)
        response.entity.asString should be("Ping")
      }
    }

    "preInit, init and postInit all extenstions" in {
      boot.extensions.size should be (2)
      boot.extensions.forall(_._3.isInstanceOf[DummyExtension]) should be (true)
      boot.extensions(0)._3.asInstanceOf[DummyExtension].state should be ("AstartpreInitinitpostInit")
      boot.extensions(1)._3.asInstanceOf[DummyExtension].state should be ("BstartpreInitinitpostInit")
    }
  }
}
