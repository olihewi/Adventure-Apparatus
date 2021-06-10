package com.olihewi.pickonastick.items;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
public class ShulkerBulletBottleItem extends Item
{
  public ShulkerBulletBottleItem()
  {
    super(new Item.Properties().tab(ItemGroup.TAB_MISC).food(
        new Food.Builder().nutrition(0).saturationMod(0.0F)
            .effect(new EffectInstance(Effects.LEVITATION,100,1),1)
            .alwaysEat()
            .build()).stacksTo(16));
  }
}
