package com.olihewi.adventureapparatus.loot;

import com.google.gson.JsonObject;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class SinkingCurseLootModifier extends LootModifier
{
  protected SinkingCurseLootModifier(ILootCondition[] conditionsIn)
  {
    super(conditionsIn);
  }

  @Nonnull
  @Override
  protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
  {
    ResourceLocation lootTable = context.getQueriedLootTableId();
    if (lootTable.equals(new ResourceLocation("minecraft:chests/shipwreck_treasure")))
    {
      ItemStack loot = new ItemStack(Items.ENCHANTED_BOOK);
      EnchantedBookItem.addEnchantment(loot, new EnchantmentData(RegistryHandler.SINKING_ENCHANT.get(),1));
      generatedLoot.add(loot);
    }
    return generatedLoot;
  }
  public static class Serializer extends GlobalLootModifierSerializer<SinkingCurseLootModifier>
  {
    @Override
    public SinkingCurseLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
    {
      return new SinkingCurseLootModifier(ailootcondition);
    }

    @Override
    public JsonObject write(SinkingCurseLootModifier instance)
    {
      return this.makeConditions(instance.conditions);
    }
  }
}
