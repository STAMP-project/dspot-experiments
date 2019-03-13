/**
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.core.internal.config.mapper.string;


import com.speedment.runtime.typemapper.string.StringToLocaleMapper;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Maria Sparenberg
 * @author Patrick Hobusch
 */
public class StringToLocaleMapperTest {
    private StringToLocaleMapper mapper;

    @Test
    public void testStringLocaleMapping() {
        String string = "DE";
        Locale javaType = mapper.toJavaType(null, null, string);
        Assertions.assertEquals(new Locale("de"), javaType, "JavaType should have value 'de'");
        String databaseType = mapper.toDatabaseType(javaType);
        Assertions.assertTrue(string.equalsIgnoreCase(databaseType), "DatabaseType should have value 'de'");
    }
}

