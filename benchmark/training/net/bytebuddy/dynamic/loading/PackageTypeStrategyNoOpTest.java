package net.bytebuddy.dynamic.loading;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.NoOp.INSTANCE;


public class PackageTypeStrategyNoOpTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testPackageNotDefined() throws Exception {
        MatcherAssert.assertThat(INSTANCE.define(getClass().getClassLoader(), PackageTypeStrategyNoOpTest.FOO, PackageTypeStrategyNoOpTest.BAR).isDefined(), CoreMatchers.is(false));
    }
}

