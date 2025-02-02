/*
 * Copyright 2024 RTAkland
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package cn.rtast.mcavatar.utils

import cn.rtast.mcavatar.SKIN_SERVER_URL
import cn.rtast.mcavatar.UUID_LOOKUP_URL
import cn.rtast.mcavatar.gson
import cn.rtast.mcavatar.entity.DecodedSkin
import cn.rtast.mcavatar.entity.Skin
import cn.rtast.mcavatar.entity.UsernameUUID
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.net.URI
import java.util.*
import javax.imageio.ImageIO


object SkinHeadUtil {

    private fun getSkinHead(skinUrl: String): BufferedImage {
        val url = URI(skinUrl).toURL()
        val image = ImageIO.read(url)
        var subImage = image.getSubimage(8, 8, 8, 8)

        if (isFullyTransparent(subImage)) {
            // single layer skin
            subImage = image.getSubimage(40, 8, 8, 8)
        } else {
            // multi layer skin
            val hairLayer = image.getSubimage(40, 8, 8, 8)
            val combined = BufferedImage(subImage.width, subImage.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = combined.createGraphics()
            g2d.drawImage(subImage, 0, 0, null);
            g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
            g2d.drawImage(hairLayer, 0, 0, null)
            g2d.dispose()
            subImage = combined
        }
        val zoom = zoomTo64(subImage)
        return zoom
    }


    private fun getSkinFavicon(skinContent: String): BufferedImage {
        val decodedSkinUrl = gson.fromJson(skinContent, DecodedSkin::class.java).textures.skin.url
        return getSkinHead(decodedSkinUrl)
    }

    fun getSkinFaviconWithUUID(uuid: String): BufferedImage {
        val skinResult = URI(SKIN_SERVER_URL + uuid).toURL().readText()
        val skinResultJson = gson.fromJson(skinResult, Skin::class.java)
        val decodedSkinContent =
            String(Base64.getDecoder().decode(skinResultJson.properties.first().value), Charsets.UTF_8)
        return getSkinFavicon(decodedSkinContent)
    }

    fun getSkinFaviconWithUsername(username: String): BufferedImage {
        val userSkinContent = URI(UUID_LOOKUP_URL + username).toURL().readText()
        val uuid = gson.fromJson(userSkinContent, UsernameUUID::class.java).id
        return getSkinFaviconWithUUID(uuid)
    }
}