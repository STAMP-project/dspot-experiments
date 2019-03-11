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
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class GenericsTest {
    @Test
    public void shouldDetermineCollectionTypeViaFieldGenericType() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(GenericsTest.Container.class, GenericsTest.ContainerDTO.class).fields(field("items").accessible(true), "items");
            }
        }).build();
        GenericsTest.Container container = prepareContainer();
        GenericsTest.Container containerSpy = Mockito.spy(container);
        Mockito.when(containerSpy.getItems()).thenThrow(new IllegalStateException());
        GenericsTest.ContainerDTO result = mapper.map(containerSpy, GenericsTest.ContainerDTO.class);
        assertDto(result);
    }

    @Test
    public void shouldDetermineCollectionTypeViaGetter() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(GenericsTest.Container.class, GenericsTest.ContainerDTO.class).fields("items", "items");
            }
        }).build();
        GenericsTest.Container container = prepareContainer();
        GenericsTest.ContainerDTO result = mapper.map(container, GenericsTest.ContainerDTO.class);
        assertDto(result);
    }

    public static class Container {
        List<GenericsTest.Item> items;

        public List<GenericsTest.Item> getItems() {
            return items;
        }
    }

    public static class ContainerDTO {
        private List<GenericsTest.ItemDTO> items;

        public List<GenericsTest.ItemDTO> getItems() {
            return items;
        }

        public void setItems(List<GenericsTest.ItemDTO> items) {
            this.items = items;
        }
    }

    public static class Item {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ItemDTO {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

