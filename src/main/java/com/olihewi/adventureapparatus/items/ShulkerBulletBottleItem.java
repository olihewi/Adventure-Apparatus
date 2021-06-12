package com.olihewi.adventureapparatus.items;

import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

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

  @Nonnull
  @ParametersAreNonnullByDefault
  @Override
  public UseAction getUseAnimation(ItemStack itemStack)
  {
    return UseAction.DRINK;
  }
}
