package com.olihewi.adventureapparatus.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ShulkerBulletBottleItem extends Item
{
  public ShulkerBulletBottleItem()
  {
    super(new Item.Properties().tab(ItemGroup.TAB_MISC).food(
        new Food.Builder().nutrition(0).saturationMod(0.0F)
            .effect(new EffectInstance(Effects.LEVITATION, 100, 1), 1)
            .alwaysEat()
            .build()).stacksTo(16));
  }

  @Override
  public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_)
  {
    if (p_77654_3_ instanceof PlayerEntity)
    {
      ((PlayerEntity) p_77654_3_).inventory.add(new ItemStack(Items.GLASS_BOTTLE));
    }
    return super.finishUsingItem(p_77654_1_, p_77654_2_, p_77654_3_);

  }

  @Nonnull
  @ParametersAreNonnullByDefault
  @Override
  public UseAction getUseAnimation(ItemStack itemStack)
  {
    return UseAction.DRINK;
  }

  @Override
  public ItemStack getContainerItem(ItemStack itemStack)
  {
    return new ItemStack(Items.GLASS_BOTTLE, itemStack.getCount());
  }

  @Override
  public boolean hasContainerItem(ItemStack stack)
  {
    return true;
  }
}
