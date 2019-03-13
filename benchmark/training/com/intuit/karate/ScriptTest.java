package com.intuit.karate;


import AssignType.AUTO;
import AssignType.BYTE_ARRAY;
import AssignType.JSON;
import AssignType.TEXT;
import AssignType.XML;
import AssignType.XML_STRING;
import MatchType.CONTAINS;
import MatchType.CONTAINS_ANY;
import MatchType.CONTAINS_ONLY;
import MatchType.EACH_CONTAINS;
import MatchType.EACH_EQUALS;
import MatchType.EACH_NOT_CONTAINS;
import MatchType.EQUALS;
import MatchType.NOT_CONTAINS;
import MatchType.NOT_EQUALS;
import ScriptValue.Type;
import ScriptValue.Type.JS_FUNCTION;
import ScriptValue.Type.LIST;
import ScriptValue.Type.MAP;
import ScriptValue.Type.PRIMITIVE;
import ScriptValue.Type.STRING;
import ScriptValueMap.VAR_RESPONSE;
import com.intuit.karate.core.FeatureContext;
import com.intuit.karate.core.ScenarioContext;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 *
 *
 * @author pthomas3
 */
public class ScriptTest {
    private static final Logger logger = LoggerFactory.getLogger(ScriptTest.class);

    @Test
    public void testParsingTextType() {
        Assert.assertTrue(Script.isVariable("foo"));
        Assert.assertTrue(Script.isJavaScriptFunction("function(){ return { bar: 'baz' } }"));
        Assert.assertTrue(Script.isXmlPath("/foo"));
        Assert.assertTrue(Script.isXmlPath("//foo"));
        Assert.assertTrue(Script.isXmlPathFunction("lower-case('Foo')"));
        Assert.assertTrue(Script.isXmlPathFunction("count(/journal/article)"));
        Assert.assertTrue(Script.isVariableAndSpaceAndPath("foo count(/journal/article)"));
        Assert.assertTrue(Script.isVariableAndSpaceAndPath("foo $"));
    }

    @Test
    public void testEvalPrimitives() {
        ScenarioContext ctx = getContext();
        ctx.vars.put("foo", "bar");
        ctx.vars.put("a", 1);
        ctx.vars.put("b", 2);
        String expression = "foo + 'baz'";
        ScriptValue value = Script.evalJsExpression(expression, ctx);
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("barbaz", value.getValue());
        value = Script.evalJsExpression("a + b", ctx);
        Assert.assertEquals(PRIMITIVE, value.getType());
        Assert.assertEquals(3.0, value.getValue());
    }

    @Test
    public void testEvalMapsAndLists() {
        ScenarioContext ctx = getContext();
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("foo", "bar");
        testMap.put("baz", 5);
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testMap.put("myList", testList);
        ctx.vars.put("myMap", testMap);
        String expression = "myMap.foo + myMap.baz";
        ScriptValue value = Script.evalJsExpression(expression, ctx);
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("bar5", value.getValue());
        value = Script.evalJsExpression("myMap.myList[0] + myMap.myList[1]", ctx);
        Assert.assertEquals(PRIMITIVE, value.getType());
        Assert.assertEquals(3.0, value.getValue());
    }

    @Test
    public void testEvalJsonDocuments() {
        ScenarioContext ctx = getContext();
        DocumentContext doc = JsonUtils.toJsonDoc("{ foo: 'bar', baz: [1, 2], ban: { hello: 'world' } }");
        ctx.vars.put("myJson", doc);
        ScriptValue value = Script.evalJsExpression("myJson.foo", ctx);
        Assert.assertEquals("bar", value.getValue());
        value = Script.evalJsExpression("myJson.baz[1]", ctx);
        Assert.assertEquals(2, value.getValue());
        value = Script.evalJsExpression("myJson.ban.hello", ctx);
        Assert.assertEquals("world", value.getValue());
    }

    @Test
    public void testEvalXmlDocuments() {
        ScenarioContext ctx = getContext();
        Document doc = XmlUtils.toXmlDoc("<root><foo>bar</foo><hello>world</hello></root>");
        ctx.vars.put("myXml", doc);
        ScriptValue value = Script.evalJsExpression("myXml.root.foo", ctx);
        Assert.assertEquals("bar", value.getValue());
    }

