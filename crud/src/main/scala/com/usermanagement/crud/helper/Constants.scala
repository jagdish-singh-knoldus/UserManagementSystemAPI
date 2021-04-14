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

package com.usermanagement.crud.helper

import akka.http.scaladsl.model.{
  HttpEntity,
  HttpProtocols,
  HttpResponse,
  StatusCodes
}

trait Constants {

  val NO_DATA_RESPONSE: HttpResponse = HttpResponse(StatusCodes.NoContent,
                                                    Nil,
                                                    HttpEntity.Empty,
                                                    HttpProtocols.`HTTP/1.1`)

  val INVALID_USER_ID = "INVALID_USER_ID"
  val INVALID_USER_TYPE = "INVALID_USER_TYPE"
  val INVALID_USERNAME = "INVALID_USERNAME"
  val INVALID_USER_ID_FOR_ADMIN = "INVALID_USER_ID_FOR_ADMIN"
  val INVALID_USER_ID_FOR_CUSTOMER = "INVALID_USER_ID_FOR_CUSTOMER"
  val GET_ALL_USERS = "GET_ALL_USERS"
  val INSERT_USER_DETAILS = "INSERT_USER_DETAILS"
  val DELETE_CUSTOMER = "DELETE_CUSTOMER"
  val ADMIN = "admin"
  val CUSTOMER = "customer"

}
