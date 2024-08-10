package kr.enak.akuru.permissiblevoicechat

import de.maxhenkel.voicechat.api.BukkitVoicechatService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PermissibleVoiceChat : JavaPlugin() {
    private lateinit var voiceChatPlugin: VoiceChatPlugin

    override fun onEnable() {
        // Plugin startup logic
        val service = server.servicesManager.load(BukkitVoicechatService::class.java)
        if (service == null) {
            logger.warning("No voice chat service found for this server")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        voiceChatPlugin = VoiceChatPlugin(logger)
        service.registerPlugin(voiceChatPlugin)
        Bukkit.getPluginManager().registerEvents(voiceChatPlugin, this)
    }
}
