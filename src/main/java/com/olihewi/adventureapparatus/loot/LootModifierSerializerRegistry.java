package com.olihewi.adventureapparatus.loot;

import com.olihewi.adventureapparatus.AdventureApparatus;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierSerializerRegistry
{
  private static final DeferredRegister<GlobalLootModifierSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, AdventureApparatus.MOD_ID);
  public static void init()
  {
    SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
  public static final RegistryObject<GlobalLootModifierSerializer<PickOnAStickLootModifier>> LOOT_MODIFIER = SERIALIZERS.register("mineshaft_loot", PickOnAStickLootModifier.Serializer::new);
}
