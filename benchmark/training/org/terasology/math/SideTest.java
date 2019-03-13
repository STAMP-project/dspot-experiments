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


import Direction.BACKWARD;
import Direction.DOWN;
import Direction.FORWARD;
import Direction.UP;
import Side.BACK;
import Side.BOTTOM;
import Side.FRONT;
import Side.LEFT;
import Side.RIGHT;
import Side.TOP;
import org.junit.Assert;
import org.junit.Test;

import static Side.FRONT;


/**
 *
 */
public class SideTest {
    @Test
    public void testSideInDirection() {
        for (Side side : Side.getAllSides()) {
            Assert.assertEquals(side, Side.inDirection(side.getVector3i().x, side.getVector3i().y, side.getVector3i().z));
        }
    }

    @Test
    public void testRelativeSides() {
        Side side = FRONT;
        Assert.assertEquals(LEFT, side.getRelativeSide(Direction.LEFT));
        Assert.assertEquals(RIGHT, side.getRelativeSide(Direction.RIGHT));
        Assert.assertEquals(TOP, side.getRelativeSide(UP));
        Assert.assertEquals(BOTTOM, side.getRelativeSide(DOWN));
        Assert.assertEquals(FRONT, side.getRelativeSide(FORWARD));
        Assert.assertEquals(BACK, side.getRelativeSide(BACKWARD));
    }
}

