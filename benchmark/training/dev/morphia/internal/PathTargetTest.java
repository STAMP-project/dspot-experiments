/**
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.internal;


import dev.morphia.TestArrayUpdates;
import dev.morphia.TestBase;
import dev.morphia.entities.EmbeddedSubtype;
import dev.morphia.entities.EmbeddedType;
import dev.morphia.entities.EntityWithListsAndArrays;
import dev.morphia.entities.ParentType;
import dev.morphia.mapping.EmbeddedMappingTest;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.Mapper;
import dev.morphia.testmodel.Article;
import org.junit.Assert;
import org.junit.Test;


public class PathTargetTest extends TestBase {
    @Test
    public void simpleResolution() {
        getMorphia().map(ParentType.class, EmbeddedType.class);
        Mapper mapper = getMorphia().getMapper();
        MappedClass mappedClass = mapper.getMappedClass(ParentType.class);
        PathTarget pathTarget = new PathTarget(mapper, mappedClass, "name");
        Assert.assertEquals("n", pathTarget.translatedPath());
        Assert.assertEquals(mappedClass.getMappedFieldByJavaField("name"), pathTarget.getTarget());
        pathTarget = new PathTarget(mapper, mappedClass, "n");
        Assert.assertEquals("n", pathTarget.translatedPath());
        Assert.assertEquals(mappedClass.getMappedField("n"), pathTarget.getTarget());
    }

    @Test
    public void dottedPath() {
        getMorphia().map(ParentType.class, EmbeddedType.class);
        Mapper mapper = getMorphia().getMapper();
        PathTarget pathTarget = new PathTarget(mapper, ParentType.class, "embedded.number");
        Assert.assertEquals("embedded.number", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(EmbeddedType.class).getMappedFieldByJavaField("number"), pathTarget.getTarget());
    }

    @Test
    public void subClasses() {
        getMorphia().map(ParentType.class, EmbeddedType.class, EmbeddedSubtype.class);
        Mapper mapper = getMorphia().getMapper();
        PathTarget pathTarget = new PathTarget(mapper, ParentType.class, "embedded.flag");
        Assert.assertEquals("embedded.flag", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(EmbeddedSubtype.class).getMappedFieldByJavaField("flag"), pathTarget.getTarget());
    }

    @Test
    public void arrays() {
        getMorphia().map(EntityWithListsAndArrays.class, EmbeddedType.class, TestArrayUpdates.Student.class);
        Mapper mapper = getMorphia().getMapper();
        MappedClass mappedClass = mapper.getMappedClass(EntityWithListsAndArrays.class);
        PathTarget pathTarget = new PathTarget(mapper, mappedClass, "listEmbeddedType.1.number");
        Assert.assertEquals("listEmbeddedType.1.number", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(EmbeddedType.class).getMappedFieldByJavaField("number"), pathTarget.getTarget());
        Assert.assertEquals("listEmbeddedType.$", translatedPath());
        Assert.assertEquals("listEmbeddedType.1", translatedPath());
    }

    @Test
    public void maps() {
        getMorphia().map(TestArrayUpdates.Student.class, Article.class);
        Mapper mapper = getMorphia().getMapper();
        MappedClass mappedClass = mapper.getMappedClass(TestArrayUpdates.Student.class);
        PathTarget pathTarget = new PathTarget(mapper, mappedClass, "grades.$.data.name");
        Assert.assertEquals("grades.$.d.name", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(TestArrayUpdates.Grade.class).getMappedFieldByJavaField("data"), pathTarget.getTarget());
        pathTarget = new PathTarget(mapper, mappedClass, "grades.$.d.name");
        Assert.assertEquals("grades.$.d.name", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(TestArrayUpdates.Grade.class).getMappedField("d"), pathTarget.getTarget());
        pathTarget = new PathTarget(mapper, Article.class, "translations");
        Assert.assertEquals("translations", pathTarget.translatedPath());
    }

    @Test
    public void interfaces() {
        getMorphia().map(EmbeddedMappingTest.WithNested.class, EmbeddedMappingTest.Nested.class, EmbeddedMappingTest.NestedImpl.class, EmbeddedMappingTest.AnotherNested.class);
        Mapper mapper = getMorphia().getMapper();
        MappedClass mappedClass = mapper.getMappedClass(EmbeddedMappingTest.WithNested.class);
        PathTarget pathTarget = new PathTarget(mapper, mappedClass, "nested.value");
        Assert.assertEquals("nested.value", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(EmbeddedMappingTest.AnotherNested.class).getMappedFieldByJavaField("value"), pathTarget.getTarget());
        pathTarget = new PathTarget(mapper, mappedClass, "nested.field");
        Assert.assertEquals("nested.field", pathTarget.translatedPath());
        Assert.assertEquals(mapper.getMappedClass(EmbeddedMappingTest.NestedImpl.class).getMappedFieldByJavaField("field"), pathTarget.getTarget());
    }

    @Test
    public void disableValidation() {
        getMorphia().map(EmbeddedMappingTest.WithNested.class, EmbeddedMappingTest.Nested.class, EmbeddedMappingTest.NestedImpl.class, EmbeddedMappingTest.AnotherNested.class);
        Mapper mapper = getMorphia().getMapper();
        final PathTarget pathTarget = new PathTarget(mapper, EmbeddedMappingTest.WithNested.class, "nested.field.fail", false);
        Assert.assertEquals("nested.field.fail", pathTarget.translatedPath());
        Assert.assertNull(pathTarget.getTarget());
    }
}

