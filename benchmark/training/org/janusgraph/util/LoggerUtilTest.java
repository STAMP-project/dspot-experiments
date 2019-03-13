/**
 * Copyright 2017 JanusGraph Authors
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
package org.janusgraph.util;


import org.janusgraph.util.system.LoggerUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LoggerUtilTest {
    @Test
    public void testSanitizeAndLaunder() {
        String[] str = new String[]{ "asdf3", "", "f232rdfjdhjkhfafb-38`138", "8947(*&#$80124n", " _+%", "?sdf30sn?+p", null };
        for (String s : str) {
            Assertions.assertEquals(s, LoggerUtil.sanitizeAndLaunder(s));
        }
        Assertions.assertEquals("asdf%0A3", LoggerUtil.sanitizeAndLaunder("asdf\n3"));
        Assertions.assertEquals("asdf%0A%0A%0A3", LoggerUtil.sanitizeAndLaunder("asdf\n\r\n3"));
    }
}

