package gay.viktoria.mvillagenamer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class VNamesCommand {
    
    public static String cmdName = "vnames";

    private final VillageNamerPlugin vnp;

    public VNamesCommand(VillageNamerPlugin vnp) {
        this.vnp = vnp;
    }

    @SuppressWarnings("null")
    public LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
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

                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("reload")
                .executes(ctx -> {
                    this.vnp.reloadConfig();

                    return Command.SINGLE_SUCCESS;
                })
            )
        .build();
    }

    public void addName(String namesString) {

    }

    public void removeName(String name) {

    }

    private void listNames(CommandSourceStack sourceStack) {
        
    }
}
