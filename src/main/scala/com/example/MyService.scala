package com.example

import akka.actor.Actor
import spray.httpx.Json4sSupport
import spray.routing._
import spray.http._
import MediaTypes._
import org.json4s.{JValue, DefaultFormats, Formats}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

object MachineInformation {

}

case class MachineInformation(name: String,
                              `type`: String,
                              state: String,
                              location: String,
                              timestamp: String,
                              current: Double,
                              current_alert: Double)



case class EnvironmentInformation(pressure: List[Any], temperature: List[Any], humidity: List[Any])


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  val myRoute =
    path("api" / "v1" / "machines") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            List("/id1", "/id2")
          }
        }
      }
    } ~ path("api" / "v1" / "machine" / Segment) { id =>
      get {
        respondWithMediaType(`application/json`) {
          complete {
            MachineInformation(
              s"Machine $id",
              "lathe",
              "working",
              "0.0,0.0",
              "2015-11-13T16:04:53.128550",
              scala.util.Random.nextDouble(),
              3 * scala.util.Random.nextDouble())
          }
        }
      }
    }  ~ path("api" / "v1" / "env-sensor") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            EnvironmentInformation(
              List(DateTime.now.toIsoDateTimeString, 900 + 10 * scala.util.Random.nextDouble()),
              List(DateTime.now.toIsoDateTimeString, 20 + 10 * scala.util.Random.nextDouble()),
              List(DateTime.now.toIsoDateTimeString, 60 + 10 * scala.util.Random.nextDouble())
            )
          }
        }
      }
    }

}