/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config;


import io.helidon.common.CollectionsHelper;
import io.helidon.common.GenericType;
import io.helidon.config.ConfigMapperManager.MapperProviders;
import io.helidon.config.spi.ConfigMapper;
import io.helidon.config.spi.ConfigMapperProvider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link ConfigMapperManager}.
 */
public class ConfigMapperManagerTest {
    private static final ConfigMapperManager managerNoServices = BuilderImpl.buildMappers(false, MapperProviders.create());

    private static final ConfigMapperManager managerWithServices = BuilderImpl.buildMappers(true, MapperProviders.create());

    @Test
    public void testUnknownMapper() {
        Assertions.assertThrows(ConfigMappingException.class, () -> ConfigMapperManagerTest.managerNoServices.map(Mockito.mock(Config.class), ConfigMapperManagerTest.CustomClass.class));
        Assertions.assertThrows(ConfigMappingException.class, () -> ConfigMapperManagerTest.managerWithServices.map(Mockito.mock(Config.class), ConfigMapperManagerTest.CustomClass.class));
    }

    @Test
    public void testBuiltInMappers() {
        Integer builtIn = ConfigMapperManagerTest.managerWithServices.map(ConfigMapperManagerTest.managerWithServices.simpleConfig("builtIn", "49"), Integer.class);
        MatcherAssert.assertThat(builtIn, CoreMatchers.is(49));
    }

    @Test
    public void testCustomMapper() {
        Config config = ConfigMapperManagerTest.managerWithServices.simpleConfig("custom", "49");
        Integer custom = config.asString().map(( str) -> (Integer.parseInt(str)) + 1).get();
        MatcherAssert.assertThat(custom, CoreMatchers.is(50));
    }

    // this will not work, as beans are moved away from core
    public static class CustomClass {
        public CustomClass() {
        }
    }

    private static class ParametrizedConfigMapper implements ConfigMapperProvider {
        @Override
        public Map<Class<?>, Function<Config, ?>> mappers() {
            return CollectionsHelper.mapOf();
        }

        @Override
        public <T> Optional<BiFunction<Config, ConfigMapper, T>> mapper(GenericType<T> type) {
            Class<?> rawType = type.rawType();
            if (rawType.equals(Map.class)) {
                // this is our class - we support Map<String, ?>
                Type theType = type.type();
                if (theType instanceof ParameterizedType) {
                    ParameterizedType ptype = ((ParameterizedType) (theType));
                    Type[] typeArgs = ptype.getActualTypeArguments();
                    if ((typeArgs.length) == 2) {
                        if (typeArgs[0].equals(String.class)) {
                            return Optional.of(( config, mapper) -> {
                                Map<String, ?> theMap = new HashMap<>();
                                config.asMap().ifPresent(( configMap) -> {
                                    configMap.forEach(( key, value) -> {
                                        theMap.put(key, mapper.map(value, GenericType.create(typeArgs[1]), key));
                                    });
                                });
                                return type.cast(theMap);
                            });
                        }
                    }
                }
            }
            if (rawType.equals(List.class)) {
                // we support List<?>, defaults to List<String>
                Type theType = type.type();
                if (theType instanceof ParameterizedType) {
                    ParameterizedType ptype = ((ParameterizedType) (theType));
                    Type[] typeArgs = ptype.getActualTypeArguments();
                    if ((typeArgs.length) == 1) {
                        Type elementType = typeArgs[0];
                        return Optional.of(( config, mapper) -> {
                            List<Object> theList = new LinkedList<>();
                            config.asNodeList().ifPresent(( nodes) -> {
                                nodes.forEach(( confNode) -> {
                                    theList.add(confNode.as(GenericType.create(elementType)).get());
                                });
                            });
                            return type.cast(theList);
                        });
                    }
                } else {
                    return Optional.of(( config, mapper) -> type.cast(config.asList(String.class).get()));
                }
            }
            return Optional.empty();
        }
    }
}

