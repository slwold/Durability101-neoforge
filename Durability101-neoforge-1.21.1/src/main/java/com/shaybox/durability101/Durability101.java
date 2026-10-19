package com.shaybox.durability101;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import java.text.DecimalFormat;

@Mod(Durability101.MODID)
public class Durability101 {

    public static final String MODID = "durability101";

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
                int unbreaking = getItemEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
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

    /**
     * NeoForge 21.1.x 的 EnchantmentHelper 只接受 Holder&lt;Enchantment&gt;，
     * 而 Enchantments 里的常量是 ResourceKey，需要先经附魔注册表取回 Holder。
     * 取值走 ItemStack#getEnchantmentLevel（Neo 已弃用 EnchantmentHelper#getItemEnchantmentLevel）。
     */
    private static int getItemEnchantmentLevel(ResourceKey<Enchantment> enchantment, ItemStack itemStack) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return 0;

        Holder<Enchantment> holder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(enchantment);

        return itemStack.getEnchantmentLevel(holder);
    }

    public static String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

}