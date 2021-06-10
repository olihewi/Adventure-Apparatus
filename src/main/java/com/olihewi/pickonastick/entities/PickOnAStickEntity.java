package com.olihewi.pickonastick.entities;

import com.olihewi.pickonastick.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class PickOnAStickEntity extends ProjectileEntity implements IEntityAdditionalSpawnData
{
  private int harvestLevel = 0;
  private boolean inBlock = false;
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
      if (!inBlock)
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
    /*Vector3d startPos = this.position();
    Vector3d endPos = this.position().add(this.getDeltaMovement());
    RayTraceContext rayTraceContext = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this);
    BlockRayTraceResult blockRayTraceResult = this.level.clip(rayTraceContext);
    if (blockRayTraceResult.getType() == RayTraceResult.Type.MISS)
    {
      this.inBlock = false;
    }*/
    this.setDeltaMovement(0,0,0);
  }

  private void onBlockImpact(BlockPos blockPos)
  {
    this.inBlock = true;
    BlockState blockState = this.level.getBlockState(blockPos);
    this.playSound(blockState.getSoundType().getHitSound(), 1.0F, 1.0F);
  }

  private boolean shouldStopFishing(LivingEntity thrower) {
    ItemStack itemstack = thrower.getMainHandItem();
    ItemStack itemstack1 = thrower.getOffhandItem();
    boolean flag = itemstack.getItem() == RegistryHandler.PICK_ON_A_STICK.get();
    boolean flag1 = itemstack1.getItem() == RegistryHandler.PICK_ON_A_STICK.get();
    if (!thrower.removed && thrower.isAlive() && (flag || flag1) && !(this.distanceToSqr(thrower) > 1024.0D)) {
      return false;
    } else {
      this.remove();
      return true;
    }
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
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT compound)
  {
    compound.putInt("thrower", this.ownerID);
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
