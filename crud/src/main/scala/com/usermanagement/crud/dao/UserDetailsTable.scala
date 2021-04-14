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

import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
class UserDetailsTable(tag: Tag)(implicit val schema: String)
    extends Table[UserDetails](tag, Some(schema), "user_details") {

  def userId: Rep[Option[String]] = column[Option[String]]("user_id")
  def userType: Rep[String] = column[String]("user_type")
  def name: Rep[String] = column[String]("name")
  def userName: Rep[String] = column[String]("username")

  //noinspection ScalaStyle
  def * : ProvenShape[UserDetails] =
    (userId, userType, name, userName).shaped <> (UserDetails.tupled, UserDetails.unapply)
}
