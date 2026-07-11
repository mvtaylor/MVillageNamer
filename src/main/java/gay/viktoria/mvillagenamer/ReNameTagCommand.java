package gay.viktoria.mvillagenamer;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ReNameTagCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("VillageNamer: command sender must be player");
            return true;
        }

        Player player = (Player) sender;

        ItemStack renametag = new ItemStack(Material.NAME_TAG, 1);

        ItemMeta newmeta = renametag.getItemMeta();
        if (newmeta != null) {
            newmeta.displayName(Component.text("RENAMEME"));
            renametag.setItemMeta(newmeta);
        }
        
        player.give(renametag);

        String msg = "<pride:trans>[VillageNamer]</pride> Gave 1 ReNameTag to %s.".formatted(player.getName());

        MiniMessage mm = MiniMessage.miniMessage();
        Component messageComp = mm.deserialize(msg);

        sender.sendMessage(messageComp);
        return true;
    }
}
