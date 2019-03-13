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


import com.speedment.runtime.typemapper.string.TrueFalseStringToBooleanMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Maria Sparenberg
 * @author Patrick Hobusch
 */
public class TrueFalseStringToBooleanMapperTest {
    private TrueFalseStringToBooleanMapper mapper;

    @Test
    public void testStringTrueMapping() {
        String string = "TRUE";
        Boolean javaType = mapper.toJavaType(null, null, string);
        Assertions.assertEquals(true, javaType, "JavaType should have value 'true'");
        String databaseType = mapper.toDatabaseType(javaType);
        Assertions.assertTrue(string.equalsIgnoreCase(databaseType), "DatabaseType should have value 'true'");
    }

    @Test
    public void testStringFalseMapping() {
        String string = "FALSE";
        Boolean javaType = mapper.toJavaType(null, null, string);
        Assertions.assertEquals(false, javaType, "JavaType should have value 'false'");
        String databaseType = mapper.toDatabaseType(javaType);
        Assertions.assertTrue(string.equalsIgnoreCase(databaseType), "DatabaseType should have value 'true'");
    }
}

