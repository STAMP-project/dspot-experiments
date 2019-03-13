package buildcraft.test.lib.fluid;


import buildcraft.lib.fluid.Tank;
import buildcraft.lib.fluid.TankManager;
import buildcraft.test.VanillaSetupBaseTester;
import net.minecraftforge.fluids.FluidRegistry;
import org.junit.Assert;
import org.junit.Test;


public class TankManagerTester extends VanillaSetupBaseTester {
    @Test
    public void testSimpleMoving() {
        TankManager manager = new TankManager();
        manager.add(new Tank("tank_1", 3, null));
        Assert.assertEquals(2, manager.fill(new net.minecraftforge.fluids.FluidStack(FluidRegistry.WATER, 2), true));
        Assert.assertEquals(1, manager.fill(new net.minecraftforge.fluids.FluidStack(FluidRegistry.WATER, 2), true));
        Assert.assertTrue(new net.minecraftforge.fluids.FluidStack(FluidRegistry.WATER, 3).isFluidStackIdentical(manager.drain(new net.minecraftforge.fluids.FluidStack(FluidRegistry.WATER, 5), true)));
        manager.add(new Tank("tank_2", 3, null));
        Assert.assertEquals(5, manager.fill(new net.minecraftforge.fluids.FluidStack(FluidRegistry.LAVA, 5), true));
        Assert.assertTrue(new net.minecraftforge.fluids.FluidStack(FluidRegistry.LAVA, 4).isFluidStackIdentical(manager.drain(new net.minecraftforge.fluids.FluidStack(FluidRegistry.LAVA, 4), true)));
        Assert.assertEquals(1, manager.get(1).getFluid().amount);
    }
}

