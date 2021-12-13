package me.hqics.mods.mixin;

import me.hqics.mods.ContainerMenuDupeFixMod;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow ServerPlayerEntity player;
    @Inject(at = @At("HEAD"), method = "onClickSlot")
    public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo info) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler)(Object)this, player.getWorld());

        if (!player.world.isClient && player.currentScreenHandler != null && !player.currentScreenHandler.canUse(player)) {
            player.closeHandledScreen();
            player.currentScreenHandler = player.playerScreenHandler;

            ContainerMenuDupeFixMod.LOGGER.info(player.getName().asString() + " (UUID: " + player.getUuidAsString() + ") could have attempted to duplicate items.");
        }
    }
}
