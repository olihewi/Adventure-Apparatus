package com.olihewi.pickonastick.items;

import com.olihewi.pickonastick.armour.ModArmourMaterial;
import com.olihewi.pickonastick.client.model.PhantomWindbreakersModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PhantomWindbreakersItem extends ArmorItem
{
  public PhantomWindbreakersItem()
  {
    super(ModArmourMaterial.PHANTOM_WINDBREAKER_MATERIAL,EquipmentSlotType.LEGS,new Item.Properties().tab(ItemGroup.TAB_COMBAT));
  }
  @Override
  public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
  {
    if (player.isFallFlying() && player.isCrouching())
    {
      Vector3d motion = player.getDeltaMovement();
      motion = motion.subtract(motion.scale(0.1D));
      player.setDeltaMovement(motion);
      player.fallDistance = 0.0F;
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
      }
    }
  }

  @Nullable
  @Override
  public <T extends BipedModel<?>> T getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, T _default)
  {
    if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ArmorItem)
    {
      PhantomWindbreakersModel model = new PhantomWindbreakersModel();
      /*boolean equipped = armorSlot == EquipmentSlotType.LEGS;
      model.leftWindbreaker.visible = equipped;
      model.rightWindbreaker.visible = equipped;*/

      model.young = _default.young;
      model.riding = _default.riding;
      model.crouching = _default.crouching;
      return (T) model;
    }
    return null;
  }
}
