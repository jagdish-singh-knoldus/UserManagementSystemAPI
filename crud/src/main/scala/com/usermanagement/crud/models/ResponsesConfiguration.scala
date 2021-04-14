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

package com.usermanagement.crud.models

trait APIDataResponse

object ResponsesConfiguration {

  case class StandardResponseForString(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[String]
  )

  case class EmptyData(data: Option[String])

  case class StandardResponseForStringError(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[EmptyData]
  )

  case class StandardResponseForCaseClass(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[APIDataResponse]
  )

  case class StandardResponseForListCaseClass(
      resource: Option[String],
      status: String,
      errors: Option[List[Error]],
      data: Option[List[APIDataResponse]]
  )

  case class Error(
      userId: String,
      message: String,
      fields: Option[String] = None
  )

}
