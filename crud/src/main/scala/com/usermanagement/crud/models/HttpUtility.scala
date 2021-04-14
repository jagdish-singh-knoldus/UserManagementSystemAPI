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

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{Directives, RejectionHandler, Route}
import com.typesafe.scalalogging.LazyLogging

trait HttpUtility extends Directives with LazyLogging {

  val rejectionHandler: RejectionHandler = HandleRejection.newHandler()

  def logDuration(inner: Route): Route = { ctx =>
    val start = System.currentTimeMillis()
    /**
      * handling rejections here so that we get proper status codes
      */
    val innerRejectionsHandled = handleRejections(rejectionHandler)(inner)
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      logger.info(s"[${resp.status
        .intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    }(innerRejectionsHandled)(ctx)
  }

  def handleResponseWithEntity(response: String): HttpResponse = {
    HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, response))
  }
}
