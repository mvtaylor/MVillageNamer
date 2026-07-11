package gay.viktoria.mvillagenamer;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;


public class NameChunkCommand implements CommandExecutor {

    private final VillageNamerPlugin VNP;

    public NameChunkCommand(VillageNamerPlugin vnp) {
        VNP = vnp;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("VillageNamer: command sender must be player");
            return true;
        } else {
            Player player = (Player) sender;
            Chunk execChunk = player.getChunk();
            this.VNP.nameChunk(execChunk);
            sender.sendMessage("VillageNamer: naming villagers in chunk...");
            return true;
        }
    }
}
