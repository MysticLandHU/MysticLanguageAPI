package hu.mysticland.mysticlanguageapi.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.mysticland.mysticlanguageapi.Language
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI
import hu.mysticland.mysticlanguageapi.MysticLanguageAPI.Companion.plugin
import hu.mysticland.mysticlanguageapi.SavedSetting
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
import kotlin.io.path.name
import kotlin.io.path.pathString

object LanguageAPI {

    var languageData = HashMap<Language, HashMap<String,String>>()
    var languageSettings = HashMap<UUID,Language>()
    var languagesDisplayNamesList = mutableListOf<String>()

    // TODO: Try to add a sign belowName to see the language of the player

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
        languageSettings.set(player.uniqueId,lang)
    }

    fun getLanguage(player: Player): Language? {
        if(languageSettings.get(player.uniqueId) == null){
            languageSettings.set(player.uniqueId,Language("lang_hu"))
        }
        val lang = languageSettings.get(player.uniqueId)
        return lang
    }


    fun getLanguages(): MutableSet<Language> {
        return languageData.keys
    }

    fun loadLanguages(l: String) {
        Files.walk(Paths.get(MysticLanguageAPI.plugin.dataFolder.path+"/"+l))
            .filter { Files.isRegularFile(it) }
            .forEach {
                if (it != null) {
                    if (it.fileName.pathString.contains("lang_")) {
                        languageData.set(Language(it.fileName.name), loadLanguage(it))
                    }
                }
            }
    }

    fun loadLanguage(langFile: Path): HashMap<String, String> {
        var languagePack = HashMap<String,String>()
        val inputStream: InputStream = File(langFile.toUri()).inputStream()
        if (File(langFile.toUri()).name.contains("lang_")) {
            plugin.logger.info("Nyelvi fájl betöltése folyamatban: ${File(langFile.toUri()).name}")
            inputStream.bufferedReader().forEachLine {
                val it2 = it.split("\n")[0]
                var t = it2.split("|:")[0]
                var line = it2.split("|:")[1].removePrefix(" ")
                languagePack[t] = line
            }
        }
        return languagePack
    }
}