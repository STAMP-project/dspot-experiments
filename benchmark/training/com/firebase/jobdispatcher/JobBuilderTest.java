/**
 * Copyright 2016 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import Constraint.DEVICE_CHARGING;
import Constraint.ON_UNMETERED_NETWORK;
import Job.Builder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static Constraint.DEVICE_CHARGING;
import static Constraint.ON_UNMETERED_NETWORK;
import static Lifetime.FOREVER;
import static Lifetime.UNTIL_NEXT_BOOT;


/**
 * Tests for the {@link Job.Builder} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class JobBuilderTest {
    private static final int[] ALL_LIFETIMES = new int[]{ UNTIL_NEXT_BOOT, FOREVER };

    private Builder builder;

    @Test
    public void testAddConstraints() {
        builder.setConstraints().addConstraint(DEVICE_CHARGING).addConstraint(ON_UNMETERED_NETWORK);
        int[] expected = new int[]{ DEVICE_CHARGING, ON_UNMETERED_NETWORK };
        Assert.assertEquals(Constraint.compact(expected), Constraint.compact(builder.getConstraints()));
    }

    @Test
    public void testSetLifetime() {
        for (int lifetime : JobBuilderTest.ALL_LIFETIMES) {
            builder.setLifetime(lifetime);
            Assert.assertEquals(lifetime, builder.getLifetime());
        }
    }

    @Test
    public void testSetShouldReplaceCurrent() {
        for (boolean replace : new boolean[]{ true, false }) {
            builder.setReplaceCurrent(replace);
            Assert.assertEquals(replace, builder.shouldReplaceCurrent());
        }
    }
}

