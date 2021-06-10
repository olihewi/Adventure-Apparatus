package com.olihewi.adventureapparatus.util;

import com.olihewi.adventureapparatus.AdventureApparatus;
import com.olihewi.adventureapparatus.entities.PickOnAStickEntity;
import com.olihewi.adventureapparatus.items.ShulkerBulletBottleItem;
import com.olihewi.adventureapparatus.items.ShuttleShoesItem;
import com.olihewi.adventureapparatus.items.PhantomWindbreakersItem;
import com.olihewi.adventureapparatus.items.PickOnAStickItem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler
{
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdventureApparatus.MOD_ID);
  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, AdventureApparatus.MOD_ID);
  public static void init()
  {
    ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
  // Items
  public static final RegistryObject<Item> PICK_ON_A_STICK = ITEMS.register("pick_on_a_stick", PickOnAStickItem::new);
  public static final RegistryObject<Item> SHULKER_BULLET_BOTTLE = ITEMS.register("shulker_bullet_bottle", ShulkerBulletBottleItem::new);
  // Entities
  public static final RegistryObject<EntityType<PickOnAStickEntity>> PICK_ON_A_STICK_ENTITY = ENTITIES.register("thrown_pick",
      () -> EntityType.Builder.<PickOnAStickEntity>of(PickOnAStickEntity::new, EntityClassification.MISC)
          .sized(0.25F, 0.25F).build(new ResourceLocation(AdventureApparatus.MOD_ID, "thrown_pick").toString()));
  // Armour
  public static final RegistryObject<ArmorItem> PHANTOM_WINDBREAKERS = ITEMS.register("phantom_windbreakers",
      PhantomWindbreakersItem::new);
  public static final RegistryObject<ArmorItem> SHUTTLE_SHOES = ITEMS.register("shuttle_shoes",
      ShuttleShoesItem::new);
}
