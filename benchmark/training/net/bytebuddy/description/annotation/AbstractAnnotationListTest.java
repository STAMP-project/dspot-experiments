package net.bytebuddy.description.annotation;


import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.AbstractFilterableListTest;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractAnnotationListTest<U> extends AbstractFilterableListTest<AnnotationDescription, AnnotationList, U> {
    @Test
    public void testAnnotationIsPresent() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).isAnnotationPresent(AbstractAnnotationListTest.Foo.class), CoreMatchers.is(true));
    }

    @Test
    public void testAnnotationIsNotPresent() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).isAnnotationPresent(Annotation.class), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationIsPresentDescription() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).isAnnotationPresent(of(AbstractAnnotationListTest.Foo.class)), CoreMatchers.is(true));
    }

    @Test
    public void testAnnotationIsNotPresentDescription() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).isAnnotationPresent(of(Annotation.class)), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationOfType() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).ofType(AbstractAnnotationListTest.Foo.class), CoreMatchers.is(AnnotationDescription.ForLoadedAnnotation.of(AbstractAnnotationListTest.Holder.class.getAnnotation(AbstractAnnotationListTest.Foo.class))));
    }

    @Test
    public void testAnnotationOfTypeWrongType() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).ofType(Annotation.class), CoreMatchers.nullValue(AnnotationDescription.Loadable.class));
    }

    @Test
    public void testAnnotationOfTypeDescription() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).ofType(of(AbstractAnnotationListTest.Foo.class)), CoreMatchers.is(((AnnotationDescription) (AnnotationDescription.ForLoadedAnnotation.of(AbstractAnnotationListTest.Holder.class.getAnnotation(AbstractAnnotationListTest.Foo.class))))));
    }

    @Test
    public void testAnnotationWrongTypeOfTypeDescription() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).ofType(of(Annotation.class)), CoreMatchers.nullValue(AnnotationDescription.class));
    }

    @Test
    public void testInherited() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).inherited(Collections.<TypeDescription>emptySet()), CoreMatchers.is(asList(getFirst())));
    }

    @Test
    public void testInheritedIgnoreType() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).inherited(Collections.<TypeDescription>singleton(of(AbstractAnnotationListTest.Foo.class))).size(), CoreMatchers.is(0));
    }

    @Test
    public void testInheritedIgnoreNonInherited() throws Exception {
        MatcherAssert.assertThat(asList(getSecond()).inherited(Collections.<TypeDescription>emptySet()).size(), CoreMatchers.is(0));
    }

    @Test
    public void testVisible() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).visibility(ElementMatchers.is(RetentionPolicy.RUNTIME)), CoreMatchers.is(asList(getFirst())));
    }

    @Test
    public void testNotVisible() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).visibility(ElementMatchers.is(RetentionPolicy.SOURCE)).size(), CoreMatchers.is(0));
    }

    @Test
    public void testAsTypeList() throws Exception {
        MatcherAssert.assertThat(asList(getFirst()).asTypeList(), CoreMatchers.is(Collections.singletonList(asElement(getFirst()).getAnnotationType())));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    protected @interface Foo {}

    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Bar {}

    /* empty */
    @AbstractAnnotationListTest.Foo
    @AbstractAnnotationListTest.Bar
    public static class Holder {}
}

