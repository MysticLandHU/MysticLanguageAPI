package hu.mysticland.mysticlanguageapi.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.mysticland.mysticlanguageapi.Language
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI.Companion.plugin
import hu.mysticland.mysticlanguageapi.SavedSetting
import hu.mysticland.mysticlanguageapi.event.LanguageChangeEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import redempt.redlib.misc.FormatUtils
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.system.exitProcess

object LanguageAPI {

    var languageData = HashMap<Language, HashMap<String,String>>()
    var languageSettings = HashMap<UUID,Language>()
    var languageSettingsSender = HashMap<CommandSender,Language>()
    var languagesDisplayNamesList = mutableListOf<String>()

    // TODO: Try to add a sign belowName to see the language of the player
    // TODO: Nyelvi támogatás: MysticSafe, MysticBan, FakeLobbyTeleport, MysticTeamSelector, MysticBWshopBR(IMPOSSIBLE!!!444!!)

    fun getLine(x: String, player: Player): String {
        val lang = getLanguage(player)
        val lines = languageData.get(lang)
        var line = ""
        if (lines != null) {
            if (lines.containsKey(x)) {
                line = lines[x].toString()
            }else{
                return ""
            }
        }else {
            return ""
        }
        return FormatUtils.color(line)
    }
    fun getLine(x: String, sender: CommandSender): String {
        val lang = getLanguage(sender)
        val lines = languageData.get(lang)
        var line = ""
        if (lines != null) {
            if (lines.containsKey(x)) {
                line = lines[x].toString()
            }else{
                return ""
            }
        }else {
            return ""
        }
        return FormatUtils.color(line)
    }

    fun getLine(x: String, lang: Language): String {
        val lines = languageData[lang]
        var line = ""
        if (lines != null) {
            if (lines.containsKey(x)) {
                line = lines[x].toString()
            }else{
                return ""
            }
        }else {
            return ""
        }
        return FormatUtils.color(line)
    }

    fun saveLang4Player(){
        val langsToSave = ArrayList<SavedSetting>()
        languageSettings.forEach { (_uuid, _Language) ->
            langsToSave.add(
                SavedSetting(
                    _uuid,
                    _Language
                )
            )
        }

        val boostSavesBuilder = GsonBuilder().create()
        val boostData = boostSavesBuilder.toJson(langsToSave)
        val jsonWriter = FileWriter(File(MysticLanguageAPI.plugin.dataFolder, "saved-player-langs.json"))
        jsonWriter.write(boostData)
        jsonWriter.close()
    }

    fun loadLanguagesFromFile() {
        val savesFile = File(MysticLanguageAPI.plugin.dataFolder, "saved-player-langs.json")
        val fajlok = File(MysticLanguageAPI.plugin.dataFolder,"/languages").listFiles()
        if (fajlok != null) {
            for (file in fajlok) {
                val fileName = file.name
                if (fileName.startsWith("lang_")) {
                    val language = fileName.substringAfter("lang_")
                    languagesDisplayNamesList.add(language)
                }
            }
        }
        if (savesFile.exists()) {
            val langsToLoad = arrayListOf(
                *Gson().fromJson(
                    FileReader(savesFile), Array<SavedSetting>::class.java
                )
            )
            langsToLoad.forEach {
                languageSettings.set(it.uuid,it.lang)
            }
            MysticLanguageAPI.plugin.logger.warning("Sikeresen visszaállítottunk ${languageSettings.size} beálítást!")
            savesFile.delete()
        }
    }


    fun setLanguage(player: Player, lang: Language){
        val oldLanguage = getLanguage(player)
        val event = LanguageChangeEvent(player, oldLanguage?.lang ?: "", lang.lang)
        Bukkit.getPluginManager().callEvent(event)
        if (!event.isCancelled) {
            languageSettings[player.uniqueId] = lang
            languageSettingsSender[player] = lang
        }
    }

    fun getLanguage(player: Player): Language? {
        if(languageSettings.get(player.uniqueId) == null){
            languageSettings.set(player.uniqueId,Language("lang_hu"))
        }
        val lang = languageSettings.get(player.uniqueId)
        return lang
    }

    fun getLanguage(sender: CommandSender): Language? {
        if(languageSettingsSender.get(sender) == null){
            languageSettingsSender.set(sender,Language("lang_hu"))
        }
        val lang = languageSettingsSender.get(sender)
        return lang
    }


    fun getLanguages(): MutableSet<Language> {
        return languageData.keys
    }

    fun loadLanguages(l: String) {
        languageData.clear()
        Files.walk(Paths.get(plugin.dataFolder.path+"/"+l))
            .forEach {
                if (it.isRegularFile()){
                    if (it != null) {
                        if (it.fileName.pathString.contains("lang_")) {
                            val languagePack = loadLanguage(it)
                            if (languageData.get(Language(it.fileName.name)) != null) {
                                languageData.get(Language(it.fileName.name))!!.forEach {
                                    languagePack.set(it.key, it.value)
                                }
                            }
                            languageData.set(Language(it.fileName.name), languagePack)
                        }
                    }
                }
            }

    }

    fun loadLanguage(langFile: Path): HashMap<String, String> {
        val languagePack = HashMap<String, String>()
        val inputStream: InputStream = File(langFile.toUri()).inputStream()
        if (File(langFile.toUri()).name.contains("lang_")) {
            plugin.logger.info("Nyelvi fájl betöltése folyamatban: ${File(langFile.toUri()).parentFile.name} (${File(langFile.toUri()).name})")
            inputStream.bufferedReader().forEachLine { line ->
                if (!line.startsWith("#") && line.isNotBlank()) {
                    val parts = line.split("|:")
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].removePrefix(" ").trim()
                        languagePack[key] = value
                    }
                }
            }
        }
        return languagePack
    }
}