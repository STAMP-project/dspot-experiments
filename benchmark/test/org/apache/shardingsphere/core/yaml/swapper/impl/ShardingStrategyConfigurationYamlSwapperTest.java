/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.yaml.swapper.impl;


import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.core.fixture.ComplexKeysShardingAlgorithmFixture;
import org.apache.shardingsphere.core.fixture.HintShardingAlgorithmFixture;
import org.apache.shardingsphere.core.fixture.PreciseShardingAlgorithmFixture;
import org.apache.shardingsphere.core.fixture.RangeShardingAlgorithmFixture;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.strategy.YamlInlineShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.strategy.YamlNoneShardingStrategyConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingStrategyConfigurationYamlSwapperTest {
    private ShardingStrategyConfigurationYamlSwapper shardingStrategyConfigurationYamlSwapper = new ShardingStrategyConfigurationYamlSwapper();

    @Test
    public void assertSwapToYamlWithStandard() {
        PreciseShardingAlgorithm preciseShardingAlgorithm = Mockito.mock(PreciseShardingAlgorithm.class);
        RangeShardingAlgorithm rangeShardingAlgorithm = Mockito.mock(RangeShardingAlgorithm.class);
        YamlShardingStrategyConfiguration actual = shardingStrategyConfigurationYamlSwapper.swap(new StandardShardingStrategyConfiguration("id", preciseShardingAlgorithm, rangeShardingAlgorithm));
        Assert.assertThat(actual.getStandard().getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getStandard().getPreciseAlgorithmClassName(), CoreMatchers.is(preciseShardingAlgorithm.getClass().getName()));
        Assert.assertThat(actual.getStandard().getRangeAlgorithmClassName(), CoreMatchers.is(rangeShardingAlgorithm.getClass().getName()));
        Assert.assertNull(actual.getInline());
        Assert.assertNull(actual.getComplex());
        Assert.assertNull(actual.getHint());
        Assert.assertNull(actual.getNone());
    }

    @Test
    public void assertSwapToYamlWithInline() {
        YamlShardingStrategyConfiguration actual = shardingStrategyConfigurationYamlSwapper.swap(new InlineShardingStrategyConfiguration("id", "xxx_$->{id % 10}"));
        Assert.assertThat(actual.getInline().getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getInline().getAlgorithmExpression(), CoreMatchers.is("xxx_$->{id % 10}"));
        Assert.assertNull(actual.getStandard());
        Assert.assertNull(actual.getComplex());
        Assert.assertNull(actual.getHint());
        Assert.assertNull(actual.getNone());
    }

    @Test
    public void assertSwapToYamlWithComplex() {
        ComplexKeysShardingAlgorithm complexKeysShardingAlgorithm = Mockito.mock(ComplexKeysShardingAlgorithm.class);
        YamlShardingStrategyConfiguration actual = shardingStrategyConfigurationYamlSwapper.swap(new ComplexShardingStrategyConfiguration("id, creation_date", complexKeysShardingAlgorithm));
        Assert.assertThat(actual.getComplex().getShardingColumns(), CoreMatchers.is("id, creation_date"));
        Assert.assertThat(actual.getComplex().getAlgorithmClassName(), CoreMatchers.is(complexKeysShardingAlgorithm.getClass().getName()));
        Assert.assertNull(actual.getStandard());
        Assert.assertNull(actual.getInline());
        Assert.assertNull(actual.getHint());
        Assert.assertNull(actual.getNone());
    }

    @Test
    public void assertSwapToYamlWithHint() {
        HintShardingAlgorithm hintShardingAlgorithm = Mockito.mock(HintShardingAlgorithm.class);
        YamlShardingStrategyConfiguration actual = shardingStrategyConfigurationYamlSwapper.swap(new HintShardingStrategyConfiguration(hintShardingAlgorithm));
        Assert.assertThat(actual.getHint().getAlgorithmClassName(), CoreMatchers.is(hintShardingAlgorithm.getClass().getName()));
        Assert.assertNull(actual.getStandard());
        Assert.assertNull(actual.getInline());
        Assert.assertNull(actual.getComplex());
        Assert.assertNull(actual.getNone());
    }

    @Test
    public void assertSwapToYamlWithNone() {
        YamlShardingStrategyConfiguration actual = shardingStrategyConfigurationYamlSwapper.swap(new NoneShardingStrategyConfiguration());
        Assert.assertNull(actual.getStandard());
        Assert.assertNull(actual.getInline());
        Assert.assertNull(actual.getComplex());
        Assert.assertNull(actual.getHint());
        Assert.assertNull(actual.getNone());
    }

    @Test
    public void assertSwapToObjectWithStandardWithRangeShardingAlgorithm() {
        StandardShardingStrategyConfiguration actual = ((StandardShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createStandardShardingStrategyConfiguration(true))));
        Assert.assertThat(actual.getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getPreciseShardingAlgorithm(), CoreMatchers.instanceOf(PreciseShardingAlgorithmFixture.class));
        Assert.assertThat(actual.getRangeShardingAlgorithm(), CoreMatchers.instanceOf(RangeShardingAlgorithmFixture.class));
    }

    @Test
    public void assertSwapToObjectWithStandardWithoutRangeShardingAlgorithm() {
        StandardShardingStrategyConfiguration actual = ((StandardShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createStandardShardingStrategyConfiguration(false))));
        Assert.assertThat(actual.getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getPreciseShardingAlgorithm(), CoreMatchers.instanceOf(PreciseShardingAlgorithmFixture.class));
        Assert.assertNull(actual.getRangeShardingAlgorithm());
    }

    @Test
    public void assertSwapToObjectWithInline() {
        InlineShardingStrategyConfiguration actual = ((InlineShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createInlineShardingStrategyConfiguration())));
        Assert.assertThat(actual.getShardingColumn(), CoreMatchers.is("id"));
        Assert.assertThat(actual.getAlgorithmExpression(), CoreMatchers.is("xxx_$->{id % 10}"));
    }

    @Test
    public void assertSwapToObjectWithComplex() {
        ComplexShardingStrategyConfiguration actual = ((ComplexShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createComplexShardingStrategyConfiguration())));
        Assert.assertThat(actual.getShardingColumns(), CoreMatchers.is("id, creation_date"));
        Assert.assertThat(actual.getShardingAlgorithm(), CoreMatchers.instanceOf(ComplexKeysShardingAlgorithmFixture.class));
    }

    @Test
    public void assertSwapToObjectWithHint() {
        HintShardingStrategyConfiguration actual = ((HintShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createHintShardingStrategyConfiguration())));
        Assert.assertThat(actual.getShardingAlgorithm(), CoreMatchers.instanceOf(HintShardingAlgorithmFixture.class));
    }

    @Test
    public void assertSwapToObjectWithNone() {
        NoneShardingStrategyConfiguration actual = ((NoneShardingStrategyConfiguration) (shardingStrategyConfigurationYamlSwapper.swap(createNoneShardingStrategyConfiguration())));
        Assert.assertThat(actual, CoreMatchers.instanceOf(NoneShardingStrategyConfiguration.class));
    }

    @Test
    public void assertSwapToObjectWithNull() {
        Assert.assertNull(shardingStrategyConfigurationYamlSwapper.swap(new YamlShardingStrategyConfiguration()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertSwapToObjectWithMultipleSHardingStrategies() {
        YamlShardingStrategyConfiguration actual = new YamlShardingStrategyConfiguration();
        YamlInlineShardingStrategyConfiguration inlineShardingStrategyConfig = new YamlInlineShardingStrategyConfiguration();
        inlineShardingStrategyConfig.setShardingColumn("order_id");
        inlineShardingStrategyConfig.setAlgorithmExpression("t_order_${order_id % 2}");
        actual.setInline(inlineShardingStrategyConfig);
        actual.setNone(new YamlNoneShardingStrategyConfiguration());
        shardingStrategyConfigurationYamlSwapper.swap(actual);
    }
}

