package net.bytebuddy.description.annotation;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.annotation.AnnotationSource.Empty.INSTANCE;


public class AnnotationSourceTest {
    @Test
    public void testEmpty() throws Exception {
        Assert.assertThat(INSTANCE.getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testExplicit() throws Exception {
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        Assert.assertThat(new AnnotationSource.Explicit(annotationDescription).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        Assert.assertThat(new AnnotationSource.Explicit(annotationDescription).getDeclaredAnnotations().getOnly(), CoreMatchers.is(annotationDescription));
    }
}

