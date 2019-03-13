/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.cache.inspectors;


import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PatternInspectorTest {
    private AnalyzerConfigurationMock configurationMock;

    private PatternInspector a;

    private PatternInspector b;

    @Test
    public void testRedundancy01() throws Exception {
        Assert.assertTrue(a.isRedundant(b));
        Assert.assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy02() throws Exception {
        final PatternInspector x = new PatternInspector(new org.drools.verifier.core.index.model.Pattern("x", new ObjectType("org.Address", configurationMock), configurationMock), Mockito.mock(RuleInspectorUpdater.class), Mockito.mock(AnalyzerConfiguration.class));
        Assert.assertFalse(x.isRedundant(b));
        Assert.assertFalse(b.isRedundant(x));
    }

    @Test
    public void testSubsumpt01() throws Exception {
        Assert.assertTrue(a.subsumes(b));
        Assert.assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsumpt02() throws Exception {
        final PatternInspector x = new PatternInspector(new org.drools.verifier.core.index.model.Pattern("x", new ObjectType("org.Address", configurationMock), configurationMock), Mockito.mock(RuleInspectorUpdater.class), Mockito.mock(AnalyzerConfiguration.class));
        Assert.assertFalse(x.subsumes(b));
        Assert.assertFalse(b.subsumes(x));
    }
}

