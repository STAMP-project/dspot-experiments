/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.integration.config;


import java.lang.reflect.Method;
import java.net.URL;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class FormatTypeMappingIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void canOverrideDateRelatedTypes() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/format/formattedProperties.json", "com.example", CodeGenerationHelper.config("formatTypeMapping", FormatTypeMappingIT.mapping("date", LocalDate.class, "time", LocalTime.class, "date-time", DateTime.class)));
        Class generatedType = resultsClassLoader.loadClass("com.example.FormattedProperties");
        Method dateTime = generatedType.getMethod("getStringAsDateTime");
        Method time = generatedType.getMethod("getStringAsTime");
        Method date = generatedType.getMethod("getStringAsDate");
        Assert.assertThat(dateTime.getReturnType(), Matchers.typeCompatibleWith(DateTime.class));
        Assert.assertThat(time.getReturnType(), Matchers.typeCompatibleWith(LocalTime.class));
        Assert.assertThat(date.getReturnType(), Matchers.typeCompatibleWith(LocalDate.class));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void canOverrideTypes() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/format/formattedProperties.json", "com.example", CodeGenerationHelper.config("formatTypeMapping", FormatTypeMappingIT.mapping("uri", URL.class)));
        Class generatedType = resultsClassLoader.loadClass("com.example.FormattedProperties");
        Method getter = generatedType.getMethod("getStringAsUri");
        Assert.assertThat(getter.getReturnType(), Matchers.typeCompatibleWith(URL.class));
    }

    @Test
    public void canOverrideNonStandardTypes() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/format/nonStandard.json", "com.example", CodeGenerationHelper.config("formatTypeMapping", FormatTypeMappingIT.mapping("non-standard", URL.class)));
        Class generatedType = resultsClassLoader.loadClass("com.example.NonStandard");
        Method getter = generatedType.getMethod("getStringAsNonStandard");
        Assert.assertThat(getter.getReturnType(), Matchers.typeCompatibleWith(URL.class));
    }
}

