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
import java.util.Collections;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.SchemaStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class FormatRulePrimitivesTest {
    private final GenerationConfig config = Mockito.mock(GenerationConfig.class);

    private final FormatRule rule;

    private final Class<?> primitive;

    private final Class<?> wrapper;

    public FormatRulePrimitivesTest(Class<?> primitive, Class<?> wrapper) {
        this.primitive = primitive;
        this.wrapper = wrapper;
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        Mockito.when(config.getFormatTypeMapping()).thenReturn(Collections.singletonMap("test", wrapper.getName()));
        rule = new FormatRule(new RuleFactory(config, new NoopAnnotator(), new SchemaStore()));
    }

    @Test
    public void usePrimitivesWithCustomTypeMapping() {
        JType result = rule.apply("fooBar", TextNode.valueOf("test"), null, new JCodeModel().ref(Object.class), null);
        Class<?> expected = ((primitive) != null) ? primitive : wrapper;
        Assert.assertThat(result.fullName(), Matchers.equalTo(expected.getName()));
        Assert.assertThat(result.isPrimitive(), Matchers.equalTo(((primitive) != null)));
    }
}

