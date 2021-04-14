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

package com.usermanagement.crud.dao

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class UserDAO(implicit val db: Database,
              schema: String,
              ec: ExecutionContext,
              searchLimit: Int)
    extends LazyLogging {

  val userQuery = TableQuery[UserDetailsTable]

  /**
    * function to insert user
    * @param user
    * @return Future[Int]
    */
  def createUser(user: UserDetails): Future[Int] = {
    db.run(userQuery += user)
  }

  /**
    * function to validate user id
    * @param userId
    * @return  Future[Int]
    */
  def validateUserId(userId: String): Future[Int] = {
    db.run(
      userQuery
        .filter(col => col.userId === userId)
        .size
        .result)
  }

  /**
    * function to validate user type
    * @param userId
    * @param userType
    * @return Future[Int]
    */
  def validateUserType(userId: String, userType: String): Future[Int] = {
    db.run(
      userQuery
        .filter(col => col.userId === userId && col.userType === userType)
        .size
        .result)
  }

  /**
    * function to update username in db
    * @param userId
    * @param username
    * @param userType
    * @return Future[Int]
    */
  def updateUsername(userId: String,
                     username: String,
                     userType: String): Future[Int] = {
    db.run(
      userQuery
        .filter(col =>
          col.userId === userId &&
            col.userType === userType)
        .map(_.userName)
        .update(username))
  }

  /**
    * function to delete user from db
    * @param userId
    * @param userType
    * @return
    */
  def deleteUser(userId: String, userType: String): Future[Int] = {
    db.run(
      userQuery
        .filter(col =>
          col.userId === userId &&
            col.userType === userType)
        .delete)
  }

  /**
    * function to get all users
    * @return Future[Seq[UserDetails]]
    */
  def getAllUsers: Future[Seq[UserDetails]] = {
    db.run(userQuery.result)
  }
}
