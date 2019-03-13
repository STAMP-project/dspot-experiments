/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.util.serialization;


import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConfigHelperTest {
    @Test
    public void testMapToListsAndBack() {
        Map<String, String> fakeConfig = ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");
        // the map has no guarantees on order, but the lists should match up 1:1 with key:val associations preserved.
        List<String> keys = ConfigHelper.conf(fakeConfig).keys();
        List<String> vals = ConfigHelper.conf(fakeConfig).vals();
        for (int i = 0; i < (fakeConfig.size()); i++) {
            Assert.assertThat(fakeConfig.get(keys.get(i)), CoreMatchers.is(vals.get(i)));
        }
        Map<String, String> map = ConfigHelper.conf(keys, vals).asMap();
        for (int i = 0; i < (fakeConfig.size()); i++) {
            Assert.assertThat(vals.get(i), CoreMatchers.is(map.get(keys.get(i))));
        }
    }
}

