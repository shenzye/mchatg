//package com.github.mchatg.config
//
//import org.bukkit.configuration.file.FileConfiguration
//
////便于debug
//interface ConfigAccessor {
//    fun get(path: String, def: Any): Any
//    fun set(path: String, value: Any?)
////    fun getStringList(path: String): List<String>
//}
//
//class ConfigAccessorWrapper(val config: FileConfiguration) : ConfigAccessor {
//    override fun set(path: String, value: Any?) {
//        config.set(path, value)
//    }
//
//    override fun get(path: String, def: Any): Any {
//        return config.get(path, def)!!
//    }
//
////    override fun getStringList(path: String): List<String> {
////        return config.getStringList(path)
////    }
//}
//
//
////class ConfigAccessor(private val plugin: JavaPlugin, private val fileName: String? =null) {
////    private val configFile: File by lazy{File(plugin.dataFolder, fileName)}
////    var config: FileConfiguration = loadConfig()
////
////
////    private fun loadConfig(): FileConfiguration {
////        return
////
////
////
////    }
////
////    fun reloadConfig() {
////        if(fileName!=null){
////            YamlConfiguration.loadConfiguration(configFile).apply {
////                // Look for defaults in the jar
////                plugin.getResource(fileName)?.let {
////                    defaults == YamlConfiguration.loadConfiguration(InputStreamReader(it))
////                }
////            }
////        }
////
////
////
////        config = loadConfig()
////    }
////
////    fun saveConfig() {
////        try {
////            config.save(configFile)
////        } catch (ex: IOException) {
////            plugin.logger.log(Level.SEVERE, "Could not save config to $configFile", ex)
////        }
////
////    }
////
////    fun saveDefaultConfig() {
////        if (!configFile.exists()) {
////            plugin.saveResource(fileName, false)
////        }
////    }
////}