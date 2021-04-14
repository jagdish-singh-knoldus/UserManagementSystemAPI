package com.usermanagement.crud.servicehandler

import java.io.File
import java.time.OffsetDateTime
import java.util.UUID
import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.model.StatusCodes.{Conflict, Created, Forbidden, NoContent, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpProtocols, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.testkit.TestActorRef
import akka.util.ByteString
import com.usermanagement.crud.actor.UserActor._
import com.usermanagement.crud.dao._
import com.usermanagement.crud.models.ResponsesConfiguration.Error
import com.usermanagement.crud.dao.UserDAO
import io.circe.syntax._
import org.mockito.MockitoSugar
import org.scalatest.WordSpec

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

class UserRestServiceHandlerSpec extends WordSpec with UserRestServiceHandler with MockitoSugar {

  implicit val userDAO: UserDAO = mock[UserDAO]

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  "UserRestServiceHandlerSpec in user service" should {
    val userId=UUID.randomUUID().toString

    val userDetailsAdmin=UserDetails(Some(userId),"admin","name","username")
    val userDetailsCustomer=UserDetails(Some(userId),"customer","name","username")
    val invalidUserDetails=UserDetails(Some(userId),"seller","name","username")

    "send success message if able to get user details" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case GetAllUserDetails ⇒
            sender ! GetUserDetailsSuccess(Seq(userDetailsAdmin))
        }
      })
      val result = Await.result(super.getUserDetails(command), 5 second)
      assert(result.status==OK)
    }

    "send failure message if unable to get user details" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case GetAllUserDetails ⇒
            sender ! NoDataFound()
        }
      })
      val result = Await.result(super.getUserDetails(command), 5 second)
      assert(result.status==NoContent)
    }

    "send success message if admin details are inserted successfully" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserId(userId)=>
            sender ! Valid(false)
          case InsertUserDetails(userDetailsAdmin) ⇒
            sender ! CommandSuccess("User created successfully.",userDetailsAdmin.userId)
        }
      })
      val result = Await.result(super.addUserDetails(command, userDetailsAdmin), 5 second)
      assert(result.status==Created)
    }
    "send success message if customer details are inserted successfully" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserId(userId)=>
            sender ! Valid(false)
          case InsertUserDetails(userDetailsCustomer) ⇒
            sender ! CommandSuccess("User created successfully.",userDetailsCustomer.userId)
        }
      })
      val result = Await.result(super.addUserDetails(command, userDetailsCustomer), 5 second)
      assert(result.status==Created)
    }
    "send failure message if user details are not inserted" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserId(userId)=>
            sender ! Valid(false)
          case InsertUserDetails(userDetailsAdmin) ⇒
            sender ! CommandFailure("User creation failed!")
        }
      })
      val result = Await.result(super.addUserDetails(command, userDetailsAdmin), 5 second)
      assert(result.status==Forbidden)
    }
    "send failure message if user id already exists" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserId(id)=>
            sender ! Valid(true)
        }
      })
      val result = Await.result(super.addUserDetails(command, userDetailsAdmin), 5 second)
      assert(result.status==Conflict)
    }

    "send failure message for invalid user type" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserId(userId)=>
            sender ! Valid(false)
        }
      })
      val result = Await.result(super.addUserDetails(command, invalidUserDetails), 5 second)
      assert(result.status==Conflict)
    }


    "send failure message for empty user id" in {
      val update=UpdateAdminUsername(None,Some("username"))
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,admin)=>
            sender ! Valid(false)
        }
      })
      val result = Await.result(super.updateAdminUsername(command, update), 5 second)
      assert(result.status==Conflict)
    }

    "send failure message for empty user name" in {
      val update=UpdateAdminUsername(Some("id"),None)
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,admin)=>
            sender ! Valid(true)
        }
      })
      val result = Await.result(super.updateAdminUsername(command, update), 5 second)
      assert(result.status==Conflict)
    }

    "send failure message for invalid user id" in {
      val update=UpdateAdminUsername(Some("id"),Some(""))
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,admin)=>
            sender ! Valid(false)
        }
      })
      val result = Await.result(super.updateAdminUsername(command, update), 5 second)
      assert(result.status==Conflict)
    }

    "send success message for user name updation success" in {
      val update=UpdateAdminUsername(Some("id"),Some(""))
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,admin)=>
            sender ! Valid(true)
          case UpdateUsernameForAdmin(id,username,usertype)=>
            sender ! CommandSuccess("Admin username updated successfully.",Some(id))
        }
      })
      val result = Await.result(super.updateAdminUsername(command, update), 5 second)
      assert(result.status==Created)
    }
    "send failure message for user name updation failed" in {
      val update=UpdateAdminUsername(Some("id"),Some(""))
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,admin)=>
            sender ! Valid(true)
          case UpdateUsernameForAdmin(id,username,usertype)=>
            sender ! CommandFailure("Admin username updation failed!")
        }
      })
      val result = Await.result(super.updateAdminUsername(command, update), 5 second)
      assert(result.status==Forbidden)
    }

    "send failure message for invalid user in deletion" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,customer)=>
            sender ! Valid(false)
        }
      })
      val result = Await.result(super.deleteUser(command, Some("id")), 5 second)
      assert(result.status==Conflict)
    }

    "send success message for successful deletion" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,customer)=>
            sender ! Valid(true)
          case DeleteUser(id,customer)=>
            sender ! CommandSuccess("Customer deleted successfully.",Some(id))
        }
      })
      val result = Await.result(super.deleteUser(command, Some("id")), 5 second)
      assert(result.status==Created)
    }

    "send failure message for failed deletion" in {
      val command = TestActorRef(new Actor {
        def receive: Receive = {
          case ValidateUserType(id,customer)=>
            sender ! Valid(true)
          case DeleteUser(id,customer)=>
            sender ! CommandFailure("Customer deletion failed!")
        }
      })
      val result = Await.result(super.deleteUser(command, Some("id")), 5 second)
      assert(result.status==Forbidden)
    }

  }
}