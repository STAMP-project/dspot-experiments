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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class DescriptionRuleTest {
    private static final String TARGET_CLASS_NAME = (ArrayRuleTest.class.getName()) + ".DummyClass";

    private DescriptionRule rule = new DescriptionRule();

    @Test
    public void applyAddsDescriptionToJavadoc() throws JClassAlreadyExistsException {
        JDefinedClass jclass = new JCodeModel()._class(DescriptionRuleTest.TARGET_CLASS_NAME);
        ObjectMapper mapper = new ObjectMapper();
        TextNode descriptionNode = mapper.createObjectNode().textNode("some description");
        JDocComment result = rule.apply("fooBar", descriptionNode, null, jclass, null);
        MatcherAssert.assertThat(result, sameInstance(jclass.javadoc()));
        MatcherAssert.assertThat(result.size(), is(1));
        MatcherAssert.assertThat(((String) (result.get(0))), is("some description"));
    }
}

