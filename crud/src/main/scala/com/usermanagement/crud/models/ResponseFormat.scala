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

import com.usermanagement.crud.models.ResponsesConfiguration.{
  EmptyData,
  Error,
  StandardResponseForCaseClass,
  StandardResponseForListCaseClass,
  StandardResponseForString,
  StandardResponseForStringError
}

/**
  * Utility for handling the response formats
  */
trait ResponseFormat {

  val resourceName: String = "http://localhost"

  def generateCommonResponseForCaseClass(status: Boolean,
                                         error: Option[List[Error]],
                                         data: Option[APIDataResponse] = None,
                                         resource: Option[String] = Some(
                                           resourceName))
    : StandardResponseForCaseClass = {
    StandardResponseForCaseClass(resource, status, error, data)
  }

  def sendFormattedError(errorCode: String,
                         errorMessage: String,
                         resource: Option[String] = Some(resourceName))
    : StandardResponseForStringError = {
    val error = List(Error(errorCode, errorMessage))
    generateCommonResponseForError(false, Some(error), resource = resource)
  }

  def generateCommonResponseForError(status: Boolean,
                                     error: Option[List[Error]],
                                     resource: Option[String] = Some(
                                       resourceName))
    : StandardResponseForStringError = {
    StandardResponseForStringError(resource,
                                   status,
                                   error,
                                   Some(EmptyData(None)))
  }

}

object ResponseFormat extends ResponseFormat
