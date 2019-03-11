package net.bytebuddy.description.annotation;


import java.lang.annotation.Annotation;
import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class AnnotationListEmptyTest {
    @Test
    public void testAnnotationIsPresent() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().isAnnotationPresent(Annotation.class), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationIsPresentDescription() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().isAnnotationPresent(of(Annotation.class)), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationOfType() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().ofType(Annotation.class), CoreMatchers.nullValue(AnnotationDescription.Loadable.class));
    }

    @Test
    public void testAnnotationInherited() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().inherited(Collections.<TypeDescription>emptySet()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testAnnotationVisibility() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().visibility(ElementMatchers.none()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testAsTypeList() throws Exception {
        MatcherAssert.assertThat(new AnnotationList.Empty().asTypeList().size(), CoreMatchers.is(0));
    }
}

