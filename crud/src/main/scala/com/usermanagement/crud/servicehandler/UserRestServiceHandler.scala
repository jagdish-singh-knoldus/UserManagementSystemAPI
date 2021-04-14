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

package com.usermanagement.crud.servicehandler

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCodes
}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.usermanagement.crud.actor.UserActor._
import com.usermanagement.crud.helper.Constants
import com.usermanagement.crud.models.{Helper, ResponseFormat}
import com.usermanagement.crud.dao.{UpdateAdminUsername, UserDetails}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.duration._

trait UserRestServiceHandler
    extends ResponseFormat
    with Helper
    with LazyLogging
    with Constants {

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout = Timeout(40 seconds)

  import akka.pattern.ask
  import system.dispatcher

  /**
    * dunction to get user details
    * @param command
    * @return Future[HttpResponse]
    */
  def getUserDetails(command: ActorRef): Future[HttpResponse] = {
    ask(command, GetAllUserDetails)
      .map {
        case response: GetUserDetailsSuccess =>
          HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(
              ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = true,
                                                   Some(List()),
                                                   Some(response),
                                                   Some(INSERT_USER_DETAILS))))
          )
        case _: NoDataFound =>
          NO_DATA_RESPONSE
      }
  }

  /**
    * function to add user in the database
    * @param command
    * @param userData
    * @return Future[HttpResponse]
    */
  def addUserDetails(command: ActorRef,
                     userData: UserDetails): Future[HttpResponse] = {
    val userId =
      if (userData.userId.isEmpty) {Some(UUID.randomUUID().toString)}
      else {userData.userId}
    ask(command, ValidateUserId(userId.getOrElse(""))).map {
      case Valid(true) =>
        Future.successful(
          HttpResponse(
            StatusCodes.Conflict,
            entity =
              HttpEntity(ContentTypes.`application/json`,
                         write(
                           sendFormattedError(userId.get,
                                              INVALID_USER_ID,
                                              Some(INSERT_USER_DETAILS))))
          ))
      case Valid(false) =>
        if (userData.userType.replaceAll(" ", "").toLowerCase == CUSTOMER) {
          val userDetails = userData.copy(userId = userId, userType = CUSTOMER)
          insertUserDetails(command, userDetails)
        } else if (userData.userType.replaceAll(" ", "").toLowerCase == ADMIN) {
          val userDetails = userData.copy(userId = userId, userType = ADMIN)
          insertUserDetails(command, userDetails)
        } else {
          Future.successful(
            HttpResponse(
              StatusCodes.Conflict,
              entity =
                HttpEntity(ContentTypes.`application/json`,
                           write(
                             sendFormattedError(userId.get,
                                                INVALID_USER_TYPE,
                                                Some(INSERT_USER_DETAILS))))
            ))
        }
    }.flatten
  }

  /**
    * function to perist user in dtabase
    * @param command
    * @param userDetails
    * @return
    */
  def insertUserDetails(command: ActorRef, userDetails: UserDetails):Future[HttpResponse] = {
    logger.info(s"going to insert user details $userDetails")
    ask(command, InsertUserDetails(userDetails)).map {
      case response: CommandSuccess =>
        HttpResponse(
          StatusCodes.Created,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            write(
              generateCommonResponseForCaseClass(status = true,
                                                 Some(List()),
                                                 Some(response),
                                                 Some(INSERT_USER_DETAILS))))
        )
      case response: CommandFailure =>
        HttpResponse(
          StatusCodes.Forbidden,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            write(
              generateCommonResponseForCaseClass(status = true,
                                                 Some(List()),
                                                 Some(response),
                                                 Some(INSERT_USER_DETAILS))))
        )
    }
  }

  /**
    * function to update username for admin
    * @param command
    * @param updateAdminUsername
    * @return Future[HttpResponse]
    */
  def updateAdminUsername(
      command: ActorRef,
      updateAdminUsername: UpdateAdminUsername): Future[HttpResponse] = {
    val userId = updateAdminUsername.userId
    if (updateAdminUsername.userId.isEmpty) {
      Future.successful(
        HttpResponse(
          StatusCodes.Conflict,
          entity = HttpEntity(ContentTypes.`application/json`,
                              write(
                                sendFormattedError("empty",
                                                   INVALID_USER_ID_FOR_ADMIN,
                                                   Some(INSERT_USER_DETAILS))))
        ))
    } else {
      ask(command, ValidateUserType(userId.get, ADMIN)).map {
        case Valid(true) =>
          val username = updateAdminUsername.username
          if (username.isEmpty) {
            Future.successful(
              HttpResponse(
                StatusCodes.Conflict,
                entity =
                  HttpEntity(ContentTypes.`application/json`,
                             write(
                               sendFormattedError(userId.get,
                                                  INVALID_USERNAME,
                                                  Some(INSERT_USER_DETAILS))))
              ))
          } else {
            updateAdminUsernameRequest(command, userId.get, username.get)
          }
        case Valid(false) =>
          Future.successful(
            HttpResponse(
              StatusCodes.Conflict,
              entity =
                HttpEntity(ContentTypes.`application/json`,
                           write(
                             sendFormattedError(userId.get,
                                                INVALID_USER_ID_FOR_ADMIN,
                                                Some(INSERT_USER_DETAILS))))
            ))
      }.flatten
    }
  }

  /**
    * function to persist admin username in database
    * @param command
    * @param userId
    * @param username
    * @return Future[HttpResponse]
    */
  def updateAdminUsernameRequest(command: ActorRef,
                                 userId: String,
                                 username: String): Future[HttpResponse] = {
    ask(command, UpdateUsernameForAdmin(userId, username, ADMIN)).map {
      case response: CommandSuccess =>
        HttpResponse(
          StatusCodes.Created,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            write(
              generateCommonResponseForCaseClass(status = true,
                                                 Some(List()),
                                                 Some(response),
                                                 Some(INSERT_USER_DETAILS))))
        )
      case response: CommandFailure =>
        HttpResponse(
          StatusCodes.Forbidden,
          entity = HttpEntity(
            ContentTypes.`application/json`,
            write(
              generateCommonResponseForCaseClass(status = true,
                                                 Some(List()),
                                                 Some(response),
                                                 Some(INSERT_USER_DETAILS))))
        )
    }
  }

  /**
    * function to delete customer from database
    * @param command
    * @param userId
    * @return Future[HttpResponse]
    */
  def deleteUser(command: ActorRef,
                 userId: Option[String]): Future[HttpResponse] = {

    ask(command, ValidateUserType(userId.getOrElse(""), CUSTOMER)).map {
      case Valid(true) =>
        ask(command, DeleteUser(userId.get, CUSTOMER)).map {
          case response: CommandSuccess =>
            HttpResponse(
              StatusCodes.Created,
              entity = HttpEntity(
                ContentTypes.`application/json`,
                write(
                  generateCommonResponseForCaseClass(status = true,
                                                     Some(List()),
                                                     Some(response),
                                                     Some(DELETE_CUSTOMER))))
            )
          case response: CommandFailure =>
            HttpResponse(
              StatusCodes.Forbidden,
              entity = HttpEntity(
                ContentTypes.`application/json`,
                write(
                  generateCommonResponseForCaseClass(status = true,
                                                     Some(List()),
                                                     Some(response),
                                                     Some(DELETE_CUSTOMER))))
            )
        }
      case Valid(false) =>
        Future.successful(
          HttpResponse(
            StatusCodes.Conflict,
            entity =
              HttpEntity(ContentTypes.`application/json`,
                         write(
                           sendFormattedError(userId.get,
                                              INVALID_USER_ID_FOR_CUSTOMER,
                                              Some(DELETE_CUSTOMER))))
          ))
    }.flatten
  }
}
