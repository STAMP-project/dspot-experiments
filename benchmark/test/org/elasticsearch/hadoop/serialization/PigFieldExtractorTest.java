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


import FieldExtractor.NOT_FOUND;
import java.util.LinkedHashMap;
import java.util.Map;
import org.elasticsearch.hadoop.pig.PigTuple;
import org.junit.Assert;
import org.junit.Test;


public class PigFieldExtractorTest {
    @Test
    public void testMapFieldExtractorTopLevel() throws Exception {
        PigTuple tuple = createTuple("some string", createSchema("name:chararray"));
        Assert.assertEquals("some string", extract("name", tuple));
    }

    @Test
    public void testMapFieldExtractorNestedNotFound() throws Exception {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Assert.assertEquals(NOT_FOUND, extract("key", m));
    }

    @Test
    public void testMapWritableFieldExtractorNested() throws Exception {
        PigTuple nested = createTuple("found", createSchema("bar:chararray"));
        PigTuple top = createTuple(nested, createSchema("foo: (bar: chararray)"));
        Assert.assertEquals("found", extract("foo.bar", top));
    }
}

