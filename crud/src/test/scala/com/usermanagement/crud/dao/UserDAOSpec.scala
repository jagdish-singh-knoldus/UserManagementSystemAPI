package com.usermanagement.crud.dao


import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{AsyncWordSpecLike, Matchers, Sequential}
import java.util.UUID

class UserDAOSpec extends AsyncWordSpecLike with ScalaFutures with Matchers with ConfigLoader {

  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))


  implicit val searchLimit: Int = 2
  val userDAO = new UserDAO()

  val userId=UUID.randomUUID().toString

  val userDetailsAdmin=UserDetails(Some(userId),"admin","name","username")

  Sequential
  "UserDAOSpec service" should {
    "able to insert admin details in user_details table" in {
      whenReady(userDAO.createUser(userDetailsAdmin)) { res =>
        res shouldBe 1
      }
    }
    "be able to validate user id" in{
      whenReady(userDAO.validateUserId(userId)) { res=>
        res shouldBe 1
      }
    }
    "be able to validate user type" in{
      whenReady(userDAO.validateUserType(userId,"admin")) { res=>
        res shouldBe 1
      }
    }
    "be able to update user name" in{
      whenReady(userDAO.updateUsername(userId,"admin","admin")) { res=>
        res shouldBe 1
      }
    }
    "be able to get all users" in{
      whenReady(userDAO.getAllUsers){res=>
        res.head.userType shouldBe "admin"
      }
    }
    "be able to delete user" in{
      whenReady(userDAO.deleteUser(userId,"admin")) { res=>
        res shouldBe 1
      }
    }
  }
}
