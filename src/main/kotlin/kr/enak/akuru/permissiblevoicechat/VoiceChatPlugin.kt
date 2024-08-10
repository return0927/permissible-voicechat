package kr.enak.akuru.permissiblevoicechat

import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.JoinGroupEvent
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent
import de.maxhenkel.voicechat.plugins.impl.PositionImpl
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import java.util.*
import java.util.logging.Logger

class VoiceChatPlugin(
    private val logger: Logger,
) : VoicechatPlugin, Listener {
    private val GROUP_NAME = "전체방송"

    private lateinit var uniqueId: UUID

    override fun getPluginId(): String = "permissible-voicechat"

    override fun registerEvents(registration: EventRegistration) {
        super.registerEvents(registration)
        registration.registerEvent(VoicechatServerStartedEvent::class.java, this::onServerStart)
        registration.registerEvent(JoinGroupEvent::class.java, this::onJoinGroup)
        registration.registerEvent(MicrophonePacketEvent::class.java, this::onMic)
    }

    private fun onServerStart(event: VoicechatServerStartedEvent) {
        val group = event.voicechat.groupBuilder()
            .setName(GROUP_NAME)
            .setPersistent(true)
            .build()

        this.uniqueId = group.id
        logger.info("전체방송 그룹 생성 완료: $uniqueId")
    }

    private fun onJoinGroup(event: JoinGroupEvent) {
        if (event.group?.name != GROUP_NAME || (event.connection?.player?.player as? org.bukkit.entity.Player)?.isOp == true)
            return

        event.cancel()
    }

    private fun onMic(event: MicrophonePacketEvent) {
        val sender = event.senderConnection?.player?.uuid?.let { Bukkit.getPlayer(it) }
        if (sender?.isOp != true) {
            event.cancel()
            return
        }

        if (event.senderConnection?.group?.name == GROUP_NAME) {
            val api = event.voicechat

            Bukkit.getOnlinePlayers().forEach { receiver ->
                if (receiver.uniqueId == sender.uniqueId) return@forEach

                val conn = api.getConnectionOf(receiver.uniqueId)?: return@forEach
                api.sendLocationalSoundPacketTo(conn, event.packet.toLocationalSoundPacket(PositionImpl(receiver.location)))
            }
        }
    }
}