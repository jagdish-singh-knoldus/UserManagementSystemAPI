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

package com.usermanagement.crud.actor
import akka.actor.{ActorLogging, Props}
import akka.pattern.pipe
import com.typesafe.scalalogging.LazyLogging
import com.usermanagement.crud.helper.Constants
import com.usermanagement.crud.models.APIDataResponse
import com.usermanagement.crud.dao.{UserDAO, UserDetails}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future

class UserActor(userDAO: UserDAO)(implicit futureAwaitDuration: FiniteDuration)
    extends FailurePropatingActor
    with ActorLogging
    with LazyLogging
    with Constants {

  import UserActor._

  //noinspection ScalaStyle
  override def receive: Receive = {
    case m @ GetAllUserDetails =>
      val res = getAllUserDetails
      res.pipeTo(sender())
    case m @ ValidateUserId(userId: String) =>
      val res = validateUserIdForUser(userId)
      res.pipeTo(sender())
    case m @ ValidateUserType(userId: String, userType: String) =>
      val res = validateUserTpe(userId, userType)
      res.pipeTo(sender())
    case m @ InsertUserDetails(userDetails: UserDetails) =>
      val res = insertUserDetails(userDetails)
      res.pipeTo(sender())
    case m @ UpdateUsernameForAdmin(userId: String,
                                    username: String,
                                    userType: String) =>
      val res = updateAdminUsername(userId, username, userType)
      res.pipeTo(sender())
    case m @ DeleteUser(userId: String, userType: String) =>
      val res = deleteUser(userId, userType)
      res.pipeTo(sender())
  }

  /**
    * function to get user details
    * @return Future[APIDataResponse]
    */
  private def getAllUserDetails(): Future[APIDataResponse] = {
    userDAO.getAllUsers.map {
      case Nil =>
        logger.info("No data Found")
        NoDataFound()
      case data =>
        GetUserDetailsSuccess(data)
    }
  }

  /**
    * function to validate user id
    * @param userId
    * @return Future[Valid]
    */
  private def validateUserIdForUser(userId: String): Future[Valid] =
    userDAO
      .validateUserId(userId)
      .map(count => Valid(count > 0))

  /**
    * function to validate user type
    * @param userId
    * @param userType
    * @return Future[Valid]
    */
  private def validateUserTpe(userId: String, userType: String): Future[Valid] =
    userDAO
      .validateUserType(userId, userType)
      .map(count => Valid(count > 0))

  /**
    * function to  insert user details
    * @param userDetails
    * @return Future[APIDataResponse]
    */
  private def insertUserDetails(
      userDetails: UserDetails): Future[APIDataResponse] = {
    userDAO.createUser(userDetails).map {
      case 1 =>
        CommandSuccess("User created successfully.", userDetails.userId)
      case 0 =>
        CommandFailure("User creation failed!")
    }
  }

  /**
    * function t update admin username
    * @param userId
    * @param username
    * @param userType
    * @return Future[APIDataResponse]
    */
  private def updateAdminUsername(userId: String,
                                  username: String,
                                  userType: String): Future[APIDataResponse] = {
    userDAO.updateUsername(userId, username, userType).map {
      case 1 =>
        CommandSuccess("Admin username updated successfully.", Some(userId))
      case 0 =>
        CommandFailure("Admin username updation failed!")
    }
  }

  /**
    * function to delete user
    * @param userId
    * @param userType
    * @return Future[APIDataResponse]
    */
  private def deleteUser(userId: String,
                         userType: String): Future[APIDataResponse] = {
    userDAO.deleteUser(userId, userType).map {
      case 1 =>
        CommandSuccess("Customer deleted successfully.", Some(userId))
      case 0 =>
        CommandFailure("Customer deletion failed!")
    }
  }

}

object UserActor {

  // commands
  sealed trait UserActorMessage
  sealed trait UserActorAck

  final case class ValidateUserId(userId: String) extends UserActorMessage

  final case class DeleteUser(userId: String, userType: String)
      extends UserActorMessage

  final case class ValidateUserType(userId: String, userType: String)
      extends UserActorMessage

  final case class UpdateUsernameForAdmin(userId: String,
                                          username: String,
                                          userType: String)
      extends UserActorMessage

  final case class Valid(isValid: Boolean) extends UserActorAck

  final case class NoDataFound() extends APIDataResponse

  final case class GetAllUserDetails() extends UserActorMessage

  final case class InsertUserDetails(userDetails: UserDetails)
      extends APIDataResponse

  final case class CommandSuccess(message: String, userId: Option[String])
      extends APIDataResponse
  final case class CommandFailure(message: String) extends APIDataResponse
  case class GetUserDetailsSuccess(users: Seq[UserDetails])
      extends APIDataResponse

  case class GetUserDetailsException(users: String) extends APIDataResponse

  def props(userDAO: UserDAO)(
      implicit futureAwaitDuration: FiniteDuration): Props =
    Props(new UserActor(userDAO))
}
