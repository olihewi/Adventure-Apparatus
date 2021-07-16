package com.olihewi.adventureapparatus.loot;

import com.google.gson.JsonObject;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class PickOnAStickLootModifier extends LootModifier
{
  protected PickOnAStickLootModifier(ILootCondition[] conditionsIn)
  {
    super(conditionsIn);
  }
  @Nonnull
  @Override
  protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
  {
    ResourceLocation lootTable = context.getQueriedLootTableId();
    if (lootTable.equals(new ResourceLocation("minecraft:chests/abandoned_mineshaft")) ||
    lootTable.equals(new ResourceLocation("minecraft:chests/village/village_cartographer")) ||
    lootTable.equals(new ResourceLocation("minecraft:chests/village/village_weaponsmith")))
    {
      Random random = context.getRandom();
      ItemStack loot = new ItemStack(RegistryHandler.PICK_ON_A_STICK.get());
      generatedLoot.add(loot);
    }
    return generatedLoot;
  }
  public static class Serializer extends GlobalLootModifierSerializer<PickOnAStickLootModifier>
  {
    @Override
    public PickOnAStickLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
    {
      return new PickOnAStickLootModifier(ailootcondition);
    }

    @Override
    public JsonObject write(PickOnAStickLootModifier instance)
    {
      return this.makeConditions(instance.conditions);
    }
  }
}