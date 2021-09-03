package ru.cristalix.mods.flappy

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine.clientApi
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
        Texture("308380a9-2c69-11e8-b5ea-1cb72caa35fd", "https://webdata.c7x.dev/textures/skin/308380a9-2c69-11e8-b5ea-1cb72caa35fd", "EC891D9DDE2A8CA9B1856644BD5DC3EFCE021E43"),
        Texture("komfortplus", "https://implario.dev/komfortplus2.png", "EC891D9DDE2A8CA9B1856649BD5DC4EFCE021E44"),
        Texture("econom", "https://implario.dev/econom.png", "EC891D9DDE2A8CA9B1856649BD5DC4EFCE021E92"),
    )

    private val cacheDir = Paths.get("cache/delfikpro/")

    init {
        mod.onDisable.add {
            val renderEngine = clientApi.renderEngine()
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
                    clientApi.minecraft().execute {
                        if (image != null) {
                            println(name + " loaded")
                            clientApi.renderEngine().loadTexture(
                                ResourceLocation.of("delfikpro", name),
                                clientApi.renderEngine().newImageTexture(image, false, false)
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