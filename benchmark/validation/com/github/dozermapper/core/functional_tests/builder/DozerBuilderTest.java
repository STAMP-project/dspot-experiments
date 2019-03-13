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
package com.github.dozermapper.core.functional_tests.builder;


import RelationshipType.CUMULATIVE;
import RelationshipType.NON_CUMULATIVE;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.FieldsMappingOptions;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DozerBuilderTest {
    @Test
    public void testApi() {
        BeanMappingBuilder builder = new BeanMappingBuilder() {
            protected void configure() {
                mapping(DozerBuilderTest.Bean.class, DozerBuilderTest.Bean.class, TypeMappingOptions.oneWay(), TypeMappingOptions.mapId("A"), TypeMappingOptions.mapNull()).exclude("excluded").fields("src", "dest", FieldsMappingOptions.copyByReference(), FieldsMappingOptions.removeOrphans(), FieldsMappingOptions.relationshipType(NON_CUMULATIVE), FieldsMappingOptions.hintA(String.class), FieldsMappingOptions.hintB(Integer.class), FieldsMappingOptions.oneWay(), FieldsMappingOptions.useMapId("A"), FieldsMappingOptions.customConverterId("id")).fields("src", "dest", FieldsMappingOptions.customConverter("com.github.dozermapper.core.CustomConverter"));
                mapping(type(DozerBuilderTest.Bean.class), type("java.util.Map").mapNull(true), TypeMappingOptions.trimStrings(), TypeMappingOptions.relationshipType(CUMULATIVE), TypeMappingOptions.stopOnErrors(), TypeMappingOptions.mapEmptyString()).fields(field("src").accessible(true), this_().mapKey("value").mapMethods("get", "put"), FieldsMappingOptions.customConverter(CustomConverter.class)).fields("src", this_(), FieldsMappingOptions.deepHintA(Integer.class), FieldsMappingOptions.deepHintB(String.class), FieldsMappingOptions.customConverter(CustomConverter.class, "param"));
            }
        };
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(builder).build();
        mapper.map(1, String.class);
    }

    @Test
    public void shouldHaveIterateType() {
        BeanMappingBuilder builder = new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(type(DozerBuilderTest.IterateBean.class), type(DozerBuilderTest.IterateBean2.class)).fields(field("integers"), field("strings").iterate().setMethod("addString"), FieldsMappingOptions.hintB(String.class));
            }
        };
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(builder).build();
        DozerBuilderTest.IterateBean bean = new DozerBuilderTest.IterateBean();
        bean.getIntegers().add(new Integer("1"));
        DozerBuilderTest.IterateBean2 result = mapper.map(bean, DozerBuilderTest.IterateBean2.class);
        Assert.assertThat(result.strings.size(), CoreMatchers.equalTo(1));
    }

    public static class Bean {
        private String excluded;

        private String src;

        private Integer dest;

        public String getExcluded() {
            return excluded;
        }

        public void setExcluded(String excluded) {
            this.excluded = excluded;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public Integer getDest() {
            return dest;
        }

        public void setDest(Integer dest) {
            this.dest = dest;
        }
    }

    public static class IterateBean {
        List<Integer> integers = new ArrayList<>();

        List<String> strings = new ArrayList<>();

        public List<Integer> getIntegers() {
            return integers;
        }

        public List<String> getStrings() {
            return strings;
        }

        public void addString(String string) {
            strings.add(string);
        }
    }

    public static class IterateBean2 extends DozerBuilderTest.IterateBean {}
}

