package ru.cristalix.mod

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.registerHandler
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class TextureLoader(mod: KotlinMod) {

    private val textures = hashMapOf(
        "space.png" to "ADED41875F1F7DCC478B86F42F3EBE36C694DFEE"
    )

    private val cacheDir = Paths.get("cache/glory/")

    init {
        mod.onDisable.add {
            val renderEngine = clientApi.renderEngine()
            for (name in textures.keys) {
                renderEngine.deleteTexture(ResourceLocation.of("glory", name))
            }
        }
    }

    fun loadTextures(): CompletableFuture<Void> {
        val futures = ArrayList<CompletableFuture<Void>>()

        textures.forEach { (name, sha1) ->
            val future = CompletableFuture<Nothing>()

            CompletableFuture.runAsync {
                try {
                    Files.createDirectories(cacheDir)

                    val path = cacheDir.resolve(sha1)
                    val image = try {
                        Files.newInputStream(path).use {
                            ImageIO.read(it)
                        }
                    } catch (e: IOException) {
                        var connection: HttpURLConnection? = null
                        try {
                            val url = URL("https://webdata.c7x.dev/social/$name")
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
                            null
                        } finally {
                            connection?.disconnect()
                        }
                    }
                    clientApi.minecraft().execute {
                        if (image != null) {
                            clientApi.renderEngine().loadTexture(
                                ResourceLocation.of("glory", name),
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