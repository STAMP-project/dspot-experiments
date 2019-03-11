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
package io.helidon.config.tests.mappers1;


import io.helidon.config.ConfigMappingException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Same test as {@link MapperServicesEnabledTest} with {@link Config.Builder#disableMapperServices()}.
 */
public class MapperServicesDisabledTest extends AbstractMapperServicesTest {
    @Test
    public void testLogger() {
        ConfigMappingException cme = Assertions.assertThrows(ConfigMappingException.class, this::getLogger);
        MatcherAssert.assertThat(cme.getMessage(), Matchers.containsString(AbstractMapperServicesTest.LOGGER_KEY));
    }

    @Test
    public void testLocale() {
        ConfigMappingException cme = Assertions.assertThrows(ConfigMappingException.class, this::getLocale);
        MatcherAssert.assertThat(cme.getMessage(), Matchers.containsString(AbstractMapperServicesTest.LOCALE_KEY));
    }
}

