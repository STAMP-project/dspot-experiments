package com.github.mustachejava;


import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public final class AmplNestedPartialTest {
    private static final String TEMPLATE_FILE = "nested_partials_template.html";

    private static File root;

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplNestedPartialTest.root = (new File(file, AmplNestedPartialTest.TEMPLATE_FILE).exists()) ? file : new File("src/test/resources");
    }

    @Test(timeout = 10000)
    public void should_handle_more_than_one_level_of_partial_nestingnull20_failAssert0() throws Exception {
        try {
            MustacheFactory factory = new DefaultMustacheFactory(AmplNestedPartialTest.root);
            Mustache maven = factory.compile(AmplNestedPartialTest.TEMPLATE_FILE);
            StringWriter sw = new StringWriter();
            maven.execute(null, new Object() {
                List<String> messages = Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            sw.toString();
            org.junit.Assert.fail("should_handle_more_than_one_level_of_partial_nestingnull20 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Failed to get value for . @[/relative/nested_partials_subsub.html:1]", expected.getMessage());
        }
    }
}

