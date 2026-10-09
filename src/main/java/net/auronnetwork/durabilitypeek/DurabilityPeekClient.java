package net.auronnetwork.durabilitypeek;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public final class DurabilityPeekClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) ->
                DurabilityPeek.append(stack, lines));
    }
}
