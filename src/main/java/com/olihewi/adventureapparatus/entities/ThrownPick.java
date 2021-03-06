package com.olihewi.adventureapparatus.entities;

import com.olihewi.adventureapparatus.AdventureApparatus;
import com.olihewi.adventureapparatus.items.PickOnAStickItem;
import com.olihewi.adventureapparatus.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ThrownPick extends Entity implements IEntityAdditionalSpawnData
{
  public static ResourceLocation CANNOT_GRAPPLE = new ResourceLocation(AdventureApparatus.MOD_ID,"cannot_grapple");
  public BlockPos stuckInBlock = BlockPos.ZERO;

  protected LivingEntity owner;
  public int ownerID;
  private static final DataParameter<ItemStack> ITEMSTACK = EntityDataManager.defineId(ThrownPick.class, DataSerializers.ITEM_STACK);

  public ThrownPick(EntityType<? extends ThrownPick> entityType, World world)
  {
    super(entityType, world);
    this.registerData();
    this.noPhysics = true;
  }

  public ThrownPick(World world, ItemStack pickItemStack,
                    PlayerEntity thrower)
  {
    this(RegistryHandler.PICK_ON_A_STICK_ENTITY.get(), world);
    ownerID = thrower.getId();
    CompoundNBT nbt = pickItemStack.getOrCreateTag();
    nbt.putInt("thrownPick", this.getId());
    Vector3d startPosition = thrower.getEyePosition(1.0F);
    this.setPos(startPosition.x, startPosition.y, startPosition.z);
    this.setDeltaMovement(thrower.getLookAngle().scale(1.5D));
    this.setItemStack(pickItemStack);
  }

  protected void registerData()
  {
    this.getEntityData().define(ITEMSTACK, ItemStack.EMPTY);
  }

  public ItemStack getItemStack()
  {
    return this.getEntityData().get(ITEMSTACK);
  }

  public void setItemStack(ItemStack stack)
  {
    this.getEntityData().set(ITEMSTACK, stack);
  }

  public void tick()
  {
    super.tick();
    LivingEntity thrower = this.getThrower();
    if (thrower == null)
    {
      this.remove();
    }
    else if (this.level.isClientSide || !this.shouldRemove(thrower))
    {
      if (stuckInBlock == BlockPos.ZERO || this.level.getBlockState(stuckInBlock).isAir())
      {
        tickInAir();
      }
      else
      {
        tickInBlock();
      }
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean shouldRenderAtSqrDistance(double p_70112_1_)
  {
    return p_70112_1_ < 4096.0D;
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
    motion = motion.subtract(0, 0.03D, 0);
    this.setDeltaMovement(motion);
    this.reapplyPosition();
  }

  private void tickInBlock()
  {
    this.setDeltaMovement(0, 0, 0);
  }

  private void onBlockImpact(BlockPos blockPos)
  {
    this.stuckInBlock = blockPos;
    BlockState blockState = this.level.getBlockState(blockPos);
    if (isSticky())
    {
      this.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 1.0F);
    }
    else if (canBreak(blockState))
    {
      this.playSound(blockState.getSoundType().getPlaceSound(), 1.0F, 1.0F);
      this.level.destroyBlockProgress(ownerID, blockPos, 4);
    }
  }

  private boolean isSticky()
  {
    return (this.getItemStack().getItem() instanceof PickOnAStickItem && ((PickOnAStickItem) this.getItemStack().getItem()).sticky);
  }

  @SuppressWarnings("deprecation")
  private boolean shouldRemove(LivingEntity thrower)
  {
    ItemStack itemstack = thrower.getMainHandItem();
    ItemStack itemstack1 = thrower.getOffhandItem();
    boolean flag = itemstack.getItem() instanceof PickOnAStickItem && itemstack.getOrCreateTag().getInt("thrownPick") != 0;
    boolean flag1 = itemstack1.getItem() instanceof PickOnAStickItem && itemstack1.getOrCreateTag().getInt("thrownPick") != 0;
    if (!thrower.removed && thrower.isAlive() && (flag || flag1) && !(this.distanceToSqr(thrower) > 1536.0D))
    {
      return false;
    }
    else
    {
      this.level.destroyBlockProgress(ownerID, this.stuckInBlock, -1);
      this.remove();
      return true;
    }
  }

  public boolean reel()
  {
    LivingEntity thrower = this.getThrower();
    boolean success = stuckInBlock != BlockPos.ZERO;
    if (thrower != null)
    {
      BlockState blockState = this.level.getBlockState(stuckInBlock);
      if (success)
      {
        if (!isSticky())
        {
          mine(stuckInBlock, blockState);
        }
        else if (!blockState.getBlock().getTags().contains(CANNOT_GRAPPLE))
        {
          grapple(thrower);
        }
      }
      this.level.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 4.0F, 0.2F / (random.nextFloat() * 0.4F + 0.8F));
    }
    this.remove();
    return success;
  }

  private boolean canBreak(BlockState state)
  {
    return state.getHarvestLevel() <= 2 &&
        state.getDestroySpeed(this.level, this.stuckInBlock) >= 0;
  }

  private void mine(BlockPos blockPos, BlockState blockState)
  {
    LivingEntity thrower = getThrower();
    if (thrower != null && !this.level.isClientSide && canBreak(blockState))
    {
      level.levelEvent(2001, blockPos, Block.getId(blockState));
      TileEntity tileEntity = blockState.hasTileEntity() ? level.getBlockEntity(blockPos) : null;
      Block.dropResources(blockState, level, blockPos, tileEntity, thrower, this.getItemStack());
      this.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
      List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(2));
      List<ExperienceOrbEntity> xps = level.getEntitiesOfClass(ExperienceOrbEntity.class, getBoundingBox().inflate(5));
      Vector3d endPos = thrower.position();
      this.level.destroyBlockProgress(ownerID, blockPos, -1);
      for (ItemEntity item : items)
      {
        double xMov = endPos.x - item.getX();
        double yMov = endPos.y - item.getY();
        double zMov = endPos.z - item.getZ();
        item.setDeltaMovement(xMov * 0.1D, yMov * 0.1D + Math.sqrt(Math.sqrt(xMov * xMov + yMov * yMov + zMov * zMov)) * 0.08D, zMov * 0.1D);
        item.setPickUpDelay(2);
      }
      for (ExperienceOrbEntity xp : xps)
      {
        xp.setDeltaMovement(calculateVelocity(xp.position(), endPos, 14));
      }
    }
  }

  private void grapple(LivingEntity thrower)
  {
    Vector3d motion = thrower.getDeltaMovement();
    Vector3d startPos = thrower.position();
    Vector3d targetPos = this.position().add(0, 1, 0);
    Vector3d finalVelocity = calculateVelocity(startPos, targetPos, 12);
    motion = motion.add(finalVelocity);
    thrower.setDeltaMovement(motion);
  }

  private Vector3d calculateVelocity(Vector3d start, Vector3d end, double max_velocity)
  {
    Vector3d difference = end.subtract(start);
    double magnitude = Math.min(difference.length(), max_velocity);
    difference = difference.normalize().scale(magnitude);
    difference = difference.scale(0.2D);
    double yVelocity = Math.max(0.5D, difference.y);
    difference = difference.add(0, yVelocity - difference.y, 0);
    return difference;
  }

  @Nullable
  public LivingEntity getThrower()
  {
    if (this.owner == null)
    {
      Entity entity = this.level.getEntity(this.ownerID);
      if (entity instanceof LivingEntity)
      {
        this.owner = (LivingEntity) entity;
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
    this.setItemStack(ItemStack.of(compound.getCompound("itemStack")));
    if (this.getItemStack().isEmpty())
    {
      this.remove();
    }
  }

  @Override
  protected void addAdditionalSaveData(CompoundNBT compound)
  {
    compound.putInt("thrower", this.ownerID);
    compound.put("block", NBTUtil.writeBlockPos(stuckInBlock));
    compound.put("itemStack", this.getItemStack().serializeNBT());
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
    buffer.writeItemStack(this.getItemStack(), false);
  }

  @Override
  public void readSpawnData(PacketBuffer additionalData)
  {
    ownerID = additionalData.readInt();
    this.setItemStack(additionalData.readItem());
  }
}
