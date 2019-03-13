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
package org.jsonschema2pojo.integration.ant;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.jsonschema2pojo.ant.Jsonschema2PojoTask;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class Jsonschema2PojoTaskIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void antTaskExecutesSuccessfullyWithValidSchemas() throws ClassNotFoundException, URISyntaxException {
        invokeAntBuild("/ant/build.xml");
        ClassLoader resultsClassLoader = schemaRule.compile(buildCustomClasspath());
        Class<?> generatedClass = resultsClassLoader.loadClass("com.example.WordDelimit");
        Assert.assertThat(generatedClass, is(notNullValue()));
    }

    @Test
    public void antTaskDocumentationIncludesAllProperties() throws IOException {
        String documentation = FileUtils.readFileToString(new File("../jsonschema2pojo-ant/src/site/Jsonschema2PojoTask.html"));
        for (Field f : Jsonschema2PojoTask.class.getDeclaredFields()) {
            Assert.assertThat(documentation, containsString(((">" + (f.getName())) + "<")));
        }
    }
}

