package buildcraft.test.core;


import buildcraft.lib.misc.data.Box;
import buildcraft.test.TestHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class BoxTester {
    private static final BlockPos MIN = new BlockPos(1, 2, 3);

    private static final BlockPos MAX = new BlockPos(4, 5, 6);

    private static final BlockPos SIZE = new BlockPos(4, 4, 4);

    private static final BlockPos CENTER = new BlockPos(3, 4, 5);

    private static final Vec3d CENTER_EXACT = new Vec3d(3, 4, 5);

    @Test
    public void testMin() {
        Box box = new Box(BoxTester.MIN, BoxTester.MAX);
        Assert.assertEquals(BoxTester.MIN, box.min());
    }

    @Test
    public void testMax() {
        Box box = new Box(BoxTester.MIN, BoxTester.MAX);
        Assert.assertEquals(BoxTester.MAX, box.max());
    }

    @Test
    public void testSize() {
        Box box = new Box(BoxTester.MIN, BoxTester.MAX);
        Assert.assertEquals(BoxTester.SIZE, box.size());
    }

    @Test
    public void testCenter() {
        Box box = new Box(BoxTester.MIN, BoxTester.MAX);
        Assert.assertEquals(BoxTester.CENTER, box.center());
    }

    @Test
    public void testCenterExact() {
        Box box = new Box(BoxTester.MIN, BoxTester.MAX);
        TestHelper.assertVec3dEquals(BoxTester.CENTER_EXACT, box.centerExact());
    }

    @Test
    public void testIntersection1() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(1, 1, 1), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Assert.assertEquals(inter, box1.getIntersect(box2));
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }

    @Test
    public void testIntersection2() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Assert.assertEquals(inter, box1.getIntersect(box2));
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }

    @Test
    public void testIntersection3() {
        Box box1 = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(1, 1, 1));
        Assert.assertEquals(inter, box1.getIntersect(box2));
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }
}

