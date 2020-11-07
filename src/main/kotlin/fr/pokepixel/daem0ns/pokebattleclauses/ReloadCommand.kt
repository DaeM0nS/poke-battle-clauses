package fr.pokepixel.daem0ns.pokebattleclauses

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

class ReloadCommand() : CommandBase() {

    override fun getName() : String {
        return "pbcreload";
    }

    override fun getUsage(icommandsender : ICommandSender) : String {
        return "/pbcreload";
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if(!sender.canUseCommand(0, "pokebattleclauses.command.pbcreload")) {
            sender.sendMessage(TextComponentString("You don't have the permission to use this command."))
            return
        }
        PokeBattleClauses.instance.unregisterOurClauses()
        PokeBattleClauses.configAccessors.reloadAll()
        PokeBattleClauses.instance.registerClauses()
        sender.sendMessage(TextComponentString("PokeBattleClauses reloaded!"))
        PokeBattleClauses.logger.info("PokeBattleClauses reloaded!")
    }
}