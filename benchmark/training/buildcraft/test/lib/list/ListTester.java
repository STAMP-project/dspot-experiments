package buildcraft.test.lib.list;


import Type.TYPE;
import buildcraft.lib.list.ListMatchHandlerTools;
import buildcraft.test.VanillaSetupBaseTester;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.Assert;
import org.junit.Test;


public class ListTester extends VanillaSetupBaseTester {
    @Test
    public void testTools() {
        ListMatchHandlerTools matcher = new ListMatchHandlerTools();
        ItemStack woodenAxe = new ItemStack(Items.WOODEN_AXE);
        ItemStack ironAxe = new ItemStack(Items.IRON_AXE);
        ItemStack woodenShovel = new ItemStack(Items.WOODEN_SHOVEL);
        ItemStack woodenAxeDamaged = new ItemStack(Items.WOODEN_AXE);
        woodenAxeDamaged.setItemDamage(26);
        ItemStack apple = new ItemStack(Items.APPLE);
        Assert.assertTrue(matcher.isValidSource(TYPE, woodenAxe));
        Assert.assertTrue(matcher.isValidSource(TYPE, woodenAxeDamaged));
        Assert.assertFalse(matcher.isValidSource(TYPE, apple));
        Assert.assertTrue(matcher.matches(TYPE, woodenAxe, ironAxe, false));
        Assert.assertTrue(matcher.matches(TYPE, woodenAxe, woodenAxeDamaged, false));
        Assert.assertFalse(matcher.matches(TYPE, woodenAxe, woodenShovel, false));
        Assert.assertFalse(matcher.matches(TYPE, woodenAxe, apple, false));
    }
}

