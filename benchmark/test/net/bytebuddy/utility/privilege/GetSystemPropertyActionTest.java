package net.bytebuddy.utility.privilege;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GetSystemPropertyActionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testRun() throws Exception {
        System.setProperty(GetSystemPropertyActionTest.FOO, GetSystemPropertyActionTest.BAR);
        try {
            Assert.assertThat(new GetSystemPropertyAction(GetSystemPropertyActionTest.FOO).run(), CoreMatchers.is(GetSystemPropertyActionTest.BAR));
        } finally {
            System.clearProperty(GetSystemPropertyActionTest.FOO);
        }
    }
}

