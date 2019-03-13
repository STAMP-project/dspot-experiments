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


import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.SchemaStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RuleFactoryImplTest {
    @Test
    public void factoryMethodsCreateRules() {
        RuleFactory ruleFactory = new RuleFactory();
        Assert.assertThat(ruleFactory.getAdditionalPropertiesRule(), notNullValue());
        Assert.assertThat(ruleFactory.getArrayRule(), notNullValue());
        Assert.assertThat(ruleFactory.getDefaultRule(), notNullValue());
        Assert.assertThat(ruleFactory.getDescriptionRule(), notNullValue());
        Assert.assertThat(ruleFactory.getEnumRule(), notNullValue());
        Assert.assertThat(ruleFactory.getFormatRule(), notNullValue());
        Assert.assertThat(ruleFactory.getObjectRule(), notNullValue());
        Assert.assertThat(ruleFactory.getPropertiesRule(), notNullValue());
        Assert.assertThat(ruleFactory.getPropertyRule(), notNullValue());
        Assert.assertThat(ruleFactory.getSchemaRule(), notNullValue());
        Assert.assertThat(ruleFactory.getTitleRule(), notNullValue());
        Assert.assertThat(ruleFactory.getTypeRule(), notNullValue());
        Assert.assertThat(ruleFactory.getMinimumMaximumRule(), notNullValue());
        Assert.assertThat(ruleFactory.getMinItemsMaxItemsRule(), notNullValue());
        Assert.assertThat(ruleFactory.getPatternRule(), notNullValue());
        Assert.assertThat(ruleFactory.getMinLengthMaxLengthRule(), notNullValue());
        Assert.assertThat(ruleFactory.getValidRule(), notNullValue());
    }

    @Test
    public void generationConfigIsReturned() {
        GenerationConfig mockGenerationConfig = Mockito.mock(GenerationConfig.class);
        RuleFactory ruleFactory = new RuleFactory(mockGenerationConfig, new NoopAnnotator(), new SchemaStore());
        Assert.assertThat(ruleFactory.getGenerationConfig(), is(sameInstance(mockGenerationConfig)));
    }

    @Test
    public void schemaStoreIsReturned() {
        SchemaStore mockSchemaStore = Mockito.mock(SchemaStore.class);
        RuleFactory ruleFactory = new RuleFactory(new DefaultGenerationConfig(), new NoopAnnotator(), mockSchemaStore);
        Assert.assertThat(ruleFactory.getSchemaStore(), is(sameInstance(mockSchemaStore)));
    }
}

