package com.olihewi.adventureapparatus.util;

import com.olihewi.adventureapparatus.AdventureApparatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdventureApparatus.MOD_ID)
public class EventSubscriber
{
  @SubscribeEvent
  public static void bottleShulkerBullet(PlayerInteractEvent.EntityInteractSpecific event)
  {
    if (event.getTarget() != null && !event.getWorld().isClientSide)
    {
      ItemStack itemStack = event.getPlayer().getItemInHand(event.getHand());
      Item item = itemStack.getItem();

      Item bottle = null;
      Entity target = event.getTarget();
      EntityType<?> targetType = target.getType();
      PlayerEntity player = event.getPlayer();
      Hand hand = event.getHand();
      if (targetType == EntityType.SHULKER_BULLET)
      {
        bottle = RegistryHandler.SHULKER_BULLET_BOTTLE.get();
        ItemStack bottleItem = new ItemStack(bottle);
        if (item == Items.GLASS_BOTTLE)
        {
          itemStack.shrink(1);
          event.getWorld().playSound(player, event.getPos(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
          player.awardStat(Stats.ITEM_USED.get(event.getItemStack().getItem()));
          event.getTarget().remove();
          if (itemStack.isEmpty())
          {
            player.setItemInHand(hand, bottleItem);
          }
          else if (!player.inventory.add(bottleItem))
          {
            player.drop(bottleItem, false);
          }
          player.swing(hand);
        }
      }
    }
  }

  @SubscribeEvent
  public static void bottleFromShulker(LivingEntityUseItemEvent.Finish event)
  {
    Entity entity = event.getEntityLiving();
    if (entity instanceof PlayerEntity)
    {
      PlayerEntity player = (PlayerEntity) entity;
      if (event.getItem().getStack().getItem() == RegistryHandler.SHULKER_BULLET_BOTTLE.get())
      {
        ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
        itemStack.shrink(1);
        if (!player.inventory.add(itemStack))
        {
          player.drop(itemStack, false);
        }
      }
    }
  }

  @SubscribeEvent
  public static void sinkingEnchantTick(LivingEvent.LivingUpdateEvent event)
  {
    LivingEntity entity = event.getEntityLiving();
    if (EnchantmentHelper.getEnchantments(entity.getItemBySlot(EquipmentSlotType.FEET)).containsKey(RegistryHandler.SINKING_ENCHANT.get()))
    {
      if (entity.isInWater() && (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).abilities.flying))
      {
        entity.setDeltaMovement(entity.getDeltaMovement().subtract(0,0.03D,0));
        entity.setSprinting(false);
        entity.setSwimming(false);
      }
    }
  }

  @SubscribeEvent
  public static void villagerTradesEvent(VillagerTradesEvent event)
  {
    if (event.getType() == VillagerProfession.TOOLSMITH)
    {
      event.getTrades().get(3).add(new Trades.ItemsForEmeraldsTrade(RegistryHandler.PICK_ON_A_STICK.get(), 24, 1, 3));
    }
  }
}
