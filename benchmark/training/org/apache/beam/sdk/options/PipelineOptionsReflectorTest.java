/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.options;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link PipelineOptionsReflector}.
 */
@RunWith(JUnit4.class)
public class PipelineOptionsReflectorTest {
    @Test
    public void testGetOptionSpecs() throws NoSuchMethodException {
        Set<PipelineOptionSpec> properties = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.SimpleOptions.class);
        MatcherAssert.assertThat(properties, Matchers.hasItems(PipelineOptionSpec.of(PipelineOptionsReflectorTest.SimpleOptions.class, "foo", PipelineOptionsReflectorTest.SimpleOptions.class.getDeclaredMethod("getFoo"))));
    }

    /**
     * Test interface.
     */
    public interface SimpleOptions extends PipelineOptions {
        String getFoo();

        void setFoo(String value);
    }

    @Test
    public void testFiltersNonGetterMethods() {
        Set<PipelineOptionSpec> properties = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.OnlyTwoValidGetters.class);
        MatcherAssert.assertThat(properties, Matchers.not(Matchers.hasItem(PipelineOptionsReflectorTest.hasName(Matchers.isOneOf("misspelled", "hasParameter", "prefix")))));
    }

    /**
     * Test interface.
     */
    public interface OnlyTwoValidGetters extends PipelineOptions {
        String getFoo();

        void setFoo(String value);

        boolean isBar();

        void setBar(boolean value);

        String gtMisspelled();

        void setMisspelled(String value);

        String getHasParameter(String value);

        void setHasParameter(String value);

        String noPrefix();

        void setNoPrefix(String value);
    }

    @Test
    public void testBaseClassOptions() {
        Set<PipelineOptionSpec> props = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.ExtendsSimpleOptions.class);
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("foo"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.SimpleOptions.class))));
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("foo"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.ExtendsSimpleOptions.class))));
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("bar"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.ExtendsSimpleOptions.class))));
    }

    /**
     * Test interface.
     */
    public interface ExtendsSimpleOptions extends PipelineOptionsReflectorTest.SimpleOptions {
        @Override
        String getFoo();

        @Override
        void setFoo(String value);

        String getBar();

        void setBar(String value);
    }

    @Test
    public void testExcludesNonPipelineOptionsMethods() {
        Set<PipelineOptionSpec> properties = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.ExtendsNonPipelineOptions.class);
        MatcherAssert.assertThat(properties, Matchers.not(Matchers.hasItem(PipelineOptionsReflectorTest.hasName("foo"))));
    }

    /**
     * Test interface.
     */
    public interface NoExtendsClause {
        String getFoo();

        void setFoo(String value);
    }

    /**
     * Test interface.
     */
    public interface ExtendsNonPipelineOptions extends PipelineOptions , PipelineOptionsReflectorTest.NoExtendsClause {}

    @Test
    public void testExcludesHiddenInterfaces() {
        Set<PipelineOptionSpec> properties = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.HiddenOptions.class);
        MatcherAssert.assertThat(properties, Matchers.not(Matchers.hasItem(PipelineOptionsReflectorTest.hasName("foo"))));
    }

    /**
     * Test interface.
     */
    @Hidden
    public interface HiddenOptions extends PipelineOptions {
        String getFoo();

        void setFoo(String value);
    }

    @Test
    public void testShouldSerialize() {
        Set<PipelineOptionSpec> properties = PipelineOptionsReflector.getOptionSpecs(PipelineOptionsReflectorTest.JsonIgnoreOptions.class);
        MatcherAssert.assertThat(properties, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("notIgnored"), PipelineOptionsReflectorTest.shouldSerialize())));
        MatcherAssert.assertThat(properties, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("ignored"), Matchers.not(PipelineOptionsReflectorTest.shouldSerialize()))));
    }

    /**
     * Test interface.
     */
    public interface JsonIgnoreOptions extends PipelineOptions {
        String getNotIgnored();

        void setNotIgnored(String value);

        @JsonIgnore
        String getIgnored();

        void setIgnored(String value);
    }

    @Test
    public void testMultipleInputInterfaces() {
        Set<Class<? extends PipelineOptions>> interfaces = ImmutableSet.of(PipelineOptionsReflectorTest.BaseOptions.class, PipelineOptionsReflectorTest.ExtendOptions1.class, PipelineOptionsReflectorTest.ExtendOptions2.class);
        Set<PipelineOptionSpec> props = PipelineOptionsReflector.getOptionSpecs(interfaces);
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("baseOption"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.BaseOptions.class))));
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("extendOption1"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.ExtendOptions1.class))));
        MatcherAssert.assertThat(props, Matchers.hasItem(Matchers.allOf(PipelineOptionsReflectorTest.hasName("extendOption2"), PipelineOptionsReflectorTest.hasClass(PipelineOptionsReflectorTest.ExtendOptions2.class))));
    }

    /**
     * Test interface.
     */
    public interface BaseOptions extends PipelineOptions {
        String getBaseOption();

        void setBaseOption(String value);
    }

    /**
     * Test interface.
     */
    public interface ExtendOptions1 extends PipelineOptionsReflectorTest.BaseOptions {
        String getExtendOption1();

        void setExtendOption1(String value);
    }

    /**
     * Test interface.
     */
    public interface ExtendOptions2 extends PipelineOptionsReflectorTest.BaseOptions {
        String getExtendOption2();

        void setExtendOption2(String value);
    }
}

