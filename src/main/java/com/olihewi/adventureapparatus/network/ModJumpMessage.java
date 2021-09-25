package com.olihewi.adventureapparatus.network;

import com.olihewi.adventureapparatus.enchantments.SinkingCurse;
import com.olihewi.adventureapparatus.items.ShuttleShoesItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class ModJumpMessage
{
  public static void serialize(ModJumpMessage message, PacketBuffer buffer) {
  }

  public static ModJumpMessage deserialize(PacketBuffer buffer) {
    return new ModJumpMessage();
  }

  public static boolean handle(ModJumpMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER)
    {
      //context.enqueueWork(() -> {
        PlayerEntity player = context.getSender();
        ShuttleShoesItem.shuttleJump(player);
        SinkingCurse.sinkingJump(player);
      //});
      return true;
    }
    return false;
  }
}
