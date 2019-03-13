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
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class TitleIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static JavaClass classWithTitle;

    @Test
    public void descriptionAppearsInClassJavadoc() {
        String javaDocComment = TitleIT.classWithTitle.getComment();
        Assert.assertThat(javaDocComment, containsString("A title for this type"));
    }

    @Test
    public void descriptionAppearsInFieldJavadoc() {
        JavaField javaField = TitleIT.classWithTitle.getFieldByName("title");
        String javaDocComment = javaField.getComment();
        Assert.assertThat(javaDocComment, containsString("A title for this property"));
    }

    @Test
    public void descriptionAppearsInGetterJavadoc() {
        JavaMethod javaMethod = TitleIT.classWithTitle.getMethodBySignature("getTitle", new Type[]{  });
        String javaDocComment = javaMethod.getComment();
        Assert.assertThat(javaDocComment, containsString("A title for this property"));
    }

    @Test
    public void descriptionAppearsInSetterJavadoc() {
        JavaMethod javaMethod = TitleIT.classWithTitle.getMethodBySignature("setTitle", new Type[]{ new Type("java.lang.String") });
        String javaDocComment = javaMethod.getComment();
        Assert.assertThat(javaDocComment, containsString("A title for this property"));
    }

    @Test
    public void titleAppearsBeforeDescriptionInJavadoc() {
        JavaField javaField = TitleIT.classWithTitle.getFieldByName("titleAndDescription");
        String javaDocComment = javaField.getComment();
        Assert.assertThat(javaDocComment, containsString("A description for this property"));
        Assert.assertThat(javaDocComment.indexOf("A title for this property"), is(lessThan(javaDocComment.indexOf("A description for this property"))));
    }
}

