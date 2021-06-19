package com.olihewi.adventureapparatus.items;

import com.olihewi.adventureapparatus.armour.ModArmourMaterial;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PhantomWindbreakersItem extends ArmorItem
{
  public PhantomWindbreakersItem()
  {
    super(ModArmourMaterial.PHANTOM_WINDBREAKER_MATERIAL, EquipmentSlotType.LEGS, new Item.Properties().tab(ItemGroup.TAB_COMBAT));
  }

  @Override
  public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
  {
    int damageTime = 0;
    if (player.isOnGround()) { return; }
    if (player.isFallFlying() && player.isCrouching())
    {
      Vector3d motion = player.getDeltaMovement();
      motion = motion.subtract(motion.scale(0.1D));
      player.setDeltaMovement(motion);
      player.fallDistance = 0.0F;
      damageTime = 20;
    }
    else if (player.hasEffect(Effects.SLOW_FALLING))
    {
      if (!player.isCrouching())
      {
        Vector3d motion = player.getDeltaMovement();
        double vel_y = motion.y;
        vel_y = Math.max(vel_y, -0.01D);
        player.setDeltaMovement(motion.x, vel_y, motion.z);
        player.stopFallFlying();
        damageTime = 60;
      }
    }
    else if (player.isCrouching())
    {
      Vector3d motion = player.getDeltaMovement();
      double vel_y = motion.y;
      if (vel_y < -0.05D)
      {
        vel_y = Math.min(vel_y + 0.2D, -0.075D);
        player.setDeltaMovement(motion.x, vel_y, motion.z);
        player.fallDistance = (float) -vel_y * 4;
        damageTime = 20;
      }
    }
    if (damageTime != 0 && world.getGameTime() % damageTime == 0)
    {
      stack.hurtAndBreak(1,player,(p) -> {
        p.broadcastBreakEvent(EquipmentSlotType.LEGS);
      });
    }
  }
}
