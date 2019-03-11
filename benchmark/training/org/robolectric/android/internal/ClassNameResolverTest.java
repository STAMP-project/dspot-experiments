package org.robolectric.android.internal;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.shadows.ClassNameResolver;
import org.robolectric.shadows.testing.TestApplication;


@RunWith(JUnit4.class)
public class ClassNameResolverTest {
    @Test
    public void shouldResolveClassesBySimpleName() throws Exception {
        Assert.assertEquals(TestApplication.class, ClassNameResolver.resolve("org.robolectric.shadows.testing", "TestApplication"));
    }

    @Test
    public void shouldResolveClassesByDottedSimpleName() throws Exception {
        Assert.assertEquals(TestApplication.class, ClassNameResolver.resolve("org.robolectric.shadows.testing", ".TestApplication"));
    }

    @Test
    public void shouldResolveClassesByFullyQualifiedName() throws Exception {
        Assert.assertEquals(TestApplication.class, ClassNameResolver.resolve("org.robolectric.shadows.testing", "org.robolectric.shadows.testing.TestApplication"));
    }

    @Test
    public void shouldResolveClassesByPartiallyQualifiedName() throws Exception {
        Assert.assertEquals(TestApplication.class, ClassNameResolver.resolve("org", ".robolectric.shadows.testing.TestApplication"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldNotResolveClassesByUndottedPartiallyQualifiedNameBecauseAndroidDoesnt() throws Exception {
        ClassNameResolver.resolve("org", "robolectric.shadows.testing.TestApplication");
    }
}

