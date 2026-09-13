package com.fishingrodkey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = FishingRodKeyForge.MODID, version = FishingRodKeyForge.VERSION, clientSideOnly = true)
public class FishingRodKeyForge {

    public static final String MODID = "fishingrodkey";
    public static final String VERSION = "1.0.0";

    private KeyBinding fishingRodKey;
    private boolean wasPressed = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        fishingRodKey = new KeyBinding(
                "key.fishingrodkey.use",
                Keyboard.KEY_Z,
                "key.categories.fishingrodkey"
        );
        ClientRegistry.registerKeyBinding(fishingRodKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        boolean isDown = fishingRodKey.isKeyDown();

        if (isDown && !wasPressed) {
            onKeyPress(mc);
        } else if (!isDown && wasPressed) {
            onKeyRelease(mc);
        }
        wasPressed = isDown;
    }

    private void onKeyPress(Minecraft mc) {
        int rodSlot = findFishingRodSlot(mc);
        if (rodSlot == -1) return;

        if (mc.thePlayer.inventory.currentItem != rodSlot) {
            mc.thePlayer.inventory.currentItem = rodSlot;
            mc.playerController.updateController();
        }

        ItemStack stack = mc.thePlayer.inventory.getCurrentItem();
        if (stack != null && stack.getItem() instanceof ItemFishingRod) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack);
            mc.thePlayer.swingItem();
        }
    }

    private void onKeyRelease(Minecraft mc) {
        if (mc.thePlayer.inventory.currentItem == 2) return;
        mc.thePlayer.inventory.currentItem = 2;
        mc.playerController.updateController();
    }

    private int findFishingRodSlot(Minecraft mc) {
        InventoryPlayer inv = mc.thePlayer.inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemFishingRod) {
                return i;
            }
        }
        return -1;
    }
}
