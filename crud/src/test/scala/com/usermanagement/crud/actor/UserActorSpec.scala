package com.usermanagement.crud.actor

import java.time.OffsetDateTime
import java.util.UUID
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import UserActor._
import com.usermanagement.crud.dao._
import com.usermanagement.crud.actor.UserActor.NoDataFound
import com.usermanagement.crud.models.Helper
import com.usermanagement.crud.dao.{UserDAO, UserDetails}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Encoder, Json, parser}
import io.circe.syntax.EncoderOps
import io.circe.generic.auto._
import org.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import slick.jdbc.H2Profile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps
class UserActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers with BeforeAndAfterAll with Helper with MockitoSugar {

  def this() = this(ActorSystem("AccountActorSystem"))

  val driver = H2Profile

  import driver.api.Database

  implicit val db: driver.api.Database = mock[Database]
  implicit val schema: String = ""
  val futureAwaitTime: FiniteDuration = 10.minute

  implicit val futureAwaitDuration: FiniteDuration =
    FiniteDuration(futureAwaitTime.length, futureAwaitTime.unit)
  val userDAO: UserDAO = mock[UserDAO]


  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A UserActor" must {

    val userId=UUID.randomUUID().toString

    val userDetailsAdmin=UserDetails(Some(userId),"admin","name","username")

    "be able to get all user details" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.getAllUsers) thenReturn Future(Seq(userDetailsAdmin))
      }))
      actorRef ! GetAllUserDetails
      expectMsgType[GetUserDetailsSuccess](5 seconds)
    }

    "not be able to get all user details" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.getAllUsers) thenReturn Future(Nil)
      }))
      actorRef ! GetAllUserDetails
      expectMsgType[NoDataFound](5 seconds)
    }

    "be able to validate user id" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.validateUserId("user id")) thenReturn Future(1)
      }))
      actorRef ! ValidateUserId("user id")
      expectMsgType[Valid](5 seconds)
    }

    "not be able to validate user id" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.validateUserId("user id")) thenReturn Future(0)
      }))
      actorRef ! ValidateUserId("user id")
      expectMsgType[Valid](5 seconds)
    }

    "be able to validate user type" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.validateUserType("user id","admin")) thenReturn Future(1)
      }))
      actorRef ! ValidateUserType("user id","admin")
      expectMsgType[Valid](5 seconds)
    }

    "not be able to validate user type" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.validateUserType("user id","admin")) thenReturn Future(0)
      }))
      actorRef ! ValidateUserId("user id")
      expectMsgType[Valid](5 seconds)
    }

    "be able to insert user details" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.createUser(userDetailsAdmin)) thenReturn Future(1)
      }))
      actorRef ! InsertUserDetails(userDetailsAdmin)
      expectMsgType[CommandSuccess](5 seconds)
    }

    "not be able to insert user details" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.createUser(userDetailsAdmin)) thenReturn Future(0)
      }))
      actorRef ! InsertUserDetails(userDetailsAdmin)
      expectMsgType[CommandFailure](5 seconds)
    }

    "be able to update user name" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.updateUsername("user id",
          "username","admin")) thenReturn Future(1)
      }))
      actorRef ! UpdateUsernameForAdmin("user id","username","admin")
      expectMsgType[CommandSuccess](5 seconds)
    }

    "not be able to update user name" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.updateUsername("user id",
          "username","admin")) thenReturn Future(0)
      }))
      actorRef ! UpdateUsernameForAdmin("user id","username","admin")
      expectMsgType[CommandFailure](5 seconds)
    }

    "be able to delete user" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.deleteUser("user id","customer")) thenReturn Future(1)
      }))
      actorRef ! DeleteUser("user id","customer")
      expectMsgType[CommandSuccess](5 seconds)
    }

    "not be able to delete user" in {
      val actorRef = system.actorOf(Props(new UserActor(userDAO) {
        when(userDAO.deleteUser("user id","customer")) thenReturn Future(0)
      }))
      actorRef ! DeleteUser("user id","customer")
      expectMsgType[CommandFailure](5 seconds)
    }

  }
}