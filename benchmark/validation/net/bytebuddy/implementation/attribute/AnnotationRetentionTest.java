package net.bytebuddy.implementation.attribute;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AnnotationRetentionTest {
    private final AnnotationRetention annotationRetention;

    private final boolean enabled;

    public AnnotationRetentionTest(AnnotationRetention annotationRetention, boolean enabled) {
        this.annotationRetention = annotationRetention;
        this.enabled = enabled;
    }

    @Test
    public void testEnabled() throws Exception {
        MatcherAssert.assertThat(annotationRetention.isEnabled(), CoreMatchers.is(enabled));
    }

    @Test
    public void testRetention() throws Exception {
        MatcherAssert.assertThat(AnnotationRetention.of(enabled), CoreMatchers.is(annotationRetention));
    }
}

