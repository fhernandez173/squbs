/*
* Licensed to Typesafe under one or more contributor license agreements.
* See the AUTHORS file distributed with this work for
* additional information regarding copyright ownership.
* This file is licensed to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.squbs.httpclient.demo

import akka.util.Timeout
<<<<<<< HEAD
import org.squbs.httpclient._
=======
import org.squbs.httpclient.{CircuitBreakerBean, CircuitBreakerConfiguration, Configuration, HttpClientFactory}
>>>>>>> refractoring the code SQUBS-504
import scala.util.{Failure, Success}
import akka.pattern.CircuitBreakerOpenException
import scala.concurrent.duration._
import akka.actor.ActorSystem
import org.squbs.httpclient.endpoint.{Endpoint, EndpointResolver, EndpointRegistry}
import org.squbs.httpclient.env.Environment

object CircuitBreakerMain1 extends App{

  implicit val system = ActorSystem("CircuitBreakerMain1")
  implicit val ec = system.dispatcher

  implicit val timeout: Timeout = 3 seconds

  EndpointRegistry(system).register(new EndpointResolver{

    override def resolve(svcName: String, env: Environment): Option[Endpoint] = {
<<<<<<< HEAD
      if (svcName == name) Some(Endpoint("http://localhost:8888")) else None
=======
      svcName match {
        case name => Some(Endpoint("http://localhost:8888"))
        case _    => None
      }
>>>>>>> refractoring the code SQUBS-504
    }

    override def name: String = "DummyService"
  })
  val httpClient = HttpClientFactory.get("DummyService").
<<<<<<< HEAD
    withConfig(Configuration().copy(settings = Settings(hostSettings = Configuration.defaultHostSettings.copy(maxRetries = 0),
    circuitBreakerConfig = CircuitBreakerSettings().copy(callTimeout = 1 second))))
  while(true){
    Thread.sleep(2000)
    //    httpClient.get("/view") onComplete {
    //      case Success(httpResponse) =>
    //        println(s"call success, body is: ${httpResponse.entity.data.asString}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
    //      case Failure(e: CircuitBreakerOpenException) =>
    //        println(s"circuitBreaker open! remaining time is: ${e.remainingDuration.toSeconds}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
    //      case Failure(throwable) =>
    //        println(s"exception is: ${throwable.getMessage}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
    //    }
    while(true){
      Thread.sleep(2000)
      httpClient.raw.get("/view") onComplete {
        case Success(httpResponse) =>
          println(s"call success, body is: ${httpResponse.entity.data.asString}, " +
            s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
        case Failure(e: CircuitBreakerOpenException) =>
          println(s"circuitBreaker open! remaining time is: ${e.remainingDuration.toSeconds}, " +
            s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
        case Failure(throwable) =>
          println(s"exception is: ${throwable.getMessage}, " +
            s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
      }
    }
  }
}
=======
    withConfig(Configuration().copy(hostSettings = Configuration.defaultHostSettings.copy(maxRetries = 0),
                                    circuitBreakerConfig = CircuitBreakerConfiguration().copy(callTimeout = 1 second)))
  while(true){
    Thread.sleep(2000)
//    httpClient.get("/view") onComplete {
//      case Success(httpResponse) =>
//        println(s"call success, body is: ${httpResponse.entity.data.asString}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
//      case Failure(e: CircuitBreakerOpenException) =>
//        println(s"circuitBreaker open! remaining time is: ${e.remainingDuration.toSeconds}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
//      case Failure(throwable) =>
//        println(s"exception is: ${throwable.getMessage}, (status, success, fallback, failfast, exception):(${httpClient.cbMetrics.status}, ${httpClient.cbMetrics.successTimes}, ${httpClient.cbMetrics.fallbackTimes}, ${httpClient.cbMetrics.failFastTimes}, ${httpClient.cbMetrics.exceptionTimes}), cbLastMinCall:${httpClient.cbMetrics.cbLastMinCall.size}")
//    }
    while(true){
      Thread.sleep(2000)
      httpClient.get("/view") onComplete {
        case Success(httpResponse) =>
          println(s"call success, body is: ${httpResponse.entity.data.asString}, " +
                  s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
        case Failure(e: CircuitBreakerOpenException) =>
          println(s"circuitBreaker open! remaining time is: ${e.remainingDuration.toSeconds}, " +
                  s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
        case Failure(throwable) =>
          println(s"exception is: ${throwable.getMessage}, " +
                  s"error rate is: ${CircuitBreakerBean(system).getHttpClientCircuitBreakerInfo.get(0).lastDurationErrorRate}")
      }
    }
  }
}
>>>>>>> refractoring the code SQUBS-504
