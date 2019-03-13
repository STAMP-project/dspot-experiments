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
 * Tests adding a single {@link com.bumptech.glide.test.EmptyAppModule} in a project.
 */
@RunWith(JUnit4.class)
public class EmptyAppGlideModuleTest {
    private static final String MODULE_NAME = "EmptyAppModule.java";

    @Rule
    public final RegenerateResourcesRule regenerateResourcesRule = new RegenerateResourcesRule(getClass());

    private Compilation compilation;

    @Test
    public void compilation_generatesAllExpectedFiles() {
        Truth.assertThat(compilation.generatedSourceFiles()).hasSize(6);
    }

    @Test
    public void compilation_generatesExpectedGlideOptionsClass() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.subpackage("GlideOptions")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GlideOptions.java").getCharContent(true)));
    }

    @Test
    public void compilation_generatesExpectedGlideRequestClass() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.subpackage("GlideRequest")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GlideRequest.java").getCharContent(true)));
    }

    @Test
    public void compilation_generatesExpectedGlideRequestsClass() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.subpackage("GlideRequests")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GlideRequests.java").getCharContent(true)));
    }

    @Test
    public void compilationGeneratesExpectedGlideAppClass() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.subpackage("GlideApp")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GlideApp.java").getCharContent(true)));
    }

    @Test
    public void compilation_generatesExpectedGeneratedAppGlideModuleImpl() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.glide("GeneratedAppGlideModuleImpl")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GeneratedAppGlideModuleImpl.java").getCharContent(true)));
    }

    @Test
    public void compilation_generatesExpectedGeneratedRequestManagerFactory() throws IOException {
        assertThat(compilation).generatedSourceFile(Util.glide("GeneratedRequestManagerFactory")).contentsAsUtf8String().isEqualTo(Util.asUnixChars(forResource("GeneratedRequestManagerFactory.java").getCharContent(true)));
    }
}

