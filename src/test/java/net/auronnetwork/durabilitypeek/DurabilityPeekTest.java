package net.auronnetwork.durabilitypeek;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.util.Unit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurabilityPeekTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(VanillaRegistries.createWorldLookup())
                .forEach(pending -> pending.apply());
    }

    @Test
    void undamagedToolShowsFullDurability() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        List<Component> lines = new ArrayList<>();
        DurabilityPeek.append(stack, lines);
        assertLine(lines.getFirst(), stack.getMaxDamage(), stack.getMaxDamage(), "100.0", ChatFormatting.GREEN);
    }

    @Test
    void damageAndRepairAreReadOnEveryTooltip() {
        ItemStack stack = new ItemStack(Items.IRON_SWORD);
        stack.setDamageValue(230);
        List<Component> lines = new ArrayList<>();
        DurabilityPeek.append(stack, lines);
        assertLine(lines.getFirst(), 20, 250, "8.0", ChatFormatting.RED);

        stack.setDamageValue(0);
        DurabilityPeek.append(stack, lines);
        assertEquals(1, lines.size());
        assertLine(lines.getFirst(), 250, 250, "100.0", ChatFormatting.GREEN);
    }

    @Test
    void advancedTooltipIsReplacedWithoutChangingLore() {
        ItemStack stack = new ItemStack(Items.IRON_CHESTPLATE);
        stack.setDamageValue(144);
        Component name = Component.literal("Custom name");
        Component lore = Component.literal("Durability: decorative lore");
        Component itemId = Component.literal("minecraft:iron_chestplate");
        List<Component> lines = new ArrayList<>(List.of(name, lore,
                Component.translatable("item.durability", 96, 240), itemId));

        DurabilityPeek.append(stack, lines);
        assertEquals(4, lines.size());
        assertSame(name, lines.get(0));
        assertSame(lore, lines.get(1));
        assertSame(itemId, lines.get(3));
        assertLine(lines.get(2), 96, 240, "40.0", ChatFormatting.YELLOW);
    }

    @Test
    void emptyNonDamageableAndUnbreakableItemsAreSkipped() {
        ItemStack unbreakable = new ItemStack(Items.DIAMOND_PICKAXE);
        unbreakable.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        for (ItemStack stack : List.of(ItemStack.EMPTY, new ItemStack(Items.STONE), unbreakable)) {
            List<Component> lines = new ArrayList<>();
            DurabilityPeek.append(stack, lines);
            assertTrue(lines.isEmpty());
        }
    }

    @Test
    void customDurabilityComponentsAreUsed() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        stack.set(DataComponents.MAX_DAMAGE, 1000);
        stack.setDamageValue(750);
        List<Component> lines = new ArrayList<>();
        DurabilityPeek.append(stack, lines);
        assertLine(lines.getFirst(), 250, 1000, "25.0", ChatFormatting.YELLOW);
    }

    @Test
    void realVanillaTooltipsHaveOneDurabilityLineWithAndWithoutF3H() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        stack.setDamageValue(1000);
        for (TooltipFlag flag : List.of(TooltipFlag.NORMAL, TooltipFlag.ADVANCED)) {
            List<Component> lines = stack.getTooltipLines(Item.TooltipContext.EMPTY, null, flag);
            DurabilityPeek.append(stack, lines);
            long durabilityLines = lines.stream()
                    .filter(line -> line.getContents() instanceof TranslatableContents contents
                            && ("item.durability".equals(contents.getKey())
                            || DurabilityPeek.TRANSLATION_KEY.equals(contents.getKey())))
                    .count();
            assertEquals(1, durabilityLines);
            Component durability = lines.stream()
                    .filter(line -> line.getContents() instanceof TranslatableContents contents
                            && DurabilityPeek.TRANSLATION_KEY.equals(contents.getKey()))
                    .findFirst().orElseThrow();
            assertLine(durability, 561, 1561, "35.9", ChatFormatting.YELLOW);
        }
    }

    private static void assertLine(Component line, int remaining, int maximum,
                                   String percentage, ChatFormatting color) {
        TranslatableContents contents = assertInstanceOf(TranslatableContents.class, line.getContents());
        assertEquals(DurabilityPeek.TRANSLATION_KEY, contents.getKey());
        assertArrayEquals(new Object[]{remaining, maximum, percentage}, contents.getArgs());
        assertEquals(TextColor.fromLegacyFormat(color), line.getStyle().getColor());
    }
}
