package net.leanix

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.jackson.*
import kotlin.test.*
import io.ktor.server.testing.*
import java.nio.charset.Charset

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")), response.contentType())
                val value = jacksonObjectMapper().readValue<List<Item>>(response.content!!)
                assertEquals(emptyList(), value)
            }
        }
    }
}
