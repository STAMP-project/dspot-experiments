package net.bytebuddy.implementation;


import net.bytebuddy.description.modifier.Visibility;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.DEFAULT;
import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.PUBLIC;


public class MethodAccessorFactoryAccessTypeTest {
    @Test
    public void testVisibility() throws Exception {
        MatcherAssert.assertThat(DEFAULT.getVisibility(), CoreMatchers.is(Visibility.PACKAGE_PRIVATE));
        MatcherAssert.assertThat(PUBLIC.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }
}

