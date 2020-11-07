package fr.pokepixel.daem0ns.pokebattleclauses.utils

import info.pixelmon.repack.ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

fun Path.toConfigurationLoader() = HoconConfigurationLoader.builder().setPath(this).build()