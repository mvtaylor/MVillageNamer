package gay.viktoria.mvillagenamer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class VNamesCommand {

    private final VillageNamerPlugin vnp;

    public VNamesCommand(VillageNamerPlugin vnp) {
        this.vnp = vnp;
    }

    @SuppressWarnings("null")
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("vnames")
            .then(Commands.literal("add")
                .then(Commands.argument("names", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        final String namesString = ctx.getArgument("names", String.class);

                        addName(namesString);

                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .then(Commands.literal("list")
                .executes(ctx -> {
                    listNames(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(ctx -> {
                        final String name = ctx.getArgument("name", String.class);

                        removeName(name);

                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .then(Commands.literal("save")
                .executes(ctx -> {
                    this.vnp.saveConfig();

                    ctx.getSource().getSender().sendMessage("Saving current name configuration to disk...");

                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("reload")
                .executes(ctx -> {
                    this.vnp.reloadConfig();

                    ctx.getSource().getSender().sendMessage("Reloading names from config...");

                    return Command.SINGLE_SUCCESS;
                })
            )
        .build();
    }

    public void addName(String namesString) {
        this.vnp.addName(namesString); // temporarily we don't care about variants, including whole string
    }

    public void removeName(String name) {

    }

    private void listNames(CommandSourceStack sourceStack) {
        CircularList<ArrayList<String>> names = this.vnp.getConfigNames();

        List<String> nameStrings = new ArrayList<>();

        for (ArrayList<String> nameVars : names) {
            final String nameColl;
            if (nameVars.size() == 1) {
                nameColl = nameVars.get(0);
            } else {
                nameColl = nameVars.toString();
            }
            nameStrings.add(nameColl);
        }

        String _msg = String.join("\n", nameStrings);
        
        sourceStack.getSender().sendMessage(_msg);
    }
}
