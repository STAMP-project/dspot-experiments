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
import com.github.dozermapper.core.functional_tests.AbstractFunctionalTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AnnotationMappingTest extends AbstractFunctionalTest {
    private AnnotationMappingTest.User source;

    private AnnotationMappingTest.SubUser subSource;

    private AnnotationMappingTest.UserDto destination;

    @Test
    public void shouldMapProperties() {
        source.setId(1L);
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.getPk(), CoreMatchers.equalTo("1"));
        Assert.assertThat(result.getId(), CoreMatchers.nullValue());
    }

    @Test
    public void shouldMapProperties_Backwards() {
        source.setAge(new Short("1"));
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.getYears(), CoreMatchers.equalTo("1"));
    }

    @Test
    public void shouldMapFields_Custom() {
        source.setName("name");
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.username, CoreMatchers.equalTo("name"));
        Assert.assertThat(result.name, CoreMatchers.nullValue());
    }

    @Test
    public void shouldMapFields_Custom_Backwards() {
        source.freeText = "text";
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.comment, CoreMatchers.equalTo("text"));
    }

    @Test
    public void shouldMapFields_Default() {
        source.setRole("role");
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.role, CoreMatchers.equalTo("role"));
    }

    @Test
    public void shouldMapFields_Default_Backwards() {
        source.setZip("12345");
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.zip, CoreMatchers.equalTo("12345"));
    }

    @Test
    public void shouldMapFields_Inherited() {
        subSource.setName("name");
        subSource.setRole("role");
        AnnotationMappingTest.UserDto result = mapper.map(subSource, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.username, CoreMatchers.equalTo("name"));
        Assert.assertThat(result.role, CoreMatchers.equalTo("role"));
        Assert.assertThat(result.name, CoreMatchers.nullValue());
    }

    @Test
    public void shouldMapFields_Backwards_Inherited() {
        subSource.freeText = "text";
        subSource.setZip("12345");
        AnnotationMappingTest.UserDto result = mapper.map(subSource, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.comment, CoreMatchers.equalTo("text"));
        Assert.assertThat(result.zip, CoreMatchers.equalTo("12345"));
    }

    @Test(expected = NoSuchFieldException.class)
    public void shouldMapFields_Optional() throws NoSuchFieldException {
        source.setPassword("some value");
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        result.getClass().getField("password");
    }

    @Test
    public void shouldMapFields_Optional_Exists() {
        source.setToken("some value");
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.token, CoreMatchers.equalTo("some value"));
    }

    @Test
    public void shouldMapFields_Optional_Exists_Backwards() {
        source.token = "some value";
        AnnotationMappingTest.UserDto result = mapper.map(source, AnnotationMappingTest.UserDto.class);
        Assert.assertThat(result.token, CoreMatchers.equalTo("some value"));
    }

    public static class User {
        Long id;

        Short age;

        private String zip;

        @Mapping
        private String role;

        @Mapping("username")
        private String name;

        String freeText;

        @Mapping(optional = true)
        private String password;

        @Mapping(optional = true)
        private String token;

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

        public void setName(String name) {
            this.name = name;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class SubUser extends AnnotationMappingTest.User {}

    public static class UserDto {
        String id;

        String years;

        String pk;

        @Mapping
        String zip;

        String role;

        String name;

        String username;

        @Mapping("freeText")
        String comment;

        String token;

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

