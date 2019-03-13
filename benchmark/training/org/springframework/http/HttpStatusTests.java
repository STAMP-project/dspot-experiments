/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.http;


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class HttpStatusTests {
    private Map<Integer, String> statusCodes = new LinkedHashMap<>();

    @Test
    public void fromMapToEnum() {
        for (Map.Entry<Integer, String> entry : statusCodes.entrySet()) {
            int value = entry.getKey();
            HttpStatus status = HttpStatus.valueOf(value);
            Assert.assertEquals("Invalid value", value, status.value());
            Assert.assertEquals((("Invalid name for [" + value) + "]"), entry.getValue(), status.name());
        }
    }

    @Test
    public void fromEnumToMap() {
        for (HttpStatus status : HttpStatus.values()) {
            int value = status.value();
            if (((value == 302) || (value == 413)) || (value == 414)) {
                continue;
            }
            Assert.assertTrue((("Map has no value for [" + value) + "]"), statusCodes.containsKey(value));
            Assert.assertEquals((("Invalid name for [" + value) + "]"), statusCodes.get(value), status.name());
        }
    }
}

