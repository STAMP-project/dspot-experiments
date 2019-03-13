/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.common.utils;


import org.apache.druid.java.util.common.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class StringUtilsTest {
    // copied from https://github.com/apache/incubator-druid/pull/2612
    public static final String[] TEST_STRINGS = new String[]{ "peach", "p?ch?", "p?che", "sin", "", "?", "C", "c", "?", "?", "G", "g", "?", "?", "I", "?", "?", "i", "O", "o", "?", "?", "S", "s", "?", "?", "U", "u", "?", "?", "?", "\ud841\udf0e", "\ud841\udf31", "\ud844\udc5c", "\ud84f\udcb7", "\ud860\udee2", "\ud867\udd98", "n\u0303", "n", "\ufb00", "ff", "?", "\u00c5", "\u212b" };

    @Test
    public void binaryLengthAsUTF8Test() {
        for (String string : StringUtilsTest.TEST_STRINGS) {
            Assert.assertEquals(StringUtils.toUtf8(string).length, StringUtils.estimatedBinaryLengthAsUTF8(string));
        }
    }

    @Test
    public void binaryLengthAsUTF8InvalidTest() {
        // we can fix this but looks trivial case, imho
        String invalid = "\ud841";// high only

        Assert.assertEquals(1, StringUtils.toUtf8(invalid).length);
        Assert.assertEquals(4, StringUtils.estimatedBinaryLengthAsUTF8(invalid));
        invalid = "\ud841\ud841";// high + high

        Assert.assertEquals(2, StringUtils.toUtf8(invalid).length);
        Assert.assertEquals(4, StringUtils.estimatedBinaryLengthAsUTF8(invalid));
        invalid = "\ud841P";// high + char

        Assert.assertEquals(2, StringUtils.toUtf8(invalid).length);
        Assert.assertEquals(4, StringUtils.estimatedBinaryLengthAsUTF8(invalid));
        invalid = "\udee2\ud841";// low + high

        Assert.assertEquals(2, StringUtils.toUtf8(invalid).length);
        Assert.assertEquals(4, StringUtils.estimatedBinaryLengthAsUTF8(invalid));
    }
}

