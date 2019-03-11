/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.trevni;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestUtil {
    private static long seed;

    private static boolean seedSet;

    @Test
    public void testRandomLength() {
        long total = 0;
        int count = 1024 * 1024;
        int min = Short.MAX_VALUE;
        int max = 0;
        Random r = TestUtil.createRandom();
        for (int i = 0; i < count; i++) {
            int length = TestUtil.randomLength(r);
            if (min > length)
                min = length;

            if (max < length)
                max = length;

            total += length;
        }
        Assert.assertEquals(0, min);
        Assert.assertTrue((max > (1024 * 32)));
        float average = total / ((float) (count));
        Assert.assertTrue((average > 16.0F));
        Assert.assertTrue((average < 64.0F));
    }
}

