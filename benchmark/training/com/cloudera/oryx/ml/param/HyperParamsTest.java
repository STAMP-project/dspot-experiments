/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
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
package com.cloudera.oryx.ml.param;


import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public final class HyperParamsTest extends OryxTest {
    @Test
    public void testFixedContinuous() {
        HyperParamsTest.doTestContinuous(HyperParams.fixed(3.0), 1, 3.0);
        HyperParamsTest.doTestContinuous(HyperParams.fixed(3.0), 3, 3.0);
    }

    @Test
    public void testContinuousRange() {
        HyperParamsTest.doTestContinuous(HyperParams.range(3.0, 5.0), 1, 4.0);
        HyperParamsTest.doTestContinuous(HyperParams.range(3.0, 5.0), 2, 3.0, 5.0);
        HyperParamsTest.doTestContinuous(HyperParams.range(3.0, 5.0), 4, 3.0, 3.6666666666666665, 4.333333333333333, 5.0);
        HyperParamsTest.doTestContinuous(HyperParams.range(0.0, 1.0), 3, 0.0, 0.5, 1.0);
        HyperParamsTest.doTestContinuous(HyperParams.range((-1.0), 1.0), 5, (-1.0), (-0.5), 0.0, 0.5, 1.0);
        HyperParamsTest.doTestContinuous(HyperParams.range((-1.0), 1.0), 4, (-1.0), (-0.3333333333333333), 0.3333333333333333, 1.0);
    }

    @Test
    public void testAroundContinuous() {
        HyperParamsTest.doTestContinuous(HyperParams.around((-3.0), 0.1), 1, (-3.0));
        HyperParamsTest.doTestContinuous(HyperParams.around((-3.0), 0.1), 2, (-3.05), (-2.95));
        HyperParamsTest.doTestContinuous(HyperParams.around((-3.0), 0.1), 3, (-3.1), (-3.0), (-2.9));
    }

    @Test
    public void testFixedDiscrete() {
        HyperParamsTest.doTest(HyperParams.fixed(3), 1, Collections.singletonList(3));
        HyperParamsTest.doTest(HyperParams.fixed(3), 3, Collections.singletonList(3));
    }

    @Test
    public void testDiscreteRange() {
        HyperParamsTest.doTest(HyperParams.range(3, 4), 1, Collections.singletonList(3));
        HyperParamsTest.doTest(HyperParams.range(3, 5), 1, Collections.singletonList(4));
        HyperParamsTest.doTest(HyperParams.range(3, 5), 2, Arrays.asList(3, 5));
        HyperParamsTest.doTest(HyperParams.range(3, 5), 3, Arrays.asList(3, 4, 5));
        HyperParamsTest.doTest(HyperParams.range(3, 5), 4, Arrays.asList(3, 4, 5));
        HyperParamsTest.doTest(HyperParams.range(0, 1), 3, Arrays.asList(0, 1));
        HyperParamsTest.doTest(HyperParams.range((-1), 1), 5, Arrays.asList((-1), 0, 1));
        HyperParamsTest.doTest(HyperParams.range(0, 10), 3, Arrays.asList(0, 5, 10));
    }

    @Test
    public void testAroundDiscrete() {
        HyperParamsTest.doTest(HyperParams.around((-3), 1), 1, Collections.singletonList((-3)));
        HyperParamsTest.doTest(HyperParams.around((-3), 1), 2, Arrays.asList((-3), (-2)));
        HyperParamsTest.doTest(HyperParams.around((-3), 1), 3, Arrays.asList((-4), (-3), (-2)));
        HyperParamsTest.doTest(HyperParams.around((-3), 10), 2, Arrays.asList((-8), 2));
        HyperParamsTest.doTest(HyperParams.around((-3), 10), 3, Arrays.asList((-13), (-3), 7));
    }

    @Test
    public void testUnordered() {
        HyperParamsTest.doTest(HyperParams.unorderedFromValues(Arrays.asList("foo", "bar")), 1, Collections.singletonList("foo"));
        HyperParamsTest.doTest(HyperParams.unorderedFromValues(Arrays.asList("foo", "bar")), 2, Arrays.asList("foo", "bar"));
        HyperParamsTest.doTest(HyperParams.unorderedFromValues(Arrays.asList("foo", "bar")), 3, Arrays.asList("foo", "bar"));
    }

    @Test
    public void testConfig() {
        Map<String, Object> overlay = new HashMap<>();
        overlay.put("a", 1);
        overlay.put("b", 2.7);
        overlay.put("c", "[3,4]");
        overlay.put("d", "[5.3,6.6]");
        Config config = ConfigUtils.overlayOn(overlay, ConfigUtils.getDefault());
        HyperParamsTest.doTest(HyperParams.fromConfig(config, "a"), 1, Collections.singletonList(1));
        HyperParamsTest.doTest(HyperParams.fromConfig(config, "b"), 1, Collections.singletonList(2.7));
        HyperParamsTest.doTest(HyperParams.fromConfig(config, "c"), 2, Arrays.asList(3, 4));
        HyperParamsTest.doTest(HyperParams.fromConfig(config, "d"), 2, Arrays.asList(5.3, 6.6));
    }
}

