/**
 * Copyright 2008 Red Hat
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
package org.drools.compiler.conf;


import EqualityBehaviorOption.EQUALITY;
import EqualityBehaviorOption.IDENTITY;
import EventProcessingOption.CLOUD;
import EventProcessingOption.STREAM;
import IndexPrecedenceOption.EQUALITY_PRIORITY;
import IndexPrecedenceOption.PATTERN_ORDER;
import SequentialAgendaOption.DYNAMIC;
import SequentialAgendaOption.SEQUENTIAL;
import SequentialOption.NO;
import SequentialOption.PROPERTY_NAME;
import SequentialOption.YES;
import org.drools.core.runtime.rule.impl.DefaultConsequenceExceptionHandler;
import org.drools.core.util.MemoryUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.internal.conf.AlphaThresholdOption;
import org.kie.internal.conf.CompositeKeyDepthOption;
import org.kie.internal.conf.ConsequenceExceptionHandlerOption;
import org.kie.internal.conf.IndexLeftBetaMemoryOption;
import org.kie.internal.conf.IndexPrecedenceOption;
import org.kie.internal.conf.IndexRightBetaMemoryOption;
import org.kie.internal.conf.MaxThreadsOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.kie.internal.conf.PermGenThresholdOption;
import org.kie.internal.conf.SequentialAgendaOption;
import org.kie.internal.conf.SequentialOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;


public class KnowledgeBaseConfigurationTest {
    private KieBaseConfiguration config;

    @Test
    public void testSequentialConfiguration() {
        // setting the option using the type safe method
        config.setOption(YES);
        // checking the type safe getOption() method
        Assert.assertEquals(YES, config.getOption(SequentialOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(NO, config.getOption(SequentialOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(PROPERTY_NAME));
    }

    @Test
    public void testRemoveIdentitiesConfiguration() {
        // setting the option using the type safe method
        config.setOption(RemoveIdentitiesOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(RemoveIdentitiesOption.YES, config.getOption(RemoveIdentitiesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(RemoveIdentitiesOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(RemoveIdentitiesOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(RemoveIdentitiesOption.NO, config.getOption(RemoveIdentitiesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(RemoveIdentitiesOption.PROPERTY_NAME));
    }

    @Test
    public void testShareAlphaNodesConfiguration() {
        // setting the option using the type safe method
        config.setOption(ShareAlphaNodesOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(ShareAlphaNodesOption.YES, config.getOption(ShareAlphaNodesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(ShareAlphaNodesOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(ShareAlphaNodesOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(ShareAlphaNodesOption.NO, config.getOption(ShareAlphaNodesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(ShareAlphaNodesOption.PROPERTY_NAME));
    }

    @Test
    public void testShareBetaNodesConfiguration() {
        // setting the option using the type safe method
        config.setOption(ShareBetaNodesOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(ShareBetaNodesOption.YES, config.getOption(ShareBetaNodesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(ShareBetaNodesOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(ShareBetaNodesOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(ShareBetaNodesOption.NO, config.getOption(ShareBetaNodesOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(ShareBetaNodesOption.PROPERTY_NAME));
    }

    @Test
    public void testIndexLeftBetaMemoryConfiguration() {
        // setting the option using the type safe method
        config.setOption(IndexLeftBetaMemoryOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(IndexLeftBetaMemoryOption.YES, config.getOption(IndexLeftBetaMemoryOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(IndexLeftBetaMemoryOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(IndexLeftBetaMemoryOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(IndexLeftBetaMemoryOption.NO, config.getOption(IndexLeftBetaMemoryOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(IndexLeftBetaMemoryOption.PROPERTY_NAME));
    }

    @Test
    public void testIndexRightBetaMemoryConfiguration() {
        // setting the option using the type safe method
        config.setOption(IndexRightBetaMemoryOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(IndexRightBetaMemoryOption.YES, config.getOption(IndexRightBetaMemoryOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(IndexRightBetaMemoryOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(IndexRightBetaMemoryOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(IndexRightBetaMemoryOption.NO, config.getOption(IndexRightBetaMemoryOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(IndexRightBetaMemoryOption.PROPERTY_NAME));
    }

    @Test
    public void testIndexPrecedenceConfiguration() {
        // setting the option using the type safe method
        config.setOption(PATTERN_ORDER);
        // checking the type safe getOption() method
        Assert.assertEquals(PATTERN_ORDER, config.getOption(IndexPrecedenceOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("pattern", config.getProperty(IndexPrecedenceOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(IndexPrecedenceOption.PROPERTY_NAME, "equality");
        // checking the type safe getOption() method
        Assert.assertEquals(EQUALITY_PRIORITY, config.getOption(IndexPrecedenceOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("equality", config.getProperty(IndexPrecedenceOption.PROPERTY_NAME));
    }

    @Test
    public void testAssertBehaviorConfiguration() {
        // setting the option using the type safe method
        config.setOption(EQUALITY);
        // checking the type safe getOption() method
        Assert.assertEquals(EQUALITY, config.getOption(EqualityBehaviorOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("equality", config.getProperty(EqualityBehaviorOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(EqualityBehaviorOption.PROPERTY_NAME, "identity");
        // checking the type safe getOption() method
        Assert.assertEquals(IDENTITY, config.getOption(EqualityBehaviorOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("identity", config.getProperty(EqualityBehaviorOption.PROPERTY_NAME));
    }

    @Test
    public void testSequentialAgendaConfiguration() {
        // setting the option using the type safe method
        config.setOption(DYNAMIC);
        // checking the type safe getOption() method
        Assert.assertEquals(DYNAMIC, config.getOption(SequentialAgendaOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("dynamic", config.getProperty(SequentialAgendaOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(SequentialAgendaOption.PROPERTY_NAME, "sequential");
        // checking the type safe getOption() method
        Assert.assertEquals(SEQUENTIAL, config.getOption(SequentialAgendaOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("sequential", config.getProperty(SequentialAgendaOption.PROPERTY_NAME));
    }

    @Test
    public void testAlphaThresholdConfiguration() {
        // setting the option using the type safe method
        config.setOption(AlphaThresholdOption.get(5));
        // checking the type safe getOption() method
        Assert.assertEquals(AlphaThresholdOption.get(5), config.getOption(AlphaThresholdOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("5", config.getProperty(AlphaThresholdOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(AlphaThresholdOption.PROPERTY_NAME, "7");
        // checking the type safe getOption() method
        Assert.assertEquals(AlphaThresholdOption.get(7), config.getOption(AlphaThresholdOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("7", config.getProperty(AlphaThresholdOption.PROPERTY_NAME));
    }

    @Test
    public void testPermGenThresholdConfiguration() {
        Assume.assumeTrue("JVM with perm gen", MemoryUtil.hasPermGen());
        // setting the option using the type safe method
        config.setOption(PermGenThresholdOption.get(85));
        // checking the type safe getOption() method
        Assert.assertEquals(PermGenThresholdOption.get(85), config.getOption(PermGenThresholdOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("85", config.getProperty(PermGenThresholdOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(PermGenThresholdOption.PROPERTY_NAME, "87");
        // checking the type safe getOption() method
        Assert.assertEquals(PermGenThresholdOption.get(87), config.getOption(PermGenThresholdOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("87", config.getProperty(PermGenThresholdOption.PROPERTY_NAME));
    }

    @Test
    public void testCompositeKeyDepthConfiguration() {
        // setting the option using the type safe method
        config.setOption(CompositeKeyDepthOption.get(1));
        // checking the type safe getOption() method
        Assert.assertEquals(CompositeKeyDepthOption.get(1), config.getOption(CompositeKeyDepthOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("1", config.getProperty(CompositeKeyDepthOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(CompositeKeyDepthOption.PROPERTY_NAME, "2");
        // checking the type safe getOption() method
        Assert.assertEquals(CompositeKeyDepthOption.get(2), config.getOption(CompositeKeyDepthOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("2", config.getProperty(CompositeKeyDepthOption.PROPERTY_NAME));
    }

    @Test
    public void testConsequenceExceptionHandlerConfiguration() {
        Class<? extends ConsequenceExceptionHandler> handler = DefaultConsequenceExceptionHandler.class;
        // setting the option using the type safe method
        config.setOption(ConsequenceExceptionHandlerOption.get(handler));
        // checking the type safe getOption() method
        Assert.assertEquals(ConsequenceExceptionHandlerOption.get(handler), config.getOption(ConsequenceExceptionHandlerOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals(handler.getName(), config.getProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, handler.getName());
        // checking the type safe getOption() method
        Assert.assertEquals(handler.getName(), config.getOption(ConsequenceExceptionHandlerOption.class).getHandler().getName());
        // checking the string based getProperty() method
        Assert.assertEquals(handler.getName(), config.getProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME));
    }

    @Test
    public void testEventProcessingConfiguration() {
        // setting the option using the type safe method
        config.setOption(STREAM);
        // checking the type safe getOption() method
        Assert.assertEquals(STREAM, config.getOption(EventProcessingOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("stream", config.getProperty(EventProcessingOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(EventProcessingOption.PROPERTY_NAME, "cloud");
        // checking the type safe getOption() method
        Assert.assertEquals(CLOUD, config.getOption(EventProcessingOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("cloud", config.getProperty(EventProcessingOption.PROPERTY_NAME));
    }

    @Test
    public void testMaxThreadsConfiguration() {
        // setting the option using the type safe method
        config.setOption(MaxThreadsOption.get(5));
        // checking the type safe getOption() method
        Assert.assertEquals(MaxThreadsOption.get(5), config.getOption(MaxThreadsOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("5", config.getProperty(MaxThreadsOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(MaxThreadsOption.PROPERTY_NAME, "8");
        // checking the type safe getOption() method
        Assert.assertEquals(MaxThreadsOption.get(8), config.getOption(MaxThreadsOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("8", config.getProperty(MaxThreadsOption.PROPERTY_NAME));
    }

    @Test
    public void testMultithreadEvaluationConfiguration() {
        // setting the option using the type safe method
        config.setOption(MultithreadEvaluationOption.YES);
        // checking the type safe getOption() method
        Assert.assertEquals(MultithreadEvaluationOption.YES, config.getOption(MultithreadEvaluationOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("true", config.getProperty(MultithreadEvaluationOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(MultithreadEvaluationOption.PROPERTY_NAME, "false");
        // checking the type safe getOption() method
        Assert.assertEquals(MultithreadEvaluationOption.NO, config.getOption(MultithreadEvaluationOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("false", config.getProperty(MultithreadEvaluationOption.PROPERTY_NAME));
    }

    @Test
    public void testRulebaseSetUpdateHandler() {
        // this test is to avoid a regression, since update handler was supposed to be disabled in Drools 5.
        // At this moment, we no longer want to expose the update handler API, so, we did not created an Option
        // class for it.
        // checking the string based getProperty() method
        Assert.assertEquals("", config.getProperty("drools.ruleBaseUpdateHandler"));
        // setting the options using the string based setProperty() method
        config.setProperty("drools.ruleBaseUpdateHandler", "somethingElse");
        // checking the string based getProperty() method
        Assert.assertEquals("somethingElse", config.getProperty("drools.ruleBaseUpdateHandler"));
        // setting the options using the string based setProperty() method
        config.setProperty("drools.ruleBaseUpdateHandler", null);
        // checking the string based getProperty() method
        Assert.assertEquals("", config.getProperty("drools.ruleBaseUpdateHandler"));
        // setting the options using the string based setProperty() method
        config.setProperty("drools.ruleBaseUpdateHandler", "");
        // checking the string based getProperty() method
        Assert.assertEquals("", config.getProperty("drools.ruleBaseUpdateHandler"));
    }
}

