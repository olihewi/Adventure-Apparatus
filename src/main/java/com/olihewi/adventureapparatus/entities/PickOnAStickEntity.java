package com.olihewi.adventureapparatus.entities;

import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PickOnAStickEntity extends Entity implements IEntityAdditionalSpawnData
{
  private int harvestLevel = 0;
  public BlockPos stuckInBlock = BlockPos.ZERO;
  private boolean returning = false;

  protected LivingEntity owner;
  public int ownerID;

  public PickOnAStickEntity(EntityType<? extends PickOnAStickEntity> entityType, World world)
  {
    super(entityType, world);
  }

  public PickOnAStickEntity(World world, ItemStack pickItemStack,
                            PlayerEntity thrower)
  {
    super(RegistryHandler.PICK_ON_A_STICK_ENTITY.get(), world);
    ownerID = thrower.getId();
    CompoundNBT nbt = pickItemStack.getOrCreateTag();
    nbt.putInt("thrownPick",this.getId());
    Vector3d startPosition = thrower.getEyePosition(1.0F);
    this.setPos(startPosition.x,startPosition.y,startPosition.z);
    this.setDeltaMovement(thrower.getLookAngle().scale(1.5D));
    this.harvestLevel = 1;
  }

  public void tick()
  {
    super.tick();
    LivingEntity thrower = this.getThrower();
    if (thrower == null)
    {
      this.remove();
    }
    else if (this.level.isClientSide || !this.shouldStopFishing(thrower))
    {
      if (stuckInBlock == BlockPos.ZERO)
      {
        tickInAir();
      }
      else
      {
        tickInBlock();
      }
    }
  }

  private void tickInAir()
  {
    Vector3d startPos = this.position();
    Vector3d endPos = this.position().add(this.getDeltaMovement());
    RayTraceContext rayTraceContext = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this);
    BlockRayTraceResult blockRayTraceResult = this.level.clip(rayTraceContext);
    if (blockRayTraceResult.getType() != RayTraceResult.Type.MISS)
    {
      endPos = blockRayTraceResult.getLocation();
      endPos.add(endPos.subtract(startPos).normalize().scale(0.1D));
      onBlockImpact(blockRayTraceResult.getBlockPos());
    }
    Vector3d motion = endPos.subtract(startPos);
    this.move(MoverType.SELF, motion);
    motion = motion.subtract(0,0.03D, 0);
    this.setDeltaMovement(motion);
    this.reapplyPosition();
  }

  private void tickInBlock()
  {
    this.setDeltaMovement(0,0,0);
  }

  private void onBlockImpact(BlockPos blockPos)
  {
    this.stuckInBlock = blockPos;
    BlockState blockState = this.level.getBlockState(blockPos);
    this.playSound(blockState.getSoundType().getHitSound(), 1.0F, 1.0F);
  }

  @SuppressWarnings("deprecation")
  private boolean shouldStopFishing(LivingEntity thrower) {
    ItemStack itemstack = thrower.getMainHandItem();
    ItemStack itemstack1 = thrower.getOffhandItem();
    boolean flag = itemstack.getItem() == RegistryHandler.PICK_ON_A_STICK.get();
    boolean flag1 = itemstack1.getItem() == RegistryHandler.PICK_ON_A_STICK.get();
    if (!thrower.removed && thrower.isAlive() && (flag || flag1) && !(this.distanceToSqr(thrower) > 1536.0D)) {
      return false;
    } else {
      this.remove();
      return true;
    }
  }

  public void reel()
  {
    LivingEntity thrower = this.getThrower();
    if (thrower != null)
    {
      if (stuckInBlock != BlockPos.ZERO)
      {
        grapple(thrower);
      }
      this.level.playSound((PlayerEntity) null, thrower.getX(), thrower.getY(), thrower.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 4.0F, 0.2F / (random.nextFloat() * 0.4F + 0.8F));
    }
    this.remove();
  }

  private void grapple(LivingEntity thrower)
  {
    Vector3d motion = thrower.getDeltaMovement();
    Vector3d startPos = thrower.position();
    Vector3d targetPos = this.position().add(0,1,0);
    Vector3d difference = targetPos.subtract(startPos);
    double magnitude = Math.min(difference.length(),12); // Limiting velocity (increased with enchant?)
    difference = difference.normalize().scale(magnitude);
    Vector3d finalVelocity = difference.scale(0.2D);
    double yVelocity = Math.max(0.5D, finalVelocity.y); // Minimum Y velocity so the player goes up
    finalVelocity = finalVelocity.add(0,yVelocity - finalVelocity.y,0);
    motion = motion.add(finalVelocity);
    thrower.setDeltaMovement(motion);
  }

  @Nullable
  public LivingEntity getThrower()
  {
    if (this.owner == null) {
      Entity entity = this.level.getEntity(this.ownerID);
      if (entity instanceof LivingEntity) {
        this.owner = (LivingEntity)entity;
      }
    }
    return this.owner;
  }

  @Override
  protected void defineSynchedData()
  {
  }

  @Override
  protected void readAdditionalSaveData(CompoundNBT compound)
  {
    this.ownerID = compound.getInt("thrower");
    this.stuckInBlock = NBTUtil.readBlockPos(compound.getCompound("block"));
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT compound)
  {
    compound.putInt("thrower", this.ownerID);
    compound.put("block", NBTUtil.writeBlockPos(stuckInBlock));
  }

  @Nonnull
  @Override
  public IPacket<?> getAddEntityPacket()
  {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer)
  {
    buffer.writeInt(ownerID);
  }

  @Override
  public void readSpawnData(PacketBuffer additionalData)
  {
    ownerID = additionalData.readInt();
  }
}
