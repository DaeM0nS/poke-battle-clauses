package fr.pokepixel.daem0ns.pokebattleclauses.utils.config

import fr.pokepixel.daem0ns.pokebattleclauses.utils.toConfigurationLoader
import java.nio.file.Files
import java.nio.file.Path

/**
 * To be overwritten to have fields for various [ConfigHolder]s.
 */
abstract class ConfigAccessor(val configPath: Path) {

    init {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath)
        }
    }

    abstract val holders: List<ConfigHolder<*>>

    fun reloadAll() {
        holders.forEach(ConfigHolder<*>::reload)
        reloadedAll()
    }

    /**
     * Called when all configs were reloaded.
     */
    open fun reloadedAll() { }

    protected inline fun <reified T : Any> getConfigHolder(configName: String): ConfigHolder<T> {
        return ConfigManager(configPath.resolve(configName).toConfigurationLoader(), T::class.java).toConfigHolder()
    }
}