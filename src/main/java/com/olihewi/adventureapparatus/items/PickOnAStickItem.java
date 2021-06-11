package com.olihewi.adventureapparatus.items;

import com.olihewi.adventureapparatus.entities.PickOnAStickEntity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PickOnAStickItem extends FishingRodItem
{
  private static final long OXIDATION_TIME = 6000;
  public PickOnAStickItem()
  {
    super(new Item.Properties().tab(ItemGroup.TAB_TOOLS));
  }
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
    ItemStack itemstack = playerEntity.getItemInHand(hand);
    CompoundNBT tag = itemstack.getOrCreateTag();
    int thrownPickId = tag.getInt("thrownPick");
    PickOnAStickEntity thrownPick = (PickOnAStickEntity) playerEntity.level.getEntity(thrownPickId);
    if (thrownPickId != 0 && thrownPick != null)
    {
      thrownPick.reel();
      tag.putInt("thrownPick", 0);
    }
    else
    {
      world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!world.isClientSide)
      {
        world.addFreshEntity(new PickOnAStickEntity(world, itemstack, playerEntity));
      }

      playerEntity.awardStat(Stats.ITEM_USED.get(this));
    }

    return ActionResult.sidedSuccess(itemstack, world.isClientSide());
  }

  @Override
  public void inventoryTick(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
  {
    super.inventoryTick(itemStack, world, entity, p_77663_4_, p_77663_5_);
    CompoundNBT tag = itemStack.getOrCreateTag();
    long oxidationTime = tag.getLong("oxidationTime");
    int oxidationStage = tag.getInt("oxidationStage");
    boolean waxed = tag.getBoolean("waxed");
    if (oxidationTime == 0)
    {
      tag.putLong("oxidationTime", world.getGameTime());
    }
    else if (!waxed && oxidationStage < 3 && world.getGameTime() - oxidationTime > OXIDATION_TIME)
    {
      tag.putLong("oxidationTime", world.getGameTime());
      tag.putInt("oxidationStage", oxidationStage + 1);
    }
    if (world.getEntity(tag.getInt("thrownPick")) == null)
    {
      tag.putInt("thrownPick", 0);
    }
  }
}
