package net.bytebuddy.agent.builder;


import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.agent.builder.AgentBuilder.RawMatcher.ForResolvableTypes.INSTANCE;


public class RawMatcherForResolvableTypesTest {
    @Test
    public void testUnloadedMatches() throws Exception {
        Assert.assertThat(INSTANCE.matches(of(RawMatcherForResolvableTypesTest.Foo.class), RawMatcherForResolvableTypesTest.Foo.class.getClassLoader(), JavaModule.ofType(RawMatcherForResolvableTypesTest.Foo.class), null, RawMatcherForResolvableTypesTest.Foo.class.getProtectionDomain()), CoreMatchers.is(true));
    }

    @Test
    public void testResolvableMatches() throws Exception {
        Assert.assertThat(INSTANCE.matches(of(RawMatcherForResolvableTypesTest.Foo.class), RawMatcherForResolvableTypesTest.Foo.class.getClassLoader(), JavaModule.ofType(RawMatcherForResolvableTypesTest.Foo.class), RawMatcherForResolvableTypesTest.Foo.class, RawMatcherForResolvableTypesTest.Foo.class.getProtectionDomain()), CoreMatchers.is(true));
    }

    @Test
    public void testUnresolvableDoesNotMatch() throws Exception {
        Assert.assertThat(INSTANCE.matches(of(RawMatcherForResolvableTypesTest.Bar.class), RawMatcherForResolvableTypesTest.Bar.class.getClassLoader(), JavaModule.ofType(RawMatcherForResolvableTypesTest.Bar.class), RawMatcherForResolvableTypesTest.Bar.class, RawMatcherForResolvableTypesTest.Bar.class.getProtectionDomain()), CoreMatchers.is(false));
    }

    /* empty */
    private static class Foo {}

    private static class Bar {
        static {
            if (true) {
                throw new AssertionError();
            }
        }
    }
}

