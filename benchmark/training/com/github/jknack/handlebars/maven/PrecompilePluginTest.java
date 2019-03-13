package com.github.jknack.handlebars.maven;


import java.io.File;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class PrecompilePluginTest {
    @Test
    public void i18nJs() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/i18nJs");
        plugin.setSuffix(".html");
        plugin.setOutput("target/helpers-i18njs.js");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
        Assert.assertEquals(FileUtils.fileRead("src/test/resources/helpers-i18njs.expected"), FileUtils.fileRead("target/helpers-i18njs.js"));
    }

    @Test
    public void chooseSpecificFiles() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/templates");
        plugin.setSuffix(".hbs");
        plugin.setOutput("target/specific-files.js");
        plugin.addTemplate("a");
        plugin.addTemplate("c");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
        Assert.assertEquals(FileUtils.fileRead("src/test/resources/specific-files.expected"), FileUtils.fileRead("target/specific-files.js"));
    }

    @Test
    public void outputDirMustBeCreated() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/helpers");
        plugin.setSuffix(".html");
        plugin.setOutput("target/newdir/helpers.js");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
    }

    @Test
    public void missingHelperMustBeSilent() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/missing-helper");
        plugin.setSuffix(".html");
        plugin.setOutput("target/missing-helpers.js");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
    }

    @Test
    public void noFileMustBeCreatedIfNoTemplatesWereFound() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/no templates");
        plugin.setSuffix(".html");
        plugin.setOutput("target/no-helpers.js");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
        Assert.assertTrue((!(new File("target/no-helpers.js").exists())));
    }

    @Test(expected = MojoExecutionException.class)
    public void mustFailOnInvalidInputDirectory() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/missing");
        plugin.setSuffix(".html");
        plugin.setOutput("target/no-helpers.js");
        plugin.setProject(newProject());
        plugin.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void mustFailOnMissingFile() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/ioexception");
        plugin.setSuffix(".html");
        plugin.setOutput("target/no-helpers.js");
        plugin.setProject(newProject());
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void mustFailOnUnExpectedException() throws Exception {
        MavenProject project = createMock(MavenProject.class);
        Artifact artifact = createMock(Artifact.class);
        expect(project.getRuntimeClasspathElements()).andThrow(new org.apache.maven.artifact.DependencyResolutionRequiredException(artifact));
        replay(project, artifact);
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/no templates");
        plugin.setSuffix(".html");
        plugin.setOutput("target/no-helpers.js");
        plugin.setProject(project);
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
    }

    @Test
    public void fileWithRuntimeMustBeLargerThanNormalFiles() throws Exception {
        PrecompilePlugin withoutRT = new PrecompilePlugin();
        withoutRT.setPrefix("src/test/resources/helpers");
        withoutRT.setSuffix(".html");
        withoutRT.setOutput("target/without-rt-helpers.js");
        withoutRT.setProject(newProject());
        withoutRT.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        withoutRT.execute();
        PrecompilePlugin withRT = new PrecompilePlugin();
        withRT.setPrefix("src/test/resources/helpers");
        withRT.setSuffix(".html");
        withRT.setOutput("target/with-rt-helpers.js");
        withRT.setRuntime("src/test/resources/handlebars.runtime.js");
        withRT.setProject(newProject());
        withRT.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        withRT.execute();
        Assert.assertTrue("File with runtime must be larger", ((FileUtils.fileRead("target/without-rt-helpers.js").length()) < (FileUtils.fileRead("target/with-rt-helpers.js").length())));
    }

    @Test
    public void normalFileShouleBeLargerThanMinimizedFiles() throws Exception {
        PrecompilePlugin withoutRT = new PrecompilePlugin();
        withoutRT.setPrefix("src/test/resources/helpers");
        withoutRT.setSuffix(".html");
        withoutRT.setOutput("target/helpers-normal.js");
        withoutRT.setProject(newProject());
        withoutRT.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        withoutRT.execute();
        PrecompilePlugin withRT = new PrecompilePlugin();
        withRT.setPrefix("src/test/resources/helpers");
        withRT.setSuffix(".html");
        withRT.setOutput("target/helpers.min.js");
        withRT.setMinimize(true);
        withRT.setProject(newProject());
        withRT.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        withRT.execute();
        Assert.assertTrue("Normal file must be larger than minimized", ((FileUtils.fileRead("target/helpers-normal.js").length()) > (FileUtils.fileRead("target/helpers.min.js").length())));
    }

    @Test
    public void partials() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/partials");
        plugin.setSuffix(".html");
        plugin.setOutput("target/helpers.js");
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
        Assert.assertEquals(FileUtils.fileRead("src/test/resources/helpers.expected"), FileUtils.fileRead("target/helpers.js"));
    }
}

