package hu.mysticland.mysticlanguageapi.commands

import hu.mysticland.mysticboosts.api.BoostsAPI
import hu.mysticland.mysticboosts.npc.NPCManager
import hu.mysticland.mysticlanguageapi.Language
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI.Companion.plugin
import hu.mysticland.mysticlanguageapi.api.LanguageAPI
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import redempt.redlib.commandmanager.CommandHook
import redempt.redlib.misc.FormatUtils

class CommandManager {

    val commandCooldown = LinkedHashSet<Player>()

    @CommandHook("activate-language")
    fun activateLanguage(player: Player, lang: String) {
        if (commandCooldown.contains(player)) {
            player.sendMessage(String.format(LanguageAPI.getLine("commandCooldown", player)))
        } else {
            commandCooldown.add(player)
            object : BukkitRunnable() {
                override fun run() {
                    commandCooldown.remove(player)
                }
            }.runTaskLaterAsynchronously(plugin, 20 * 3L)
            if(LanguageAPI.languageData.keys.contains(Language(lang))) {
                LanguageAPI.setLanguage(player, Language(lang))
                BoostsAPI.reloadNpcLanguage(player)
                player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("successLangChange", player))))
            }else{
                player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("langNotExist", player))))
            }
        }
    }

    @CommandHook("set-language")
    fun setLanguage(player: Player) {
        var languages = LanguageAPI.getLanguages().toMutableList()
        if (languages.isNotEmpty()) {
            player.sendMessage(FormatUtils.color("&b----------------------------"))
            player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("availableLanguages", player))))
            player.sendMessage("")
            while (languages.isNotEmpty()) {
                var language = languages.get(0)
                var languageName = LanguageAPI.languageData.get(language)!!.get("languageName")
                val defs = TextComponent(FormatUtils.color("&8- "))
                val message = TextComponent(FormatUtils.color("&f$languageName"))
                message.clickEvent =
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/activate-language ${language.lang}")
                player.sendMessage(defs, message)
                while (languages.contains(language)) {
                    languages.remove(language)
                }
            }
            player.sendMessage("")
            player.sendMessage(FormatUtils.color("&b----------------------------"))
        }else {
            player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("errorLoadingLang", player))))
        }

    }
    @CommandHook("cmd_lang")
    fun getLang(player: Player){
        val lang = LanguageAPI.getLine("languageName",player)
        var langShort = LanguageAPI.getLanguage(player)!!.lang
        player.sendMessage(FormatUtils.color("${String.format(LanguageAPI.getLine("activeLanguage",player),"&a"+lang)} &7(&a${langShort.removePrefix("lang_")}&7)"))
    }

    @CommandHook("cmd_getlang")
    fun getLangForPlayer(sender: CommandSender, player: Player){

        val lang = LanguageAPI.getLine("languageName",player)
        var langShort = LanguageAPI.getLanguage(player)!!.lang
        sender.sendMessage(FormatUtils.color("${String.format(LanguageAPI.getLine("activeLanguageAdmin", Bukkit.getPlayer(sender.name)!!),player.name,"&a"+lang)} &7(&a${langShort.removePrefix("lang_")}&7)"))
    }
}