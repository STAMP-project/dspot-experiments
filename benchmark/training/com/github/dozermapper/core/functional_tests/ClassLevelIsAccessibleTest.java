/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapping;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for situations when the entire class has been declared as accessible
 * (is-accessible=true).
 */
public class ClassLevelIsAccessibleTest extends AbstractFunctionalTest {
    private final String FIELD_VALUE = "someValue";

    @Test
    public void testClassLevelMappingOfField_FirstToSecond() {
        ClassLevelIsAccessibleTest.First source = new ClassLevelIsAccessibleTest.First();
        source.someField = FIELD_VALUE;
        source.another = 2;
        ClassLevelIsAccessibleTest.Second target = mapper.map(source, ClassLevelIsAccessibleTest.Second.class);
        Assert.assertThat(target.someField, CoreMatchers.equalTo(FIELD_VALUE));
    }

    @Test
    public void testClassLevelMappingOfField_SecondToFirst() {
        ClassLevelIsAccessibleTest.Second source = new ClassLevelIsAccessibleTest.Second();
        source.someField = FIELD_VALUE;
        ClassLevelIsAccessibleTest.First target = mapper.map(source, ClassLevelIsAccessibleTest.First.class);
        Assert.assertThat(target.someField, CoreMatchers.equalTo(FIELD_VALUE));
        Assert.assertThat(target.another, CoreMatchers.equalTo(source.another));
    }

    @Test
    public void testExplicitXMLFieldMappingOverridesDefaultFieldMapping() {
        ClassLevelIsAccessibleTest.First source = new ClassLevelIsAccessibleTest.First();
        source.someField = FIELD_VALUE;
        ClassLevelIsAccessibleTest.CustomFieldMapped target = mapper.map(source, ClassLevelIsAccessibleTest.CustomFieldMapped.class);
        Assert.assertThat(target.someField, CoreMatchers.nullValue());
        Assert.assertThat(target.otherField, CoreMatchers.equalTo(FIELD_VALUE));
    }

    @Test
    public void testExplicitAnnotationMappingOverridesDefaultFieldMapping() {
        ClassLevelIsAccessibleTest.AnnotationMapped source = new ClassLevelIsAccessibleTest.AnnotationMapped();
        source.aField = FIELD_VALUE;
        source.bField = 42;
        ClassLevelIsAccessibleTest.First target = mapper.map(source, ClassLevelIsAccessibleTest.First.class);
        Assert.assertThat(target.someField, CoreMatchers.equalTo(source.aField));
        Assert.assertThat(target.another, CoreMatchers.equalTo(source.bField));
    }

    @Test
    public void testSetterUsedWhenMappingToNonAccessibleClass() {
        ClassLevelIsAccessibleTest.First source = new ClassLevelIsAccessibleTest.First();
        source.someField = FIELD_VALUE;
        ClassLevelIsAccessibleTest.BeanClass target = mapper.map(source, ClassLevelIsAccessibleTest.BeanClass.class);
        Assert.assertThat(target.setterInvoked, CoreMatchers.is(true));
    }

    @Test
    public void testGetterUsedWhenMappingFromNonAccessibleClass() {
        ClassLevelIsAccessibleTest.BeanClass source = new ClassLevelIsAccessibleTest.BeanClass();
        source.someField = FIELD_VALUE;
        mapper.map(source, ClassLevelIsAccessibleTest.First.class);
        Assert.assertThat(source.getterInvoked, CoreMatchers.is(true));
    }

    public static class First {
        private String someField;

        private int another;
    }

    public static class Second {
        private String someField;

        private final int another = 8;
    }

    public static class CustomFieldMapped {
        @SuppressWarnings("unused")
        private String someField;

        @SuppressWarnings("unused")
        private String otherField;
    }

    public static class AnnotationMapped {
        @Mapping("someField")
        private String aField;

        @Mapping("another")
        private int bField;
    }

    public static class BeanClass {
        private String someField;

        private boolean setterInvoked;

        private boolean getterInvoked;

        @SuppressWarnings("unused")
        public String getSomeField() {
            getterInvoked = true;
            return someField;
        }

        @SuppressWarnings("unused")
        public void setSomeField(String someField) {
            setterInvoked = true;
            this.someField = someField;
        }
    }
}

