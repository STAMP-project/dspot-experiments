package com.bumptech.glide.annotation.compiler;


import com.bumptech.glide.annotation.compiler.test.RegenerateResourcesRule;
import com.bumptech.glide.annotation.compiler.test.SubDirectory;
import com.bumptech.glide.annotation.compiler.test.TestDescription;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Verifies only the output we expect to change based on the various configurations of GlideOptions.
 */
@RunWith(JUnit4.class)
public class GlideExtensionOptionsTest {
    @Rule
    public final RegenerateResourcesRule regenerateResourcesRule = new RegenerateResourcesRule(getClass());

    @Rule
    public final TestDescription testDescription = new TestDescription();

    private static final String EXTENSION_NAME = "Extension.java";

    @Test
    @SubDirectory("OverrideExtend")
    public void compilation_withOverrideExtend_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("OverrideExtend")
    public void compilation_withOverrideExtend_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    @Test
    @SubDirectory("OverrideExtendMultipleArguments")
    public void compilation_withOverrideReplace_andMultipleArguments_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("OverrideExtendMultipleArguments")
    public void compilation_withOverrideReplace_andMultipleArguments_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    @Test
    @SubDirectory("OverrideReplace")
    public void compilation_withOverrideReplace_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("OverrideReplace")
    public void compilation_withOverrideReplace_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    @Test
    @SubDirectory("StaticMethodName")
    public void compilation_withStaticMethodName_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("StaticMethodName")
    public void compilation_withStaticMethodName_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    @Test
    @SubDirectory("MemoizeStaticMethod")
    public void compilation_withMemoizeStaticMethod_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("MemoizeStaticMethod")
    public void compilation_withMemoizeStaticMethod_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    @Test
    @SubDirectory("SkipStaticMethod")
    public void compilation_withSkipStaticMethod_validOptions() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideOptions);
    }

    @Test
    @SubDirectory("SkipStaticMethod")
    public void compilation_withSkipStaticMethod_validRequest() throws IOException {
        runTest(GlideExtensionOptionsTest.Subject.GlideRequest);
    }

    private enum Subject {

        GlideOptions,
        GlideRequest;
        String file() {
            return (name()) + ".java";
        }
    }
}

