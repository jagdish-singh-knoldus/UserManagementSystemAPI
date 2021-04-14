package com.usermanagement.crud.routes

import java.io.File
import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.usermanagement.crud.dao._
import com.usermanagement.crud.actor.UserActor
import com.usermanagement.crud.dao.UserDAO
import org.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import slick.jdbc.H2Profile

import scala.concurrent.Future
import scala.concurrent.duration._

class UserRestServiceSpec extends WordSpec with Matchers with ScalatestRouteTest
  with UserRestService with MockitoSugar {

  val driver = H2Profile

  import driver.api.Database

  implicit val db: driver.api.Database = mock[Database]
  implicit val schema: String = ""

  val userDAO: UserDAO = mock[UserDAO]

  val futureAwaitTime: FiniteDuration = 10.minute

  implicit val futureAwaitDuration: FiniteDuration =
    FiniteDuration(futureAwaitTime.length, futureAwaitTime.unit)

  val userActor: ActorRef = system.actorOf(
    UserActor
      .props(userDAO),
    "userActor")

  override   def addUserDetails(command: ActorRef,
                                userData: UserDetails): Future[HttpResponse] =
    Future.successful(HttpResponse.apply())

  override def updateAdminUsername(command: ActorRef,
                                         updateAdminUsername: UpdateAdminUsername): Future[HttpResponse] =
    Future.successful(HttpResponse.apply())

  override def deleteUser(command: ActorRef, userId: Option[String]): Future[HttpResponse] =
    Future.successful(HttpResponse.apply())

  override def getUserDetails(command: ActorRef): Future[HttpResponse] =
    Future.successful(HttpResponse.apply())

  val route: Route = routes(userActor)

  "UserRestServiceSpec service" should {

    "return ok when create  user route is hit" in {
      val data = HttpEntity(
        ContentTypes.`application/json`,
        s"""{
           |    "userType": "user type",
           |    "name": "name",
           |    "username": "username"
           |}""".stripMargin)

      Put("/user/insert-user", data) ~> route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return ok when update username route is hit" in {
      val data = HttpEntity(
        ContentTypes.`application/json`,
        s"""{
           |    "userId": "user id",
           |    "username": "username"
           |}""".stripMargin)

      Put("/user/update-admin-username", data) ~> route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return ok when delete user route is hit" in {
      Delete("/user/delete-customer?userId=id") ~> route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return ok when get all user route is hit" in {
      Get("/user/get-all-users") ~> route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
  }