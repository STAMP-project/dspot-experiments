package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.test.visibility.Sample;
import net.bytebuddy.test.visibility.child.Child;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractPackageDescriptionTest {
    @Test
    public void testTrivialPackage() throws Exception {
        MatcherAssert.assertThat(describe(Child.class).getName(), CoreMatchers.is(Child.class.getPackage().getName()));
        MatcherAssert.assertThat(describe(Child.class).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
    }

    @Test
    public void testNonTrivialPackage() throws Exception {
        MatcherAssert.assertThat(describe(Sample.class).getName(), CoreMatchers.is(Sample.class.getPackage().getName()));
        MatcherAssert.assertThat(describe(Sample.class).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(Sample.class.getPackage().getDeclaredAnnotations())))));
    }

    @Test
    public void testPackageContains() throws Exception {
        MatcherAssert.assertThat(describe(Child.class).contains(of(Child.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).contains(of(Child.class)), CoreMatchers.is(false));
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(Child.class).hashCode(), CoreMatchers.is(Child.class.getPackage().hashCode()));
        MatcherAssert.assertThat(describe(Child.class).hashCode(), CoreMatchers.is(describe(Child.class).hashCode()));
        MatcherAssert.assertThat(describe(Child.class).hashCode(), CoreMatchers.not(describe(Sample.class).hashCode()));
        MatcherAssert.assertThat(describe(Sample.class).hashCode(), CoreMatchers.is(Sample.class.getPackage().hashCode()));
        MatcherAssert.assertThat(describe(Sample.class).hashCode(), CoreMatchers.is(describe(Sample.class).hashCode()));
        MatcherAssert.assertThat(describe(Sample.class).hashCode(), CoreMatchers.not(describe(Child.class).hashCode()));
    }

    @Test
    public void testEquals() throws Exception {
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.not(CoreMatchers.equalTo(null)));
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.is(describe(Child.class).toString()));
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.not(describe(Sample.class).toString()));
        MatcherAssert.assertThat(describe(Sample.class).toString(), CoreMatchers.is(describe(Sample.class).toString()));
        MatcherAssert.assertThat(describe(Sample.class).toString(), CoreMatchers.not(describe(Child.class).toString()));
    }

    @Test
    public void testToString() throws Exception {
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.is(Child.class.getPackage().toString()));
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.is(describe(Child.class).toString()));
        MatcherAssert.assertThat(describe(Child.class).toString(), CoreMatchers.not(describe(Sample.class).toString()));
        MatcherAssert.assertThat(describe(Sample.class).toString(), CoreMatchers.is(Sample.class.getPackage().toString()));
        MatcherAssert.assertThat(describe(Sample.class).toString(), CoreMatchers.is(describe(Sample.class).toString()));
        MatcherAssert.assertThat(describe(Sample.class).toString(), CoreMatchers.not(describe(Child.class).toString()));
    }
}

