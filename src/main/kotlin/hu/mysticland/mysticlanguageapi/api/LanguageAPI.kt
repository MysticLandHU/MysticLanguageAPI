package hu.mysticland.mysticlanguageapi.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.mysticland.mysticlanguageapi.Language
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI.Companion.plugin
import hu.mysticland.mysticlanguageapi.SavedSetting
import hu.mysticland.mysticlanguageapi.event.LanguageChangeEvent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import redempt.redlib.misc.FormatUtils
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

object LanguageAPI {
    var availableLangs = mutableListOf<Language>()
    var availablePlugins = mutableListOf<String>()
    var languageData = HashMap<Language, HashMap<String, String>>()
    var languageSettings = HashMap<UUID, Language>()
    var languageSettingsSender = HashMap<CommandSender, Language>()
    var jsonFilePath = plugin.dataFolder.path + "/languages/languages.json"
    var jsonFilePathSavedSetting = plugin.dataFolder.path + "/saved-player-langs.json"

    fun getLine(x: String, player: Player, pluginName: String): String {
        val lang = getLanguage(player)
        return FormatUtils.color(getLineForLanguage(x, lang!!.lang, pluginName)).removeSurrounding("\"")
    }

    fun getLine(x: String, sender: CommandSender, pluginName: String): String {
        val lang = getLanguage(sender)
        return FormatUtils.color(getLineForLanguage(x, lang!!.lang, pluginName)).removeSurrounding("\"")
    }

    fun getLine(x: String, lang: Language, pluginName: String): String {
        return FormatUtils.color(getLineForLanguage(x, lang.lang, pluginName)).removeSurrounding("\"")
    }

    private fun getLineForLanguage(x: String, lang: String?, pluginName: String): String {
        if (lang != null) {
            val translation = getTranslation(pluginName, lang, x)
            if (translation.isNotEmpty()) {
                return translation
            } else {
                plugin.logger.severe("A $lang nyelv nem tartalmazza a(z) $x sort a(z) $pluginName pluginben!")
            }
        }
        return ""
    }

    private fun getTranslation(pluginName: String, lang: String, key: String): String {
        val jsonContent = File(jsonFilePath).readText()
        val json = Json.decodeFromString<JsonObject>(jsonContent)
        val pluginsObject = json["AvailablePlugins"]?.jsonObject
        val pluginData = pluginsObject?.get(pluginName)?.jsonObject
        val languageData = pluginData?.get(lang)?.jsonObject
        return languageData?.get(key)?.toString() ?: ""
    }

    fun saveLang4Player() {
        val langsToSave = ArrayList<SavedSetting>()
        languageSettings.forEach { (uuid, language) ->
            langsToSave.add(
                SavedSetting(
                    uuid,
                    language
                )
            )
        }
        val boostSavesBuilder = GsonBuilder()
            .create()
        val boostData = boostSavesBuilder.toJson(langsToSave)
        val jsonWriter = FileWriter(File(jsonFilePathSavedSetting))
        jsonWriter.write(boostData)
        jsonWriter.close()
    }

    fun loadLangs4Player() {
        val savesFile = File(jsonFilePathSavedSetting)

        if (savesFile.exists()) {
            val langsToLoad = Gson().fromJson(
                FileReader(savesFile),
                Array<SavedSetting>::class.java
            )
            langsToLoad.forEach {
                val language = availableLangs.find { lang -> lang.lang == it.lang.lang }
                if (language != null) {
                    languageSettings.set(it.uuid, it.lang)
                } else {
                    plugin.logger.warning("Ismeretlen nyelvet találtam a JSON fájlban: ${it.lang.lang}")
                }
            }
            plugin.logger.warning("Sikeresen visszaállítottunk ${langsToLoad.size} beállítást!")
        }
        savesFile.delete()
    }

    fun setLanguage(player: Player, lang: Language) {
        val oldLanguage = getLanguage(player)
        val event = LanguageChangeEvent(player, oldLanguage?.lang ?: "", lang.lang)
        Bukkit.getPluginManager().callEvent(event)
        if (!event.isCancelled) {
            languageSettings[player.uniqueId] = lang
            languageSettingsSender[player] = lang
        }
    }

    fun getLanguage(player: Player): Language? {
        if (languageSettings[player.uniqueId] == null) {
            languageSettings[player.uniqueId] = Language("Magyar")
        }
        return languageSettings[player.uniqueId]
    }

    fun getLanguage(sender: CommandSender): Language? {
        if (languageSettingsSender[sender] == null) {
            languageSettingsSender[sender] = Language("Magyar")
        }
        return languageSettingsSender[sender]
    }

    fun loadLanguages() {
        languageData.clear()
        val jsonContent = File(jsonFilePath).readText()
        val json = Json.decodeFromString<JsonObject>(jsonContent)
        val availablePlugins = json["AvailablePlugins"]?.jsonObject ?: return
        availablePlugins.forEach { (pluginName, pluginData) ->
            val languages = pluginData.jsonObject
            languages.forEach { (lang, langData) ->
                val languagePack = loadLanguageFromJsonObject(langData.jsonObject)
                val language = Language(lang)
                languageData[language] = languagePack
            }
        }
    }

    private fun loadLanguageFromJsonObject(jsonObj: JsonObject): HashMap<String, String> {
        val languagePack = HashMap<String, String>()

        jsonObj.forEach { (key, value) ->
            val translation = value.toString()
            languagePack[key] = translation
        }
        return languagePack
    }

    fun getAvailableLanguages() {
        val jsonContent = File(jsonFilePath).readText()
        val json = Json.decodeFromString<JsonObject>(jsonContent)
        val availableLanguagesArray = json["AvailableLanguages"]?.jsonArray
        val languages = mutableListOf<Language>()
        availableLanguagesArray?.forEach { element ->
            val languageString = element.toString()
            val cleanedLanguage = languageString.substring(1, languageString.length - 1)
            languages.add(Language(cleanedLanguage))
        }
        availableLangs = languages
    }

    fun getPluginNames() {
        val jsonContent = File(jsonFilePath).readText()
        val json = Json.decodeFromString<JsonObject>(jsonContent)
        val pluginsObject = json["AvailablePlugins"]?.jsonObject
        val pluginNames = mutableListOf<String>()
        pluginsObject?.keys?.forEach {
            pluginNames.add(it)
        }
        availablePlugins = pluginNames
    }
}
