package com.github.jknack.handlebars;


import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Test;


public class LazyPartialBlockEvaluationTest extends AbstractTest {
    @Test
    public void shouldSupportMultipleLevelsOfNestedPartialBlocks() throws IOException {
        String myMoreNestedPartial = "I{{> @partial-block}}I";
        String myNestedPartial = "A{{#> myMoreNestedPartial}}{{> @partial-block}}{{/myMoreNestedPartial}}B";
        String myPartial = "{{#> myNestedPartial}}{{> @partial-block}}{{/myNestedPartial}}";
        Template t = compile("C{{#> myPartial}}hello{{/myPartial}}D", new AbstractTest.Hash(), AbstractTest.$("myPartial", myPartial, "myNestedPartial", myNestedPartial, "myMoreNestedPartial", myMoreNestedPartial));
        String result = t.apply(null);
        TestCase.assertEquals((("'CAIhelloIBD' should === '" + result) + "': "), "CAIhelloIBD", result);
    }

    @Test(expected = HandlebarsException.class)
    public void shouldNotDefineInlinePartialsInPartialBlockCall() throws IOException {
        // myPartial should not be defined and thus throw a handlebars exception
        shouldCompileToWithPartials("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}", AbstractTest.$, AbstractTest.$("dude", "{{> myPartial }}"), "");
    }
}

