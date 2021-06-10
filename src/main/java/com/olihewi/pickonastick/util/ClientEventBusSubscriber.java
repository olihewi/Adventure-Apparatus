package com.olihewi.pickonastick.util;

import com.olihewi.pickonastick.PickOnAStick;
import com.olihewi.pickonastick.client.render.PickOnAStickRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PickOnAStick.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
  private static final Minecraft mc = Minecraft.getInstance();
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event)
  {
    RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.PICK_ON_A_STICK_ENTITY.get(), PickOnAStickRenderer::new);
  }
}
