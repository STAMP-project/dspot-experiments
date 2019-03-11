package org.bukkit;


import org.bukkit.util.Vector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class LocationTest {
    private static final double ? = 1.0 / 1000000;

    /**
     * <pre>
     * a? + b? = c?, a = b
     * => 2?(a?) = 2?(b?) = c?, c = 1
     * => 2?(a?) = 1
     * => a? = 1/2
     * => a = ?(1/2) ?
     * </pre>
     */
    private static final double HALF_UNIT = Math.sqrt((1 / 2.0F));

    /**
     * <pre>
     * a? + b? = c?, c = ?(1/2)
     * => a? + b? = ?(1/2)?, a = b
     * => 2?(a?) = 2?(b?) = 1/2
     * => a? = 1/4
     * => a = ?(1/4) ?
     * </pre>
     */
    private static final double HALF_HALF_UNIT = Math.sqrt((1 / 4.0F));

    @Parameterized.Parameter(0)
    public String nane;

    @Parameterized.Parameter(1)
    public double x;

    @Parameterized.Parameter(2)
    public double y;

    @Parameterized.Parameter(3)
    public double z;

    @Parameterized.Parameter(4)
    public float yaw;

    @Parameterized.Parameter(5)
    public float pitch;

    @Test
    public void testExpectedPitchYaw() {
        Location location = LocationTest.getEmptyLocation().setDirection(getVector());
        Assert.assertThat(((double) (location.getYaw())), is(closeTo(yaw, LocationTest.?)));
        Assert.assertThat(((double) (location.getPitch())), is(closeTo(pitch, LocationTest.?)));
    }

    @Test
    public void testExpectedXYZ() {
        Vector vector = getLocation().getDirection();
        Assert.assertThat(vector.getX(), is(closeTo(x, LocationTest.?)));
        Assert.assertThat(vector.getY(), is(closeTo(y, LocationTest.?)));
        Assert.assertThat(vector.getZ(), is(closeTo(z, LocationTest.?)));
    }
}

