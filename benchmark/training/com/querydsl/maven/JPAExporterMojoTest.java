package com.querydsl.maven;


import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


public class JPAExporterMojoTest {
    @Test
    public void execute() throws Exception {
        MavenProject mavenProject = new MavenProject();
        mavenProject.getBuild().setOutputDirectory("target/classes");
        JPAExporterMojo mojo = new JPAExporterMojo();
        mojo.setTargetFolder(new File("target/generated-test-data2"));
        mojo.setPackages(new String[]{ "com.querydsl.maven" });
        mojo.setProject(mavenProject);
        mojo.execute();
        File file = new File("target/generated-test-data2/com/querydsl/maven/QEntity.java");
        Assert.assertTrue(file.exists());
    }
}

