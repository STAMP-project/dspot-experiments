/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.agent.common.util;


import java.util.HashMap;
import org.junit.Test;


public class HeaderUtilTest {
    @Test
    public void shouldGetExtraPropertiesFromHeader() {
        assertExtraPropertiesWithoutBase64(null, new HashMap<>());
        assertExtraPropertiesWithoutBase64("", new HashMap<>());
        assertExtraProperties("", new HashMap<>());
        assertExtraProperties("Key1=Value1 key2=value2", new HashMap<String, String>() {
            {
                put("Key1", "Value1");
                put("key2", "value2");
            }
        });
        assertExtraProperties("  Key1=Value1    key2=value2  ", new HashMap<String, String>() {
            {
                put("Key1", "Value1");
                put("key2", "value2");
            }
        });
        assertExtraProperties("Key1=Value1 key2=value2 key2=value3", new HashMap<String, String>() {
            {
                put("Key1", "Value1");
                put("key2", "value2");
            }
        });
        assertExtraProperties("Key1%20WithSpace=Value1%20WithSpace key2=value2", new HashMap<String, String>() {
            {
                put("Key1 WithSpace", "Value1 WithSpace");
                put("key2", "value2");
            }
        });
    }

    @Test
    public void shouldNotFailIfExtraPropertiesAreNotFormattedProperly() {
        assertExtraProperties("abc", new HashMap<>());
    }
}

