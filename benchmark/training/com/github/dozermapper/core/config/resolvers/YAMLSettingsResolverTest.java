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
package com.github.dozermapper.core.config.resolvers;


import SettingsKeys.CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE;
import com.github.dozermapper.core.util.DefaultClassLoader;
import org.junit.Assert;
import org.junit.Test;


public class YAMLSettingsResolverTest {
    @Test
    public void canResolve() {
        SettingsResolver resolver = new YAMLSettingsResolver(new DefaultClassLoader(getClass().getClassLoader()), "dozer.yaml");
        resolver.init();
        Integer answer = Integer.valueOf(resolver.get(CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE, 0).toString());
        Assert.assertNotNull(answer);
        Assert.assertEquals(Integer.valueOf(5000), answer);
    }
}

