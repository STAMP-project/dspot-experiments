package buildcraft.test.lib.misc.data;


import buildcraft.lib.misc.data.AverageLong;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Assert;
import org.junit.Test;


public class AverageLongTester {
    @Test
    public void testNbt() {
        final long val = 62129658859368976L;
        AverageLong avg = new AverageLong(5);
        AverageLong avg2 = new AverageLong(5);
        avg.tick(val);
        avg.tick(val);
        avg.tick(val);
        avg.tick(val);
        avg.tick(val);
        avg.tick(val);
        NBTTagCompound nbt = new NBTTagCompound();
        avg.writeToNbt(nbt, "test");
        avg2.readFromNbt(nbt, "test");
        Assert.assertEquals(avg.getAverageLong(), avg2.getAverageLong());
    }
}

