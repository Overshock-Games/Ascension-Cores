package com.ascensioncores.command;

import com.ascensioncores.AscensionCommonMod;
import com.ascensioncores.AscensionCoresConfig;
import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.RolledStat;
import com.ascensioncores.gear.StatPool;
import com.ascensioncores.item.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class AscensionCoresCommand {

    private static final SimpleCommandExceptionType ERROR_NOT_GEAR =
        new SimpleCommandExceptionType(Component.literal("Hold a weapon, armor piece, or tool in your main hand."));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ascensioncores")
            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            .then(Commands.literal("reload")
                .executes(context -> reload(context.getSource())))
            .then(Commands.literal("level")
                .then(Commands.literal("get")
                    .executes(context -> getLevel(context.getSource())))
                .then(Commands.literal("set")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0))
                        .executes(context -> setLevel(
                            context.getSource(),
                            IntegerArgumentType.getInteger(context, "level"))))))
            .then(Commands.literal("info")
                .executes(context -> showInfo(context.getSource())))
            .then(Commands.literal("reroll")
                .executes(context -> reroll(context.getSource())))
            .then(Commands.literal("trait")
                .then(Commands.literal("set")
                    .then(Commands.argument("id", StringArgumentType.word())
                        .executes(context -> forceTrait(
                            context.getSource(),
                            StringArgumentType.getString(context, "id"),
                            -1.0))
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0))
                            .executes(context -> forceTrait(
                                context.getSource(),
                                StringArgumentType.getString(context, "id"),
                                DoubleArgumentType.getDouble(context, "amount")))))))
            .then(Commands.literal("givecore")
                .then(Commands.literal("upgrade")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                        .executes(context -> giveCore(
                            context.getSource(),
                            new ItemStack(ModItems.ASCENSION_CORE, IntegerArgumentType.getInteger(context, "count"))))))
                .then(Commands.literal("chaos")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                        .executes(context -> giveCore(
                            context.getSource(),
                            new ItemStack(ModItems.CHAOS_CORE, IntegerArgumentType.getInteger(context, "count"))))))));
    }

    private static int reload(CommandSourceStack source) {
        AscensionCoresConfig.reload(AscensionCommonMod.LOGGER);
        source.sendSuccess(() -> Component.literal("Ascension Cores config reloaded."), true);
        return 1;
    }

    private static int getLevel(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack stack = selectedGear(source.getPlayerOrException());
        source.sendSuccess(() -> Component.literal("Held item level: " + GearHelper.getLevel(stack)), false);
        return GearHelper.getLevel(stack);
    }

    private static int setLevel(CommandSourceStack source, int level) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = selectedGear(player);
        GearHelper.setLevel(stack, level);
        player.containerMenu.broadcastChanges();
        source.sendSuccess(() -> Component.literal("Set held item level to " + GearHelper.getLevel(stack) + "."), true);
        return GearHelper.getLevel(stack);
    }

    private static int showInfo(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = selectedGear(player);

        List<StatPool.StatDef> pool = GearHelper.getPool(stack);
        List<RolledStat> stats = GearHelper.getRolledStats(stack);

        source.sendSuccess(() -> Component.literal("=== Ascension Cores Item Info ===").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);

        for (StatPool.StatDef def : pool) {
            double currentAmount = 0.0;
            boolean hasStat = false;
            for (RolledStat rolled : stats) {
                if (rolled.id().equals(def.id())) {
                    currentAmount = rolled.amount();
                    hasStat = true;
                    break;
                }
            }

            String range = StatPool.formatValue(def, def.minAmount()) + " to " + StatPool.formatValue(def, def.maxAmount());
            String line = def.id() + " | " + def.displayName() + " | " + range;

            if (hasStat) {
                line += " | Current base roll: " + StatPool.formatValue(def, currentAmount);
            }
            final String finalText = line;
            final boolean finalHasStat = hasStat;
            source.sendSuccess(() -> Component.literal(finalText).withStyle(finalHasStat ? ChatFormatting.GREEN : ChatFormatting.GRAY), false);
        }
        return 1;
    }

    private static int reroll(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = selectedGear(player);
        if (GearHelper.getLevel(stack) <= 0) {
            source.sendFailure(Component.literal("Held item must be level 1 or higher."));
            return 0;
        }
        GearHelper.rerollDeterministic(stack, GearHelper.getRolledStats(stack).size());
        player.containerMenu.broadcastChanges();
        source.sendSuccess(() -> Component.literal("Rerolled held item traits."), true);
        return 1;
    }

    private static int giveCore(CommandSourceStack source, ItemStack stack) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int count = stack.getCount();
        String name = stack.getHoverName().getString();
        boolean inserted = player.getInventory().add(stack);
        if (!inserted) {
            player.drop(stack, false);
        }
        source.sendSuccess(() -> Component.literal("Gave " + count + " " + name + "."), true);
        return count;
    }

    private static int forceTrait(CommandSourceStack source, String id, double amount) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = selectedGear(player);

        StatPool.StatDef def = StatPool.getById(id);
        if (def == null) {
            // also search ranged pool since getById only checks weapon/armor/tool
            def = GearHelper.getPool(stack).stream()
                .filter(d -> d.id().equals(id)).findFirst().orElse(null);
        }
        if (def == null) {
            source.sendFailure(Component.literal("Unknown trait id: " + id));
            return 0;
        }

        double baseAmount = amount > 0 ? amount : (def.minAmount() + def.maxAmount()) / 2.0;
        baseAmount = Math.round(baseAmount * 100.0) / 100.0;

        List<RolledStat> stats = new java.util.ArrayList<>(GearHelper.getRolledStats(stack));
        stats.removeIf(s -> s.id().equals(id)); // remove existing if any
        stats.add(new RolledStat(id, baseAmount));

        int level = GearHelper.getLevel(stack);
        if (level == 0) {
            GearHelper.setLevel(stack, 1);
            level = 1;
        }

        stack.set(com.ascensioncores.component.ModComponents.ROLLED_STATS, stats);
        GearHelper.rebuildAttributes(stack, level, stats);
        player.containerMenu.broadcastChanges();

        final String msg = "Set trait " + def.displayName() + " (base=" + baseAmount + ") on held item.";
        source.sendSuccess(() -> Component.literal(msg).withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    private static ItemStack selectedGear(ServerPlayer player) throws CommandSyntaxException {
        ItemStack stack = player.getInventory().getSelectedItem();
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            throw ERROR_NOT_GEAR.create();
        }
        return stack;
    }
}
