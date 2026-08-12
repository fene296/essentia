package de.fene296.essentia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.fene296.essentia.block.entity.EssenceBurnerEntity;
import de.fene296.essentia.util.EssenceType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public class EssentiaCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("essentia")
                .then(Commands.literal("checkburner")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(EssenceType.values()).map(type -> type.name().toLowerCase()),
                                        builder
                                ))
                                .executes(EssentiaCommands::checkBurner))));
    }

    private static int checkBurner(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
        String typeName = StringArgumentType.getString(context, "type");

        EssenceType type;
        try {
            type = EssenceType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            context.getSource().sendFailure(Component.literal(
                    "Unknown essence type '" + typeName + "'. Valid: earth, soul, light, shadow, arcane, time, aether, chaos"));
            return 0;
        }

        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("Only players can run this command."));
            return 0;
        }

        java.util.Optional<net.minecraft.core.BlockPos> foundAt =
                EssenceBurnerEntity.findActiveNearby(player.level(), player.blockPosition(), type);

        if (foundAt.isPresent()) {
            net.minecraft.core.BlockPos pos = foundAt.get();
            context.getSource().sendSuccess(() -> Component.literal(
                    type + "-Essence Burner Active  at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
            ), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal(
                    "No active " + type + "-Essence Burner"
            ), false);
        }

        return 1;
    }
}
