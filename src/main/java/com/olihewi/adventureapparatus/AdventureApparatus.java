package com.olihewi.adventureapparatus;

import com.olihewi.adventureapparatus.client.render.ThrownPickRenderer;
import com.olihewi.adventureapparatus.items.PickOnAStickItem;
import com.olihewi.adventureapparatus.loot.LootModifierSerializerRegistry;
import com.olihewi.adventureapparatus.network.ModJumpMessage;
import com.olihewi.adventureapparatus.util.EventSubscriber;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
  public static KeyBinding shuttleJumpKeybind;
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
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

    RegistryHandler.init();

    MinecraftForge.EVENT_BUS.register(EventSubscriber.class);
    LootModifierSerializerRegistry.init();
  }

  private void setup(final FMLCommonSetupEvent event)
  {
    registerMessages();
  }

  private void clientSetup(final FMLClientSetupEvent event)
  {
    ClientSetup.clientSetup(event);
  }
}
