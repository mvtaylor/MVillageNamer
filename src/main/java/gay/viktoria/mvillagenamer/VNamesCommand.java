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
                .then(Commands.argument("names", new StringListArgument())
                    .executes(ctx -> {
                        @SuppressWarnings("unchecked")
                        List<String> nameVars = ctx.getArgument("names", List.class);

                        addName(ctx.getSource(), nameVars);

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
                    ctx.getSource().getSender().sendMessage("Reloading names from config...");

                    this.vnp.reloadConfig();

                    int lNsuccess = this.vnp.loadNames();
                    switch (lNsuccess) {
                        case 0: // success
                            this.vnp.getLogger().info("Names successfully loaded from config");
                            break;
                        case 1: // failure
                            this.vnp.getLogger().severe("Failed to load names from config!");
                        default:
                            break;
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
        .build();
    }

    private void addName(CommandSourceStack sourceStack, List<String> nameVariants) {
        assert nameVariants.size() > 0;

        String _msg = "Added name: ";

        if (nameVariants.size() == 1) {
            this.vnp.addName(nameVariants.get(0));
            _msg += nameVariants.get(0);
        }
        else {
            List<String> nameList = List.copyOf(nameVariants);
            this.vnp.addName(nameList);
            _msg += nameList.toString();
        }

        sourceStack.getSender().sendMessage(_msg);
    }

    private void removeName(CommandSourceStack sourceStack, String name) {
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
                nameColl = String.join("/", nameVars);
            }
            nameStrings.add(nameColl);
        }

        String _msg = String.join("\n", nameStrings);
        
        sourceStack.getSender().sendMessage(_msg);
    }
}
