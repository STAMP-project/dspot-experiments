package net.bytebuddy.description.annotation;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.RESOLVED;
import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.UNDEFINED;
import static net.bytebuddy.description.annotation.AnnotationValue.Loaded.State.UNRESOLVED;


public class AnnotationDescriptionAnnotationValueLoadedStateTest {
    @Test
    public void testIsDefined() throws Exception {
        MatcherAssert.assertThat(RESOLVED.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(UNRESOLVED.isResolved(), CoreMatchers.is(false));
        MatcherAssert.assertThat(UNDEFINED.isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testIsResolved() throws Exception {
        MatcherAssert.assertThat(RESOLVED.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(UNRESOLVED.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(UNDEFINED.isDefined(), CoreMatchers.is(false));
    }
}

