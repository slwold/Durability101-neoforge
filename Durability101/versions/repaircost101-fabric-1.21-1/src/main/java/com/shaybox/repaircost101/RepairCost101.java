package com.shaybox.repaircost101;

import net.fabricmc.api.ClientModInitializer;

public class RepairCost101 implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 实际逻辑通过 Mixin（RepairCost101Mixin）注入物品渲染，这里无需额外初始化
    }
}
