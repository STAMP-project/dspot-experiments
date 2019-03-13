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


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.hamcrest.MatcherAssert;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.util.NameHelper;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class EnumRuleTest {
    private Schema schema = Mockito.mock(Schema.class);

    private NameHelper nameHelper = Mockito.mock(NameHelper.class);

    private Annotator annotator = Mockito.mock(Annotator.class);

    private RuleFactory ruleFactory = Mockito.mock(RuleFactory.class);

    private TypeRule typeRule = Mockito.mock(TypeRule.class);

    private EnumRule rule = new EnumRule(ruleFactory);

    @Test
    public void applyGeneratesUniqueEnumNamesForMultipleEnumNodesWithSameName() {
        Answer<String> firstArgAnswer = new EnumRuleTest.FirstArgAnswer<>();
        Mockito.when(nameHelper.getClassName(ArgumentMatchers.anyString(), Matchers.any(JsonNode.class))).thenAnswer(firstArgAnswer);
        Mockito.when(nameHelper.replaceIllegalCharacters(ArgumentMatchers.anyString())).thenAnswer(firstArgAnswer);
        Mockito.when(nameHelper.normalizeName(ArgumentMatchers.anyString())).thenAnswer(firstArgAnswer);
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add("open");
        arrayNode.add("closed");
        ObjectNode enumNode = objectMapper.createObjectNode();
        enumNode.put("type", "string");
        enumNode.set("enum", arrayNode);
        // We're always a string for the purposes of this test
        Mockito.when(typeRule.apply("status", enumNode, null, jpackage, schema)).thenReturn(jpackage.owner()._ref(String.class));
        JType result1 = rule.apply("status", enumNode, null, jpackage, schema);
        JType result2 = rule.apply("status", enumNode, null, jpackage, schema);
        MatcherAssert.assertThat(result1.fullName(), is("org.jsonschema2pojo.rules.Status"));
        MatcherAssert.assertThat(result2.fullName(), is("org.jsonschema2pojo.rules.Status_"));
    }

    private static class FirstArgAnswer<T> implements Answer<T> {
        @SuppressWarnings("unchecked")
        @Override
        public T answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            // noinspection unchecked
            return ((T) (args[0]));
        }
    }
}

