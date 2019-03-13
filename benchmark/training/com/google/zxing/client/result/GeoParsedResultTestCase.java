/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.client.result;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link GeoParsedResult}.
 *
 * @author Sean Owen
 */
public final class GeoParsedResultTestCase extends Assert {
    private static final double EPSILON = 1.0E-10;

    @Test
    public void testGeo() {
        GeoParsedResultTestCase.doTest("geo:1,2", 1.0, 2.0, 0.0, null, "geo:1.0,2.0");
        GeoParsedResultTestCase.doTest("geo:80.33,-32.3344,3.35", 80.33, (-32.3344), 3.35, null, null);
        GeoParsedResultTestCase.doTest("geo:-20.33,132.3344,0.01", (-20.33), 132.3344, 0.01, null, null);
        GeoParsedResultTestCase.doTest("geo:-20.33,132.3344,0.01?q=foobar", (-20.33), 132.3344, 0.01, "q=foobar", null);
        GeoParsedResultTestCase.doTest("GEO:-20.33,132.3344,0.01?q=foobar", (-20.33), 132.3344, 0.01, "q=foobar", null);
    }
}

