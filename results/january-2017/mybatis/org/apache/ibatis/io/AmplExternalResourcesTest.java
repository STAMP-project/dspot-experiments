/**
 * Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.io;


public class AmplExternalResourcesTest {
    private java.io.File sourceFile;

    private java.io.File destFile;

    private java.io.File badFile;

    private java.io.File tempFile;

    /* @throws java.lang.Exception */
    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        tempFile = java.io.File.createTempFile("migration", "properties");
        tempFile.canWrite();
        sourceFile = java.io.File.createTempFile("test1", "sql");
        destFile = java.io.File.createTempFile("test2", "sql");
    }

    @org.junit.Test
    public void testcopyExternalResource() {
        try {
            org.apache.ibatis.io.ExternalResources.copyExternalResource(sourceFile, destFile);
        } catch (java.io.IOException e) {
        }
    }

    @org.junit.Test
    public void testcopyExternalResource_fileNotFound() {
        try {
            badFile = new java.io.File("/tmp/nofile.sql");
            org.apache.ibatis.io.ExternalResources.copyExternalResource(badFile, destFile);
        } catch (java.io.IOException e) {
            org.junit.Assert.assertTrue((e instanceof java.io.FileNotFoundException));
        }
    }

    @org.junit.Test
    public void testcopyExternalResource_emptyStringAsFile() {
        try {
            badFile = new java.io.File(" ");
            org.apache.ibatis.io.ExternalResources.copyExternalResource(badFile, destFile);
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue((e instanceof java.io.FileNotFoundException));
        }
    }

    @org.junit.Test
    public void testGetConfiguredTemplate() {
        java.lang.String templateName = "";
        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(tempFile);
            fileWriter.append("new_command.template=templates/col_new_template_migration.sql");
            fileWriter.flush();
            fileWriter.close();
            templateName = org.apache.ibatis.io.ExternalResources.getConfiguredTemplate(tempFile.getAbsolutePath(), "new_command.template");
            org.junit.Assert.assertEquals("templates/col_new_template_migration.sql", templateName);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(("Test failed with execption: " + (e.getMessage())));
        }
    }

    @org.junit.After
    public void cleanUp() {
        sourceFile.delete();
        destFile.delete();
        tempFile.delete();
    }
}

