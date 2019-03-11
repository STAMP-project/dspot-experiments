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


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CollectionWithNullTest {
    private DozerBeanMapperBuilder mapperBuilder;

    private CollectionWithNullTest.Foo foo;

    private CollectionWithNullTest.Bar bar;

    @Test
    public void shouldMapNullAsListFirstElement() {
        Mapper mapper = mapperBuilder.withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CollectionWithNullTest.Foo.class, CollectionWithNullTest.Bar.class, TypeMappingOptions.mapNull(false)).fields("wheeIds", "wheeList");
            }
        }).build();
        bar.getWheeList().add(null);
        bar.getWheeList().add(new CollectionWithNullTest.Whee("1"));
        CollectionWithNullTest.Foo result = mapper.map(bar, CollectionWithNullTest.Foo.class);
        MatcherAssert.assertThat(result.getWheeIds().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(result.getWheeIds().get(0), CoreMatchers.equalTo("1"));
    }

    @Test
    public void shouldMapNullAsListSecondElement() {
        Mapper mapper = mapperBuilder.withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CollectionWithNullTest.Foo.class, CollectionWithNullTest.Bar.class, TypeMappingOptions.mapNull(false)).fields("wheeIds", "wheeList");
            }
        }).build();
        bar.getWheeList().add(new CollectionWithNullTest.Whee("1"));
        bar.getWheeList().add(null);
        CollectionWithNullTest.Foo result = mapper.map(bar, CollectionWithNullTest.Foo.class);
        MatcherAssert.assertThat(result.getWheeIds().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(result.getWheeIds().get(0), CoreMatchers.equalTo("1"));
    }

    @Test
    public void shouldMapNullAsSetSecondElement() {
        Mapper mapper = mapperBuilder.withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CollectionWithNullTest.Foo.class, CollectionWithNullTest.Bar.class, TypeMappingOptions.mapNull(false)).fields("wheeIds", "wheeSet");
            }
        }).build();
        bar.getWheeSet().add(new CollectionWithNullTest.Whee("1"));
        bar.getWheeSet().add(null);
        CollectionWithNullTest.Foo result = mapper.map(bar, CollectionWithNullTest.Foo.class);
        MatcherAssert.assertThat(result.getWheeIds().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(result.getWheeIds().get(0), CoreMatchers.equalTo("1"));
    }

    @Test
    public void shouldMapNullAsSetSecondElement_Reverse() {
        Mapper mapper = mapperBuilder.withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CollectionWithNullTest.Foo.class, CollectionWithNullTest.Bar.class, TypeMappingOptions.mapNull(false)).fields("wheeIds", "wheeSet");
            }
        }).build();
        foo.getWheeIds().add("1");
        foo.getWheeIds().add(null);
        CollectionWithNullTest.Bar result = mapper.map(foo, CollectionWithNullTest.Bar.class);
        MatcherAssert.assertThat(result.getWheeSet().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(result.getWheeSet().iterator().next().getId(), CoreMatchers.equalTo("1"));
    }

    public static class Foo {
        List<String> wheeIds = new ArrayList<>();

        public List<String> getWheeIds() {
            return wheeIds;
        }

        public void setWheeIds(List<String> wheeIds) {
            this.wheeIds = wheeIds;
        }
    }

    public static class Bar {
        List<CollectionWithNullTest.Whee> wheeList = new ArrayList<>();

        Set<CollectionWithNullTest.Whee> wheeSet = new HashSet<>();

        public List<CollectionWithNullTest.Whee> getWheeList() {
            return wheeList;
        }

        public void setWheeList(List<CollectionWithNullTest.Whee> wheeList) {
            this.wheeList = wheeList;
        }

        public Set<CollectionWithNullTest.Whee> getWheeSet() {
            return wheeSet;
        }

        public void setWheeSet(Set<CollectionWithNullTest.Whee> wheeSet) {
            this.wheeSet = wheeSet;
        }
    }

    public static class Whee {
        String id;

        public Whee(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class WheeConverter extends DozerConverter<String, CollectionWithNullTest.Whee> {
        public WheeConverter() {
            super(String.class, CollectionWithNullTest.Whee.class);
        }

        @Override
        public CollectionWithNullTest.Whee convertTo(String source, CollectionWithNullTest.Whee destination) {
            return new CollectionWithNullTest.Whee(source);
        }

        @Override
        public String convertFrom(CollectionWithNullTest.Whee source, String destination) {
            return source.getId();
        }
    }
}

