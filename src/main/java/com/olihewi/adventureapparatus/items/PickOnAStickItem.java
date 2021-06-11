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
import net.minecraft.util.math.BlockPos;
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
      if (thrownPick.stuckInBlock != BlockPos.ZERO)
      {
        grapple(itemstack, world, playerEntity, thrownPick);
      }
      thrownPick.remove();
      tag.putInt("thrownPick", 0);
      world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 4.0F, 0.2F / (random.nextFloat() * 0.4F + 0.8F));
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
    int oxidationStage = tag.getInt("CustomModelData");
    boolean waxed = tag.getBoolean("waxed");
    if (oxidationTime == 0)
    {
      tag.putLong("oxidationTime", world.getGameTime());
      tag.putInt("CustomModelData", 0);
    }
    else if (!waxed && oxidationStage < 3 && world.getGameTime() - oxidationTime > OXIDATION_TIME)
    {
      tag.putLong("oxidationTime", world.getGameTime());
      tag.putInt("CustomModelData", oxidationStage + 1);
    }
    if (world.getEntity(tag.getInt("thrownPick")) == null)
    {
      tag.putInt("thrownPick", 0);
    }
  }

  private void grapple(ItemStack itemStack, World world, PlayerEntity thrower, PickOnAStickEntity thrownPick)
  {
    Vector3d motion = thrower.getDeltaMovement();
    Vector3d startPos = thrower.position();
    Vector3d targetPos = thrownPick.position().add(0,1,0);
    Vector3d difference = targetPos.subtract(startPos);
    double magnitude = Math.min(difference.length(),12); // Limiting velocity (increased with enchant?)
    difference = difference.normalize().scale(magnitude);
    Vector3d finalVelocity = difference.scale(0.2D);
    double yVelocity = Math.max(0.5D, finalVelocity.y); // Minimum Y velocity so the player goes up
    finalVelocity = finalVelocity.add(0,yVelocity - finalVelocity.y,0);
    motion = motion.add(finalVelocity);
    thrower.setDeltaMovement(motion);
  }
}
