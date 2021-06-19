package com.olihewi.adventureapparatus.items;

import com.olihewi.adventureapparatus.entities.ThrownPick;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PickOnAStickItem extends FishingRodItem
{
  private static final long OXIDATION_TIME = 6000;
  public int oxidationStage;
  public boolean waxed;

  public PickOnAStickItem(int oxidation_stage, boolean waxed)
  {
    super(new Item.Properties().tab(ItemGroup.TAB_TOOLS).durability(64));
    this.oxidationStage = oxidation_stage;
    this.waxed = waxed;
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
  {
    ItemStack itemstack = playerEntity.getItemInHand(hand);
    CompoundNBT tag = itemstack.getOrCreateTag();
    int thrownPickId = tag.getInt("thrownPick");
    ThrownPick thrownPick = (ThrownPick) playerEntity.level.getEntity(thrownPickId);
    if (thrownPickId != 0 && thrownPick != null)
    {
      thrownPick.reel();
      tag.putInt("thrownPick", 0);
    }
    else
    {
      world.playSound((PlayerEntity) null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide)
      {
        world.addFreshEntity(new ThrownPick(world, itemstack, playerEntity));
      }

      playerEntity.awardStat(Stats.ITEM_USED.get(this));
    }

    return ActionResult.sidedSuccess(itemstack, world.isClientSide());
  }

  @Override
  public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean p_77663_5_)
  {
    super.inventoryTick(itemStack, world, entity, itemSlot, p_77663_5_);
    CompoundNBT tag = itemStack.getOrCreateTag();
    long oxidationTime = tag.getLong("oxidationTime");
    if (oxidationTime == 0)
    {
      tag.putLong("oxidationTime", world.getGameTime());
    }
    else if (!waxed && oxidationStage < 3 && world.getGameTime() - oxidationTime > OXIDATION_TIME)
    {
      ItemStack replacement = ItemStack.EMPTY;
      if (oxidationStage == 0)
      {
        replacement = new ItemStack(RegistryHandler.EXPOSED_PICK_ON_A_STICK.get());
      }
      else if (oxidationStage == 1)
      {
        replacement = new ItemStack(RegistryHandler.WEATHERED_PICK_ON_A_STICK.get());
      }
      if (oxidationStage == 2)
      {
        replacement = new ItemStack(RegistryHandler.OXIDIZED_PICK_ON_A_STICK.get());
      }
      replacement.setDamageValue(itemStack.getDamageValue());
      replacement.getEnchantmentTags().addAll(itemStack.getEnchantmentTags());
      replacement.getOrCreateTag().putInt("thrownPick", itemStack.getOrCreateTag().getInt("thrownPick"));
      if (entity instanceof PlayerEntity)
      {
        ((PlayerEntity) entity).inventory.setItem(itemSlot, replacement);
      }

    }
    if (world.getEntity(tag.getInt("thrownPick")) == null)
    {
      tag.putInt("thrownPick", 0);
    }
  }

  private final Enchantment[] VALID_ENCHANTMENTS =
      {
          Enchantments.SILK_TOUCH, Enchantments.BLOCK_FORTUNE, Enchantments.UNBREAKING,
          Enchantments.MENDING
      };

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
  {
    for (Enchantment check : VALID_ENCHANTMENTS)
    {
      if (check == enchantment)
      {
        return true;
      }
    }
    return false;
  }
}
