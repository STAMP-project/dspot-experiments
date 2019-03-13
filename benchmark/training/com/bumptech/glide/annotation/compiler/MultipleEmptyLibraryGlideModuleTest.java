package com.bumptech.glide.annotation.compiler;


import com.bumptech.glide.annotation.compiler.test.RegenerateResourcesRule;
import com.bumptech.glide.annotation.compiler.test.Util;
import com.google.common.truth.Truth;
import com.google.testing.compile.Compilation;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests adding multiple {@link com.bumptech.glide.module.LibraryGlideModule}s in a project.
 */
@RunWith(JUnit4.class)
public class MultipleEmptyLibraryGlideModuleTest {
    @Rule
    public final RegenerateResourcesRule regenerateResourcesRule = new RegenerateResourcesRule(getClass());

    private Compilation compilation;

    @Test
    public void compilation_generatesAllExpectedFiles() {
        Truth.assertThat(compilation.generatedSourceFiles()).hasSize(1);
    }

    @Test
    public void compilation_generatesExpectedIndexerForModules() throws IOException {
        String expectedClassName = "GlideIndexer_GlideModule_com_bumptech_glide_test_EmptyLibraryModule1_com_bumptech_glide" + "_test_EmptyLibraryModule2";
        assertThat(compilation).generatedSourceFile(Util.annotation(expectedClassName)).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource((expectedClassName + ".java")).getCharContent(true)));
    }
}

