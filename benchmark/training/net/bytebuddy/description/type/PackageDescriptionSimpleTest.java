package net.bytebuddy.description.type;


import net.bytebuddy.description.annotation.AnnotationList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PackageDescriptionSimpleTest {
    private static final String FOO = "foo";

    @Test
    public void testPackageName() throws Exception {
        MatcherAssert.assertThat(new PackageDescription.Simple(PackageDescriptionSimpleTest.FOO).getName(), CoreMatchers.is(PackageDescriptionSimpleTest.FOO));
    }

    @Test
    public void testPackageAnnotations() throws Exception {
        MatcherAssert.assertThat(new PackageDescription.Simple(PackageDescriptionSimpleTest.FOO).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
    }
}

