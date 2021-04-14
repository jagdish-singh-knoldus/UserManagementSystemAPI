// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.usermanagement.crud.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods}
import akka.http.scaladsl.server.Route
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{
  cors,
  corsRejectionHandler
}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import slick.jdbc.MySQLProfile.api._
import com.typesafe.scalalogging.LazyLogging
import com.usermanagement.crud.actor.UserActor
import com.usermanagement.crud.flyway.FlywayService
import com.usermanagement.crud.models.{APIConfigurations, HandleRejection}
import com.usermanagement.crud.dao.UserDAO
import org.fusesource.jansi.Ansi.Color._
import org.fusesource.jansi.Ansi._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures

import scala.collection.immutable
import scala.concurrent.duration._
import scala.util.{Failure, Success}
// NOTE* - DO NOT remove this import, it sometime show's as unused import in intellij and gets removed.
// import pureconfig.generic.auto._
import pureconfig.generic.auto._

object UserHTTPServer extends App with UserRestService with LazyLogging {

  implicit val system: ActorSystem = ActorSystem("user")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val config: APIConfigurations = ConfigSource
    .resources("application.conf")
    .withFallback(ConfigSource.systemProperties)
    .load[APIConfigurations] match {
    case Left(e: ConfigReaderFailures) =>
      throw new RuntimeException(
        s"Unable to load config, original error: ${e.prettyPrint()}")
    case Right(x) => x
  }

  implicit val schema: String = config.dbConfig.schema
  implicit val db: Database = Database.forURL(
    config.dbConfig.url,
    user = config.dbConfig.user,
    password = config.dbConfig.password,
    driver = config.dbConfig.driver,
    executor = AsyncExecutor("postgres",
                             numThreads = config.dbConfig.threadsPoolCount,
                             queueSize = config.dbConfig.queueSize)
  )
  implicit val searchLimit: Int = config.dbConfig.searchLimit

  val futureAwaitTime: FiniteDuration =
    config.akka.futureAwaitDurationMins.minutes
  implicit val futureAwaitDuration: FiniteDuration =
    FiniteDuration(futureAwaitTime.length, futureAwaitTime.unit)

  // Migrate DB
  val flyWayService = new FlywayService(config.dbConfig)
  flyWayService.migrateDatabaseSchema()

  // create Actor
  val userDAO = new UserDAO()
  val userActor: ActorRef = system.actorOf(
    UserActor
      .props(userDAO)
      .withRouter(RoundRobinPool(nrOfInstances = config.akka.akkaWorkersCount)),
    "userActor")

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowedMethods(
    immutable.Seq(
      HttpMethods.DELETE,
      GET,
      PUT,
      POST,
      HEAD,
      OPTIONS
    ))

  lazy val routes: Route = {
    cors(settings)(
      ignoreTrailingSlash {
        pathSingleSlash {
          complete(
            HttpEntity(ContentTypes.`text/html(UTF-8)`,
                       "<html><body>Hello world!</body></html>"))
        }
      } ~
        handleRejections(
          corsRejectionHandler.withFallback(HandleRejection.newHandler())) {
          routes(userActor)
        }
    )
  }

  //bind route to server
  val binding = Http().bindAndHandle(routes, config.app.host, config.app.port)

  //scalastyle:off
  binding.onComplete {
    case Success(binding) ⇒
      val localAddress = binding.localAddress
      println(
        ansi()
          .fg(GREEN)
          .a("""
               |
               |╔╗ ╔╗               ╔═╗╔═╗                                   ╔╗     ╔═══╗          ╔╗
               |║║ ║║               ║║╚╝║║                                  ╔╝╚╗    ║╔═╗║         ╔╝╚╗
               |║║ ║║╔══╗╔══╗╔═╗    ║╔╗╔╗║╔══╗ ╔═╗ ╔══╗ ╔══╗╔══╗╔╗╔╗╔══╗╔═╗ ╚╗╔╝    ║╚══╗╔╗ ╔╗╔══╗╚╗╔╝╔══╗╔╗╔╗
               |║║ ║║║══╣║╔╗║║╔╝    ║║║║║║╚ ╗║ ║╔╗╗╚ ╗║ ║╔╗║║╔╗║║╚╝║║╔╗║║╔╗╗ ║║     ╚══╗║║║ ║║║══╣ ║║ ║╔╗║║╚╝║
               |║╚═╝║╠══║║║═╣║║     ║║║║║║║╚╝╚╗║║║║║╚╝╚╗║╚╝║║║═╣║║║║║║═╣║║║║ ║╚╗    ║╚═╝║║╚═╝║╠══║ ║╚╗║║═╣║║║║
               |╚═══╝╚══╝╚══╝╚╝     ╚╝╚╝╚╝╚═══╝╚╝╚╝╚═══╝╚═╗║╚══╝╚╩╩╝╚══╝╚╝╚╝ ╚═╝    ╚═══╝╚═╗╔╝╚══╝ ╚═╝╚══╝╚╩╩╝
               |                                        ╔═╝║                             ╔═╝║
               |                                        ╚══╝                             ╚══╝
               |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+
               || operation             | route                                           | data-format              | data                     |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+
               || insert user           | (PUT) localhost:8003/user/insert-user           | body -> raw (JSON)       | {                        |
               ||                       |                                                 |                          | "userType": "user-type", |
               ||                       |                                                 |                          |     "name": "name",      |
               ||                       |                                                 |                          | "username": "user-name"  |
               ||                       |                                                 |                          | }                        |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+
               || get all users         | (GET) localhost:8003/user/get-all-users         |          --              |           --             |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+
               || udpate admin username | (PUT) localhost:8003/user/update-admin-username | body -> raw (JSON)       | {                        |
               ||                       |                                                 |                          |   "userId":"user-id",    |
               ||                       |                                                 |                          | "username":"user-name"   |
               ||                       |                                                 |                          | }                        |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+
               || delete customer       | (DELETE) localhost:8003/user/delete-customer?   | Query Parameter (String) | userId=user-id           |
               |+-----------------------+-------------------------------------------------+--------------------------+--------------------------+               |
               |
               |
               |                                                 MySql configuration
               |---------------------------------------------------------------------------------------------------------------------------------
               |
               |+----------+-----------+
               || url      | localhost |
               |+----------+-----------+
               || port     | 3306      |
               |+----------+-----------+
               || user     | root      |
               |+----------+-----------+
               || password | root      |
               |+----------+-----------+
               |
               |
               |""".stripMargin)
          .reset())
      //scalastyle:on

      logger.info(
        s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
    case Failure(e) ⇒
      logger.error(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }
}
