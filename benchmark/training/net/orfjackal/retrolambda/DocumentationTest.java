/**
 * Copyright ? 2013-2015 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda;


import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class DocumentationTest {
    @Test
    public void README_contains_the_usage_instructions() throws IOException {
        String readme = DocumentationTest.toString(DocumentationTest.findInClosestParentDir("README.md"));
        String help = new SystemPropertiesConfig(new Properties()).getHelp();
        Assert.assertTrue(("Expected README to contain the following text:\n\n" + help), readme.contains(help));
    }
}

