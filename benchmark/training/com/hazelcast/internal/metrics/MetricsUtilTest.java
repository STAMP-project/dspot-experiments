/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.metrics;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MetricsUtilTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test_escapeMetricKeyPart() {
        Assert.assertSame("", MetricsUtil.escapeMetricNamePart(""));
        Assert.assertSame("aaa", MetricsUtil.escapeMetricNamePart("aaa"));
        Assert.assertEquals("\\=", MetricsUtil.escapeMetricNamePart("="));
        Assert.assertEquals("\\,", MetricsUtil.escapeMetricNamePart(","));
        Assert.assertEquals("\\\\", MetricsUtil.escapeMetricNamePart("\\"));
        Assert.assertEquals("a\\=b", MetricsUtil.escapeMetricNamePart("a=b"));
        Assert.assertEquals("\\=b", MetricsUtil.escapeMetricNamePart("=b"));
        Assert.assertEquals("a\\=", MetricsUtil.escapeMetricNamePart("a="));
    }

    @Test
    public void test_parseMetricKey() {
        // empty list
        Assert.assertEquals(Collections.emptyList(), MetricsUtil.parseMetricName("[]"));
        // normal single tag
        Assert.assertEquals(Collections.singletonList(MetricsUtilTest.entry("tag", "value")), MetricsUtil.parseMetricName("[tag=value]"));
        // normal multiple tags
        Assert.assertEquals(Arrays.asList(MetricsUtilTest.entry("tag1", "value1"), MetricsUtilTest.entry("tag2", "value2")), MetricsUtil.parseMetricName("[tag1=value1,tag2=value2]"));
        // tag with escaped characters
        Assert.assertEquals(Collections.singletonList(MetricsUtilTest.entry("tag=", "value,")), MetricsUtil.parseMetricName("[tag\\==value\\,]"));
    }

    @Test
    public void test_parseMetricKey_fail_keyNotEnclosed() {
        exception.expectMessage("key not enclosed in []");
        MetricsUtil.parseMetricName("tag=value");
    }

    @Test
    public void test_parseMetricKey_fail_emptyTagName() {
        exception.expectMessage("empty tag name");
        MetricsUtil.parseMetricName("[=value]");
    }

    @Test
    public void test_parseMetricKey_fail_equalsSignAfterValue() {
        exception.expectMessage("equals sign not after tag");
        MetricsUtil.parseMetricName("[tag=value=]");
    }

    @Test
    public void test_parseMetricKey_fail_commaInTag1() {
        exception.expectMessage("comma in tag");
        MetricsUtil.parseMetricName("[,]");
    }

    @Test
    public void test_parseMetricKey_fail_backslashAtTheEnd1() {
        exception.expectMessage("backslash at the end");
        MetricsUtil.parseMetricName("[\\]");
    }

    @Test
    public void test_parseMetricKey_fail_backslashAtTheEnd2() {
        exception.expectMessage("backslash at the end");
        MetricsUtil.parseMetricName("[tag=value\\]");
    }

    @Test
    public void test_parseMetricKey_fail_unfinishedTagAtTheEnd() {
        exception.expectMessage("unfinished tag at the end");
        MetricsUtil.parseMetricName("[tag=value,tag2]");
    }
}

