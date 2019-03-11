package com.intuit.karate;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class RunnerOptionsTest {
    @Test
    public void testArgs() {
        RunnerOptions options = RunnerOptions.parseStringArgs(new String[]{  });
        Assert.assertNull(options.features);
        Assert.assertNull(options.tags);
        Assert.assertNull(options.name);
        options = RunnerOptions.parseStringArgs(new String[]{ "--name", "foo" });
        Assert.assertNull(options.features);
        Assert.assertNull(options.tags);
        Assert.assertEquals("foo", options.name);
        options = RunnerOptions.parseStringArgs(new String[]{ "--tags", "~@ignore" });
        Assert.assertNull(options.features);
        Assert.assertEquals("~@ignore", options.tags.get(0));
        Assert.assertNull(options.name);
        options = RunnerOptions.parseStringArgs(new String[]{ "--tags", "~@ignore", "foo.feature" });
        Assert.assertEquals("foo.feature", options.features.get(0));
        Assert.assertEquals("~@ignore", options.tags.get(0));
        Assert.assertNull(options.name);
    }

    @Test
    public void testParsingCommandLine() {
        RunnerOptions options = RunnerOptions.parseCommandLine(IdeUtilsTest.INTELLIJ1);
        Assert.assertEquals("^get users and then get first by id$", options.getName());
        Assert.assertNull(options.getTags());
        Assert.assertEquals(1, options.getFeatures().size());
        Assert.assertEquals("/Users/pthomas3/dev/zcode/karate/karate-junit4/src/test/java/com/intuit/karate/junit4/demos/users.feature", options.getFeatures().get(0));
        options = RunnerOptions.parseCommandLine(IdeUtilsTest.ECLIPSE1);
        Assert.assertEquals(1, options.getFeatures().size());
        Assert.assertEquals("/Users/pthomas3/dev/zcode/karate/karate-junit4/src/test/resources/com/intuit/karate/junit4/demos/users.feature", options.getFeatures().get(0));
    }
}

