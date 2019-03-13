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


import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Rule;
import org.junit.Test;


public class IncludeToStringExcludesIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void beansIncludeAllToStringPropertiesByDefault() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        testConfig(CodeGenerationHelper.config(), "com.example.PrimitiveProperties@%s[a=<null>,b=<null>,c=<null>,additionalProperties={}]");
    }

    @Test
    public void beansOmitToStringProperties() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        testConfig(CodeGenerationHelper.config("toStringExcludes", new String[]{ "b", "c" }), "com.example.PrimitiveProperties@%s[a=<null>,additionalProperties={}]");
    }
}

