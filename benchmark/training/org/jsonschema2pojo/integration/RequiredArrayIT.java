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
package org.jsonschema2pojo.integration;


import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class RequiredArrayIT extends RequiredIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static JavaClass classWithRequired;

    @Test
    public void requiredAppearsInFieldJavadoc() {
        JavaField javaField = RequiredArrayIT.classWithRequired.getFieldByName("requiredProperty");
        String javaDocComment = javaField.getComment();
        Assert.assertThat(javaDocComment, Matchers.containsString("(Required)"));
    }

    @Test
    public void requiredAppearsInGetterJavadoc() {
        JavaMethod javaMethod = RequiredArrayIT.classWithRequired.getMethodBySignature("getRequiredProperty", new Type[]{  });
        String javaDocComment = javaMethod.getComment();
        Assert.assertThat(javaDocComment, Matchers.containsString("(Required)"));
    }

    @Test
    public void requiredAppearsInSetterJavadoc() {
        JavaMethod javaMethod = RequiredArrayIT.classWithRequired.getMethodBySignature("setRequiredProperty", new Type[]{ new Type("java.lang.String") });
        String javaDocComment = javaMethod.getComment();
        Assert.assertThat(javaDocComment, Matchers.containsString("(Required)"));
    }

    @Test
    public void nonRequiredFiedHasNoRequiredText() {
        JavaField javaField = RequiredArrayIT.classWithRequired.getFieldByName("nonRequiredProperty");
        String javaDocComment = javaField.getComment();
        Assert.assertThat(javaDocComment, Matchers.not(Matchers.containsString("(Required)")));
    }

    @Test
    public void notRequiredIsTheDefault() {
        JavaField javaField = RequiredArrayIT.classWithRequired.getFieldByName("defaultNotRequiredProperty");
        String javaDocComment = javaField.getComment();
        Assert.assertThat(javaDocComment, Matchers.not(Matchers.containsString("(Required)")));
    }
}

