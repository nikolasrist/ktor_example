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
import io.ktor.jackson.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        route("/") {
            get {
                call.respond(Todos.getAll())
            }
            get("{id}") {
                var url = urlFor(call)
                call.respond(Todos.get(url))
            }
            post {
                val item = call.receive<Item>()
                item.url = urlFor(call);

                call.respond(Todos.save(item.url!!, item))
            }
            patch("{id}") {
                val newItem = call.receive<PatchItem>()
                var url = urlFor(call)
                call.respond(Todos.update(url, newItem))
            }
            delete("{id}") {
                var url = urlFor(call)
                call.respond(Todos.delete(url)!!)
            }
            delete {
                call.respond(Todos.deleteAll())
            }
        }
    }
}

fun urlFor(call: ApplicationCall): String {
    val host = call.request.host()
    val id = call.parameters["id"] ?: UUID.randomUUID().toString()
    return "https://${host}/${id}"
}

