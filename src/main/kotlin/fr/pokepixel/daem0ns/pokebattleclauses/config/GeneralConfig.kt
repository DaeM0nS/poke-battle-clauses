package fr.pokepixel.daem0ns.pokebattleclauses.config

import info.pixelmon.repack.ninja.leaping.configurate.objectmapping.Setting
import info.pixelmon.repack.ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class GeneralConfig(
        @Setting("debug", comment = "Activate to get more info about how your clauses' black/whitelists work") val debug: Boolean = false)