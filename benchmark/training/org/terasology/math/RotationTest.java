/**
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.math;


import Side.FRONT;
import Side.LEFT;
import Side.RIGHT;
import Side.TOP;
import Yaw.CLOCKWISE_180;
import Yaw.CLOCKWISE_90;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;


/**
 *
 */
public class RotationTest {
    @Test
    public void testRotateSideNone() {
        Assert.assertEquals(LEFT, Rotation.none().rotate(LEFT));
    }

    @Test
    public void testRotateSideYaw() {
        Rotation rotation = Rotation.rotate(CLOCKWISE_90);
        Quat4f rot = rotation.getQuat4f();
        Vector3f dir = rot.rotate(FRONT.toDirection().getVector3f(), new Vector3f());
        Assert.assertEquals(Direction.inDirection(dir).toSide(), rotation.rotate(FRONT));
        Assert.assertEquals(LEFT, Rotation.rotate(CLOCKWISE_90).rotate(FRONT));
        Assert.assertEquals(TOP, Rotation.rotate(CLOCKWISE_90).rotate(TOP));
    }

    @Test
    public void testRotateSidePitch() {
        Rotation rotation = Rotation.rotate(Pitch.CLOCKWISE_90);
        Quat4f rot = rotation.getQuat4f();
        Vector3f dir = rot.rotate(FRONT.toDirection().getVector3f(), new Vector3f());
        Assert.assertEquals(Direction.inDirection(dir).toSide(), rotation.rotate(FRONT));
        Assert.assertEquals(TOP, Rotation.rotate(Pitch.CLOCKWISE_90).rotate(FRONT));
        Assert.assertEquals(RIGHT, Rotation.rotate(Pitch.CLOCKWISE_90).rotate(RIGHT));
    }

    @Test
    public void testRotateSideRoll() {
        Rotation rotation = Rotation.rotate(Roll.CLOCKWISE_90);
        Quat4f rot = rotation.getQuat4f();
        Vector3f dir = rot.rotate(TOP.toDirection().getVector3f(), new Vector3f());
        Assert.assertEquals(Direction.inDirection(dir).toSide(), rotation.rotate(TOP));
        Assert.assertEquals(LEFT, Rotation.rotate(Roll.CLOCKWISE_90).rotate(TOP));
        Assert.assertEquals(FRONT, Rotation.rotate(Roll.CLOCKWISE_90).rotate(FRONT));
    }

    @Test
    public void testRotateMixed() {
        Rotation rotation = Rotation.rotate(CLOCKWISE_180, Pitch.CLOCKWISE_90, Roll.CLOCKWISE_90);
        Quat4f rot = rotation.getQuat4f();
        Vector3f dir = rot.rotate(FRONT.toDirection().getVector3f(), new Vector3f());
        Assert.assertEquals(Direction.inDirection(dir).toSide(), rotation.rotate(FRONT));
    }

    @Test
    public void testAllRotations() {
        Assert.assertEquals(24, Iterables.size(Rotation.values()));
        Assert.assertEquals(64, Iterables.size(Rotation.allValues()));
    }

    @Test
    public void testReverseRotation() {
        for (Rotation rotation : Rotation.allValues()) {
            Rotation reverseRotation = Rotation.findReverse(rotation);
            for (Side side : Side.getAllSides()) {
                Assert.assertEquals(side, reverseRotation.rotate(rotation.rotate(side)));
            }
        }
    }
}

