package com.shaybox.repaircost101;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(DrawContext.class)
public abstract class RepairCost101Mixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private MatrixStack matrices;

    @Shadow
    public abstract int drawText(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow);

    @Inject(at = @At("RETURN"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack itemStack, int x, int y, String countOverride, CallbackInfo ci) {
        renderRepairCost101(textRenderer, itemStack, x, y);
    }

    @Unique
    public void renderRepairCost101(TextRenderer renderer, ItemStack itemStack, int xPosition, int yPosition) {
        if (this.client.world == null || itemStack.isEmpty()) return;

        int repairCost = itemStack.getBaseRepairCost();
        if (repairCost <= 0) return;

        String string = format(repairCost);
        int stringWidth = renderer.getWidth(string);
        int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
        int y = (yPosition * 2) + 18;
        // 代价达到 40（MC “过于昂贵”阈值）用红色，否则沿用物品条颜色
        int color = repairCost >= 40 ? 0xFF5555 : itemStack.getItem().getItemBarColor(itemStack);

        this.matrices.push();
        this.matrices.scale(0.5F, 0.5F, 0.5F);
        this.matrices.translate(0.0F, 0.0F, 301.0F);
        this.drawText(renderer, string, x, y, color, true);
        this.matrices.pop();
    }

    @Unique
    public String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }
}
