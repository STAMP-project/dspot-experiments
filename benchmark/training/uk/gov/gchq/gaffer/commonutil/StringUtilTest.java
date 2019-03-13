/**
 * Copyright 2018-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.commonutil;


import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void nullIfEmptyStringTest() {
        Assert.assertEquals(null, StringUtil.nullIfEmpty(null));
        Assert.assertEquals(null, StringUtil.nullIfEmpty(""));
        Assert.assertEquals(" ", StringUtil.nullIfEmpty(" "));
        Assert.assertEquals("String", StringUtil.nullIfEmpty("String"));
        Assert.assertEquals(" string ", StringUtil.nullIfEmpty(" string "));
    }
}

