package org.rajawali3d;


import Vector3.NEG_X;
import Vector3.NEG_Y;
import Vector3.NEG_Z;
import Vector3.X;
import Vector3.Y;
import Vector3.Z;
import WorldParameters.FORWARD_AXIS;
import WorldParameters.NEG_FORWARD_AXIS;
import WorldParameters.NEG_RIGHT_AXIS;
import WorldParameters.NEG_UP_AXIS;
import WorldParameters.RIGHT_AXIS;
import WorldParameters.UP_AXIS;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class WorldParametersTest {
    @Test
    public void testConstructor() {
        Assert.assertNotNull(new WorldParameters());
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Test
    public void testSetWorldAxes() {
        WorldParameters.setWorldAxes(Y, Z, X);
        Assert.assertEquals(FORWARD_AXIS, X);
        Assert.assertEquals(NEG_FORWARD_AXIS, NEG_X);
        Assert.assertEquals(RIGHT_AXIS, Y);
        Assert.assertEquals(NEG_RIGHT_AXIS, NEG_Y);
        Assert.assertEquals(UP_AXIS, Z);
        Assert.assertEquals(NEG_UP_AXIS, NEG_Z);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Test(expected = IllegalArgumentException.class)
    public void testSetWorldAxesNotOrthogonal() {
        WorldParameters.setWorldAxes(Y, Y, X);
    }
}