    @Test
    public void testAssignXmlWithLineBreaksAndMatchJson() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "<records>\n  <record>a</record>\n  <record>b</record>\n  <record>c</record>\n</records>", ctx);
        Script.assign("bar", "foo.records", ctx);
        ScriptValue value = ctx.vars.get("bar");
        Assert.assertTrue(((value.getType()) == (Type.MAP)));
        Assert.assertTrue(Script.matchNamed(EQUALS, "bar.record", null, "['a', 'b', 'c']", ctx).pass);
        Assert.assertTrue(Script.assertBoolean("foo.records.record.length == 3", ctx).pass);
    }

    @Test
    public void testAssignXmlWithLineBreaksAndNullElements() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "<records>\n  <record>a</record>\n  <record/>\n</records>", ctx);
        Script.assign("bar", "foo.records", ctx);
        ScriptValue value = ctx.vars.get("bar");
        Assert.assertTrue(((value.getType()) == (Type.MAP)));
        Assert.assertTrue(Script.matchNamed(EQUALS, "bar.record", null, "['a', null]", ctx).pass);
    }

    @Test
    public void testJsonPathOnVarsByName() {
        ScenarioContext ctx = getContext();
        DocumentContext doc = JsonUtils.toJsonDoc("{ foo: 'bar', baz: [1, 2], ban: { hello: 'world' } }");
        ctx.vars.put("myJson", doc);
        ScriptValue value = Script.evalJsonPathOnVarByName("myJson", "$.foo", ctx);
        Assert.assertEquals("bar", value.getValue());
        value = Script.evalKarateExpression("myJson.foo", ctx);
        Assert.assertEquals("bar", value.getValue());
        value = Script.evalJsonPathOnVarByName("myJson", "$.baz[1]", ctx);
        Assert.assertEquals(2, value.getValue());
        value = Script.evalKarateExpression("myJson.baz[1]", ctx);
        Assert.assertEquals(2, value.getValue());
        value = Script.evalJsonPathOnVarByName("myJson", "$.baz", ctx);
        Assert.assertEquals(LIST, value.getType());
        value = Script.evalJsonPathOnVarByName("myJson", "$.ban", ctx);
        Assert.assertEquals(MAP, value.getType());
    }

    @Test
    public void testXmlPathOnVarsByName() {
        ScenarioContext ctx = getContext();
        Document doc = XmlUtils.toXmlDoc("<root><foo>bar</foo></root>");
        ctx.vars.put("myXml", doc);
        ScriptValue value = Script.evalXmlPathOnVarByName("myXml", "/root/foo", ctx);
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("bar", value.getAsString());
        value = Script.evalKarateExpression("$myXml/root/foo", ctx);
        Assert.assertEquals("bar", value.getAsString());
    }

    @Test
    public void testEvalXmlEmbeddedExpressions() {
        ScenarioContext ctx = getContext();
        ctx.vars.put("a", 1);
        ctx.vars.put("b", 2);
        Document doc = XmlUtils.toXmlDoc("<root><foo>#(a + b)</foo></root>");
        Script.evalXmlEmbeddedExpressions(doc, ctx, false);
        ctx.vars.put("myXml", doc);
        ScriptValue value = Script.evalXmlPathOnVarByName("myXml", "/root/foo", ctx);
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("3.0", value.getAsString());
    }

    @Test
    public void testEvalXmlEmbeddedExpressionsThatReturnChunks() {
        ScenarioContext ctx = getContext();
        Script.assign("hello", "<hello>world</hello>", ctx);
        Script.assign("xml", "<foo><bar>#(hello)</bar></foo>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><bar><hello>world</hello></bar></foo>", ctx).pass);
    }

    @Test
    public void testEvalXmlEmbeddedExpressionsThatReturnNull() {
        ScenarioContext ctx = getContext();
        Script.assign("hello", "null", ctx);
        Script.assign("xml", "<foo><bar>#(hello)</bar></foo>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><bar></bar></foo>", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><bar/></foo>", ctx).pass);
    }

    @Test
    public void testEvalXmlEmbeddedExpressionsInAttributes() {
        ScenarioContext ctx = getContext();
        ctx.vars.put("a", 5);
        String xml = "<foo bar=\"#(a)\">#(a)</foo>";
        Document doc = XmlUtils.toXmlDoc(xml);
        Script.evalXmlEmbeddedExpressions(doc, ctx, false);
        String result = XmlUtils.toString(doc);
        ScriptTest.logger.debug("result: {}", result);
        Assert.assertTrue(result.endsWith("<foo bar=\"5\">5</foo>"));
    }

    @Test
    public void testEvalXmlEmbeddedOptionalExpressionsInAttributes() {
        ScenarioContext ctx = getContext();
        Script.assign("a", "null", ctx);
        Script.assign("xml", "<foo bar=\"##(a)\">baz</foo>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo>baz</foo>", ctx).pass);
    }

    @Test
    public void testEvalXmlEmbeddedOptionalExpressions() {
        ScenarioContext ctx = getContext();
        Script.assign("a", "null", ctx);
        Script.assign("xml", "<foo><a>hello</a><b>##(a)</b></foo>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><a>hello</a></foo>", ctx).pass);
    }

    @Test
    public void testEvalJsonEmbeddedExpressions() {
        ScenarioContext ctx = getContext();
        ctx.vars.put("a", 1);
        ctx.vars.put("b", 2);
        DocumentContext doc = JsonUtils.toJsonDoc("{ foo: '#(a + b)' }");
        Script.evalJsonEmbeddedExpressions(doc, ctx, false);
        ctx.vars.put("myJson", doc);
        ScriptValue value = Script.evalJsonPathOnVarByName("myJson", "$.foo", ctx);
        Assert.assertEquals(PRIMITIVE, value.getType());
        Assert.assertEquals(3.0, value.getValue());
    }

    @Test
    public void testEvalEmbeddedExpressionsWithJsonPath() {
        ScenarioContext ctx = getContext();
        String ticket = "{ ticket: 'my-ticket', userId: '12345' }";
        ctx.vars.put("ticket", JsonUtils.toJsonDoc(ticket));
        String json = "{ foo: '#(ticket.userId)' }";
        DocumentContext doc = JsonUtils.toJsonDoc(json);
        Script.evalJsonEmbeddedExpressions(doc, ctx, false);
        String result = doc.jsonString();
        ScriptTest.logger.debug("result: {}", result);
        Assert.assertEquals("{\"foo\":\"12345\"}", result);
    }

    @Test
    public void testEvalEmbeddedExpressionsWithJsonPathsWhichAreTricky() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ a: 1, b: 2, c: 3 }", ctx);
        Script.assign("bar", "{ 'sp ace': '#(foo.a)', 'hy-phen': '#(foo.b)', 'full.stop': '#(foo.c)' }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "bar", null, "{ 'sp ace': 1, 'hy-phen': 2, 'full.stop': 3 }", ctx).pass);
    }

    @Test
    public void testEvalEmbeddedOptionalExpressions() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ a: null, b: null }", ctx);
        Script.assign("bar", "{ hello: '#(foo.a)', world: '##(foo.b)'  }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "bar", null, "{ hello: null }", ctx).pass);
    }

    @Test
    public void testVariableNameValidation() {
        Assert.assertTrue(Script.isValidVariableName("foo"));
        Assert.assertTrue(Script.isValidVariableName("foo_bar"));
        Assert.assertTrue(Script.isValidVariableName("foo_"));
        Assert.assertTrue(Script.isValidVariableName("foo1"));
        Assert.assertTrue(Script.isValidVariableName("a"));
        Assert.assertTrue(Script.isValidVariableName("a1"));
        // bad
        Assert.assertFalse(Script.isValidVariableName("foo.bar"));
        Assert.assertFalse(Script.isValidVariableName("foo-bar"));
        Assert.assertFalse(Script.isValidVariableName("$foo"));
        Assert.assertFalse(Script.isValidVariableName("$foo/bar"));
        Assert.assertFalse(Script.isValidVariableName("_foo"));
        Assert.assertFalse(Script.isValidVariableName("_foo_"));
        Assert.assertFalse(Script.isValidVariableName("0"));
        Assert.assertFalse(Script.isValidVariableName("2foo"));
    }

    @Test
    public void testMatchMapObjects() {
        ScenarioContext ctx = getContext();
        Map<String, Object> left = new HashMap<>();
        left.put("foo", "bar");
        Map<String, Object> right = new HashMap<>();
        right.put("foo", "bar");
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        right.put("baz", "#ignore");
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        right.put("baz", "#notpresent");
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        left.put("baz", Arrays.asList(1, 2, 3));
        right.put("baz", Arrays.asList(1, 2, 3));
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        left.put("baz", Arrays.asList(1, 2));
        Assert.assertFalse(matchJsonObject(left, right, ctx).pass);
        Map<String, Object> leftChild = new HashMap<>();
        leftChild.put("a", 1);
        Map<String, Object> rightChild = new HashMap<>();
        rightChild.put("a", 1);
        left.put("baz", leftChild);
        right.put("baz", rightChild);
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        List<Map> leftList = new ArrayList<>();
        leftList.add(leftChild);
        List<Map> rightList = new ArrayList<>();
        rightList.add(rightChild);
        left.put("baz", leftList);
        right.put("baz", rightList);
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
        rightChild.put("a", 2);
        Assert.assertFalse(matchJsonObject(left, right, ctx).pass);
        rightChild.put("a", "#ignore");
        Assert.assertTrue(matchJsonObject(left, right, ctx).pass);
    }

    @Test
    public void testMatchListObjects() {
        List left = new ArrayList();
        List right = new ArrayList();
        Map<String, Object> leftChild = new HashMap<>();
        leftChild.put("a", 1);
        left.add(leftChild);
        Map<String, Object> rightChild = new HashMap<>();
        rightChild.put("a", 1);
        right.add(rightChild);
        Assert.assertTrue(matchJsonObject(left, right, null).pass);
    }

    @Test
    public void testMatchJsonPath() {
        DocumentContext doc = JsonPath.parse("{ foo: 'bar', baz: { ban: [1, 2, 3]} }");
        ScenarioContext ctx = getContext();
        ctx.vars.put("myJson", doc);
        ScriptValue myJson = ctx.vars.get("myJson");
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, myJson, "$.foo", "'bar'", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, myJson, "$.baz", "{ ban: [1, 2, 3]} }", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, myJson, "$.baz.ban[1]", "2", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, myJson, "$.baz", "{ ban: [1, '#ignore', 3]} }", ctx).pass);
    }

    @Test
    public void testMatchJsonPathThatReturnsList() {
        DocumentContext doc = JsonPath.parse("{ foo: [{ bar: 1}, {bar: 2}, {bar: 3}]}");
        ScenarioContext ctx = getContext();
        ctx.vars.put("json", doc);
        Script.assign("list", "json.foo", ctx);
        ScriptValue list = ctx.vars.get("list");
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, list, "$[0]", "{ bar: 1}", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, list, "$[0].bar", "1", ctx).pass);
    }

    @Test
    public void testMatchJsonPathOnLeftHandSide() {
        ScenarioContext ctx = getContext();
        String json = "[\n" + (((((((((("    {\n" + "        \"a\": \"a\",\n") + "        \"b\": \"a\",\n") + "        \"c\": \"a\",\n") + "    },\n") + "    {\n") + "        \"a\": \"ab\",\n") + "        \"b\": \"ab\",\n") + "        \"c\": \"ab\",\n") + "    }\n") + "]");
        Script.assign("response", json, ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "response[?(@.b=='ab')]", null, "'#[1]'", ctx).pass);
    }

    @Test
    public void testMatchAllJsonPath() {
        DocumentContext doc = JsonPath.parse("{ foo: [{bar: 1, baz: 'a'}, {bar: 2, baz: 'b'}, {bar:3, baz: 'c'}]}");
        ScenarioContext ctx = getContext();
        ctx.vars.put("myJson", doc);
        ScriptValue myJson = ctx.vars.get("myJson");
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, myJson, "$.foo", "[{bar: 1, baz: 'a'}, {bar: 2, baz: 'b'}, {bar:3, baz: 'c'}]", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(CONTAINS, myJson, "$.foo", "[{bar: 1, baz: 'a'}, {bar: 2, baz: 'b'}, {bar:3, baz: 'c'}]", ctx).pass);
        Assert.assertFalse(Script.matchJsonOrObject(NOT_CONTAINS, myJson, "$.foo", "[{bar: 1, baz: 'a'}]", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(NOT_CONTAINS, myJson, "$.foo", "[{bar: 9, baz: 'z'}, {bar: 99, baz: 'zz'}]", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(CONTAINS_ONLY, myJson, "$.foo", "[{bar: 1, baz: 'a'}, {bar: 2, baz: 'b'}, {bar:3, baz: 'c'}]", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(CONTAINS_ANY, myJson, "$.foo", "[{bar: 9, baz: 'z'}, {bar: 2, baz: 'b'}]", ctx).pass);
        // shuffle
        Assert.assertTrue(Script.matchJsonOrObject(CONTAINS_ONLY, myJson, "$.foo", "[{bar: 2, baz: 'b'}, {bar:3, baz: 'c'}, {bar: 1, baz: 'a'}]", ctx).pass);
        Assert.assertFalse(Script.matchJsonOrObject(CONTAINS_ONLY, myJson, "$.foo", "[{bar: 1, baz: 'a'}, {bar: 2, baz: 'b'}]", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EACH_EQUALS, myJson, "$.foo", "{bar:'#number', baz:'#string'}", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EACH_CONTAINS, myJson, "$.foo", "{bar:'#number'}", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EACH_CONTAINS, myJson, "$.foo", "{baz:'#string'}", ctx).pass);
        Assert.assertTrue(Script.matchJsonOrObject(EACH_NOT_CONTAINS, myJson, "$.foo", "{baz:'z'}", ctx).pass);
        Assert.assertFalse(Script.matchJsonOrObject(EACH_NOT_CONTAINS, myJson, "$.foo", "{baz:'a'}", ctx).pass);
        Assert.assertFalse(Script.matchJsonOrObject(EACH_EQUALS, myJson, "$.foo", "{bar:'#? _ < 3',  baz:'#string'}", ctx).pass);
    }

    @Test
    public void testMatchNotEquals() {
        ScenarioContext ctx = getContext();
        Script.assign("temp", "[1, 2]", ctx);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "temp", null, "'#[1]'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "temp", null, "'#[2]'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "temp", null, "'#[]? _ > 2'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "temp", null, "'#[]? _ > 0'", ctx).pass);
        Script.assign("temp", "'foo'", ctx);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "temp", null, "'#regex .{2}'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "temp", null, "'#regex .{3}'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "temp", null, "'#? _.length == 2'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "temp", null, "'#? _.length == 3'", ctx).pass);
        Script.assign("json", "null", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "null", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "1", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "json", null, "null", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "1", ctx).pass);
        Script.assign("json", "1", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "null", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "null", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "1", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "json", null, "1", ctx).pass);
        Script.assign("nope", "{ foo: '#number' }", ctx);
        Script.assign("json", "{ foo: 'bar' }", ctx);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "'#(^nope)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "'#(nope)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "'#array'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "'foo'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "[]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "1", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "json", null, "{ foo: 'bar' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{}", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ foo: 'blah' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ foo: 'bar', baz: 'ban' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ foo: 'blah' }", ctx).pass);
        Script.assign("json", "[{ foo: 'bar'}, { foo: 'baz' }]", ctx);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "json", null, "[{ foo: 'bar'}, { foo: 'baz' }]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "[{ foo: 'bar'}, { foo: 'blah' }]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ foo: 'blah' }", ctx).pass);
    }

    @Test
    public void testMatchJsonObjectReturnedFromJs() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return { foo: 'bar' } }", ctx);
        Script.assign("json", "{ foo: 'bar' }", ctx);
        Script.assign("expected", "fun()", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "expected", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "fun()", ctx).pass);
    }

    @Test
    public void testMatchJsonArrayReturnedFromJs() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return [ 'foo', 'bar', 'baz' ] }", ctx);
        Script.assign("json", "[ 'foo', 'bar', 'baz' ]", ctx);
        Script.assign("expected", "fun()", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "expected", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "fun()", ctx).pass);
    }

    @Test
    public void testMatchJsonPathOnResponse() {
        DocumentContext doc = JsonPath.parse("{ foo: 'bar' }");
        ScenarioContext ctx = getContext();
        ctx.vars.put("response", doc);
        Assert.assertTrue(Script.matchNamed(EQUALS, "$", null, "{ foo: 'bar' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "$.foo", null, "'bar'", ctx).pass);
    }

    private final String ACTUAL = "{\"id\":{\"domain\":\"ACS\",\"type\":\"entityId\",\"value\":\"bef90f66-bb57-4fea-83aa-a0acc42b0426\"},\"primaryId\":\"bef90f66-bb57-4fea-83aa-a0acc42b0426\",\"created\":{\"on\":\"2016-02-28T05:56:48.485+0000\"},\"lastUpdated\":{\"on\":\"2016-02-28T05:56:49.038+0000\"},\"organization\":{\"id\":{\"domain\":\"ACS\",\"type\":\"entityId\",\"value\":\"631fafe9-8822-4c82-b4a4-8735b202c16c\"},\"created\":{\"on\":\"2016-02-28T05:56:48.486+0000\"},\"lastUpdated\":{\"on\":\"2016-02-28T05:56:49.038+0000\"}},\"clientState\":\"ACTIVE\"}";

    private final String EXPECTED = "{\"id\":{\"domain\":\"ACS\",\"type\":\"entityId\",\"value\":\"#ignore\"},\"primaryId\":\"#ignore\",\"created\":{\"on\":\"#ignore\"},\"lastUpdated\":{\"on\":\"#ignore\"},\"organization\":{\"id\":{\"domain\":\"ACS\",\"type\":\"entityId\",\"value\":\"#ignore\"},\"created\":{\"on\":\"#ignore\"},\"lastUpdated\":{\"on\":\"#ignore\"}},\"clientState\":\"ACTIVE\"}";

    @Test
    public void testMatchTwoJsonDocsWithIgnores() {
        DocumentContext actual = JsonPath.parse(ACTUAL);
        DocumentContext expected = JsonPath.parse(EXPECTED);
        ScenarioContext ctx = getContext();
        ctx.vars.put("actual", actual);
        ctx.vars.put("expected", expected);
        ScriptValue act = ctx.vars.get("actual");
        Assert.assertTrue(Script.matchJsonOrObject(EQUALS, act, "$", "expected", ctx).pass);
    }

    @Test
    public void testMatchXmlPathThatReturnsTextNode() {
        ScenarioContext ctx = getContext();
        Document doc = XmlUtils.toXmlDoc("<root><foo>bar</foo><hello>world</hello></root>");
        ctx.vars.put("myXml", doc);
        ScriptValue myXml = ctx.vars.get("myXml");
        Assert.assertTrue(Script.matchXml(EQUALS, myXml, "/root/foo", "'bar'", ctx).pass);
        Assert.assertTrue(Script.matchXml(EQUALS, myXml, "/root/hello", "'world'", ctx).pass);
    }

    @Test
    public void testMatchXmlPathThatReturnsXmlChunk() {
        ScenarioContext ctx = getContext();
        Document doc = XmlUtils.toXmlDoc("<root><foo><bar>baz</bar></foo></root>");
        ctx.vars.put("myXml", doc);
        ScriptValue myXml = ctx.vars.get("myXml");
        Assert.assertTrue(Script.matchXml(EQUALS, myXml, "/root/foo", "<foo><bar>baz</bar></foo>", ctx).pass);
    }

    @Test
    public void testMatchXmlPathThatReturnsNull() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo>bar</foo></root>", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "myXml//baz", null, "<baz>1</baz>", ctx).pass);
    }

    @Test
    public void testMatchXmlEmptyAndNotPresent() {
        ScenarioContext ctx = getContext();
        Script.assign("xml", "<root><foo>bar</foo><baz/><ban></ban></root>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml/root/foo", null, "'bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml/root/baz", null, "''", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml/root/ban", null, "''", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml/root/foo", null, "'#present'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "xml/root/foo", null, "'#notpresent'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml/root/nope", null, "'#notpresent'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "xml/root/nope", null, "'#present'", ctx).pass);
    }

    @Test
    public void testJsonEmptyAndNotPresent() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ foo: 'bar' }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json.foo", null, "'bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json.foo", null, "'#present'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json.foo", null, "'#notpresent'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json.nope", null, "'#notpresent'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json.nope", null, "'#present'", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlText() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo>bar</foo></root>", ctx);
        Script.assign("myStr", "$myXml/root/foo", ctx);
        Assert.assertTrue(Script.assertBoolean("myStr == 'bar'", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlChunk() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>baz</bar></foo></root>", ctx);
        Script.assign("myChunk", "$myXml/root/foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myChunk", null, "<foo><bar>baz</bar></foo>", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlChunkByVariableReference() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>baz</bar></foo></root>", ctx);
        Script.assign("myChunk", "$myXml/root/foo", ctx);
        Script.assign("expected", "<foo><bar>baz</bar></foo>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myChunk", null, "expected", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlPathChunk() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>baz</bar></foo></root>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myXml/root/foo", null, "<foo><bar>baz</bar></foo>", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlPathThatReturnsNodeListAgainstJsonArray() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>one</bar><bar>two</bar></foo></root>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myXml/root/foo/bar", null, "['one', 'two']", ctx).pass);
    }

    @Test
    public void testAssignAndMatchXmlPathThatReturnsNodeListAgainstList() {
        ScenarioContext ctx = getContext();
        Script.assign("myJson", "[{ val: 'one' }, { val: 'two' }]", ctx);
        Script.assign("myList", "get myJson $[*].val", ctx);
        Script.assign("myXml", "<root><foo><bar>one</bar><bar>two</bar></foo></root>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myXml/root/foo/bar", null, "myList", ctx).pass);
    }

    @Test
    public void testMatchXmlPathAutoConvertingFromMap() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>baz</bar></foo></root>", ctx);
        Script.assign("myMap", "myXml", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myMap/root/foo", null, "<foo><bar>baz</bar></foo>", ctx).pass);
    }

    @Test
    public void testEvalXmlPathAutoConvertingFromMap() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo><bar>baz</bar></foo></root>", ctx);
        Script.assign("myMap", "myXml", ctx);
        Script.assign("temp", "get myXml /root/foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "temp", null, "<foo><bar>baz</bar></foo>", ctx).pass);
        Script.assign("temp", "get myMap /root/foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "temp", null, "<foo><bar>baz</bar></foo>", ctx).pass);
    }

    @Test
    public void testAssignXmlPathThatReturnsListThenMatch() {
        ScenarioContext ctx = getContext();
        Script.assign("response", XmlUtilsTest.TEACHERS_XML, ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "//teacher[@department='science']/subject", null, "['math', 'physics']", ctx).pass);
        Script.assign("subjects", "//teacher[@department='science']/subject", ctx);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "subjects", null, "['physics', 'math']", ctx).pass);
        Script.assign("teachers", "response", ctx);// becomes a map

        Script.assign("subjects", "get teachers //teacher[@department='science']/subject", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "subjects", null, "['math', 'physics']", ctx).pass);
    }

    @Test
    public void testRunningJsonPathOnStringAutoConvertsStringToJson() {
        ScenarioContext ctx = getContext();
        Script.assign(AssignType.STRING, "response", "{ foo: { hello: 'world' } }", ctx, true);
        Script.assign("foo", "$response.foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ hello: 'world' }", ctx).pass);
        Script.assign("foo", "$.foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ hello: 'world' }", ctx).pass);
    }

    @Test
    public void testCastJsonToString() {
        ScenarioContext ctx = getContext();
        Script.assign("myJson", "{ root: { foo: 'bar' } }", ctx);
        Script.assign(AssignType.STRING, "myString", "myJson", ctx, true);
        ScriptValue value = ctx.vars.get("myString");
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("{\"root\":{\"foo\":\"bar\"}}", value.getAsString());
    }

    @Test
    public void testCastStringToJson() {
        ScenarioContext ctx = getContext();
        Script.assign("myString", "{\"root\":{\"foo\":\"bar\"}}", ctx);
        Script.assign(JSON, "myJson", "myString", ctx, true);
        ScriptValue value = ctx.vars.get("myJson");
        Assert.assertEquals(ScriptValue.Type.JSON, value.getType());
        Assert.assertEquals("{\"root\":{\"foo\":\"bar\"}}", value.getAsString());
    }

    @Test
    public void testCastJsonToXml() {
        ScenarioContext ctx = getContext();
        Script.assign("myJson", "{ root: { foo: 'bar' } }", ctx);
        Script.assign(XML, "myXml", "myJson", ctx, true);
        ScriptValue value = ctx.vars.get("myXml");
        Assert.assertEquals(ScriptValue.Type.XML, value.getType());
        Assert.assertEquals("<root><foo>bar</foo></root>", value.getAsString());
    }

    @Test
    public void testCastStringToXml() {
        ScenarioContext ctx = getContext();
        Script.assign(AssignType.STRING, "myString", "<root><foo>bar</foo></root>", ctx, true);
        Script.assign(XML, "myXml", "myString", ctx, true);
        ScriptValue value = ctx.vars.get("myXml");
        Assert.assertEquals(ScriptValue.Type.XML, value.getType());
        Assert.assertEquals("<root><foo>bar</foo></root>", value.getAsString());
    }

    @Test
    public void testCastXmlToString() {
        ScenarioContext ctx = getContext();
        Script.assign("myXml", "<root><foo>bar</foo></root>", ctx);
        Script.assign(XML_STRING, "myString", "myXml", ctx, true);
        ScriptValue value = ctx.vars.get("myString");
        Assert.assertEquals(STRING, value.getType());
        Assert.assertEquals("<root><foo>bar</foo></root>", value.getValue());
    }

    @Test
    public void testCastPojoToJson() {
        ScenarioContext ctx = getContext();
        Script.assign("pojo", "new com.intuit.karate.SimplePojo()", ctx);
        Script.assign(JSON, "json", "pojo", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: null, bar: 0 }", ctx).pass);
    }

    @Test
    public void testCastPojoToXml() {
        ScenarioContext ctx = getContext();
        Script.assign("pojo", "new com.intuit.karate.SimplePojo()", ctx);
        Script.assign(XML, "xml", "pojo", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo></foo><bar>0</bar></root>", ctx).pass);
    }

    @Test
    public void testXmlShortCutsForResponse() {
        ScenarioContext ctx = getContext();
        Script.assign("response", "<root><foo>bar</foo></root>", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "response", "/", "<root><foo>bar</foo></root>", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "response/", null, "<root><foo>bar</foo></root>", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "response", null, "<root><foo>bar</foo></root>", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "/", null, "<root><foo>bar</foo></root>", ctx).pass);
    }

    @Test
    public void testMatchXmlButUsingJsonPath() {
        ScenarioContext ctx = getContext();
        Document doc = XmlUtils.toXmlDoc("<cat><name>Billie</name><scores><score>2</score><score>5</score></scores></cat>");
        ctx.vars.put("myXml", doc);
        Assert.assertTrue(Script.matchNamed(EQUALS, "myXml/cat/scores/score[2]", null, "'5'", ctx).pass);
        // using json path for xml !
        Assert.assertTrue(Script.matchNamed(EQUALS, "myXml.cat.scores.score[1]", null, "'5'", ctx).pass);
    }

    @Test
    public void testXmlStringConversion() {
        ScenarioContext ctx = getContext();
        Script.assign("response", "<foo><bar bbb=\"2\" aaa=\"1\"/></foo>", ctx);
        Script.assign(XML_STRING, "temp", "response", ctx, false);
        Assert.assertTrue(Script.matchNamed(EQUALS, "temp", null, "<foo><bar bbb=\"2\" aaa=\"1\"/></foo>", ctx).pass);
        // XML DOM parsing unfortunately re-orders attributes
        Assert.assertTrue(Script.matchNamed(EQUALS, "temp", null, "\'<foo><bar aaa=\"1\" bbb=\"2\"/></foo>\'", ctx).pass);
    }

    @Test
    public void testXmlStringConversionInJs() {
        ScenarioContext ctx = getContext();
        Script.assign(AUTO, "response", "<foo><bar bbb=\"2\" aaa=\"1\"/></foo>", ctx, false);
        Script.assign(XML, "xml", "karate.prettyXml(response)", ctx, false);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><bar bbb=\"2\" aaa=\"1\"/></foo>", ctx).pass);
        Script.assign(AUTO, "temp", "karate.prettyXml(response)", ctx, false);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "temp", null, "\'<bar aaa=\"1\" bbb=\"2\"/>\'", ctx).pass);
    }

    @Test
    public void testMatchXmlRepeatedElements() {
        ScenarioContext ctx = getContext();
        String xml = "<foo><bar>baz1</bar><bar>baz2</bar></foo>";
        Document doc = XmlUtils.toXmlDoc(xml);
        ctx.vars.put(VAR_RESPONSE, doc);
        ScriptValue response = ctx.vars.get(VAR_RESPONSE);
        Assert.assertTrue(Script.matchXml(EQUALS, response, "/", "<foo><bar>baz1</bar><bar>baz2</bar></foo>", ctx).pass);
        Assert.assertTrue(Script.matchXml(EQUALS, response, "/foo/bar[2]", "'baz2'", ctx).pass);
        Assert.assertTrue(Script.matchXml(EQUALS, response, "/foo/bar[1]", "'baz1'", ctx).pass);
    }

    @Test
    public void testMatchXmlAttributeErrorReporting() {
        ScenarioContext ctx = getContext();
        Script.assign("xml", "<hello foo=\"bar\">world</hello>", ctx);
        ScriptValue xml = ctx.vars.get("xml");
        Assert.assertTrue(Script.matchXml(EQUALS, xml, "/", "<hello foo=\"bar\">world</hello>", ctx).pass);
        AssertionResult ar = Script.matchXml(EQUALS, xml, "/", "<hello foo=\"baz\">world</hello>", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("/hello/@foo"));
    }

    @Test
    public void testAssigningAndCallingFunctionThatUpdatesVars() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(){ return { bar: 'baz' } }", ctx);
        ScriptValue testFoo = ctx.vars.get("foo");
        Assert.assertEquals(JS_FUNCTION, testFoo.getType());
        Script.callAndUpdateConfigAndAlsoVarsIfMapReturned(false, "foo", null, ctx);
        ScriptValue testBar = ctx.vars.get("bar");
        Assert.assertEquals("baz", testBar.getValue());
    }

    @Test
    public void testAssigningAndCallingFunctionThatCanBeUsedToAssignVariable() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(){ return 'world' }", ctx);
        Script.assign("hello", "call foo", ctx);
        ScriptValue hello = ctx.vars.get("hello");
        Assert.assertEquals("world", hello.getValue());
    }

    @Test
    public void testAssigningAndCallingFunctionWithArgumentsThatCanBeUsedToAssignVariable() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(pre){ return pre + ' world' }", ctx);
        Script.assign("hello", "call foo 'hello'", ctx);
        ScriptValue hello = ctx.vars.get("hello");
        Assert.assertEquals("hello world", hello.getValue());
    }

    @Test
    public void testCallingFunctionThatTakesPrimitiveArgument() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(a){ return { bar: a } }", ctx);
        ScriptValue testFoo = ctx.vars.get("foo");
        Assert.assertEquals(JS_FUNCTION, testFoo.getType());
        Script.callAndUpdateConfigAndAlsoVarsIfMapReturned(false, "foo", "'hello'", ctx);
        ScriptValue testBar = ctx.vars.get("bar");
        Assert.assertEquals("hello", testBar.getValue());
    }

    @Test
    public void testCallingFunctionThatTakesJsonArgument() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(a){ return { bar: a.hello } }", ctx);
        ScriptValue testFoo = ctx.vars.get("foo");
        Assert.assertEquals(JS_FUNCTION, testFoo.getType());
        Script.callAndUpdateConfigAndAlsoVarsIfMapReturned(false, "foo", "{ hello: 'world' }", ctx);
        ScriptValue testBar = ctx.vars.get("bar");
        Assert.assertEquals("world", testBar.getValue());
    }

    @Test
    public void testCallingFunctionWithJsonArray() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(a){ return a[0] }", ctx);
        Script.assign("bar", "call foo ['hello']", ctx);
        ScriptValue bar = ctx.vars.get("bar");
        Assert.assertEquals("hello", bar.getValue());
    }

    @Test
    public void testCallingFunctionWithJavaList() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "function(a){ return a[0] }", ctx);
        Script.assign("bar", "['hello']", ctx);
        Script.assign("baz", "call foo bar", ctx);
        ScriptValue baz = ctx.vars.get("baz");
        Assert.assertEquals("hello", baz.getValue());
    }

    @Test
    public void testCallingFunctionThatUsesJsonPath() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ bar: [{baz: 1}, {baz: 2}, {baz: 3}]}", ctx);
        Script.assign("fun", "function(){ return karate.get('$foo.bar[*].baz') }", ctx);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "[1, 2, 3]", ctx).pass);
        // 'normal' variable name
        Script.assign("fun", "function(){ return karate.get('foo') }", ctx);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "{ bar: [{baz: 1}, {baz: 2}, {baz: 3}]}", ctx).pass);
    }

    @Test
    public void testCallingFunctionWithJsonArrayReturnedFromAnotherFunction() {
        ScenarioContext ctx = getContext();
        Script.assign("fun1", "function(){ return [1, 2, 3] }", ctx);
        Script.assign("res1", "call fun1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res1", null, "[1, 2, 3]", ctx).pass);
        Script.assign("fun2", "function(arg){ return arg.length }", ctx);
        Script.assign("res2", "call fun2 res1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res2", null, "3", ctx).pass);
    }

    @Test
    public void testCallingFunctionWithJsonReturnedFromAnotherFunction() {
        ScenarioContext ctx = getContext();
        Script.assign("fun1", "function(){ return { foo: 'bar' } }", ctx);
        Script.assign("res1", "call fun1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res1", null, "{ foo: 'bar' }", ctx).pass);
        Script.assign("fun2", "function(arg){ return arg.foo }", ctx);
        Script.assign("res2", "call fun2 res1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res2", null, "'bar'", ctx).pass);
    }

    @Test
    public void testCallingFunctionWithStringReturnedFromAnotherFunction() {
        ScenarioContext ctx = getContext();
        Script.assign("fun1", "function(){ return 'foo' }", ctx);
        Script.assign("res1", "call fun1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res1", null, "'foo'", ctx).pass);
        Script.assign("fun2", "function(arg){ return arg + 'bar' }", ctx);
        Script.assign("res2", "call fun2 res1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res2", null, "'foobar'", ctx).pass);
    }

    @Test
    public void testJsonReturnedFromJsRead() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return karate.read('classpath:test.json') }", ctx);
        Script.assign("val", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "val", null, "{ foo: 'bar' }", ctx).pass);
    }

    @Test
    public void testJsonFromJsRead() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ var temp = karate.read('classpath:test.json'); return temp.foo == 'bar'; }", ctx);
        Script.assign("val", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "val", null, "true", ctx).pass);
    }

    @Test
    public void testParsingVariableAndJsonPath() {
        Assert.assertEquals(StringUtils.pair("foo", "$"), Script.parseVariableAndPath("foo"));
        Assert.assertEquals(StringUtils.pair("foo", "$.bar"), Script.parseVariableAndPath("foo.bar"));
        Assert.assertEquals(StringUtils.pair("foo", "$['bar']"), Script.parseVariableAndPath("foo['bar']"));
        Assert.assertEquals(StringUtils.pair("foo", "$[0]"), Script.parseVariableAndPath("foo[0]"));
        Assert.assertEquals(StringUtils.pair("foo", "$[0].bar"), Script.parseVariableAndPath("foo[0].bar"));
        Assert.assertEquals(StringUtils.pair("foo", "$[0]['bar']"), Script.parseVariableAndPath("foo[0]['bar']"));
        Assert.assertEquals(StringUtils.pair("foo", "/bar"), Script.parseVariableAndPath("foo/bar"));
        Assert.assertEquals(StringUtils.pair("foo", "/"), Script.parseVariableAndPath("foo/"));
        Assert.assertEquals(StringUtils.pair("foo", "/bar/baz[1]/ban"), Script.parseVariableAndPath("foo/bar/baz[1]/ban"));
    }

    @Test
    public void testSetValueOnVariableByPath() {
        ScenarioContext ctx = getContext();
        // json
        Script.assign("json", "{ foo: 'bar' }", ctx);
        Script.setValueByPath("json", "$.foo", "'hello'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'hello' }", ctx).pass);
        Script.setValueByPath("json.foo", null, "null", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: null }", ctx).pass);
        Script.setValueByPath("json.foo", null, "'world'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'world' }", ctx).pass);
        Script.setValueByPath("json.bar[0]", null, "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'world', bar: [1] }", ctx).pass);
        Script.setValueByPath("json.bar[0]", null, "2", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'world', bar: [2] }", ctx).pass);
        Script.setValueByPath("json.bar[1]", null, "3", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'world', bar: [2, 3] }", ctx).pass);
        // json key that needs to be within quotes
        Script.assign("json", "{ 'bad-name': 'foo' }", ctx);
        Script.setValueByPath("json", "$['bad-name']", "'bar'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ 'bad-name': 'bar' }", ctx).pass);
        // json where parent nodes are built automatically
        Script.assign("json", "{}", ctx);
        Script.setValueByPath("json", "$.foo.bar", "'hello'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: { bar: 'hello' } }", ctx).pass);
        Script.assign("json", "[]", ctx);
        Script.setValueByPath("json", "$[0].a[0].c", "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "[{a:[{c:1}]}]", ctx).pass);
        // json append to arrays
        Script.assign("json", "[]", ctx);
        Script.setValueByPath("json", "$[]", "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "[1]", ctx).pass);
        Script.assign("json", "{ a: [] }", ctx);
        Script.setValueByPath("json", "$.a[]", "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: [1] }", ctx).pass);
        Script.assign("json", "{}", ctx);
        Script.setValueByPath("json", "$.a[]", "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: [1] }", ctx).pass);
        Script.assign("json", "{ a: [1] }", ctx);
        Script.setValueByPath("json", "$.a[]", "2", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: [1, 2] }", ctx).pass);
        // xml
        Script.assign("xml", "<root><foo>bar</foo></root>", ctx);
        Script.setValueByPath("xml", "/root/foo", "'hello'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo>hello</foo></root>", ctx).pass);
        Script.setValueByPath("xml/root/foo", null, "null", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo/></root>", ctx).pass);
        Script.setValueByPath("xml/root/foo", null, "'world'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo>world</foo></root>", ctx).pass);
        // xml where parent nodes are built automatically
        Script.assign("xml", "<root/>", ctx);
        Script.setValueByPath("xml", "/root/foo", "'hello'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo>hello</foo></root>", ctx).pass);
        Script.assign("xml", "<root/>", ctx);
        Script.setValueByPath("xml/root/foo/@bar", null, "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo bar=\"1\"/></root>", ctx).pass);
        Script.setValueByPath("xml/root/foo[2]", null, "1", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo bar=\"1\"/><foo>1</foo></root>", ctx).pass);
    }

    @Test
    public void testSetXmlChunkAutoConversion() {
        ScenarioContext ctx = getContext();
        Script.assign("xml", "<foo><bar></bar></foo>", ctx);
        Script.assign("chunk", "<hello>world</hello>", ctx);
        Script.setValueByPath("xml", "/foo/bar", "chunk", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<foo><bar><hello>world</hello></bar></foo>", ctx).pass);
    }

    @Test
    public void testDeleteValueOnVariableByPath() {
        ScenarioContext ctx = getContext();
        // json
        Script.assign("json", "{ foo: 'bar', baz: 'ban' }", ctx);
        Script.removeValueByPath("json", "$.baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'bar' }", ctx).pass);
        Script.setValueByPath("json.baz", null, "[1, 2, 3]", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'bar', baz: [1, 2, 3] }", ctx).pass);
        Script.removeValueByPath("json", "$.baz[1]", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: 'bar', baz: [1, 3] }", ctx).pass);
        // xml
        Script.assign("xml", "<root><foo>bar</foo></root>", ctx);
        Script.removeValueByPath("xml", "/root/foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root/>", ctx).pass);
        // xml attribute
        Script.assign("xml", "<root hello=\"world\"><foo>bar</foo></root>", ctx);
        Script.removeValueByPath("xml", "/root/@hello", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "xml", null, "<root><foo>bar</foo></root>", ctx).pass);
    }

    @Test
    public void testCallJsFunctionWithMap() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ foo: 'bar', hello: 'world' }", ctx);
        Script.assign("fun", "function(o){ return o }", ctx);
        Script.assign("res", "call fun json", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "json", ctx).pass);
    }

    @Test
    public void testDefaultValidators() {
        ScenarioContext ctx = getContext();
        DocumentContext doc = JsonUtils.toJsonDoc("{ foo: 'bar' }");
        ctx.vars.put("json", doc);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#ignore' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#notnull' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#regex^bar' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#regex ^bar' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#regex^baX' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#regex ^baX' }", ctx).pass);
        doc = JsonUtils.toJsonDoc("{ foo: null }");
        ctx.vars.put("json", doc);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#ignore' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#null' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#notnull' }", ctx).pass);
        doc = JsonUtils.toJsonDoc("{ foo: 'a9f7a56b-8d5c-455c-9d13-808461d17b91' }");
        ctx.vars.put("json", doc);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#uuid' }", ctx).pass);
        doc = JsonUtils.toJsonDoc("{ foo: 'a9f7a56b-8d5c-455c-9d13' }");
        ctx.vars.put("json", doc);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#uuid' }", ctx).pass);
        doc = JsonUtils.toJsonDoc("{ foo: 5 }");
        ctx.vars.put("json", doc);
        ctx.vars.put("min", 4);
        ctx.vars.put("max", 6);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#number' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#? _ == 5' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#? _ < 6' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#? _ > 4' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#? _ > 4 && _ < 6' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", "$", "{ foo: '#? _ > min && _ < max' }", ctx).pass);
    }

    @Test
    public void testStringThatStartsWithHashSymbol() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ bar: '#####' }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#####' }", ctx).pass);
    }

    @Test
    public void testSimpleJsonMatch() {
        ScenarioContext ctx = getContext();
        DocumentContext doc = JsonUtils.toJsonDoc("{ foo: 'bar' }");
        ctx.vars.put("json", doc);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", "$", "{ }", ctx).pass);
    }

    @Test
    public void testAssignJsonChunkObjectAndUse() {
        ScenarioContext ctx = getContext();
        // ===
        Script.assign("parent", "{ foo: 'bar', 'ban': { a: 1 } }", ctx);
        Script.assign("child", "parent.ban", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "child.a", null, "1", ctx).pass);
        // ===
        Script.assign("parent", "{ foo: 'bar', 'ban': { a: [1, 2, 3] } }", ctx);
        Script.assign("child", "parent.ban", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "child.a[1]", null, "2", ctx).pass);
    }

    @Test
    public void testAssignJsonChunkListAndUse() {
        ScenarioContext ctx = getContext();
        // ===
        Script.assign("parent", "{ foo: { bar: [{ baz: 1}, {baz: 2}, {baz: 3}] }}", ctx);
        Script.assign("child", "parent.foo", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "child", null, "{ bar: [{ baz: 1}, {baz: 2}, {baz: 3}]}", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "child.bar", null, "[{ baz: 1}, {baz: 2}, {baz: 3}]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "child.bar[0]", null, "{ baz: 1}", ctx).pass);
    }

    @Test
    public void testEvalUrl() {
        ScenarioContext ctx = getContext();
        String url = "'http://localhost:8089/v1/cats'";
        Assert.assertEquals("http://localhost:8089/v1/cats", Script.evalKarateExpression(url, ctx).getAsString());
    }

    @Test
    public void testEvalParamWithDot() {
        ScenarioContext ctx = getContext();
        String param = "'ACS.Itself'";
        Assert.assertEquals("ACS.Itself", Script.evalKarateExpression(param, ctx).getAsString());
    }

    @Test
    public void testMatchHandlesNonStringNullsGracefully() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ foo: null }", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json.foo", null, "[]", ctx).pass);
    }

    @Test
    public void testMatchJsonObjectContains() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ foo: 'bar', baz: [1, 2, 3] }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ baz: [1, 2, 3], foo: 'bar' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ baz: [1, 2, 3] }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ foo: 'bar' }", ctx).pass);
    }

    @Test
    public void testMatchJsonObjectPartialNotContains() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ a: 1, b: 2}", ctx);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ a: 1, b: 3 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ a: 1, b: '#string' }", ctx).pass);
    }

    @Test
    public void testMatchJsonArrayContains() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ bar: [1, 2, 3] }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.bar", null, "[1 ,2, 3]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "foo.bar", null, "[1]", ctx).pass);
    }

    @Test
    public void testMatchContainsForSingleElements() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ bar: [1, 2, 3] }", ctx);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "foo.bar", null, "1", ctx).pass);
        Script.assign("json", "[{ foo: 1 }, { foo: 2 }, { foo: 3 }]", ctx);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ foo: 1 }", ctx).pass);
        Script.assign("json", "[{ foo: 1 }]", ctx);
        Assert.assertTrue(Script.matchNamed(CONTAINS_ONLY, "json", null, "{ foo: 1 }", ctx).pass);
    }

    @Test
    public void testMatchJsonObjectErrorReporting() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ a: 1, b: 2, c: 3}", ctx);
        AssertionResult ar = Script.matchNamed(EQUALS, "json", null, "{ a: 1, c: 3 }", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("actual value has 1 more key"));
    }

    @Test
    public void testMatchJsonArrayErrorReporting() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "[{ foo: 1 }, { foo: 2 }, { foo: 3 }]", ctx);
        AssertionResult ar = Script.matchNamed(EQUALS, "json", null, "[{ foo: 1 }, { foo: 2 }, { foo: 4 }]", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("actual: 3, expected: 4"));
        ar = Script.matchNamed(CONTAINS, "json", null, "[{ foo: 1 }, { foo: 2 }, { foo: 4 }]", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("$[*]"));
        ar = Script.matchNamed(CONTAINS_ONLY, "json", null, "[{ foo: 3 }, { foo: 2 }, { foo: 0 }]", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("$[*]"));
        ar = Script.matchNamed(CONTAINS_ONLY, "json", null, "[{ foo: 3 }, { foo: 2 }, { foo: 1 }]", ctx);
        Assert.assertTrue(ar.pass);
        ar = Script.matchNamed(CONTAINS_ONLY, "json", null, "[{ foo: 3 }, { foo: 2 }]", ctx);
        Assert.assertFalse(ar.pass);
        Assert.assertTrue(ar.message.contains("not the same size"));
    }

    @Test
    public void testMatchStringEqualsAndContains() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "'hello world'", ctx);
        // assertTrue(Script.matchNamed(MatchType.EQUALS, "foo", null, "'hello world'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "foo", null, "'blah'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'blah'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "foo", null, "'hello world'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "foo", null, "'hello'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "foo", null, "'zoo'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "foo", null, "'blah'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_CONTAINS, "foo", null, "'world'", ctx).pass);
    }

    @Test
    public void testKarateEnvAccessFromScript() {
        FeatureContext featureContext = FeatureContext.forEnv("baz");
        CallContext callContext = new CallContext(null, true);
        ScenarioContext ctx = new ScenarioContext(featureContext, callContext, null);
        Script.assign("foo", "function(){ return karate.env }", ctx);
        Script.assign("bar", "call foo", ctx);
        ScriptValue bar = ctx.vars.get("bar");
        Assert.assertEquals("baz", bar.getValue());
        // null
        featureContext = FeatureContext.forEnv();
        ctx = new ScenarioContext(featureContext, callContext, null);
        Script.assign("foo", "function(){ return karate.env }", ctx);
        Script.assign("bar", "call foo", ctx);
        bar = ctx.vars.get("bar");
        Assert.assertNull(bar.getValue());
    }

    @Test
    public void testCallingFeatureWithNoArgument() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "call read('test-called.feature')", ctx);
        ScriptValue a = Script.evalJsonPathOnVarByName("foo", "$.a", ctx);
        Assert.assertEquals(1, a.getValue());
        ScriptValue b = Script.evalJsonPathOnVarByName("foo", "$.b", ctx);
        Assert.assertEquals(2, b.getValue());
    }

    @Test
    public void testCallingFeatureWithVarOverrides() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "call read('test-called.feature') { c: 3 }", ctx);
        ScriptValue a = Script.evalJsonPathOnVarByName("foo", "$.a", ctx);
        Assert.assertEquals(1, a.getValue());
        ScriptValue b = Script.evalJsonPathOnVarByName("foo", "$.b", ctx);
        Assert.assertEquals(2, b.getValue());
        ScriptValue c = Script.evalJsonPathOnVarByName("foo", "$.c", ctx);
        Assert.assertEquals(3, c.getValue());
    }

    @Test
    public void testCallingFeatureWithVarOverrideFromVariable() {
        ScenarioContext ctx = getContext();
        Script.assign("bar", "{ c: 3 }", ctx);
        Script.assign("foo", "call read('test-called.feature') bar", ctx);
        ScriptValue a = Script.evalJsonPathOnVarByName("foo", "$.a", ctx);
        Assert.assertEquals(1, a.getValue());
        ScriptValue b = Script.evalJsonPathOnVarByName("foo", "$.b", ctx);
        Assert.assertEquals(2, b.getValue());
        ScriptValue c = Script.evalJsonPathOnVarByName("foo", "$.c", ctx);
        Assert.assertEquals(3, c.getValue());
    }

    @Test
    public void testCallingFeatureWithList() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "call read('test-called.feature') [{c: 100}, {c: 200}, {c: 300}]", ctx);
        ScriptValue c0 = Script.evalJsonPathOnVarByName("foo", "$[0].c", ctx);
        Assert.assertEquals(100, c0.getValue());
        ScriptValue c1 = Script.evalJsonPathOnVarByName("foo", "$[1].c", ctx);
        Assert.assertEquals(200, c1.getValue());
        ScriptValue c2 = Script.evalJsonPathOnVarByName("foo", "$[2].c", ctx);
        Assert.assertEquals(300, c2.getValue());
    }

    @Test
    public void testCallingFeatureThatEvaluatesEmbeddedExpressions() {
        ScenarioContext ctx = getContext();
        Script.assign("result", "call read('test-called-embedded.feature') { foo: 'world' }", ctx);
        ScriptValue sv1 = Script.evalJsonPathOnVarByName("result", "$.json.hello", ctx);
        Assert.assertEquals("world", sv1.getValue());
        ScriptValue sv2 = Script.evalJsonPathOnVarByName("result", "$.xml.hello", ctx);
        Assert.assertEquals("world", sv2.getValue());
    }

    @Test
    public void testCallingFeatureThatEvaluatesEmbeddedExpressionsFromFileRead() {
        ScenarioContext ctx = getContext();
        Script.assign("result", "call read('test-called-embedded-file.feature') { foo: 'world' }", ctx);
        ScriptValue sv1 = Script.evalJsonPathOnVarByName("result", "$.json.hello", ctx);
        Assert.assertEquals("world", sv1.getValue());
        ScriptValue sv2 = Script.evalJsonPathOnVarByName("result", "$.xml.hello", ctx);
        Assert.assertEquals("world", sv2.getValue());
    }

    @Test
    public void testCallingFeatureWithJsonCreatedByJavaScript() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return { c: 100} }", ctx);
        Script.assign("res", "call fun", ctx);
        Script.assign("foo", "call read('test-called.feature') res", ctx);
        ScriptValue c = Script.evalJsonPathOnVarByName("foo", "$.c", ctx);
        Assert.assertEquals(100, c.getValue());
    }

    @Test
    public void testCallingFeatureWithJsonArrayCreatedByJavaScript() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return [{ c: 100}] }", ctx);
        Script.assign("res", "call fun", ctx);
        Script.assign("foo", "call read('test-called.feature') res", ctx);
        ScriptValue c = Script.evalJsonPathOnVarByName("foo", "$[0].c", ctx);
        Assert.assertEquals(100, c.getValue());
    }

    @Test
    public void testSetOnJsonArrayCreatedByJavaScript() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return [{a: 1}, {a: 2}, {b: 3}] }", ctx);
        Script.assign("json", "call fun", ctx);
        Script.setValueByPath("json[1].a", null, "5", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "[{a: 1}, {a: 5}, {b: 3}]", ctx).pass);
    }

    @Test
    public void testGetSyntaxForJson() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "[{baz: 1}, {baz: 2}, {baz: 3}]", ctx);
        Script.assign("nums", "get foo[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "nums", null, "[1, 2, 3]", ctx).pass);
        Script.assign("first", "get[0] foo[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "first", null, "1", ctx).pass);
        Script.assign("second", "get[1] foo[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "second", null, "2", ctx).pass);
        // alternative to get, usable in-line within match statements
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[*].baz", null, "$foo[*].baz", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "$foo[*].baz", null, "$foo[*].baz", ctx).pass);
        Script.assign("foo", "{ bar: [{baz: 1}, {baz: 2}, {baz: 3}]}", ctx);
        Script.assign("nums", "get foo.bar[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "nums", null, "[1, 2, 3]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.bar[*].baz", null, "$foo.bar[*].baz", ctx).pass);
        Script.assign("nums", "get foo $.bar[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "nums", null, "[1, 2, 3]", ctx).pass);
        Script.assign("response", "[{baz: 1}, {baz: 2}, {baz: 3}]", ctx);
        Script.assign("second", "get[1] $[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "second", null, "2", ctx).pass);
        Script.assign("third", "get[2] response $[*].baz", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "third", null, "3", ctx).pass);
    }

    @Test
    public void testGetSyntaxForXml() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "<records>\n  <record>a</record>\n  <record>b</record>\n  <record>c</record>\n</records>", ctx);
        Script.assign("count", "get foo count(//record)", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "count", null, "3", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "count", null, "3", ctx).pass);
    }

    @Test
    public void testFromJsKarateCallFeatureWithNoArg() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return karate.call('test-called.feature') }", ctx);
        Script.assign("res", "fun()", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res.a", null, "1", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res.b", null, "2", ctx).pass);
    }

    @Test
    public void testFromJsKarateCallFeatureWithJsonArg() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return karate.call('test-called.feature', {c: 3}) }", ctx);
        Script.assign("res", "fun()", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res.a", null, "1", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res.b", null, "2", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res.c", null, "3", ctx).pass);
    }

    @Test
    public void testFromJsKarateGetForNonExistentVariable() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ var foo = karate.get('foo'); return foo ? true : false }", ctx);
        Script.assign("res", "fun()", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "false", ctx).pass);
    }

    @Test
    public void testFromJsKarateGetForJsonArrayVariable() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ return [1, 2, 3] }", ctx);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "[1, 2, 3]", ctx).pass);
    }

    @Test
    public void testFromJsKarateGetForJsonObjectVariableAndCallFeatureAndJs() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "read('headers.js')", ctx);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "{ foo: 'bar_someValue' }", ctx).pass);
        Script.assign("signin", "call read('signin.feature')", ctx);
        Script.assign("ticket", "signin.ticket", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "ticket", null, "{ foo: 'bar' }", ctx).pass);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "{ foo: 'bar_someValue', baz: 'ban' }", ctx).pass);
    }

    @Test
    public void testFromJsKarateJsonPath() {
        ScenarioContext ctx = getContext();
        Script.assign("fun", "function(){ var foo = [{v:1},{v:2}]; return karate.jsonPath(foo, '$[*].v') }", ctx);
        Script.assign("res", "call fun", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "[1, 2]", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "karate.jsonPath([{v:1},{v:2}], '$[*].v')", ctx).pass);
        Script.assign("foo", "[{v:1},{v:2}]", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "karate.jsonPath(foo, '$[*].v')", ctx).pass);
    }

    @Test
    public void testAssigningRawTextWhichOtherwiseConfusesKarate() {
        ScenarioContext ctx = getContext();
        try {
            Script.assign("foo", "{ not json }", ctx);
            Assert.fail("we expected this to fail");
        } catch (InvalidJsonException e) {
            ScriptTest.logger.debug("expected {}", e.getMessage());
        }
        Script.assign(TEXT, "foo", "{ not json }", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'{ not json }'", ctx).pass);
    }

    @Test
    public void testBigDecimalsInJson() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ val: -1002.2000000000002 }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.2000000000002 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.2000000000002 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.2000000000001 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.2000000000001 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
        Script.assign("foo", "{ val: -1002.20 }", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.2000000000001 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.2000000000001 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.2000000000000 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.2000000000000 }", ctx).pass);
        Script.assign("foo", "{ val: -1002.2000000000001 }", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
        Script.assign("foo", "{ val: -1002.2000000000000 }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(NOT_EQUALS, "foo", null, "{ val: -1002.20 }", ctx).pass);
    }

    @Test
    public void testDollarInEmbeddedExpressions() {
        ScenarioContext ctx = getContext();
        Script.assign("temperature", "{ celsius: 100, fahrenheit: 212 }", ctx);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "temperature", null, "{ fahrenheit: 212 }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "temperature", null, "{ fahrenheit: '#($.celsius * 1.8 + 32)' }", ctx).pass);
    }

    @Test
    public void testValidationStringInsteadOfNumberInPredicate() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ bar: 5 }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#number' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#? _ == 5' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#? _ > 0' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#number? _ > 0' }", ctx).pass);
        Script.assign("foo", "{ bar: '5' }", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#number' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#? _ == 5' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#? _ === 5' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#? _ > 0' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "{ bar: '#number? _ > 0' }", ctx).pass);
    }

    @Test
    public void testMatchMacroArray() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "['bar', 'baz']", ctx);
        Script.assign("arr", "'#string'", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#array'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#number'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[]'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[2]'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[1]'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[_ == 2]'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[_ != 2]'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] arr'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (arr)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] #string'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] #number'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[2] #string'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[1] arr'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[1] (arr)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[2]? _.length == 3'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[2]? _.length == 4'", ctx).pass);
        // non-root path
        Script.assign("foo", "{ ban: ['bar', 'baz'], count: 2 }", ctx);
        Script.assign("len", "2", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[] arr'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[] (arr)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[2] arr'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[2] (arr)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[$.count] #string'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[foo.count] #string'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[len] #string'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo.ban", null, "'#[_ < 3]'", ctx).pass);
    }

    @Test
    public void testMatchMacroArrayComplex() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "[{ a: 1, b: 2 }, { a: 3, b: 4 }]", ctx);
        Script.assign("bar", "{ a: '#number', b: '#number' }", ctx);
        Script.assign("baz", "{ c: '#number' }", ctx);
        Script.assign("ban", "{ b: '#number' }", ctx);
        Assert.assertTrue(Script.matchNamed(EACH_EQUALS, "foo", null, "bar", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^*bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^ban)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^*ban)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^^bar)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^^ban)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo[0]", null, "'#(!^bar)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo[0]", null, "'#(!^ban)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(!^baz)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^*bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^^bar'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] ^^ban'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^*ban'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^ban'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] !^bar'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] !^ban'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] !^baz'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^*bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^ban)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^*ban)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^^bar)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] (^^ban)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] (!^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (!^baz)'", ctx).pass);
    }

    @Test
    public void testMatchMacroArrayComplexContains() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "[{ a: 1, b: 2 }, { a: 3, b: 4 }]", ctx);
        Script.assign("rev", "[{ a: 3, b: 4 }, { a: 1, b: 2 }]", ctx);
        Script.assign("part", "[{ a: 1, b: 2 }]", ctx);
        Script.assign("one", "{ a: 1, b: 2 }", ctx);
        Script.assign("nopes", "[{ a: 6, b: 7 }, { a: 8, b: 9 }]", ctx);
        Script.assign("nope", "{ a: 8, b: 9 }", ctx);
        Script.assign("bar", "{ b: '#number' }", ctx);
        Script.assign("baz", "{ c: '#number' }", ctx);
        Assert.assertFalse(Script.matchNamed(EACH_EQUALS, "foo", null, "bar", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo[0]", null, "'#(bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(^*bar)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo[0]", null, "'#(!^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo[0]", null, "'#(!^baz)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] ^bar'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] !^bar'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] !^baz'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] (bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (^bar)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#[] (!^bar)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#[] (!^baz)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(foo)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^foo)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^*foo)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^^foo)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^rev)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^^rev)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(rev)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^part)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^^part)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(!^part)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(^one)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^^one)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(!^one)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(nopes)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^nopes)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^^nopes)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(!^nopes)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(nope)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^nope)'", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "foo", null, "'#(^^nope)'", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "'#(!^nope)'", ctx).pass);
    }

    @Test
    public void testSchemaLikeAndOptionalKeys() {
        ScenarioContext ctx = getContext();
        Script.assign("child", "{ hello: '#string' }", ctx);
        Script.assign("json", "{ foo: 'bar', baz: [1, 2, 3]}", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '#[] #number' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '#[] #number', child: '##(child)' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '#[] #number', child: '#(child)' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '##[] #number' }", ctx).pass);
        Script.assign("json", "{ foo: 'bar', child: { hello: 'world' } }", ctx);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '#[] #number', child: '#(child)' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '##[] #number', child: '#(child)' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '##[] #number', child: '##(child)' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', blah: '##number', child: '#(child)' }", ctx).pass);
        Script.assign("json", "{ foo: 'bar', baz: null }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ foo: '#string', baz: '##string' }", ctx).pass);
        Script.assign("json", "null", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "'##string'", ctx).pass);
    }

    @Test
    public void testPresentNotPresentAndOptionalNulls() {
        ScenarioContext ctx = getContext();
        Script.assign("json", "{ }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: '##null' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ a: '#null' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ a: null }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "json", null, "{ a: '#present' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ a: '#present' }", ctx).pass);
        Script.assign("json", "{ a: 1 }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: '#present' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ a: '#present' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ a: null }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ a: '#null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ a: '##null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Script.assign("json", "{ a: null }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: null }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ a: null }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: '#null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "json", null, "{ a: '##null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ a: '#null' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(CONTAINS, "json", null, "{ a: '##null' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(EQUALS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_EQUALS, "json", null, "{ a: '#notpresent' }", ctx).pass);
        Assert.assertTrue(Script.matchNamed(NOT_CONTAINS, "json", null, "{ a: '#notpresent' }", ctx).pass);
    }

    @Test
    public void testReplace() {
        ScenarioContext ctx = getContext();
        Assert.assertEquals("foo", Script.replacePlaceholderText("foo", "foo", "'bar'", ctx));
        Assert.assertEquals("bar", Script.replacePlaceholderText("<foo>", "foo", "'bar'", ctx));
        Assert.assertEquals("bar", Script.replacePlaceholderText("<foo>", "foo", "'bar'", ctx));
        Assert.assertEquals("bar", Script.replacePlaceholderText("@@foo@@", "@@foo@@", "'bar'", ctx));
        Assert.assertEquals("bar bar bar", Script.replacePlaceholderText("<foo> <foo> <foo>", "foo", "'bar'", ctx));
    }

    @Test
    public void testEvalFromJs() {
        ScenarioContext ctx = getContext();
        Script.assign("temperature", "{ celsius: 100, fahrenheit: 212 }", ctx);
        Script.assign("res", "karate.eval('temperature.celsius')", ctx);
        Script.assign("bool", "karate.eval('temperature.celsius == 100')", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "res", null, "100", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "bool", null, "true", ctx).pass);
        Assert.assertTrue(Script.matchNamed(EQUALS, "temperature.fahrenheit", null, "karate.eval('temperature.celsius * 1.8 + 32')", ctx).pass);
    }

    @Test
    public void testRemoveIfNullMultiple() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ first: 'bar', second: '##(null)', third: '##(null)' }", ctx);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{ first: 'bar' }", ctx).pass);
    }

    @Test
    public void testMatchingIsStrictForDataTypes() {
        ScenarioContext ctx = getContext();
        Script.assign("foo", "{ a: '5', b: 5, c: true, d: 'true' }", ctx);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "foo", null, "{ a: 5 }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "foo", null, "{ b: '5' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "foo", null, "{ c: 'true' }", ctx).pass);
        Assert.assertFalse(Script.matchNamed(CONTAINS, "foo", null, "{ d: true }", ctx).pass);
    }

    @Test
    public void testTypeConversion() {
        ScenarioContext ctx = getContext();
        Script.assign(JSON, "foo", "[]", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "[]", ctx).pass);
        Script.assign(JSON, "foo", "{}", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "foo", null, "{}", ctx).pass);
    }

    @Test
    public void testBinaryMatching() {
        ScenarioContext ctx = getContext();
        Script.assign(BYTE_ARRAY, "data", "read('file:src/main/resources/karate-logo.png')", ctx, true);
        Assert.assertTrue(Script.matchNamed(EQUALS, "data", null, "read('file:src/main/resources/karate-logo.png')", ctx).pass);
    }
}

