package com.intuit.karate;


import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class MatchStepTest {
    @Test
    public void testMatchStep() {
        test("hello ==", EQUALS, "hello", null, null);
        test("hello world == foo", EQUALS, "hello", "world", "foo");
        test("each hello world == foo", EACH_EQUALS, "hello", "world", "foo");
        test("hello.foo(bar) != blah", NOT_EQUALS, "hello.foo(bar)", null, "blah");
        test("foo count(/records//record) contains any blah", CONTAINS_ANY, "foo", "count(/records//record)", "blah");
        test("__arg == karate.get('foos[' + __loop + ']')", EQUALS, "__arg", null, "karate.get('foos[' + __loop + ']')");
        test("response $[?(@.b=='ab')] == '#[1]'", EQUALS, "response", "$[?(@.b=='ab')]", "'#[1]'");
        test("test != '#? _.length == 2'", NOT_EQUALS, "test", null, "'#? _.length == 2'");
        test("actual[0] !contains badSchema", NOT_CONTAINS, "actual[0]", null, "badSchema");
        test("actual[0] contains badSchema", CONTAINS, "actual[0]", null, "badSchema");
        test("driver.eval(\'{ foo: \"bar\" }\') == { hello: \'world\' }", EQUALS, "driver.eval(\'{ foo: \"bar\" }\')", null, "{ hello: 'world' }");
    }
}

