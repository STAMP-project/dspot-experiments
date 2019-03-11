/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization;


import ParsingUtils.NOT_FOUND;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonValuePathTest {
    private Parser parser;

    @Test
    public void testFirstLevel() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "firstName", "foo", "age");
        Assert.assertEquals(3, vals.size());
        Assert.assertEquals("John", vals.get(0));
        Assert.assertSame(NOT_FOUND, vals.get(1));
        Assert.assertEquals(25, vals.get(2));
    }

    @Test
    public void testSecondLevel() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "address.state", "address.foo", "address.building.floors", "address.building.bar");
        Assert.assertEquals(4, vals.size());
        Assert.assertEquals("NY", vals.get(0));
        Assert.assertSame(NOT_FOUND, vals.get(1));
        Assert.assertEquals(10, vals.get(2));
        Assert.assertSame(NOT_FOUND, vals.get(3));
    }

    @Test
    public void testRichObject() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "address");
        Assert.assertEquals(1, vals.size());
        Assert.assertThat(vals.get(0).toString(), CoreMatchers.containsString("floors"));
    }

    @Test
    public void testMultipleNestedMatches1() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "firstName");
        Assert.assertEquals(1, vals.size());
        Assert.assertThat(vals.get(0).toString(), CoreMatchers.containsString("John"));
    }

    @Test
    public void testMultipleNestedMatches2() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "age");
        Assert.assertEquals(1, vals.size());
        Assert.assertThat(((Integer) (vals.get(0))), Matchers.is(Integer.valueOf(25)));
    }

    @Test
    public void testRichObjectNested() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "address.building");
        Assert.assertEquals(1, vals.size());
        Assert.assertThat(vals.get(0).toString(), CoreMatchers.containsString("floors"));
    }

    @Test
    public void testCorrectLevelMatched() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "state");
        Assert.assertEquals(1, vals.size());
        Assert.assertThat(vals.get(0).toString(), CoreMatchers.containsString("CA"));
    }

    @Test
    public void testSmallerMixedLevels() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "firstName", "address.state", "state");
        Assert.assertEquals(3, vals.size());
        Assert.assertEquals("John", vals.get(0));
        Assert.assertEquals("NY", vals.get(1));
        Assert.assertEquals("CA", vals.get(2));
    }

    @Test
    public void testMixedLevels() throws Exception {
        List<Object> vals = ParsingUtils.values(parser, "firstName", "address.building.floors", "address.decor.walls", "zzz");
        Assert.assertEquals(4, vals.size());
        Assert.assertEquals("John", vals.get(0));
        Assert.assertEquals(10, vals.get(1));
        Assert.assertEquals("white", vals.get(2));
        Assert.assertEquals("end", vals.get(3));
    }
}

