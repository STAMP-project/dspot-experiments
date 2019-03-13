/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.maven;


import java.io.File;
import java.util.Collections;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


public class TestMetadataExportMojoTest {
    private final String url = "jdbc:h2:mem:testdb" + (System.currentTimeMillis());

    @Test
    public void execute() throws Exception {
        MavenProject project = new MavenProject();
        TestMetadataExportMojo mojo = new TestMetadataExportMojo();
        mojo.setProject(project);
        mojo.setJdbcDriver("org.h2.Driver");
        mojo.setJdbcUrl(url);
        mojo.setJdbcUser("sa");
        mojo.setNamePrefix("Q");// default value

        mojo.setNameSuffix("");
        mojo.setBeanPrefix("");
        mojo.setBeanSuffix("Bean");
        mojo.setPackageName("com.example");
        mojo.setTargetFolder("target/export4");
        mojo.setImports(new String[]{ "com.pck1", "com.pck2", "com.Q1", "com.Q2" });
        mojo.execute();
        // 'target/export4' seems to conflict with MetadataExportMojoTest.Execute_With_TypeMappings
        Assert.assertEquals(Collections.singletonList("target/export4"), project.getTestCompileSourceRoots());
        Assert.assertTrue(new File("target/export4").exists());
    }
}

