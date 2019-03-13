/**
 * Copyright (c) 2016, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.lang;


import com.cloudera.oryx.common.OryxTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ExecUtils}.
 */
public final class ExecUtilsTest extends OryxTest {
    @Test
    public void testDoInParallel() {
        Assert.assertEquals(1, ExecUtilsTest.maxActive(1, 1, false));
        Assert.assertEquals(1, ExecUtilsTest.maxActive(1, 1, true));
        Assert.assertEquals(1, ExecUtilsTest.maxActive(4, 1, false));
        Assert.assertEquals(1, ExecUtilsTest.maxActive(4, 1, true));
        int cores = Runtime.getRuntime().availableProcessors();
        Assert.assertEquals(Math.min(cores, 4), ExecUtilsTest.maxActive(4, 4, false));
        Assert.assertEquals(4, ExecUtilsTest.maxActive(4, 4, true));
    }

    @Test
    public void testCollectInParallel() {
        Assert.assertEquals(1, ExecUtilsTest.maxActiveCollect(1, 1, false));
        Assert.assertEquals(1, ExecUtilsTest.maxActiveCollect(1, 1, true));
        Assert.assertEquals(1, ExecUtilsTest.maxActiveCollect(4, 1, false));
        Assert.assertEquals(1, ExecUtilsTest.maxActiveCollect(4, 1, true));
        int cores = Runtime.getRuntime().availableProcessors();
        Assert.assertEquals(Math.min(cores, 4), ExecUtilsTest.maxActiveCollect(4, 4, false));
        Assert.assertEquals(4, ExecUtilsTest.maxActiveCollect(4, 4, true));
    }
}

