package hu.mysticland.mysticlanguageapi.commands

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
            player.sendMessage(String.format(LanguageAPI.getLine("commandCooldown", player, plugin.name)))
        } else {
            commandCooldown.add(player)
            object : BukkitRunnable() {
                override fun run() {
                    commandCooldown.remove(player)
                }
            }.runTaskLaterAsynchronously(plugin, 20 * 3L)
            if(LanguageAPI.languageData.keys.contains(Language(lang))) {
                LanguageAPI.setLanguage(player, Language(lang))
                player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("successLangChange", player, plugin.name))))
            }else{
                player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("langNotExist", player, plugin.name))))
            }
        }
    }

    @CommandHook("set-language")
    fun setLanguage(player: Player) {
        val availableLanguages = LanguageAPI.availableLangs

        if (availableLanguages.isNotEmpty()) {
            player.sendMessage(FormatUtils.color("&b----------------------------"))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("availableLanguages", player, plugin.name)))
            player.sendMessage("")

            for (language in availableLanguages) {
                val defs = TextComponent(FormatUtils.color("&8- "))
                val message = TextComponent(FormatUtils.color("&f${language.lang}"))
                message.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/activate-language ${language.lang}")
                player.sendMessage(defs, message)
            }

            player.sendMessage("")
            player.sendMessage(FormatUtils.color("&b----------------------------"))
        } else {
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("errorLoadingLang", player, plugin.name)))
        }
    }

    @CommandHook("cmd_lang")
    fun getLang(player: Player){
        var langShort = LanguageAPI.getLanguage(player)!!.lang
        player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("activeLanguage",player, plugin.name),"&a"+langShort)))
    }

    @CommandHook("cmd_getlang")
    fun getLangForPlayer(sender: CommandSender, player: Player){
        var langShort = LanguageAPI.getLanguage(player)!!.lang
        sender.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("activeLanguageAdmin", Bukkit.getPlayer(sender.name)!!, plugin.name),player.name,"&a"+langShort)))
    }
}