package net.auronnetwork.durabilitypeek;

import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

final class DurabilityPeek {
    static final String TRANSLATION_KEY = "durability_peek.durability";

    private DurabilityPeek() {
    }

    static void append(ItemStack stack, List<Component> lines) {
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }

        int maximum = stack.getMaxDamage();
        if (maximum <= 0) {
            return;
        }

        int remaining = Math.clamp((long) maximum - stack.getDamageValue(), 0, maximum);
        double percentage = remaining * 100.0 / maximum;
        ChatFormatting color = percentage >= 50.0 ? ChatFormatting.GREEN
                : percentage >= 25.0 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        Component durability = Component.translatable(TRANSLATION_KEY,
                remaining, maximum, String.format(Locale.ROOT, "%.1f", percentage))
                .withStyle(color);

        // Replace only the vanilla durability line; preserve lore and other mods' text.
        for (int index = 0; index < lines.size(); index++) {
            if (lines.get(index).getContents() instanceof TranslatableContents contents
                    && ("item.durability".equals(contents.getKey())
                    || TRANSLATION_KEY.equals(contents.getKey()))) {
                lines.set(index, durability);
                return;
            }
        }
        lines.add(durability);
    }
}
