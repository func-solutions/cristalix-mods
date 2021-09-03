package ru.cristalix.mods.socials

import com.google.gson.Gson
import dev.xdark.clientapi.entity.*
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.network.ServerSwitch
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.event.render.RenderTickPost
import dev.xdark.clientapi.gui.ingame.ChatScreen
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.mods.socials.notification.Notification
import ru.cristalix.mods.socials.notification.NotificationOverlay
import ru.cristalix.mods.socials.notification.NotificationScreen
import ru.cristalix.socials.data.NotificationButton
import ru.cristalix.socials.data.NotificationData
import ru.cristalix.socials.data.friendsCommand
import ru.cristalix.socials.data.getPrefix
import ru.cristalix.uiengine.ClickEvent
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.MouseButton
import java.nio.charset.StandardCharsets
import java.util.*

lateinit var socialsScreen: SocialsScreen
lateinit var notificationScreen: NotificationScreen

class SocialsMod : KotlinMod() {

    val cache: MutableMap<UUID, SocialNode> = HashMap()

    override fun onEnable() {

        UIEngine.initialize(this)

        TextureLoader(this).loadTextures()

        var loaded = false

        registerHandler<GameLoop> {
            val player = UIEngine.clientApi.minecraft().player
            if (!loaded && player != null) {

                socialsScreen = SocialsScreen()
                val notificationOverlay = UIEngine.overlayContext + NotificationOverlay()

                loaded = true
                registerHandler<ServerSwitch> {
                    socialsScreen.skin.uuid = UIEngine.clientApi.minecraft().player?.uniqueID ?: UUID(0, 0)
                    socialsScreen.nameText.content = UIEngine.clientApi.minecraft().player?.name ?: ""
                    for (child in socialsScreen.friendList.children) {
                        if (child is SocialNode) {
                            child.head.reload()
                        }
                    }
                }
                val lastMouseState = booleanArrayOf(false, false, false)

                registerHandler<RenderTickPost> {
                    socialsScreen.transformAndRender()
                    for (button in MouseButton.VALUES) {
                        val idx = button.ordinal
                        val oldState = lastMouseState[idx]
                        val newState = Mouse.isButtonDown(idx)
                        if (oldState != newState) {
                            socialsScreen.getForemostHovered()?.run {
                                onClick?.invoke(ClickEvent(newState, button))
                            }
                            lastMouseState[idx] = newState
                        }
                    }
                }

                registerHandler<ChatSend> {
                    if (message == "/dev disable socials") {
                        unload()
                        isCancelled = true
                    }
                    if (message == "/dev notify") {

                        isCancelled = true
                        notificationOverlay.push(Notification(NotificationData(null, "party", "hello\nhello",
                        0x2a66bd, 0x183968, 3000, listOf(
                                NotificationButton("Принять", 0x2a66bd, "hello", false, false)
                        ))))
                    }
                }

                registerHandler<GuiOverlayRender> {
                    val currentScreen = clientApi.minecraft().currentScreen()
                    if (currentScreen != null && currentScreen::class.java.simpleName == "agZ") {
                        val resolution = clientApi.resolution()
                        clientApi.overlayRenderer()
                            .drawRect(0, 0, resolution.scaledWidth + 1, resolution.scaledHeight + 1, 0xFF000000.toInt())
                    }
                }

                val gson = Gson()

                fun updateOnlineStatuses() {
                    UIEngine.schedule(5) {
                        socialsScreen.friendList.children.forEach {
                            if (it is SocialNode) it.status = it.status
                        }
                        updateOnlineStatuses()
                    }
                }

                updateOnlineStatuses()

                registerChannel("notify:new") {

                    val json = this.toString(StandardCharsets.UTF_8)
                    val notificationData = gson.fromJson(json, NotificationData::class.java)
                    notificationOverlay.push(Notification(notificationData))

                }

                registerChannel("friends:list") {
                    val json = this.toString(StandardCharsets.UTF_8)

                    val socials = gson.fromJson(json, Array<SocialInfo>::class.java)


                    for (info in socials) {

//                    println(info)

//                    clientApi.chat().printChatMessage("" + info.uuid)
                        var social = cache[info.uuid]
                        if (social == null) {
                            social = SocialNode(info.uuid)
                            cache[info.uuid] = social
                        }

                        if (info.staffGroup != null) social.staffGroup = info.staffGroup
                        if (info.donateGroup != null) social.donateGroup = info.donateGroup
                        if (info.partyUid != null) social.partyUid = info.partyUid
                        if (info.name != null) social.name = info.name
                        if (info.status != null) social.status = info.status
                        if (info.relation != null) social.relation = info.relation
                        if (info.lastSeenOnline != null) {
                            social.lastSeenTime =
                                if (info.lastSeenOnline <= 0) -1 else (System.currentTimeMillis() - info.lastSeenOnline)
                        }
                        if (info.realm != null) social.realm = info.realm

//                    context = SocialsScreen()

                        socialsScreen.friendList.children.clear()

                        var i = 0

                        cache.values.forEach {
                            if (it.relation == "FRIEND" && it.id != clientApi.minecraft().player?.uniqueID) {
                                socialsScreen.friendList.addChild(it)
                                i++
                            }
                            if (it.id == clientApi.minecraft().player?.uniqueID) {
//                            clientApi.chat().printChatMessage(it.donateGroup + " " + it.staffGroup)
                                if (it.donateGroup != null) socialsScreen.donateGroup.prefix = getPrefix(it.donateGroup)
                                if (it.staffGroup != null) socialsScreen.staffGroup.prefix = getPrefix(it.staffGroup)
                                if (it.realm != null) socialsScreen.currentRealm.content = it.realm!!
                                if (it.status != null) {
                                    socialsScreen.currentRealm.color =
                                        when (it.status!!) {
                                            "AFK" -> Color(0xE0, 0x76, 0x14, 1.0)
                                            "ONLINE" -> Color(100, 255, 100, 1.0)
                                            else -> Color(100, 100, 100, 1.0)
                                        }
                                }
                            }
                        }




                        socialsScreen.friendList.children.sortBy {
                            when ((it as SocialNode).status) {
                                "ONLINE" -> 1
                                "AFK" -> 2
                                else -> if (it.millisSinceOnline >= 0) it.millisSinceOnline + 3 else Long.MAX_VALUE
                            }
                        }


                        socialsScreen.friendList.update()

                    }


                }

                clientApi.chat().sendChatMessage("$friendsCommand request")
            }


            if (loaded) {
                socialsScreen.enabled = clientApi.minecraft().currentScreen() is ChatScreen
                if (!socialsScreen.enabled) socialsScreen.currentMenu?.let {
                    it.enabled = false
                    socialsScreen.currentMenu = null
                }
            }

        }




    }

}