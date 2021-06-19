package com.olihewi.adventureapparatus.util;

import com.olihewi.adventureapparatus.AdventureApparatus;
import com.olihewi.adventureapparatus.client.render.ThrownPickRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AdventureApparatus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
  private static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event)
  {
    RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.PICK_ON_A_STICK_ENTITY.get(), ThrownPickRenderer::new);
  }
}
