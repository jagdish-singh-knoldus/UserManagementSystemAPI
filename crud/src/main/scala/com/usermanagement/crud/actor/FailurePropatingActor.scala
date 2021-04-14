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

import akka.actor.{Actor, Status}

/**
  * The idea is from: https://medium.com/@linda0511ny/error-handling-in-akka-actor-with-future-ded3da0579dd
  * How it works: When there is a failure or exception, the actor gets restarted and the exception doesn't propagate
  * back to the Sender. Using this technique, we send back the exception back to the sender to deal with it.
  *
  */
trait FailurePropatingActor extends Actor {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    sender() ! Status.Failure(reason)
  }
}
