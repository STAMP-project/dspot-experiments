package io.fabric8.maven.docker.log;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author roland
 * @since 04.11.17
 */
@RunWith(Parameterized.class)
public class LogOutputSpecFactoryTest {
    private static String ALIAS = "fcn";

    private static String NAME = "rhuss/fcn:1.0";

    private static String CONTAINER_ID = "1234567890";

    @Parameterized.Parameter(0)
    public String prefixFormat;

    @Parameterized.Parameter(1)
    public String expectedPrefix;

    @Test
    public void prefix() {
        LogOutputSpec spec = createSpec(prefixFormat);
        Assert.assertEquals(expectedPrefix, spec.getPrompt(false, null));
    }
}

