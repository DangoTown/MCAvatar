package cn.rtast.mcavatar.plugins

import cn.rtast.mcavatar.utils.SkinHeadUtil
import cn.rtast.mcavatar.utils.toByteArray
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello User! get /uuid/<uuid> or /username/<username>")
        }

        get("/uuid/{uuid}") {
            val uuid = call.parameters["uuid"] ?: return@get call.respondText("Params must contain uuid. Example: /uuid/bb033844e68e4909a6361a5d1821ddc4")
            val headImage = SkinHeadUtil.getSkinFaviconWithUUID(uuid).toByteArray()
            call.respondBytes(headImage, ContentType.Image.PNG)
        }

        get("/username/{username}") {
            val username = call.parameters["username"] ?: return@get call.respondText("Params must contain username. Example: /username/RTAkland")
            val headImage = SkinHeadUtil.getSkinFaviconWithUsername(username).toByteArray()
            call.respondBytes(headImage, ContentType.Image.PNG)
        }
    }
}
