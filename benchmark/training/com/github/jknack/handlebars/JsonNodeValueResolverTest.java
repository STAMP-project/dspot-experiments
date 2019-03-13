package com.github.jknack.handlebars;


import JsonNodeValueResolver.INSTANCE;
import ValueResolver.UNRESOLVED;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.POJONode;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static ValueResolver.UNRESOLVED;


public class JsonNodeValueResolverTest {
    @Test
    public void resolveValueNode() throws IOException {
        Handlebars handlebars = new Handlebars();
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("string", "abc");
        root.put("int", 678);
        root.put("long", 6789L);
        root.put("float", 7.13F);
        root.put("double", 3.14);
        root.put("bool", true);
        Assert.assertEquals("abc 678 6789 7.13 3.14 true", handlebars.compileInline("{{string}} {{int}} {{long}} {{float}} {{double}} {{bool}}").apply(JsonNodeValueResolverTest.context(root)));
    }

    @Test
    public void nullMustBeResolvedToUnresolved() {
        Assert.assertEquals(UNRESOLVED, INSTANCE.resolve(null, "nothing"));
    }

    @Test
    public void resolveBinaryNode() {
        String name = "binary";
        byte[] result = new byte[]{ 1 };
        JsonNode node = createMock(JsonNode.class);
        BinaryNode value = BinaryNode.valueOf(result);
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void resolveNullNode() {
        String name = "null";
        Object result = UNRESOLVED;
        JsonNode node = createMock(JsonNode.class);
        NullNode value = NullNode.instance;
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void resolveBigIntegerNode() {
        String name = "bigInt";
        BigInteger result = BigInteger.ONE;
        JsonNode node = createMock(JsonNode.class);
        JsonNode value = BigIntegerNode.valueOf(result);
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void resolveDecimalNode() {
        String name = "decimal";
        BigDecimal result = BigDecimal.TEN;
        JsonNode node = createMock(JsonNode.class);
        JsonNode value = DecimalNode.valueOf(result);
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void resolveLongNode() {
        String name = "long";
        Long result = 678L;
        JsonNode node = createMock(JsonNode.class);
        JsonNode value = LongNode.valueOf(result);
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void resolvePojoNode() {
        String name = "pojo";
        Object result = new Object();
        JsonNode node = createMock(JsonNode.class);
        JsonNode value = new POJONode(result);
        expect(node.get(name)).andReturn(value);
        Object[] mocks = new Object[]{ node };
        replay(mocks);
        Assert.assertEquals(result, INSTANCE.resolve(node, name));
        verify(mocks);
    }

    @Test
    public void propertySet() throws IOException {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("string", "abc");
        root.put("int", 678);
        root.put("double", 3.14);
        root.put("bool", true);
        Assert.assertEquals(root.entrySet(), INSTANCE.propertySet(JsonNodeValueResolverTest.node(root)));
    }

    @Test
    public void emptyPropertySet() throws IOException {
        Set<Map.Entry<String, Object>> propertySet = INSTANCE.propertySet(new Object());
        Assert.assertNotNull(propertySet);
        Assert.assertEquals(0, propertySet.size());
    }

    @Test
    public void resolveObjectNode() throws IOException {
        Handlebars handlebars = new Handlebars();
        Object item = new Object() {
            @SuppressWarnings("unused")
            public String getKey() {
                return "pojo";
            }
        };
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("pojo", item);
        Assert.assertEquals("pojo", handlebars.compileInline("{{pojo.key}}").apply(JsonNodeValueResolverTest.context(root)));
    }

    @Test
    public void resolveSimpleArrayNode() throws IOException {
        Handlebars handlebars = new Handlebars();
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("array", new Object[]{ 1, 2, 3 });
        Assert.assertEquals("123", handlebars.compileInline("{{array.[0]}}{{array.[1]}}{{array.[2]}}").apply(JsonNodeValueResolverTest.context(root)));
        Assert.assertEquals("123", handlebars.compileInline("{{#array}}{{this}}{{/array}}").apply(JsonNodeValueResolverTest.context(root)));
    }

    @Test
    public void resolveArrayNode() throws IOException {
        Handlebars handlebars = new Handlebars();
        Object item = new Object() {
            @SuppressWarnings("unused")
            public String getKey() {
                return "pojo";
            }
        };
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("array", new Object[]{ item });
        Assert.assertEquals("pojo", handlebars.compileInline("{{array.[0].key}}").apply(JsonNodeValueResolverTest.context(root)));
        Assert.assertEquals("pojo", handlebars.compileInline("{{#array}}{{key}}{{/array}}").apply(JsonNodeValueResolverTest.context(root)));
    }
}

