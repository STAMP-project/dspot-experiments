package com.arellomobile.mvp.compiler;


import com.arellomobile.mvp.MvpProcessor;
import com.google.testing.compile.Compilation;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Evgeny Kursakov
 */
@RunWith(Parameterized.class)
public class PresentersBinderTest extends CompilerTest {
    @Parameterized.Parameter
    public String targetClassName;

    @Test
    public void test() throws Exception {
        JavaFileObject target = getSourceFile(targetClassName);
        JavaFileObject exceptedPresentersBinder = getSourceFile(((targetClassName) + (MvpProcessor.PRESENTER_BINDER_SUFFIX)));
        Compilation targetCompilation = compileSourcesWithProcessor(target);
        Compilation exceptedPresentersBinderCompilation = compileSources(exceptedPresentersBinder);
        assertThat(targetCompilation).succeededWithoutWarnings();
        assertExceptedFilesGenerated(targetCompilation.generatedFiles(), exceptedPresentersBinderCompilation.generatedFiles());
    }
}

