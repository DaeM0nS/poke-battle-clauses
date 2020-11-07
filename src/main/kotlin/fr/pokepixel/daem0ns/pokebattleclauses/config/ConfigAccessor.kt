package fr.pokepixel.daem0ns.pokebattleclauses.config

import fr.pokepixel.daem0ns.pokebattleclauses.utils.config.ConfigAccessor
import java.nio.file.Path

class ConfigAccessor(configPath: Path) : ConfigAccessor(configPath) {

    val general = getConfigHolder<GeneralConfig>("general.conf")
    val clauses = getConfigHolder<ClausesConfig>("clauses.conf")

    override val holders = listOf(general, clauses)

    override fun reloadedAll() {
        clauses.get().parseValues()
    }
}