package ru.cristalix.mods.socials

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO


data class Texture(
    val name: String,
    val url: String,
    val hash: String,
)

class TextureLoader(mod: KotlinMod) {

    private val textures = listOf(
        Texture("prefixes", "https://webdata.c7x.dev/social/prefixes.png", "EC891D9DDE2A8CA9B1856655BD5DC3EFCE021E43"),
        Texture("friends24", "https://webdata.c7x.dev/social/friends24.png", "EC891D9DDE2A8CA9B1855549BD5DC4EFCE021E44"),
        Texture("friends16", "https://webdata.c7x.dev/social/friends16.png", "EC891D9DD02A8CA9B1855549BD5DC4EFCE021E44"),
        Texture("bell", "https://webdata.c7x.dev/social/bell.png", "EC891D9DD02A8CA9B1855549BD5DC4EFCE043E44")
//        Texture("econom", "https://implario.dev/econom.png", "EC891D9DDE2A8CA9B1856649BD5DC4EFCE021E92"),
    )

    private val cacheDir = Paths.get("cache/delfikpro/")

    init {
        mod.onDisable.add {
            val renderEngine = UIEngine.clientApi.renderEngine()
            for (name in textures) {
                renderEngine.deleteTexture(ResourceLocation.of("delfikpro", name.hash))
            }
        }
    }

    fun loadTextures(): CompletableFuture<Void> {
        val futures = ArrayList<CompletableFuture<Void>>()

        textures.forEach { texture ->

            val name = texture.name
            val future = CompletableFuture<Void>()
            futures.add(future)

            CompletableFuture.runAsync {
                try {
                    Files.createDirectories(cacheDir)

                    val path = cacheDir.resolve(texture.hash)
                    val image = try {
                        Files.newInputStream(path).use {
                            ImageIO.read(it)
                        }
                    } catch (e: IOException) {
                        var connection: HttpURLConnection? = null
                        try {
                            val url = URL(texture.url)
                            connection = url.openConnection() as HttpURLConnection
                            connection.useCaches = false
                            connection.addRequestProperty(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"
                            )
                            connection.connect()

                            val bytes = connection.inputStream.readBytes()
                            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                            ImageIO.read(ByteArrayInputStream(bytes))
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                            null
                        } finally {
                            connection?.disconnect()
                        }
                    }
                    UIEngine.clientApi.minecraft().execute {
                        if (image != null) {
//                            println(name + " loaded")
                            val location = ResourceLocation.of("delfikpro", name)
                            UIEngine.clientApi.renderEngine().deleteTexture(location)
                            UIEngine.clientApi.renderEngine().loadTexture(
                                location,
                                UIEngine.clientApi.renderEngine().newImageTexture(image, false, false)
                            )
                        }
                        future.complete(null)
                    }
                } catch (e: Exception) {
                    future.completeExceptionally(e)
                }
            }
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }
}