/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
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
package io.bootique.config.jackson;


import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class PathSegmentTest {
    @Test
    public void testLastPathComponent_Value() {
        JsonNode node = YamlReader.read("a");
        Optional<PathSegment<?>> last = PathSegment.create(node, "").lastPathComponent();
        Assert.assertNotNull(last.get());
        Assert.assertEquals("a", last.get().getNode().asText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLastPathComponent_Value_PastEnd() {
        JsonNode node = YamlReader.read("a");
        PathSegment.create(node, "x").lastPathComponent();
    }

    @Test
    public void testLastPathComponent_Root() {
        JsonNode node = YamlReader.read("a: b\nc: d");
        Optional<PathSegment<?>> last = PathSegment.create(node, "").lastPathComponent();
        Assert.assertNotNull(last.get());
        Assert.assertEquals("b", last.get().getNode().get("a").asText());
        Assert.assertEquals("d", last.get().getNode().get("c").asText());
    }

    @Test
    public void testLastPathComponent_Object_NullValue() {
        JsonNode node = YamlReader.read("a: null");
        Optional<PathSegment<?>> last = PathSegment.create(node, "").lastPathComponent();
        Assert.assertNotNull(last.get());
        Assert.assertTrue(last.get().getNode().get("a").isNull());
    }

    @Test
    public void testLastPathComponent_Object() {
        JsonNode node = YamlReader.read("a: b\nc: d");
        Optional<PathSegment<?>> last = PathSegment.create(node, "a").lastPathComponent();
        Assert.assertNotNull(last.get());
        Assert.assertEquals("b", last.get().getNode().asText());
    }

    @Test
    public void testLastPathComponent_ObjectNested() {
        JsonNode node = YamlReader.read("a: b\nc:\n  d: e");
        Optional<PathSegment<?>> last = PathSegment.create(node, "c.d").lastPathComponent();
        Assert.assertNotNull(last);
        Assert.assertEquals("e", last.get().getNode().asText());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testLastPathComponent_ArrayOutOfBounds() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        PathSegment.create(node, "a[-1]").lastPathComponent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLastPathComponent_Array_NonNumericIndex() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        PathSegment.create(node, "a[a]").lastPathComponent();
    }

    @Test(expected = IllegalStateException.class)
    public void testLastPathComponent_Array_MissingClosingParen1() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        PathSegment.create(node, "a[1.").lastPathComponent();
    }

    @Test(expected = IllegalStateException.class)
    public void testLastPathComponent_Array_MissingClosingParen2() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        PathSegment.create(node, "a[12").lastPathComponent();
    }

    @Test(expected = IllegalStateException.class)
    public void testLastPathComponent_Array_Nested_PropertyMissingDot() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        PathSegment.create(node, "a[1]b").lastPathComponent();
    }

    @Test
    public void testLastPathComponent_ArrayRootValue() {
        JsonNode node = YamlReader.read("- 1\n- 2");
        Optional<PathSegment<?>> last0 = PathSegment.create(node, "[0]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve '[0]' path", last0.isPresent());
        Assert.assertNotNull("Couldn't resolve '[0]' path", last0.get().getNode());
        Assert.assertEquals(1, last0.get().getNode().asInt());
        Optional<PathSegment<?>> last1 = PathSegment.create(node, "[1]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve '[1]' path", last1.isPresent());
        Assert.assertNotNull("Couldn't resolve '[1]' path", last1.get().getNode());
        Assert.assertEquals(2, last1.get().getNode().asInt());
    }

    @Test
    public void testLastPathComponent_ArrayValue() {
        JsonNode node = YamlReader.read("a:\n  - 1\n  - 2");
        Optional<PathSegment<?>> last0 = PathSegment.create(node, "a[0]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve 'a[0]' path", last0.isPresent());
        Assert.assertNotNull("Couldn't resolve 'a[0]' path", last0.get().getNode());
        Assert.assertEquals(1, last0.get().getNode().asInt());
        Optional<PathSegment<?>> last1 = PathSegment.create(node, "a[1]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve 'a[1]' path", last1.isPresent());
        Assert.assertNotNull("Couldn't resolve 'a[1]' path", last1.get().getNode());
        Assert.assertEquals(2, last1.get().getNode().asInt());
    }

    @Test
    public void testLastPathComponent_Array_PastEnd() {
        JsonNode node = YamlReader.read("a:\n  - 1\n  - 2");
        Optional<PathSegment<?>> last = PathSegment.create(node, "a[2]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve 'a[2]' path", last.isPresent());
        Assert.assertNull("Index past array end must resolve to an null element", last.get().getNode());
    }

    @Test
    public void testLastPathComponent_Array_PastEnd_Symbolic() {
        JsonNode node = YamlReader.read("a:\n  - 1\n  - 2");
        Optional<PathSegment<?>> last = PathSegment.create(node, "a[.length]").lastPathComponent();
        Assert.assertTrue("Couldn't resolve 'a[.length]' path", last.isPresent());
        Assert.assertNull("Index past array end must resolve to an null element", last.get().getNode());
    }

    @Test
    public void testLastPathComponent_ArrayObject() {
        JsonNode node = YamlReader.read("a:\n  - b: 1\n  - b: 2");
        Optional<PathSegment<?>> last = PathSegment.create(node, "a[1].b").lastPathComponent();
        Assert.assertTrue("Couldn't resolve 'a[1].b' path", last.isPresent());
        Assert.assertNotNull("Couldn't resolve 'a[1].b' path", last.get().getNode());
        Assert.assertEquals(2, last.get().getNode().asInt());
    }
}

