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
package org.jsonschema2pojo.rules;


import JMod.PRIVATE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.Schema;
import org.junit.Test;


public class RequiredArrayRuleTest {
    private static final String TARGET_CLASS_NAME = (RequiredArrayRuleTest.class.getName()) + ".DummyClass";

    private RequiredArrayRule rule = new RequiredArrayRule(new RuleFactory());

    @Test
    public void shouldUpdateJavaDoc() throws JClassAlreadyExistsException {
        JDefinedClass jclass = new JCodeModel()._class(RequiredArrayRuleTest.TARGET_CLASS_NAME);
        jclass.field(PRIVATE, jclass.owner().ref(String.class), "fooBar");
        jclass.field(PRIVATE, jclass.owner().ref(String.class), "foo");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode requiredNode = mapper.createArrayNode().add("fooBar");
        rule.apply("Class", requiredNode, null, jclass, new Schema(null, requiredNode, null));
        JDocComment fooBarJavaDoc = jclass.fields().get("fooBar").javadoc();
        JDocComment fooJavaDoc = jclass.fields().get("foo").javadoc();
        MatcherAssert.assertThat(fooBarJavaDoc.size(), Matchers.is(1));
        MatcherAssert.assertThat(((String) (fooBarJavaDoc.get(0))), Matchers.is("\n(Required)"));
        MatcherAssert.assertThat(fooJavaDoc.size(), Matchers.is(0));
    }

    @Test
    public void shouldUpdateAnnotations() throws JClassAlreadyExistsException {
        setupRuleFactoryToIncludeJsr303();
        JDefinedClass jclass = new JCodeModel()._class(RequiredArrayRuleTest.TARGET_CLASS_NAME);
        jclass.field(PRIVATE, jclass.owner().ref(String.class), "fooBar");
        jclass.field(PRIVATE, jclass.owner().ref(String.class), "foo");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode requiredNode = mapper.createArrayNode().add("foo_bar");
        rule.apply("Class", requiredNode, null, jclass, new Schema(null, requiredNode, null));
        Collection<JAnnotationUse> fooBarAnnotations = jclass.fields().get("fooBar").annotations();
        Collection<JAnnotationUse> fooAnnotations = jclass.fields().get("foo").annotations();
        MatcherAssert.assertThat(fooBarAnnotations.size(), Matchers.is(1));
        MatcherAssert.assertThat(fooBarAnnotations.iterator().next().getAnnotationClass().name(), Matchers.is(NotNull.class.getSimpleName()));
        MatcherAssert.assertThat(fooAnnotations.size(), Matchers.is(0));
    }
}

