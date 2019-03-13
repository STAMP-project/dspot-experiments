/**
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.world.time;


import org.junit.Assert;
import org.junit.Test;

import static WorldTime.TICK_EVENTS_PER_DAY;
import static WorldTime.TICK_EVENT_RATE;


/**
 * Tests the world-time related classes
 */
public class WorldTimeTest {
    @Test
    public void testEventMatchesDaily() {
        float[] fracs = new float[]{ 0.4F, 0.405F, 0.395F, 0.11111F, 0.3950001F, 0.400001F };
        int[] hits = new int[fracs.length];
        long days = 3;
        for (int tick = 0; tick < ((TICK_EVENTS_PER_DAY) * days); tick++) {
            WorldTimeEvent event = new WorldTimeEvent((tick * (TICK_EVENT_RATE)));
            for (int i = 0; i < (fracs.length); i++) {
                if (event.matchesDaily(fracs[i])) {
                    (hits[i])++;
                }
            }
        }
        for (int i = 0; i < (fracs.length); i++) {
            Assert.assertEquals((((("Fraction " + (fracs[i])) + " was hits ") + (hits[i])) + " times"), days, hits[i]);
        }
    }
}

