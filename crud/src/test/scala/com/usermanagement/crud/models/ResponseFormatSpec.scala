package com.usermanagement.crud.models

import com.usermanagement.crud.models.ResponsesConfiguration._
import org.scalatest.WordSpec

class ResponseFormatSpec extends WordSpec with ResponseFormat {

  "ResponseFormatSpec" should {

    "be able to generate a commonResponseForCaseClass" in {
      val res = generateCommonResponseForCaseClass(true, None, None, Some("user"))
      assert(res == StandardResponseForCaseClass(Some("user"), true, None, None))
    }

    "be able to generate generateCommonResponseForError" in {
      val res = sendFormattedError("CODE", "unsuccessful", Some("user"))
      assert(res == generateCommonResponseForError(false,
        Some(List(Error("CODE","unsuccessful", None))), Some("user")))
    }
  }
}
