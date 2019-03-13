package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class VisibilityExpansionTest {
    @Test
    public void testPublic() throws Exception {
        MatcherAssert.assertThat(Visibility.PUBLIC.expandTo(Visibility.PRIVATE), CoreMatchers.is(Visibility.PUBLIC));
        MatcherAssert.assertThat(Visibility.PUBLIC.expandTo(Visibility.PACKAGE_PRIVATE), CoreMatchers.is(Visibility.PUBLIC));
        MatcherAssert.assertThat(Visibility.PUBLIC.expandTo(Visibility.PROTECTED), CoreMatchers.is(Visibility.PUBLIC));
        MatcherAssert.assertThat(Visibility.PUBLIC.expandTo(Visibility.PUBLIC), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testProtected() throws Exception {
        MatcherAssert.assertThat(Visibility.PROTECTED.expandTo(Visibility.PRIVATE), CoreMatchers.is(Visibility.PROTECTED));
        MatcherAssert.assertThat(Visibility.PROTECTED.expandTo(Visibility.PACKAGE_PRIVATE), CoreMatchers.is(Visibility.PROTECTED));
        MatcherAssert.assertThat(Visibility.PROTECTED.expandTo(Visibility.PROTECTED), CoreMatchers.is(Visibility.PROTECTED));
        MatcherAssert.assertThat(Visibility.PROTECTED.expandTo(Visibility.PUBLIC), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testPackagePrivate() throws Exception {
        MatcherAssert.assertThat(Visibility.PACKAGE_PRIVATE.expandTo(Visibility.PRIVATE), CoreMatchers.is(Visibility.PACKAGE_PRIVATE));
        MatcherAssert.assertThat(Visibility.PACKAGE_PRIVATE.expandTo(Visibility.PACKAGE_PRIVATE), CoreMatchers.is(Visibility.PACKAGE_PRIVATE));
        MatcherAssert.assertThat(Visibility.PACKAGE_PRIVATE.expandTo(Visibility.PROTECTED), CoreMatchers.is(Visibility.PROTECTED));
        MatcherAssert.assertThat(Visibility.PACKAGE_PRIVATE.expandTo(Visibility.PUBLIC), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testPrivate() throws Exception {
        MatcherAssert.assertThat(Visibility.PRIVATE.expandTo(Visibility.PRIVATE), CoreMatchers.is(Visibility.PRIVATE));
        MatcherAssert.assertThat(Visibility.PRIVATE.expandTo(Visibility.PACKAGE_PRIVATE), CoreMatchers.is(Visibility.PACKAGE_PRIVATE));
        MatcherAssert.assertThat(Visibility.PRIVATE.expandTo(Visibility.PROTECTED), CoreMatchers.is(Visibility.PROTECTED));
        MatcherAssert.assertThat(Visibility.PRIVATE.expandTo(Visibility.PUBLIC), CoreMatchers.is(Visibility.PUBLIC));
    }
}

