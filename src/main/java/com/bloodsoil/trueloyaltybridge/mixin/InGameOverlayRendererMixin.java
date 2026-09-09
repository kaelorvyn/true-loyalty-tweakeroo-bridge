package com.bloodsoil.trueloyaltybridge.mixin;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "setFloatingItem", at = @At("HEAD"))
    private void trueloyalty$refillViaTweakeroo(ItemStack stack, Random random, CallbackInfo ci) {
        if (!FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || !hasTrueLoyalty(stack)) {
            return;
        }
        Hand hand = findFloatingCloneHand(player);
        if (hand == null) {
            return;
        }
        ItemStack cleaned = player.getStackInHand(hand).copy();
        cleaned.remove(DataComponentTypes.DEATH_PROTECTION);
        player.setStackInHand(hand, cleaned);
        PlacementTweaks.cacheStackInHand(hand);
        player.setStackInHand(hand, ItemStack.EMPTY);
        PlacementTweaks.onProcessRightClickPost(player, hand);
    }

    private static Hand findFloatingCloneHand(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() == Items.TRIDENT
                    && stack.contains(DataComponentTypes.DEATH_PROTECTION)
                    && hasTrueLoyalty(stack)) {
                return hand;
            }
        }
        return null;
    }

    private static boolean hasTrueLoyalty(ItemStack stack) {
        if (stack.getItem() != Items.TRIDENT) {
            return false;
        }
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments == null) {
            return false;
        }
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.getKey().isPresent()
                    && entry.getKey().get().getValue().toString().equals("bloodsoil:true_loyalty")) {
                return true;
            }
        }
        return false;
    }
}
