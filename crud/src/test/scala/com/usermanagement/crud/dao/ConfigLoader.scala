package com.usermanagement.crud.dao

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile

trait ConfigLoader extends LazyLogging {
  implicit val schema: String = "user"

  val driver = H2Profile
  import driver.api.Database
  val h2Url =
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE;INIT=RUNSCRIPT FROM './src/test/resources/user.sql';"
  implicit val db: Database = Database.forURL(url = h2Url, driver = "org.h2.Driver")

}