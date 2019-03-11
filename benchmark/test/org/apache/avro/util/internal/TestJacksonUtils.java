/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.util.internal;


import JsonProperties.NULL_VALUE;
import Schema.Type.BYTES;
import Schema.Type.FLOAT;
import Schema.Type.LONG;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Collections;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;


public class TestJacksonUtils {
    enum Direction {

        UP,
        DOWN;}

    @Test
    public void testToJsonNode() {
        Assert.assertEquals(null, JacksonUtils.toJsonNode(null));
        Assert.assertEquals(NullNode.getInstance(), JacksonUtils.toJsonNode(NULL_VALUE));
        Assert.assertEquals(BooleanNode.TRUE, JacksonUtils.toJsonNode(true));
        Assert.assertEquals(IntNode.valueOf(1), JacksonUtils.toJsonNode(1));
        Assert.assertEquals(LongNode.valueOf(2), JacksonUtils.toJsonNode(2L));
        Assert.assertEquals(FloatNode.valueOf(1.0F), JacksonUtils.toJsonNode(1.0F));
        Assert.assertEquals(DoubleNode.valueOf(2.0), JacksonUtils.toJsonNode(2.0));
        Assert.assertEquals(TextNode.valueOf("\u0001\u0002"), JacksonUtils.toJsonNode(new byte[]{ 1, 2 }));
        Assert.assertEquals(TextNode.valueOf("a"), JacksonUtils.toJsonNode("a"));
        Assert.assertEquals(TextNode.valueOf("UP"), JacksonUtils.toJsonNode(TestJacksonUtils.Direction.UP));
        ArrayNode an = JsonNodeFactory.instance.arrayNode();
        an.add(1);
        Assert.assertEquals(an, JacksonUtils.toJsonNode(Collections.singletonList(1)));
        ObjectNode on = JsonNodeFactory.instance.objectNode();
        on.put("a", 1);
        Assert.assertEquals(on, JacksonUtils.toJsonNode(Collections.singletonMap("a", 1)));
    }

    @Test
    public void testToObject() {
        Assert.assertEquals(null, JacksonUtils.toObject(null));
        Assert.assertEquals(NULL_VALUE, JacksonUtils.toObject(NullNode.getInstance()));
        Assert.assertEquals(true, JacksonUtils.toObject(BooleanNode.TRUE));
        Assert.assertEquals(1, JacksonUtils.toObject(IntNode.valueOf(1)));
        Assert.assertEquals(2L, JacksonUtils.toObject(IntNode.valueOf(2), Schema.create(LONG)));
        Assert.assertEquals(1.0F, JacksonUtils.toObject(DoubleNode.valueOf(1.0), Schema.create(FLOAT)));
        Assert.assertEquals(2.0, JacksonUtils.toObject(DoubleNode.valueOf(2.0)));
        Assert.assertEquals(TextNode.valueOf("\u0001\u0002"), JacksonUtils.toJsonNode(new byte[]{ 1, 2 }));
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, ((byte[]) (JacksonUtils.toObject(TextNode.valueOf("\u0001\u0002"), Schema.create(BYTES)))));
        Assert.assertEquals("a", JacksonUtils.toObject(TextNode.valueOf("a")));
        Assert.assertEquals("UP", JacksonUtils.toObject(TextNode.valueOf("UP"), SchemaBuilder.enumeration("Direction").symbols("UP", "DOWN")));
        ArrayNode an = JsonNodeFactory.instance.arrayNode();
        an.add(1);
        Assert.assertEquals(Collections.singletonList(1), JacksonUtils.toObject(an));
        ObjectNode on = JsonNodeFactory.instance.objectNode();
        on.put("a", 1);
        Assert.assertEquals(Collections.singletonMap("a", 1), JacksonUtils.toObject(on));
        Assert.assertEquals(Collections.singletonMap("a", 1L), JacksonUtils.toObject(on, SchemaBuilder.record("r").fields().requiredLong("a").endRecord()));
        Assert.assertEquals(NULL_VALUE, JacksonUtils.toObject(NullNode.getInstance(), SchemaBuilder.unionOf().nullType().and().intType().endUnion()));
        Assert.assertEquals("a", JacksonUtils.toObject(TextNode.valueOf("a"), SchemaBuilder.unionOf().stringType().and().intType().endUnion()));
    }
}

