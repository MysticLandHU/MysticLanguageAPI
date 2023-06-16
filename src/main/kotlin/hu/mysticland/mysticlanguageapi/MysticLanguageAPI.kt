package hu.mysticland.mysticlanguageapi

import hu.mysticland.mysticlanguageapi.api.LanguageAPI
import hu.mysticland.mysticlanguageapi.commands.CommandManager
import org.bukkit.plugin.java.JavaPlugin
import redempt.redlib.commandmanager.CommandParser
import redempt.redlib.commandmanager.Messages
import java.util.*

class MysticLanguageAPI: JavaPlugin() {

    lateinit var languageAPI: MysticLanguageAPI

    override fun onEnable() {
        languageAPI = this
        languageAPI.languageAPI = plugin
        CommandParser(getResource("commands.rdcml"), Messages.getLoaded(this)).parse()
            .register("mysticlang", CommandManager())
        logger.info("The API started successfully")
    }

    override fun onDisable() {
        LanguageAPI.saveLang4Player()
        logger.info("The API stopped successfully")

    }
    companion object {
        val plugin by lazy { getPlugin(MysticLanguageAPI::class.java) }
    }
}
data class Language(
    val lang: String
)

data class SavedSetting(
    val uuid: UUID,
    val lang: Language
)