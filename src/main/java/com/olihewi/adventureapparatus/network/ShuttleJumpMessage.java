package com.olihewi.adventureapparatus.network;

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

public final class ShuttleJumpMessage
{
  public static void serialize(ShuttleJumpMessage message, PacketBuffer buffer) {
  }

  public static ShuttleJumpMessage deserialize(PacketBuffer buffer) {
    return new ShuttleJumpMessage();
  }

  public static boolean handle(ShuttleJumpMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        PlayerEntity player = context.getSender();
        if (player != null && !player.isOnGround() && !player.isSpectator()) {
          ItemStack stack = player.inventory.getArmor(0);
          if (stack.getItem() == RegistryHandler.SHUTTLE_SHOES.get() && ShuttleShoesItem.canJump(stack, player))
          {
            CompoundNBT tag = stack.getOrCreateTag();
            int increment = tag.getInt("timesJumped") + 1;
            tag.putInt("timesJumped", increment);
            if (!player.isFallFlying())
            {
              double jumpHeight = 0.8D - (increment * 0.05D) - (player.hasEffect(Effects.SLOW_FALLING) ? 0.2D : 0.0D);
              Vector3d motion = player.getDeltaMovement();
              double yvel = Math.max(motion.y + jumpHeight, jumpHeight);
              player.setDeltaMovement(motion.x, yvel, motion.z);
            }
            else
            {
              Vector3d motion = player.getDeltaMovement();
              Vector3d forceDir = player.getLookAngle().add(0, 0.75D, 0).normalize();
              player.setDeltaMovement(motion.add(forceDir));
            }
            player.getCooldowns().addCooldown(stack.getItem(), 10 + increment * 3);
            player.level.playSound(player, player, SoundEvents.SHULKER_SHOOT, SoundCategory.PLAYERS, 1.0F, MathHelper.clamp(1.2F - (increment * 0.2F), 0.1F, 1.0F));
            stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(EquipmentSlotType.FEET));
            for (int i = 0; i < 6; i++)
            {
              player.level.addParticle(ParticleTypes.END_ROD, player.getX() + player.getRandom().nextDouble(), player.getY(), player.getZ() + player.getRandom().nextDouble(), 0.0D, -0.25D, 0.0D);
            }
          }
        }
      });
      return true;
    }
    return false;
  }
}
