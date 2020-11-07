package fr.pokepixel.daem0ns.pokebattleclauses

import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClauseRegistry
import fr.pokepixel.daem0ns.pokebattleclauses.config.ConfigAccessor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(modid= PokeBattleClauses.ID,
        name= PokeBattleClauses.NAME,
        version= PokeBattleClauses.VERSION,
        acceptableRemoteVersions="*",
        acceptedMinecraftVersions="[1.12.2]",
        dependencies = "required-after:pixelmon",
        serverSideOnly = true)
class PokeBattleClauses{
    internal companion object {
        lateinit var logger: Logger
        private val file: File = File("./config/poke-battle-clauses")
        val configAccessors: ConfigAccessor = ConfigAccessor(file.toPath())
        const val ID = "pokebattleclauses"
        const val NAME = "Poke Battle Clauses"
        const val VERSION = "2.0.1"
        val instance: PokeBattleClauses
            get() = PokeBattleClauses()
    }

    private val registry: BattleClauseRegistry<BattleClause>
        get() = BattleClauseRegistry.getClauseRegistry()

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @Mod.EventHandler
    fun onPostInit(event: FMLPostInitializationEvent) {
        configAccessors.reloadAll()
        registerClauses()

        logger.info("$NAME loaded: $VERSION")
    }

    @Mod.EventHandler
    fun onServerStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(ReloadCommand())
    }

    fun unregisterOurClauses() {
        val ourClauseIds = configAccessors.clauses.get().clauses.keys
        val notOurClauses = registry.customClauses.filterNot { it.id in ourClauseIds }
        registry.replaceCustomClauses(notOurClauses)
    }

    fun registerClauses() {
        val clausesConfig = configAccessors.clauses.get().clauses

        val alreadyUsedIds = clausesConfig.keys.filter { id -> registry.hasClause(id) }
        if (alreadyUsedIds.isNotEmpty()) {
            logger.error("The following clause ID(s) are already in use: ${alreadyUsedIds.joinToString()}")
            return
        }
        clausesConfig.forEach { (id, clauseConfig) ->
            registry.registerCustomClause(VariableClause(id, clauseConfig))
        }
    }
}