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

import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCodes
}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{
  MissingQueryParamRejection,
  RejectionHandler,
  ValidationRejection
}

object HandleRejection {
  def newHandler(): RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case MissingQueryParamRejection(msg) =>
          complete((StatusCodes.BadRequest, msg))
      }
      .handle {
        case ValidationRejection(msg, _) =>
          complete((StatusCodes.BadRequest, msg))
      }
      .handleNotFound {
        complete(StatusCodes.NotFound)
      }
      .result()
      .withFallback(RejectionHandler.default)
      .mapRejectionResponse {
        case res @ HttpResponse(status, headers, ent: HttpEntity.Strict, _) =>
          // since all Akka default rejection responses are Strict this will handle all rejections
          val message = ent.data.utf8String.replaceAll("\"", """\"""")

          // we copy the response in order to keep all headers and status code, wrapping the message as hand rolled JSON
          // you could the entity using your favourite marshalling library (e.g. spray json or anything else)
          res.withHeadersAndEntity(
            headers = headers,
            HttpEntity(
              ContentTypes.`application/json`,
              s"""{"code": ${status.intValue}, "type": "${status.reason}", "message": "$message"}""")
          )

        case x => x // pass through all other types of responses
      }
}
