package org.bukkit.potion;


import Material.POTION;
import PotionType.POISON;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static PotionType.INSTANT_HEAL;
import static PotionType.POISON;
import static PotionType.WATER;


public class PotionTest {
    @Test
    public void applyToItemStack() {
        Potion potion = new Potion(POISON);
        ItemStack stack = new ItemStack(Material.POTION, 1);
        potion.apply(stack);
        Assert.assertTrue(((stack.getDurability()) == (potion.toDamageValue())));
    }

    @Test
    public void fromDamage() {
        Potion potion = Potion.fromDamage(POISON.getDamageValue());
        Assert.assertTrue(((potion.getType()) == (POISON)));
        potion = Potion.fromDamage(((POISON.getDamageValue()) | (PotionTest.SPLASH_BIT)));
        Assert.assertTrue((((potion.getType()) == (POISON)) && (potion.isSplash())));
        potion = /* Potion of Healing II */
        Potion.fromDamage(37);
        Assert.assertTrue((((potion.getType()) == (INSTANT_HEAL)) && ((potion.getLevel()) == 2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalApplyToItemStack() {
        Potion potion = new Potion(POISON);
        potion.apply(new ItemStack(Material.AIR, 1));
    }

    @Test
    public void ItemStackConversion() {
        Potion potion = new Potion(POISON);
        ItemStack itemstack = potion.toItemStack(1);
        Assert.assertThat(itemstack.getType(), Matchers.is(POTION));
        Assert.assertTrue(((itemstack.getAmount()) == 1));
        Assert.assertTrue(((itemstack.getDurability()) == (potion.toDamageValue())));
    }

    @Test
    public void setExtended() {
        PotionEffectType.registerPotionEffectType(new PotionEffectType(19) {
            @Override
            public double getDurationModifier() {
                return 1;
            }

            @Override
            public String getName() {
                return "Poison";
            }

            @Override
            public boolean isInstant() {
                return false;
            }
        });
        Potion potion = new Potion(POISON);
        Assert.assertFalse(potion.hasExtendedDuration());
        potion.setHasExtendedDuration(true);
        Assert.assertTrue(potion.hasExtendedDuration());
        Assert.assertTrue((((potion.toDamageValue()) & (PotionTest.EXTENDED_BIT)) != 0));
    }

    @Test
    public void setSplash() {
        Potion potion = new Potion(POISON);
        Assert.assertFalse(potion.isSplash());
        potion.setSplash(true);
        Assert.assertTrue(potion.isSplash());
        Assert.assertTrue((((potion.toDamageValue()) & (PotionTest.SPLASH_BIT)) != 0));
    }

    @Test
    public void setLevel() {
        Potion potion = new Potion(POISON);
        Assert.assertEquals(1, potion.getLevel());
        potion.setLevel(2);
        Assert.assertEquals(2, potion.getLevel());
        Assert.assertTrue((((potion.toDamageValue()) & 63) == ((POISON.getDamageValue()) | 32)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullType() {
        new Potion(null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxLevelConstruct() {
        new Potion(POISON, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxLevelSet() {
        Potion potion = new Potion(POISON);
        potion.setLevel(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullStack() {
        Potion potion = new Potion(POISON);
        potion.apply(((ItemStack) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEntity() {
        Potion potion = new Potion(POISON);
        potion.apply(((LivingEntity) (null)));
    }

    @Test
    public void water() {
        Potion potion = new Potion(WATER);
        Assert.assertEquals(0, potion.getLevel());
        Assert.assertFalse(potion.isSplash());
        Assert.assertFalse(potion.hasExtendedDuration());
        Assert.assertEquals(0, potion.toDamageValue());
    }

    @Test
    public void mundane() {
        Potion potion = new Potion(0);
        Assert.assertFalse(((potion.getType()) == (WATER)));
        Assert.assertFalse(((potion.toDamageValue()) == 0));
        Assert.assertEquals(8192, potion.toDamageValue());
        Potion potion2 = Potion.fromDamage(8192);
        Assert.assertEquals(potion, potion2);
        Assert.assertEquals(0, potion.getLevel());
    }

    @Test
    public void awkward() {
        Potion potion = new Potion(16);
        Assert.assertEquals(16, potion.getNameId());
        Assert.assertFalse(potion.isSplash());
        Assert.assertFalse(potion.hasExtendedDuration());
        Assert.assertNull(potion.getType());
        Assert.assertEquals(16, potion.toDamageValue());
    }

    private static final int EXTENDED_BIT = 64;

    private static final int SPLASH_BIT = 16384;
}

