/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler.builder.generator;


import org.drools.modelcompiler.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void test() {
        Assert.assertEquals("__123stella", StringUtil.toId("123stella"));
        Assert.assertEquals("__123__stella", StringUtil.toId("123_stella"));
        Assert.assertEquals("__stella", StringUtil.toId("_stella"));
        Assert.assertEquals("__stella__123", StringUtil.toId("_stella_123"));
        Assert.assertEquals("my_32stella", StringUtil.toId("my stella"));
        Assert.assertEquals("$tella", StringUtil.toId("$tella"));
        Assert.assertEquals("$tella_40123_41", StringUtil.toId("$tella(123)"));
        Assert.assertEquals("my_45stella", StringUtil.toId("my-stella"));
        Assert.assertEquals("my_43stella", StringUtil.toId("my+stella"));
        Assert.assertEquals("o_39stella", StringUtil.toId("o'stella"));
        Assert.assertEquals("stella_38you", StringUtil.toId("stella&you"));
        Assert.assertEquals("stella_32_38_32Co_46", StringUtil.toId("stella & Co."));
    }
}

