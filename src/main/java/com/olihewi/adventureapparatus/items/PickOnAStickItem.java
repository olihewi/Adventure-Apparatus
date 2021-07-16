package com.olihewi.adventureapparatus.items;

import com.olihewi.adventureapparatus.entities.ThrownPick;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class PickOnAStickItem extends FishingRodItem
{
  public boolean sticky;

  public PickOnAStickItem(boolean sticky)
  {
    super(new Item.Properties().tab(ItemGroup.TAB_TOOLS).durability(64));
    this.sticky = sticky;
  }

  public static ThrownPick getEntity(World world, ItemStack stack)
  {
    CompoundNBT tag = stack.getOrCreateTag();
    int thrownPickId = tag.getInt("thrownPick");
    if (thrownPickId != 0 && world != null)
    {
      Entity thrownEntity = world.getEntity(thrownPickId);
      if (thrownEntity instanceof ThrownPick)
      {
        return (ThrownPick) thrownEntity;
      }
    }
    return null;
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
      if (thrownPick.reel())
      {
        itemstack.hurtAndBreak(1, playerEntity, (player) -> {
          player.broadcastBreakEvent(hand);
        });

      }
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
