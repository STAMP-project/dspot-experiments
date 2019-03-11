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


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CaseInsensitiveWildcardMappingTest extends AbstractFunctionalTest {
    @Test
    public void testDefaultMappingIsCaseSensitive() {
        CaseInsensitiveWildcardMappingTest.A a = newInstance(CaseInsensitiveWildcardMappingTest.A.class);
        a.setCamelCaseValue("test");
        CaseInsensitiveWildcardMappingTest.A selfResult = mapper.map(a, CaseInsensitiveWildcardMappingTest.A.class);
        CaseInsensitiveWildcardMappingTest.B result = mapper.map(a, CaseInsensitiveWildcardMappingTest.B.class);
        Assert.assertThat(selfResult, CoreMatchers.notNullValue());
        Assert.assertThat(selfResult.getCamelCaseValue(), CoreMatchers.equalTo("test"));
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.getCamelcasevalue(), CoreMatchers.nullValue());
    }

    @Test
    public void testCaseInsensitiveGlobalMappingEnabled() {
        mapper = getMapper("mappings/caseInsensitiveWildcardMappingGlobal.xml");
        CaseInsensitiveWildcardMappingTest.A a = newInstance(CaseInsensitiveWildcardMappingTest.A.class);
        a.setCamelCaseValue("test");
        CaseInsensitiveWildcardMappingTest.B result = mapper.map(a, CaseInsensitiveWildcardMappingTest.B.class);
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.getCamelcasevalue(), CoreMatchers.equalTo("test"));
    }

    @Test
    public void testCaseInsensitiveClassLevelMappingEnabled() {
        mapper = getMapper("mappings/caseInsensitiveWildcardMappingClassLevel.xml");
        CaseInsensitiveWildcardMappingTest.A a = newInstance(CaseInsensitiveWildcardMappingTest.A.class);
        a.setCamelCaseValue("test");
        CaseInsensitiveWildcardMappingTest.B result1 = mapper.map(a, CaseInsensitiveWildcardMappingTest.B.class);
        CaseInsensitiveWildcardMappingTest.CNoMappingConfigured result2 = mapper.map(a, CaseInsensitiveWildcardMappingTest.CNoMappingConfigured.class);
        Assert.assertThat(result1, CoreMatchers.notNullValue());
        Assert.assertThat(result1.getCamelcasevalue(), CoreMatchers.equalTo("test"));
        Assert.assertThat(result2, CoreMatchers.notNullValue());
        Assert.assertThat(result2.getCamelcasevalue(), CoreMatchers.nullValue());
    }

    @Test
    public void testCaseInsensitiveBeanMapperBuilder() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(type(CaseInsensitiveWildcardMappingTest.A.class), type(CaseInsensitiveWildcardMappingTest.B.class), TypeMappingOptions.wildcardCaseInsensitive(true));
            }
        }).build();
        CaseInsensitiveWildcardMappingTest.A a = newInstance(CaseInsensitiveWildcardMappingTest.A.class);
        a.setCamelCaseValue("test");
        CaseInsensitiveWildcardMappingTest.B result = mapper.map(a, CaseInsensitiveWildcardMappingTest.B.class);
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.getCamelcasevalue(), CoreMatchers.equalTo("test"));
    }

    @Test
    public void testCaseInsensitiveWithIsAccessible() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(type(CaseInsensitiveWildcardMappingTest.A.class), type(CaseInsensitiveWildcardMappingTest.DNoSetter.class).accessible(), TypeMappingOptions.wildcardCaseInsensitive(true));
            }
        }).build();
        CaseInsensitiveWildcardMappingTest.A a = newInstance(CaseInsensitiveWildcardMappingTest.A.class);
        a.setCamelCaseValue("test");
        CaseInsensitiveWildcardMappingTest.DNoSetter result = mapper.map(a, CaseInsensitiveWildcardMappingTest.DNoSetter.class);
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.getCamelcasevalue(), CoreMatchers.equalTo("test"));
    }

    public static class A {
        private String camelCaseValue;

        public String getCamelCaseValue() {
            return camelCaseValue;
        }

        public void setCamelCaseValue(String camelCaseValue) {
            this.camelCaseValue = camelCaseValue;
        }
    }

    public static class B {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }

        public void setCamelcasevalue(String camelcasevalue) {
            this.camelcasevalue = camelcasevalue;
        }
    }

    public static class CNoMappingConfigured {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }

        public void setCamelcasevalue(String camelcasevalue) {
            this.camelcasevalue = camelcasevalue;
        }
    }

    public static class DNoSetter {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }
    }
}

