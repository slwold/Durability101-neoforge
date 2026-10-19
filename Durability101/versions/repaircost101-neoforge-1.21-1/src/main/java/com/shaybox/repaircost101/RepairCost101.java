package com.shaybox.repaircost101;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import java.text.DecimalFormat;

@Mod(RepairCost101.MODID)
public class RepairCost101 {

    public static final String MODID = "repaircost101";

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
            RepairCost101.RepairCostDecorator repairCost101 = new RepairCost101.RepairCostDecorator();
            for (Item item : BuiltInRegistries.ITEM) {
                event.register(item, repairCost101);
            }
        }

    }

    private static class RepairCostDecorator implements IItemDecorator {

        @Override
        public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int xPosition, int yPosition) {
            // 仅在物品存在且带有铁砧修复代价（>0）时绘制
            int repairCost = itemStack.getBaseRepairCost();
            if (!itemStack.isEmpty() && repairCost > 0) {
                PoseStack poseStack = guiGraphics.pose();

                String string = format(repairCost);
                int stringWidth = font.width(string);
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;
                // 代价达到 40（MC “过于昂贵”阈值）时用红色提示，否则沿用物品条颜色
                int color = repairCost >= 40 ? 0xFF5555 : itemStack.getItem().getBarColor(itemStack);

                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0F, 0.0F, 301.0F);

                guiGraphics.drawString(font, string, x, y, color, true);

                poseStack.popPose();
            }

            return true;
        }

    }

    public static String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

}
