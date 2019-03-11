/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util;


import java.util.Random;
import junit.framework.TestCase;


/**
 * Tests for basic functinality of TreeMaps
 */
public class OldAndroidTreeMapTest extends TestCase {
    private Random mRandom = new Random(1);

    private static final boolean SPEW = false;

    public void testTreeMap() {
        for (int i = 0; i < 10; i++) {
            if (OldAndroidTreeMapTest.SPEW)
                System.out.println(("Running doTest cycle #" + (i + 1)));

            doTest();
        }
    }
}

