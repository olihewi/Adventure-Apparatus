package com.olihewi.adventureapparatus.items;

import com.olihewi.adventureapparatus.armour.ModArmourMaterial;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ShuttleShoesItem extends ArmorItem
{
  public static final String TIMES_JUMPED = "timesJumped";

  public ShuttleShoesItem()
  {
    super(ModArmourMaterial.SHUTTLE_SHOES_MATERIAL, EquipmentSlotType.FEET, new Item.Properties().tab(ItemGroup.TAB_COMBAT));
  }

  @Override
  public void onArmorTick(ItemStack item, World world, PlayerEntity player)
  {
    CompoundNBT tag = item.getOrCreateTag();
    if (player.isOnGround() || player.isInWater() || player.isPassenger() || player.hasEffect(Effects.SLOW_FALLING))
    {
      tag.putInt(TIMES_JUMPED, 0);
    }
  }

  public static void shuttleJump(PlayerEntity player)
  {
    ItemStack itemStack = player.inventory.getArmor(0);
    Item item = itemStack.getItem();
    if (item == RegistryHandler.SHUTTLE_SHOES.get() && !player.isOnGround() && !player.isSpectator() &&
        !player.getCooldowns().isOnCooldown(item))
    {
      //AdventureApparatus.CHANNEL.sendToServer(new ShuttleJumpMessage());
      CompoundNBT tag = itemStack.getOrCreateTag();
      int increment = tag.getInt(TIMES_JUMPED) + 1;
      tag.putInt(TIMES_JUMPED, increment);
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
      player.getCooldowns().addCooldown(item, 10 + increment * 3);
      player.level.playSound(player, player, SoundEvents.SHULKER_SHOOT, SoundCategory.PLAYERS, 1.0F, MathHelper.clamp(1.2F - (increment * 0.2F), 0.1F, 1.0F));
      itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlotType.FEET));
      for (int i = 0; i < 6; i++)
      {
        player.level.addParticle(ParticleTypes.END_ROD, player.getX() + player.getRandom().nextDouble(), player.getY(), player.getZ() + player.getRandom().nextDouble(), 0.0D, -0.25D, 0.0D);
      }
    }
  }
  public static boolean canJump(ItemStack itemStack, PlayerEntity player)
  {
    return player.getCooldowns().isOnCooldown(itemStack.getItem());
  }
}
