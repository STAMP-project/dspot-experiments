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


import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.SchemaStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class FormatRuleTest {
    private GenerationConfig config = Mockito.mock(GenerationConfig.class);

    private FormatRule rule = new FormatRule(new RuleFactory(config, new NoopAnnotator(), new SchemaStore()));

    private final String formatValue;

    private final Class<?> expectedType;

    public FormatRuleTest(String formatValue, Class<?> expectedType) {
        this.formatValue = formatValue;
        this.expectedType = expectedType;
    }

    @Test
    public void applyGeneratesTypeFromFormatValue() {
        TextNode formatNode = TextNode.valueOf(formatValue);
        JType result = rule.apply("fooBar", formatNode, null, new JCodeModel().ref(String.class), null);
        Assert.assertThat(result.fullName(), equalTo(expectedType.getName()));
    }

    @Test
    public void applyDefaultsToBaseType() {
        TextNode formatNode = TextNode.valueOf("unknown-format");
        JType baseType = new JCodeModel().ref(Long.class);
        JType result = rule.apply("fooBar", formatNode, null, baseType, null);
        Assert.assertThat(result, equalTo(baseType));
    }
}

