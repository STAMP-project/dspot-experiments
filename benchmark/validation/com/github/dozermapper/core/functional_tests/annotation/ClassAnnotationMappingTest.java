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
package com.github.dozermapper.core.functional_tests.annotation;


import com.github.dozermapper.core.Mapping;
import com.github.dozermapper.core.OptionValue;
import com.github.dozermapper.core.functional_tests.AbstractFunctionalTest;
import com.github.dozermapper.core.util.MappingOptions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ClassAnnotationMappingTest extends AbstractFunctionalTest {
    private ClassAnnotationMappingTest.User source;

    private ClassAnnotationMappingTest.UserDto destination;

    @Test
    public void shouldMapNonEmptyString() {
        source.name = "name";
        ClassAnnotationMappingTest.UserDto result = mapper.map(source, ClassAnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.username, CoreMatchers.equalTo("name"));
    }

    @Test
    public void shouldNotMapEmptyString() {
        destination.username = "name";
        mapper.map(source, destination);
        Assert.assertThat(destination.username, CoreMatchers.equalTo("name"));
    }

    @Test
    public void shouldMapNonEmptyString_Backwards() {
        source.freeText = "text";
        source.zip = "12345";
        ClassAnnotationMappingTest.UserDto result = mapper.map(source, ClassAnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.comment, CoreMatchers.equalTo("text"));
        Assert.assertThat(result.zip, CoreMatchers.equalTo("12345"));
    }

    @Test
    public void shouldNotMapEmptyString_Backwards() {
        destination.comment = "text";
        destination.zip = "12345";
        mapper.map(source, destination);
        Assert.assertThat(destination.comment, CoreMatchers.equalTo("text"));
        Assert.assertThat(destination.zip, CoreMatchers.equalTo("12345"));
    }

    @Test
    public void shouldMapNonNull() {
        source.id = 4L;
        source.age = 64;
        destination.pk = "1064";
        destination.years = "531";
        mapper.map(source, destination);
        Assert.assertThat(destination.pk, CoreMatchers.equalTo("4"));
        Assert.assertThat(destination.years, CoreMatchers.equalTo("64"));
    }

    @Test
    public void shouldNotMapNull() {
        destination.pk = "4310";
        destination.years = "4353";
        mapper.map(source, destination);
        Assert.assertThat(destination.pk, CoreMatchers.equalTo("4310"));
        Assert.assertThat(destination.years, CoreMatchers.equalTo("4353"));
    }

    @MappingOptions(mapNull = OptionValue.OFF)
    public static class User {
        Long id;

        Short age;

        @Mapping("username")
        String name;

        String freeText;

        String zip;

        @Mapping("pk")
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Short getAge() {
            return age;
        }

        public void setAge(Short age) {
            this.age = age;
        }
    }

    @MappingOptions(mapEmptyString = OptionValue.OFF)
    public static class UserDto {
        String years;

        String pk;

        @Mapping
        String zip;

        String username;

        @Mapping("freeText")
        String comment;

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        @Mapping("age")
        public String getYears() {
            return years;
        }

        public void setYears(String years) {
            this.years = years;
        }
    }
}

