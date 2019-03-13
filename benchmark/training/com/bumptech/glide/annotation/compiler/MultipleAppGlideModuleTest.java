package com.bumptech.glide.annotation.compiler;


import com.bumptech.glide.annotation.compiler.test.RegenerateResourcesRule;
import com.google.testing.compile.Compilation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Ensures that adding more than one {@link com.bumptech.glide.module.AppGlideModule} to a project
 * will fail.
 */
@RunWith(JUnit4.class)
public class MultipleAppGlideModuleTest {
    private static final String FIRST_MODULE = "EmptyAppModule1.java";

    private static final String SECOND_MODULE = "EmptyAppModule2.java";

    @Rule
    public final RegenerateResourcesRule regenerateResourcesRule = new RegenerateResourcesRule(getClass());

    // Throws.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void compilation_withTwoAppModules_fails() {
        Assert.assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                javac().withProcessors(new GlideAnnotationProcessor()).compile(forResource(MultipleAppGlideModuleTest.FIRST_MODULE), forResource(MultipleAppGlideModuleTest.SECOND_MODULE));
            }
        });
    }

    @Test
    public void compilation_withFirstModuleOnly_succeeds() {
        Compilation compilation = javac().withProcessors(new GlideAnnotationProcessor()).compile(forResource(MultipleAppGlideModuleTest.FIRST_MODULE));
        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    public void compilation_withSecondModuleOnly_succeeds() {
        Compilation compilation = javac().withProcessors(new GlideAnnotationProcessor()).compile(forResource(MultipleAppGlideModuleTest.SECOND_MODULE));
        assertThat(compilation).succeededWithoutWarnings();
    }
}

