package com.olihewi.adventureapparatus.armour;

import com.olihewi.adventureapparatus.AdventureApparatus;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ModArmourMaterial implements IArmorMaterial
{
  PHANTOM_WINDBREAKER_MATERIAL(AdventureApparatus.MOD_ID + ":phantom_windbreakers", 325,
      new int[]{1, 1, 1, 1}, 18, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, Ingredient.of(Items.PHANTOM_MEMBRANE)),
  SHUTTLE_SHOES_MATERIAL(AdventureApparatus.MOD_ID + ":shuttle_shoes", 250,
      new int[]{1, 1, 1, 1}, 18, SoundEvents.SHULKER_BOX_CLOSE, 0.0F, Ingredient.of(RegistryHandler.SHULKER_BULLET_BOTTLE.get()));

  private final String name;
  private final int durability;
  private final int[] defenseArray;
  private final int enchantability;
  private final SoundEvent soundEvent;
  private final float toughness;
  private final Ingredient repairMaterial;

  ModArmourMaterial(String name, int durability, int[] defenseArray, int enchantability,
                    SoundEvent soundEvent, float toughness, Ingredient repairMaterial)
  {
    this.name = name;
    this.durability = durability;
    this.defenseArray = defenseArray;
    this.enchantability = enchantability;
    this.soundEvent = soundEvent;
    this.toughness = toughness;
    this.repairMaterial = repairMaterial;
  }

  @Override
  public int getDurabilityForSlot(EquipmentSlotType p_200896_1_)
  {
    return this.durability;
  }

  @Override
  public int getDefenseForSlot(EquipmentSlotType p_200902_1_)
  {
    return this.defenseArray[p_200902_1_.getIndex()];
  }

  @Override
  public int getEnchantmentValue()
  {
    return this.enchantability;
  }

  @Override
  public SoundEvent getEquipSound()
  {
    return this.soundEvent;
  }

  @Override
  public Ingredient getRepairIngredient()
  {
    return this.repairMaterial;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public String getName()
  {
    return this.name;
  }

  @Override
  public float getToughness()
  {
    return this.toughness;
  }

  @Override
  public float getKnockbackResistance()
  {
    return 0.0F;
  }
}
