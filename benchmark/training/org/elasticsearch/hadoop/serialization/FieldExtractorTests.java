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
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.elasticsearch.hadoop.serialization.bulk.RawJson;
import org.elasticsearch.hadoop.serialization.field.ConstantFieldExtractor;
import org.elasticsearch.hadoop.serialization.field.MapWritableFieldExtractor;
import org.junit.Assert;
import org.junit.Test;


public class FieldExtractorTests {
    @Test
    public void testConstantFieldExtractorWithString() throws Exception {
        ConstantFieldExtractor cfe = new ConstantFieldExtractor();
        Assert.assertEquals(new RawJson("\"fixed\""), extract(cfe, "<fixed>", new Object()));
    }

    @Test
    public void testConstantFieldExtractorWithNumber() throws Exception {
        ConstantFieldExtractor cfe = new ConstantFieldExtractor();
        Assert.assertEquals(new RawJson("123"), extract(cfe, "<123>", new Object()));
    }

    @Test
    public void testConstantFieldExtractorNotFound() throws Exception {
        ConstantFieldExtractor cfe = new ConstantFieldExtractor();
        Assert.assertEquals(NOT_FOUND, extract(cfe, "non-existing", new Object()));
    }

    @Test
    public void testMapFieldExtractorTopLevel() throws Exception {
        ConstantFieldExtractor cfe = new MapFieldExtractor();
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("key", "value");
        Assert.assertEquals("value", extract(cfe, "key", m));
    }

    @Test
    public void testMapFieldExtractorNestedNotFound() throws Exception {
        ConstantFieldExtractor cfe = new MapFieldExtractor();
        Map<String, String> m = new LinkedHashMap<String, String>();
        Assert.assertEquals(NOT_FOUND, extract(cfe, "key", m));
    }

    @Test
    public void testMapWritableFieldExtractorNested() throws Exception {
        ConstantFieldExtractor cfe = new MapFieldExtractor();
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        Map<String, String> nested = new LinkedHashMap<String, String>();
        nested.put("bar", "found");
        m.put("foo", nested);
        Assert.assertEquals("found", extract(cfe, "foo.bar", m));
    }

    @Test
    public void testMapWritableFieldExtractorTopLevel() throws Exception {
        ConstantFieldExtractor cfe = new MapWritableFieldExtractor();
        Map<Writable, Writable> m = new MapWritable();
        m.put(new Text("key"), new Text("value"));
        Assert.assertEquals(new Text("value"), extract(cfe, "key", m));
    }

    @Test
    public void testMapWritableFieldExtractorNestedNotFound() throws Exception {
        ConstantFieldExtractor cfe = new MapWritableFieldExtractor();
        Map<Writable, Writable> m = new MapWritable();
        Assert.assertEquals(NOT_FOUND, extract(cfe, "key", m));
    }

    @Test
    public void testMapFieldExtractorNested() throws Exception {
        ConstantFieldExtractor cfe = new MapWritableFieldExtractor();
        Map<Writable, Writable> m = new MapWritable();
        MapWritable nested = new MapWritable();
        nested.put(new Text("bar"), new Text("found"));
        m.put(new Text("foo"), nested);
        Assert.assertEquals(new Text("found"), extract(cfe, "foo.bar", m));
    }
}

