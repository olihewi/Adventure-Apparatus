package com.olihewi.adventureapparatus.enchantments;

import com.olihewi.adventureapparatus.AdventureApparatus;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SinkingCurse extends Enchantment
{
  public SinkingCurse()
  {
    super(Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] {EquipmentSlotType.FEET});
  }

  @Override
  protected boolean checkCompatibility(Enchantment enchantment)
  {
    return enchantment != Enchantments.DEPTH_STRIDER && enchantment != Enchantments.FROST_WALKER;
  }

  @Override
  public boolean isCurse()
  {
    return true;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack)
  {
    return stack.getEquipmentSlot() == EquipmentSlotType.FEET;
  }
}
