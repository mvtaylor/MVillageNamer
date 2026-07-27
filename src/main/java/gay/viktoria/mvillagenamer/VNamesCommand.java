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

                        addName(ctx.getSource(), namesString);

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

                        removeName(ctx.getSource(), name);

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

    public void addName(CommandSourceStack sourceStack, String namesString) {
        String[] names = namesString.split("\\s+");

        assert names.length > 0;

        String _msg = "Added name: ";

        if (names.length == 1) {
            this.vnp.addName(names[0]);
            _msg += names[0];
        }
        else {
            List<String> nameList = List.of(names);
            this.vnp.addName(nameList);
            _msg += nameList.toString();
        }

        sourceStack.getSender().sendMessage(_msg);
    }

    public void removeName(CommandSourceStack sourceStack, String name) {
        this.vnp.removeName(name);
        sourceStack.getSender().sendMessage("Removed all instances of name '%s' from name list.".formatted(name));
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
