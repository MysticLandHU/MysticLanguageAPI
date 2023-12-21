package hu.mysticland.mysticlanguageapi.commands

import hu.mysticland.mysticlanguageapi.Language
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI.Companion.plugin
import hu.mysticland.mysticlanguageapi.api.LanguageAPI
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
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
        if (player != null){
            var langShort = LanguageAPI.getLanguage(player)!!.lang
            player.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("activeLanguage",player, plugin.name),"&a"+langShort.toString())))
        }

    }

    @CommandHook("cmd_getlang")
    fun getLangForPlayer(sender: CommandSender, player: Player){
        if (sender is Player) {
            var langShort = LanguageAPI.getLanguage(player)!!.lang
            sender.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("activeLanguageAdmin", Bukkit.getPlayer(sender.name)!!, plugin.name),player.name,"&a"+langShort)))
        } else {
            var langShort = LanguageAPI.getLanguage(player)!!.lang
            sender.sendMessage(FormatUtils.color(String.format(LanguageAPI.getLine("activeLanguageAdmin", sender, plugin.name), player.name,"&a"+langShort)))
        }

    }
    @CommandHook("cmd_reload")
    fun reloadCommand(sender: CommandSender){
        if (sender is Player) {
            if (sender.hasPermission("mysticlanguageapi.reload")) {
                LanguageAPI.reloadLanguages()
                LanguageAPI.getLanguage(sender)!!.lang
                sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("configReloaded", sender, plugin.name)))
            } else {
                sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("configNotReloaded", sender, plugin.name)))
            }
        } else{
            LanguageAPI.reloadLanguages()
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("configReloaded", sender, plugin.name)))
        }
    }

    @CommandHook("cmd_main")
    fun mainCommand(sender: CommandSender) {
        if (sender is Player) {
            val player = sender.player!!
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("mainCommand", player, plugin.name)))
        } else {
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("mainCommand", sender, plugin.name)))
        }
    }

    @CommandHook("cmd_help")
    fun helpCommand(sender: CommandSender) {
        if (sender is Player) {
            val player = sender.player!!
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", player, "MysticBoosts")))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line1", player, plugin.name)))
            player.sendMessage("")
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line2", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line3", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line4", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line5", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", player, "MysticBoosts")))
        } else {
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", sender, "MysticBoosts")))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line1", sender, plugin.name)))
            sender.sendMessage("")
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line2", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line3", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line4", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage1Line5", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", sender, "MysticBoosts")))
        }

    }
    @CommandHook("cmd_help_2")
    fun helpCommand2(sender: CommandSender) {
        if (sender is Player) {
            val player = sender.player!!
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", player, "MysticBoosts")))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line1", player, plugin.name)))
            player.sendMessage("")
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line2", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line3", player, plugin.name)))
            player.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", player, "MysticBoosts")))
        } else {
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", sender, "MysticBoosts")))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line1", sender, plugin.name)))
            sender.sendMessage("")
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line2", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandPage2Line3", sender, plugin.name)))
            sender.sendMessage(FormatUtils.color(LanguageAPI.getLine("helpCommandVonalak", sender, "MysticBoosts")))
        }
    }
}