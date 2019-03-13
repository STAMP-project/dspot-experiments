package org.owasp.dependencycheck;


import java.io.File;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.analyzer.FileTypeAnalyzer;
import org.owasp.dependencycheck.analyzer.HintAnalyzer;
import org.owasp.dependencycheck.dependency.Dependency;


public class AnalysisTaskTest extends BaseTest {
    @Mocked
    private FileTypeAnalyzer fileTypeAnalyzer;

    @Mocked
    private Dependency dependency;

    @Mocked
    private Engine engine;

    @Test
    public void shouldAnalyzeReturnsTrueForNonFileTypeAnalyzers() {
        AnalysisTask instance = new AnalysisTask(new HintAnalyzer(), null, null, null);
        boolean shouldAnalyze = instance.shouldAnalyze();
        Assert.assertTrue(shouldAnalyze);
    }

    @Test
    public void shouldAnalyzeReturnsTrueIfTheFileTypeAnalyzersAcceptsTheDependency() {
        final File dependencyFile = new File("");
        new Expectations() {
            {
                dependency.getActualFile();
                result = dependencyFile;
                fileTypeAnalyzer.accept(dependencyFile);
                result = true;
            }
        };
        AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, null, null);
        boolean shouldAnalyze = analysisTask.shouldAnalyze();
        Assert.assertTrue(shouldAnalyze);
    }

    @Test
    public void shouldAnalyzeReturnsFalseIfTheFileTypeAnalyzerDoesNotAcceptTheDependency() {
        final File dependencyFile = new File("");
        new Expectations() {
            {
                dependency.getActualFile();
                result = dependencyFile;
                fileTypeAnalyzer.accept(dependencyFile);
                result = false;
            }
        };
        AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, null, null);
        boolean shouldAnalyze = analysisTask.shouldAnalyze();
        Assert.assertFalse(shouldAnalyze);
    }

    @Test
    public void taskAnalyzes() throws Exception {
        final AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, engine, null);
        new Expectations(analysisTask) {
            {
                analysisTask.shouldAnalyze();
                result = true;
            }
        };
        analysisTask.call();
        new Verifications() {
            {
                fileTypeAnalyzer.analyze(dependency, engine);
                times = 1;
            }
        };
    }

    @Test
    public void taskDoesNothingIfItShouldNotAnalyze() throws Exception {
        final AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, engine, null);
        new Expectations(analysisTask) {
            {
                analysisTask.shouldAnalyze();
                result = false;
            }
        };
        analysisTask.call();
        new Verifications() {
            {
                fileTypeAnalyzer.analyze(dependency, engine);
                times = 0;
            }
        };
    }
}

