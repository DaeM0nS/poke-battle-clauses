package de.randombyte.pokebattleclauses.config

import com.pixelmonmod.pixelmon.battles.attacks.Attack
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase
import com.pixelmonmod.pixelmon.client.gui.GuiResources.types
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender
import com.pixelmonmod.pixelmon.enums.EnumType
import com.pixelmonmod.pixelmon.enums.EnumSpecies
import com.pixelmonmod.pixelmon.items.ItemHeld
import com.pixelmonmod.pixelmon.items.heldItems.NoItem
import de.randombyte.kosp.extensions.orNull
import de.randombyte.pokebattleclauses.PokeBattleClauses
import de.randombyte.pokebattleclauses.config.ListType.BLACK
import de.randombyte.pokebattleclauses.config.ListType.WHITE
import net.minecraft.item.Item
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.registries.IForgeRegistryEntry
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.Sponge
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import net.minecraft.item.Item.getByNameOrId



@ConfigSerializable class ClausesConfig(
        @Setting("clauses") val clauses: Map<String, ClauseConfig> = emptyMap()
) {

    // default config
    constructor() : this(
            clauses = mapOf(
                    "an_example" to ClauseConfig(
                            description = "An example clause",
                            types = BlackWhiteList(
                                    listType = WHITE,
                                    list = listOf("electric", "ground")
                            ),
                            moves = BlackWhiteList(
                                    listType = BLACK,
                                    list = listOf("Tackle")
                            ),
                            abilities = BlackWhiteList(
                                    listType = WHITE,
                                    list = listOf("Overgrow")
                            ),
                            items = BlackWhiteList(
                                    listType = BLACK,
                                    list = listOf("pixelmon:smoke_ball")
                            ),
                            levels = BlackWhiteList(
                                    listType = WHITE,
                                    list = listOf("1-20", "44", "60-63")
                            ),
                            legendary = false,
                            pokemons = BlackWhiteList(
                                    listType = BLACK,
                                    list = listOf("Arceus","Bidoof", "Pikachu", "Floette-az")
                            ),
                            genders = BlackWhiteList(
                                    listType = WHITE,
                            list = listOf("Male", "Female", "None")
                            )
                    )
            )
    )

    fun parseValues() {
        clauses.values.forEach { it.parseValues() }
    }

    @ConfigSerializable class ClauseConfig(
            @Setting("description") val description: String = "",
            @Setting("types") val types: BlackWhiteList<EnumType>? = null,
            @Setting("moves") val moves: BlackWhiteList<Attack>? = null,
            @Setting("abilities") val abilities: BlackWhiteList<Class<out AbilityBase>>? = null,
            @Setting("items") val items: BlackWhiteList<String>? = null,
            @Setting("levels") val levels: BlackWhiteList<IntRange>? = null,
            @Setting("legendary") val legendary: Boolean? = null,
            @Setting("pokemons") val pokemons: BlackWhiteList<String>? = null,
            @Setting("gender") val genders: BlackWhiteList<Gender>? = null
    ) {

        /**
         * Must be called after loading the config
         *
         * @return true if the parsing was successful, false if not
         */
        fun parseValues(): Boolean {
            val logger = PokeBattleClauses.INSTANCE.logger

            types?.parseTypeValues { typeName ->
                val type = EnumType.values().singleOrNull { it.name.equals(typeName, ignoreCase = true) }
                if (type == null) {
                    logger.error("Could not find Pokemon type '$typeName'")
                    return false
                }

                return@parseTypeValues type
            }

            moves?.parseTypeValues { moveName ->
                val attackBase = AttackBase.getAttackBase(moveName).orNull()
                if (attackBase == null) {
                    logger.error("Could not find move '$moveName'!")
                    return false
                }

                return@parseTypeValues Attack(attackBase)
            }

            abilities?.parseTypeValues { abilityName ->
                val ability = AbilityBase.getAbility(abilityName).orNull()
                if (ability == null) {
                    logger.error("Could not find ability '$abilityName'!")
                    return false
                }

                return@parseTypeValues ability::class.java
            }

            items?.parseTypeValues { itemName ->
                try{
                    val item = Item.getByNameOrId(itemName)
                    if (item == null) {
                        logger.error("Could not find '$itemName' item")
                        return false
                    }
                    return@parseTypeValues itemName
                }catch(e: NullPointerException ){
                    return@parseTypeValues "minecraft:dirt"
                }
            }

            levels?.parseTypeValues { levelRangeString ->
                val splits = levelRangeString.split("-", limit = 2)
                val numbers = splits.map {
                    val number = it.toIntOrNull()
                    if (number == null) {
                        logger.error("'$it' is not a number!")
                        return false
                    } else number
                }
                numbers[0]..numbers[if (numbers.size == 2) 1 else 0]
            }
            
            pokemons?.parseTypeValues { pokemonName ->
                if(pokemonName=="Ho-Oh" || pokemonName=="Porygon-Z" || pokemonName=="Jangmo-o" || pokemonName=="Hakamo-o" || pokemonName=="Kommo-o"){
                    val pokemon = EnumSpecies.getNameList().singleOrNull { it==pokemonName }
                    if (pokemon == null) {
                        logger.error("Could not find Pokemon '$pokemonName'")
                        return false
                    }

                    return@parseTypeValues pokemonName
                }else{
                    val pokemon = EnumSpecies.getNameList().singleOrNull { it==pokemonName.split("-")[0] }
                    if (pokemon == null) {
                        logger.error("Could not find Pokemon '$pokemonName'")
                        return false
                    }

                    return@parseTypeValues pokemonName
                }
            }

            genders?.parseTypeValues { genderValue ->
                try{
                    val pokeGender = Gender.getGender(genderValue)
                    if (pokeGender == null) {
                        logger.error("Could not find gender '$genderValue'!")
                        return false
                    }
                    return@parseTypeValues pokeGender
                }catch(e: NullPointerException){
                    return@parseTypeValues Gender.None
                }
            }
            return true
        }
    }
}

enum class ListType { WHITE, BLACK }

@ConfigSerializable class BlackWhiteList<T>(
        @Setting("list-type") val listType: ListType = WHITE,
        @Setting("list") val list: List<String> = emptyList()
) {

    var listValues: List<T>? = null

    // Must be called after loading the config
    inline fun parseTypeValues(parser: (String) -> T) {
        listValues = list.map(parser)
    }

    fun ensureInitialization(): Boolean {
        if (listValues == null) {
            PokeBattleClauses.INSTANCE.logger.error("The clause values were not initialized, check for previous errors!")
            return false
        }
        return true
    }

    fun isAllowed(obj: T): Boolean {
        if (!ensureInitialization()) return false
        return when (listType) {
            WHITE -> obj in listValues!!
            BLACK -> obj !in listValues!!
        }
    }
}