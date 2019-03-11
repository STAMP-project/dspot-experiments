package net.bytebuddy;


import java.util.Collection;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class ClassFileVersionKnownVersionsTest {
    private final int javaVersion;

    private final int derivedVersion;

    private final Collection<String> javaVersionStrings;

    private final int minorMajorVersion;

    private final int majorVersion;

    private final int minorVersion;

    private final boolean atLeastJava5;

    private final boolean atLeastJava7;

    private final boolean atLeastJava8;

    public ClassFileVersionKnownVersionsTest(int javaVersion, int derivedVersion, Collection<String> javaVersionStrings, int minorMajorVersion, int majorVersion, int minorVersion, boolean atLeastJava5, boolean atLeastJava7, boolean atLeastJava8) {
        this.javaVersion = javaVersion;
        this.derivedVersion = derivedVersion;
        this.javaVersionStrings = javaVersionStrings;
        this.minorMajorVersion = minorMajorVersion;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.atLeastJava5 = atLeastJava5;
        this.atLeastJava7 = atLeastJava7;
        this.atLeastJava8 = atLeastJava8;
    }

    @Test
    public void testVersion() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).getMinorMajorVersion(), CoreMatchers.is(minorMajorVersion));
    }

    @Test
    public void testMinorVersion() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).getMinorVersion(), CoreMatchers.is(minorVersion));
    }

    @Test
    public void testMajorVersion() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).getMajorVersion(), CoreMatchers.is(majorVersion));
    }

    @Test
    public void testAtLeastJava5() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isAtLeast(ClassFileVersion.JAVA_V5), CoreMatchers.is(atLeastJava5));
    }

    @Test
    public void testAtLeastJava7() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isAtLeast(ClassFileVersion.JAVA_V7), CoreMatchers.is(atLeastJava7));
    }

    @Test
    public void testAtLeastJava8() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isAtLeast(ClassFileVersion.JAVA_V8), CoreMatchers.is(atLeastJava8));
    }

    @Test
    public void testLessThanJava8() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isLessThan(ClassFileVersion.JAVA_V8), CoreMatchers.is((!(atLeastJava8))));
    }

    @Test
    public void testAtMostJava8() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isAtMost(ClassFileVersion.JAVA_V8), CoreMatchers.is(((!(atLeastJava8)) || ((javaVersion) == 8))));
    }

    @Test
    public void testGreaterThanJava8() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).isGreaterThan(ClassFileVersion.JAVA_V8), CoreMatchers.is(((atLeastJava8) && ((javaVersion) != 8))));
    }

    @Test
    public void testJavaVersion() throws Exception {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).getJavaVersion(), CoreMatchers.is(derivedVersion));
    }

    @Test
    public void testJavaVersionString() throws Exception {
        for (String javaVersionString : javaVersionStrings) {
            MatcherAssert.assertThat(ofJavaVersionString(javaVersionString).getJavaVersion(), CoreMatchers.is(derivedVersion));
        }
    }

    @Test
    public void testSimpleClassCreation() throws Exception {
        ClassFileVersion classFileVersion = ClassFileVersion.ofJavaVersion(javaVersion);
        if ((ClassFileVersion.ofThisVm().compareTo(classFileVersion)) >= 0) {
            Class<?> type = new ByteBuddy(classFileVersion).subclass(ClassFileVersionKnownVersionsTest.Foo.class).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
            MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        }
    }

    @Test
    public void testNotPreview() throws Exception {
        MatcherAssert.assertThat(isPreviewVersion(), CoreMatchers.is(false));
    }

    @Test
    public void testPreview() throws Exception {
        ClassFileVersion classFileVersion = asPreviewVersion();
        MatcherAssert.assertThat(classFileVersion.getJavaVersion(), CoreMatchers.is(javaVersion));
        MatcherAssert.assertThat(isPreviewVersion(), CoreMatchers.is(true));
    }

    @Test
    public void testToString() {
        MatcherAssert.assertThat(ClassFileVersion.ofJavaVersion(javaVersion).toString(), CoreMatchers.is(("Java " + (derivedVersion))));
    }

    /* empty */
    public static class Foo {}
}

