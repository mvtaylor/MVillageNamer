package gay.viktoria.mvillagenamer;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

@SuppressWarnings("null")
public class StringListArgument implements CustomArgumentType<List<String>, String> {

    @Override
    public @NonNull List<String> parse(StringReader reader) throws CommandSyntaxException {
        List<String> arguments = new ArrayList<>();

        while (reader.canRead()) {
            reader.skipWhitespace();

            if (!reader.canRead()) {
                break;
            }

            arguments.add(reader.readString());
        }

        return List.copyOf(arguments);
    }

    @Override
    public @NonNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }
    
}
