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
package com.querydsl.codegen;


import Keywords.JPA;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.querydsl.core.domain.Cat;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class GenericExporterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private GenericExporter exporter;

    @Test
    public void export() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
    }

    @Test
    public void export_with_keywords() throws IOException {
        exporter.setKeywords(JPA);
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(getClass().getPackage());
        String str = Files.toString(new File(folder.getRoot(), "com/querydsl/codegen/QGroup.java"), Charsets.UTF_8);
        Assert.assertTrue(str.contains("QGroup group = new QGroup(\"group1\");"));
    }

    @Test
    public void export_with_stopClass() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.addStopClass(Examples.Supertype.class);
        exporter.export(getClass().getPackage());
        Assert.assertFalse(new File(folder.getRoot(), "com/querydsl/codegen/QExamples_Supertype.java").exists());
    }

    @Test
    public void override_serializer() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.setSerializerClass(EntitySerializer.class);
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
    }

    @Test
    public void export_package_as_string() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(getClass().getPackage().getName());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
    }

    @Test
    public void export_with_package_suffix() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.setPackageSuffix("types");
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegentypes/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegentypes/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegentypes/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegentypes/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegentypes/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/subtypes/QExampleEntity2.java").exists());
    }

    @Test
    public void export_handle_no_methods_nor_fields() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.setHandleFields(false);
        exporter.setHandleMethods(false);
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
    }

    @Test
    public void export_domain_package() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(Cat.class.getPackage());
    }

    @Test
    public void export_serializerConfig() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.setSerializerConfig(new SimpleSerializerConfig(true, true, true, true, ""));
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
    }

    @Test
    public void export_useFieldTypes() {
        exporter.setTargetFolder(folder.getRoot());
        exporter.setUseFieldTypes(true);
        exporter.export(getClass().getPackage());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbeddable.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEmbedded.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntity.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleEntityInterface.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/QExampleSupertype.java").exists());
        Assert.assertTrue(new File(folder.getRoot(), "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
    }

    @Test
    public void export_propertyHandling() throws IOException {
        for (PropertyHandling ph : PropertyHandling.values()) {
            File f = folder.newFolder();
            GenericExporter e = new GenericExporter();
            e.setTargetFolder(f);
            e.setPropertyHandling(ph);
            e.export(getClass().getPackage());
            Assert.assertTrue(new File(f, "com/querydsl/codegen/QExampleEmbeddable.java").exists());
            Assert.assertTrue(new File(f, "com/querydsl/codegen/QExampleEntity.java").exists());
            Assert.assertTrue(new File(f, "com/querydsl/codegen/QExampleEntityInterface.java").exists());
            Assert.assertTrue(new File(f, "com/querydsl/codegen/QExampleSupertype.java").exists());
            Assert.assertTrue(new File(f, "com/querydsl/codegen/sub/QExampleEntity2.java").exists());
        }
    }
}

