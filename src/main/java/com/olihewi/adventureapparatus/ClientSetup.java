package com.olihewi.adventureapparatus;

import com.olihewi.adventureapparatus.client.render.ThrownPickRenderer;
import com.olihewi.adventureapparatus.items.PickOnAStickItem;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup
{
  public static void clientSetup(final FMLClientSetupEvent event)
  {
    AdventureApparatus.shuttleJumpKeybind = new KeyBinding("key.adventureapparatus.shuttlejump", 32, "key.categories.movement");
    ClientRegistry.registerKeyBinding(AdventureApparatus.shuttleJumpKeybind);
    RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.PICK_ON_A_STICK_ENTITY.get(), ThrownPickRenderer::new);
    event.enqueueWork(() ->
    {
      ItemModelsProperties.register(RegistryHandler.PICK_ON_A_STICK.get(),
          new ResourceLocation(AdventureApparatus.MOD_ID, "cast"), (stack, world, living) ->
              living != null && PickOnAStickItem.getEntity(world, stack) != null ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.STICKY_PICK_ON_A_STICK.get(),
          new ResourceLocation(AdventureApparatus.MOD_ID, "cast"), (stack, world, living) ->
              living != null && PickOnAStickItem.getEntity(world, stack) != null ? 1.0F : 0.0F);
    });
  }
}
