package com.olihewi.adventureapparatus;

import com.olihewi.adventureapparatus.loot.LootModifierSerializerRegistry;
import com.olihewi.adventureapparatus.network.ModJumpMessage;
import com.olihewi.adventureapparatus.util.EventSubscriber;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("adventureapparatus")
public class AdventureApparatus
{
  private static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "adventureapparatus";
  public static final String NETWORK_PROTOCOL = "AA1";
  public static SimpleChannel CHANNEL;
  public static void registerMessages()
  {
    CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MOD_ID,"net"))
      .networkProtocolVersion(() -> NETWORK_PROTOCOL)
      .clientAcceptedVersions(NETWORK_PROTOCOL::equals)
      .serverAcceptedVersions(NETWORK_PROTOCOL::equals)
      .simpleChannel();

    CHANNEL.registerMessage(0,
        ModJumpMessage.class,
        ModJumpMessage::serialize,
        ModJumpMessage::deserialize,
        ModJumpMessage::handle);
  }

  public AdventureApparatus()
  {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    RegistryHandler.init();
    registerMessages();

    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(EventSubscriber.class);
    LootModifierSerializerRegistry.init();
  }

  private void setup(final FMLCommonSetupEvent event)
  {

  }

  private void doClientStuff(final FMLClientSetupEvent event)
  {
    event.enqueueWork(() ->
    {
      ItemModelsProperties.register(RegistryHandler.PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.EXPOSED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.WEATHERED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.OXIDIZED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.WAXED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.WAXED_EXPOSED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.WAXED_WEATHERED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
      ItemModelsProperties.register(RegistryHandler.WAXED_OXIDIZED_PICK_ON_A_STICK.get(),
          new ResourceLocation(MOD_ID, "cast"), (stack, world, living) ->
              living != null && stack.getOrCreateTag().getInt("thrownPick") != 0 ? 1.0F : 0.0F);
    });
  }
}
