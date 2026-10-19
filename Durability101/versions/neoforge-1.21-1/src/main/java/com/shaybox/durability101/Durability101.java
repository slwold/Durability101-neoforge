package com.shaybox.durability101;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import java.text.DecimalFormat;

@Mod(Durability101.MODID)
public class Durability101 {

    public static final String MODID = "durability101";

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
            Durability101.DurabilityDecorator durability101 = new Durability101.DurabilityDecorator();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item.getDefaultInstance().isDamageableItem()) {
                    event.register(item, durability101);
                }
            }
        }

    }

    private static class DurabilityDecorator implements IItemDecorator {

        @Override
        public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int xPosition, int yPosition) {
            if (!itemStack.isEmpty() && itemStack.isDamaged()) {
                PoseStack poseStack = guiGraphics.pose();

                // 耐久度 = (最大耐久 - 当前损伤) * (不毁等级 + 1)
                int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
                int maxDamage = itemStack.getMaxDamage();
                int damage = itemStack.getDamageValue();

                // 构造显示字符串、位置与颜色
                String string = format(((maxDamage - damage) * (unbreaking + 1)));
                int stringWidth = font.width(string);
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;
                int color = itemStack.getItem().getBarColor(itemStack);

                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0F, 0.0F, 301.0F);

                // 绘制字符串
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
