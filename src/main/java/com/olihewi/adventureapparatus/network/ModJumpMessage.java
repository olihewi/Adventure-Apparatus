package com.olihewi.adventureapparatus.network;

import com.olihewi.adventureapparatus.enchantments.SinkingCurse;
import com.olihewi.adventureapparatus.items.ShuttleShoesItem;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
