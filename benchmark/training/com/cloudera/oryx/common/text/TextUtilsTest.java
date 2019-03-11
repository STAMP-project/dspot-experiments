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
package com.cloudera.oryx.common.text;


import com.cloudera.oryx.common.OryxTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link TextUtils}.
 */
public final class TextUtilsTest extends OryxTest {
    @Test
    public void testParseJSON() throws Exception {
        Assert.assertArrayEquals(new String[]{ "a", "1", "foo" }, TextUtils.parseJSONArray("[\"a\",\"1\",\"foo\"]"));
        Assert.assertArrayEquals(new String[]{ "a", "1", "foo", "" }, TextUtils.parseJSONArray("[\"a\",\"1\",\"foo\",\"\"]"));
        Assert.assertArrayEquals(new String[]{ "2.3" }, TextUtils.parseJSONArray("[\"2.3\"]"));
        Assert.assertArrayEquals(new String[]{  }, TextUtils.parseJSONArray("[]"));
    }

    @Test
    public void testParseDelimited() throws Exception {
        Assert.assertArrayEquals(new String[]{ "a", "1", "foo" }, TextUtils.parseDelimited("a,1,foo", ','));
        Assert.assertArrayEquals(new String[]{ "a", "1", "foo", "" }, TextUtils.parseDelimited("a,1,foo,", ','));
        Assert.assertArrayEquals(new String[]{ "2.3" }, TextUtils.parseDelimited("2.3", ','));
        Assert.assertArrayEquals(new String[]{ "\"a\"" }, TextUtils.parseDelimited("\"\"\"a\"\"\"", ','));
        Assert.assertArrayEquals(new String[]{ "\"", "\"\"" }, TextUtils.parseDelimited("\"\"\"\" \"\"\"\"\"\"", ' '));
        // Different from JSON, sort of:
        Assert.assertArrayEquals(new String[]{ "" }, TextUtils.parseDelimited("", ','));
        Assert.assertArrayEquals(new String[]{ "a", "1,", ",foo" }, TextUtils.parseDelimited("a\t1,\t,foo", '\t'));
        Assert.assertArrayEquals(new String[]{ "a", "1", "foo", "" }, TextUtils.parseDelimited("a 1 foo ", ' '));
        Assert.assertArrayEquals(new String[]{ "-1.0", "a\" \"b" }, TextUtils.parseDelimited("-1.0 a\"\\ \"b", ' '));
        Assert.assertArrayEquals(new String[]{ "-1.0", "a\"b\"c" }, TextUtils.parseDelimited("-1.0 \"a\\\"b\\\"c\"", ' '));
    }

    @Test
    public void testParsePMMLDelimited() {
        Assert.assertArrayEquals(new String[]{ "1", "22", "3" }, TextUtils.parsePMMLDelimited("1 22 3"));
        Assert.assertArrayEquals(new String[]{ "ab", "a b", "with \"quotes\" " }, TextUtils.parsePMMLDelimited("ab  \"a b\"   \"with \\\"quotes\\\" \" "));
        Assert.assertArrayEquals(new String[]{ "\" \"" }, TextUtils.parsePMMLDelimited("\"\\\" \\\"\""));
        Assert.assertArrayEquals(new String[]{ " c\" d \"e ", " c\" d \"e " }, TextUtils.parsePMMLDelimited(" \" c\\\" d \\\"e \" \" c\\\" d \\\"e \" "));
    }

    @Test
    public void testJoinDelimited() {
        Assert.assertEquals("1,2,3", TextUtils.joinDelimited(Arrays.asList("1", "2", "3"), ','));
        Assert.assertEquals("\"a,b\"", TextUtils.joinDelimited(Arrays.asList("a,b"), ','));
        Assert.assertEquals("\"\"\"a\"\"\"", TextUtils.joinDelimited(Arrays.asList("\"a\""), ','));
        Assert.assertEquals("1 2 3", TextUtils.joinDelimited(Arrays.asList("1", "2", "3"), ' '));
        Assert.assertEquals("\"1 \" \"2 \" 3", TextUtils.joinDelimited(Arrays.asList("1 ", "2 ", "3"), ' '));
        Assert.assertEquals("\"\"\"a\"\"\"", TextUtils.joinDelimited(Arrays.asList("\"a\""), ' '));
        Assert.assertEquals("\"\"\"\" \"\"\"\"\"\"", TextUtils.joinDelimited(Arrays.asList("\"", "\"\""), ' '));
        Assert.assertEquals("", TextUtils.joinDelimited(Collections.emptyList(), '\t'));
    }

    @Test
    public void testJoinPMMLDelimited() {
        Assert.assertEquals("ab \"a b\" \"with \\\"quotes\\\" \"", TextUtils.joinPMMLDelimited(Arrays.asList("ab", "a b", "with \"quotes\" ")));
        Assert.assertEquals("1 22 3", TextUtils.joinPMMLDelimited(Arrays.asList("1", "22", "3")));
        Assert.assertEquals("\" c\\\" d \\\"e \" \" c\\\" d \\\"e \"", TextUtils.joinPMMLDelimited(Arrays.asList(" c\" d \"e ", " c\" d \"e ")));
    }

    @Test
    public void testJoinPMMLDelimitedNumbers() {
        Assert.assertEquals("-1.0 2.01 3.5", TextUtils.joinPMMLDelimitedNumbers(Arrays.asList((-1.0), 2.01, 3.5)));
    }

    @Test
    public void testJoinJSON() {
        Assert.assertEquals("[\"1\",\"2\",\"3\"]", TextUtils.joinJSON(Arrays.asList("1", "2", "3")));
        Assert.assertEquals("[\"1 \",\"2 \",\"3\"]", TextUtils.joinJSON(Arrays.asList("1 ", "2 ", "3")));
        Assert.assertEquals("[]", TextUtils.joinJSON(Collections.emptyList()));
    }

    @Test
    public void testJSONList() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        list.add(2);
        Assert.assertEquals("[\"A\",[\"foo\",2],\"B\"]", TextUtils.joinJSON(Arrays.asList("A", list, "B")));
    }

    @Test
    public void testJSONMap() {
        Map<Object, Object> map = new HashMap<>();
        map.put(1, "bar");
        map.put("foo", 2);
        Assert.assertEquals("[\"A\",{\"1\":\"bar\",\"foo\":2},\"B\"]", TextUtils.joinJSON(Arrays.asList("A", map, "B")));
    }

    @Test
    public void testReadJSON() {
        Assert.assertEquals(3, TextUtils.readJSON("3", Integer.class).intValue());
        Assert.assertEquals(Arrays.asList("foo", "bar"), TextUtils.readJSON("[\"foo\", \"bar\"]", List.class));
        OryxTest.assertArrayEquals(new float[]{ 1.0F, 2.0F }, TextUtils.readJSON("[1,2]", float[].class));
    }

    @Test
    public void testConvertViaJSON() {
        Assert.assertEquals(3, TextUtils.convertViaJSON("3", Long.class).longValue());
        OryxTest.assertArrayEquals(new float[]{ 1.0F, 2.0F }, TextUtils.convertViaJSON(new double[]{ 1.0, 2.0 }, float[].class));
    }
}

