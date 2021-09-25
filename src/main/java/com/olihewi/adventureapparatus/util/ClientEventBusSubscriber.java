package com.olihewi.adventureapparatus.util;

import com.olihewi.adventureapparatus.AdventureApparatus;
import com.olihewi.adventureapparatus.enchantments.SinkingCurse;
import com.olihewi.adventureapparatus.items.ShuttleShoesItem;
import com.olihewi.adventureapparatus.network.ModJumpMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdventureApparatus.MOD_ID, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
  private static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void onKeyPressed(InputEvent.KeyInputEvent key)
  {
    Minecraft mc = Minecraft.getInstance();
    PlayerEntity player = mc.player;
    if (mc.screen != null || player == null)
    {
      return;
    }
    if (key.getKey() == AdventureApparatus.shuttleJumpKeybind.getKey().getValue() && AdventureApparatus.shuttleJumpKeybind.consumeClick() && !player.abilities.flying)
    {
      AdventureApparatus.CHANNEL.sendToServer(new ModJumpMessage());
      ShuttleShoesItem.shuttleJump(player);
      SinkingCurse.sinkingJump(player);
    }
  }
}
