package com.querydsl.maven;


import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


public class GenericExporterMojoTest {
    @Test
    public void execute() throws Exception {
        MavenProject mavenProject = new MavenProject();
        mavenProject.getBuild().setOutputDirectory("target/classes");
        GenericExporterMojo mojo = new GenericExporterMojo();
        mojo.setTargetFolder(new File("target/generated-test-data"));
        mojo.setPackages(new String[]{ "com.querydsl.maven" });
        mojo.setProject(mavenProject);
        mojo.execute();
        File file = new File("target/generated-test-data/com/querydsl/maven/QEntity.java");
        Assert.assertTrue(file.exists());
    }
}

