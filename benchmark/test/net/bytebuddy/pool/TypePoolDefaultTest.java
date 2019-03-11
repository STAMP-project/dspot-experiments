package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.pool.TypePool.Default.of;


public class TypePoolDefaultTest {
    private TypePool typePool;

    @Test(expected = IllegalArgumentException.class)
    public void testNameCannotContainSlash() throws Exception {
        typePool.describe("/");
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotFindClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe("foo");
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        resolution.resolve();
        Assert.fail();
    }

    @Test
    public void testNoSuperFlag() throws Exception {
        MatcherAssert.assertThat(((typePool.describe(Object.class.getName()).resolve().getModifiers()) & (Opcodes.ACC_SUPER)), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNoDeprecationFlag() throws Exception {
        MatcherAssert.assertThat(((typePool.describe(TypePoolDefaultTest.DeprecationSample.class.getName()).resolve().getModifiers()) & (Opcodes.ACC_DEPRECATED)), CoreMatchers.is(0));
        MatcherAssert.assertThat(typePool.describe(TypePoolDefaultTest.DeprecationSample.class.getName()).resolve().getDeclaredFields().filter(ElementMatchers.named("foo")).getOnly().getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typePool.describe(TypePoolDefaultTest.DeprecationSample.class.getName()).resolve().getDeclaredMethods().filter(ElementMatchers.named("foo")).getOnly().getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testTypeIsCached() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(typePool.describe(Object.class.getName()).resolve(), CoreMatchers.is(resolution.resolve()));
        Mockito.verify(classFileLocator).locate(Object.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testReferencedTypeIsCached() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(String.class.getName());
        MatcherAssert.assertThat(typePool.describe(String.class.getName()).resolve(), CoreMatchers.is(resolution.resolve()));
        MatcherAssert.assertThat(typePool.describe(String.class.getName()).resolve().getSuperClass().asErasure(), CoreMatchers.is(TypeDescription.OBJECT));
        Mockito.verify(classFileLocator).locate(String.class.getName());
        Mockito.verify(classFileLocator).locate(Object.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Deprecated
    private static class DeprecationSample {
        @Deprecated
        Void foo;

        @Deprecated
        void foo() {
            /* empty */
        }
    }
}

