package net.bytebuddy.implementation;


import net.bytebuddy.ClassFileVersion;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.implementation.Implementation.Target.AbstractBase.DefaultMethodInvocation.DISABLED;
import static net.bytebuddy.implementation.Implementation.Target.AbstractBase.DefaultMethodInvocation.ENABLED;
import static net.bytebuddy.implementation.Implementation.Target.AbstractBase.DefaultMethodInvocation.of;


public class ImplementationTargetAbstractBaseDefaultMethodInvocationTest {
    @Test
    public void testEnabled() throws Exception {
        MatcherAssert.assertThat(of(ClassFileVersion.JAVA_V8), CoreMatchers.is(ENABLED));
    }

    @Test
    public void testDisabled() throws Exception {
        MatcherAssert.assertThat(of(ClassFileVersion.JAVA_V7), CoreMatchers.is(DISABLED));
    }
}

