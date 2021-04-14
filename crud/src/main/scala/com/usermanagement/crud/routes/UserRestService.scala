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

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import com.usermanagement.crud.servicehandler.UserRestServiceHandler
import com.usermanagement.crud.helper.Constants
import com.usermanagement.crud.models.HttpUtility
import com.usermanagement.crud.dao.{UpdateAdminUsername, UserDetails}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

/**
  * Service to handle the account of the user
  */
trait UserRestService
    extends UserRestServiceHandler
    with HttpUtility
    with LazyLogging
    with Constants {
  // ==============================
  //     REST ROUTES
  // ==============================

  /**
    * user routes
    * @param actor
    * @return Route
    */

  def userRoutes(actor: ActorRef): Route =
    pathPrefix("user") {
      get {
        path("get-all-users") {
          val result =
            getUserDetails(actor)
          logDuration(complete(result))
        } }~
      delete{    path("delete-customer") {
            parameters('userId.?) { (userId: Option[String]) =>
              val result =
                deleteUser(actor, userId)
              logDuration(complete(result))
            }
          }
      } ~
        pathPrefix("insert-user") {
          pathEnd {
            (put & entity(as[UserDetails])) { userDetails =>
              logger.info("add user details " + userDetails)
              val result =
                addUserDetails(actor, userDetails)
              logDuration(complete(result))
            }
          }
        } ~
        pathPrefix("update-admin-username") {
          pathEnd {
            (put & entity(as[UpdateAdminUsername])) { adminDetails =>
              logger.info("update admin username " + adminDetails)
              val result =
                updateAdminUsername(actor, adminDetails)
              logDuration(complete(result))
            }
          }
        }
    }

  def routes(command: ActorRef): Route =
    userRoutes(command)
}
