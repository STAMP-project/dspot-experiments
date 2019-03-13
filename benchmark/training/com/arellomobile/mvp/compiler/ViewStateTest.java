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
public class ViewStateTest extends CompilerTest {
    @Parameterized.Parameter
    public String viewClassName;

    @Test
    public void test() throws Exception {
        JavaFileObject presenter = createDummyPresenter(viewClassName);
        JavaFileObject exceptedViewState = getSourceFile(((viewClassName) + (MvpProcessor.VIEW_STATE_SUFFIX)));
        Compilation presenterCompilation = compileSourcesWithProcessor(presenter);
        Compilation exceptedViewStateCompilation = compileSources(exceptedViewState);
        assertThat(presenterCompilation).succeededWithoutWarnings();
        assertExceptedFilesGenerated(presenterCompilation.generatedFiles(), exceptedViewStateCompilation.generatedFiles());
    }
}

