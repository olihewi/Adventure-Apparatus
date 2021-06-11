package com.olihewi.adventureapparatus;

import com.olihewi.adventureapparatus.util.EventSubscriber;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("adventureapparatus")
public class AdventureApparatus
{
  private static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "adventureapparatus";

  public AdventureApparatus()
  {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    RegistryHandler.init();

    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(EventSubscriber.class);
  }

  private void setup(final FMLCommonSetupEvent event)
  {

  }

  private void doClientStuff(final FMLClientSetupEvent event)
  {
    event.enqueueWork(() ->
    {
      ItemModelsProperties.register(RegistryHandler.PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "oxidation"), (stack, world, living) ->
              stack.getOrCreateTag().getInt("oxidationStage"));
      ItemModelsProperties.register(RegistryHandler.PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
    });
  }
}
