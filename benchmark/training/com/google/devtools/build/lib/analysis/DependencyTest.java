/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.analysis;


import AspectCollection.EMPTY;
import HostTransition.INSTANCE;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.analysis.util.TestAspects;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.AspectDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static AspectCollection.EMPTY;


/**
 * Tests for {@link Dependency}.
 *
 * <p>Although this is just a data class, we need a way to create a configuration.
 */
@RunWith(JUnit4.class)
public class DependencyTest extends AnalysisTestCase {
    @Test
    public void withNullConfiguration_BasicAccessors() throws Exception {
        Dependency nullDep = Dependency.withNullConfiguration(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(nullDep.getLabel()).isEqualTo(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(nullDep.hasExplicitConfiguration()).isTrue();
        assertThat(nullDep.getConfiguration()).isNull();
        assertThat(nullDep.getAspects().getAllAspects()).isEmpty();
        try {
            nullDep.getTransition();
            Assert.fail("withNullConfiguration-created Dependencies should throw ISE on getTransition()");
        } catch (IllegalStateException ex) {
            // good. expected.
        }
    }

    @Test
    public void withConfiguration_BasicAccessors() throws Exception {
        update();
        Dependency targetDep = Dependency.withConfiguration(Label.parseAbsolute("//a", ImmutableMap.of()), getTargetConfiguration());
        assertThat(targetDep.getLabel()).isEqualTo(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(targetDep.hasExplicitConfiguration()).isTrue();
        assertThat(targetDep.getConfiguration()).isEqualTo(getTargetConfiguration());
        assertThat(targetDep.getAspects().getAllAspects()).isEmpty();
        try {
            targetDep.getTransition();
            Assert.fail("withConfiguration-created Dependencies should throw ISE on getTransition()");
        } catch (IllegalStateException ex) {
            // good. expected.
        }
    }

    @Test
    public void withConfigurationAndAspects_BasicAccessors() throws Exception {
        update();
        AspectDescriptor simpleAspect = new AspectDescriptor(TestAspects.SIMPLE_ASPECT);
        AspectDescriptor attributeAspect = new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT);
        AspectCollection twoAspects = AspectCollection.createForTests(ImmutableSet.of(simpleAspect, attributeAspect));
        Dependency targetDep = Dependency.withConfigurationAndAspects(Label.parseAbsolute("//a", ImmutableMap.of()), getTargetConfiguration(), twoAspects);
        assertThat(targetDep.getLabel()).isEqualTo(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(targetDep.hasExplicitConfiguration()).isTrue();
        assertThat(targetDep.getConfiguration()).isEqualTo(getTargetConfiguration());
        assertThat(targetDep.getAspects()).isEqualTo(twoAspects);
        assertThat(targetDep.getAspectConfiguration(simpleAspect)).isEqualTo(getTargetConfiguration());
        assertThat(targetDep.getAspectConfiguration(attributeAspect)).isEqualTo(getTargetConfiguration());
        try {
            targetDep.getTransition();
            Assert.fail("withConfigurationAndAspects-created Dependencies should throw ISE on getTransition()");
        } catch (IllegalStateException ex) {
            // good. that's what I WANTED to happen.
        }
    }

    @Test
    public void withConfigurationAndAspects_RejectsNullConfigWithNPE() throws Exception {
        // Although the NullPointerTester should check this, this test invokes a different code path,
        // because it includes aspects (which the NPT test will not).
        AspectDescriptor simpleAspect = new AspectDescriptor(TestAspects.SIMPLE_ASPECT);
        AspectDescriptor attributeAspect = new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT);
        AspectCollection twoAspects = AspectCollection.createForTests(simpleAspect, attributeAspect);
        try {
            Dependency.withConfigurationAndAspects(Label.parseAbsolute("//a", ImmutableMap.of()), null, twoAspects);
            Assert.fail("should not be allowed to create a dependency with a null configuration");
        } catch (NullPointerException expected) {
            // good. you fell rrrrright into my trap.
        }
    }

    @Test
    public void withConfigurationAndAspects_AllowsEmptyAspectSet() throws Exception {
        update();
        Dependency dep = Dependency.withConfigurationAndAspects(Label.parseAbsolute("//a", ImmutableMap.of()), getTargetConfiguration(), EMPTY);
        // Here we're also checking that this doesn't throw an exception. No boom? OK. Good.
        assertThat(dep.getAspects().getAllAspects()).isEmpty();
    }

    @Test
    public void withConfiguredAspects_BasicAccessors() throws Exception {
        update();
        AspectDescriptor simpleAspect = new AspectDescriptor(TestAspects.SIMPLE_ASPECT);
        AspectDescriptor attributeAspect = new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT);
        AspectCollection aspects = AspectCollection.createForTests(ImmutableSet.of(simpleAspect, attributeAspect));
        ImmutableMap<AspectDescriptor, BuildConfiguration> twoAspectMap = ImmutableMap.of(simpleAspect, getTargetConfiguration(), attributeAspect, getHostConfiguration());
        Dependency targetDep = Dependency.withConfiguredAspects(Label.parseAbsolute("//a", ImmutableMap.of()), getTargetConfiguration(), aspects, twoAspectMap);
        assertThat(targetDep.getLabel()).isEqualTo(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(targetDep.hasExplicitConfiguration()).isTrue();
        assertThat(targetDep.getConfiguration()).isEqualTo(getTargetConfiguration());
        assertThat(targetDep.getAspects().getAllAspects()).containsExactly(simpleAspect, attributeAspect);
        assertThat(targetDep.getAspectConfiguration(simpleAspect)).isEqualTo(getTargetConfiguration());
        assertThat(targetDep.getAspectConfiguration(attributeAspect)).isEqualTo(getHostConfiguration());
        try {
            targetDep.getTransition();
            Assert.fail("withConfiguredAspects-created Dependencies should throw ISE on getTransition()");
        } catch (IllegalStateException ex) {
            // good. all according to keikaku. (TL note: keikaku means plan)
        }
    }

    @Test
    public void withConfiguredAspects_AllowsEmptyAspectMap() throws Exception {
        update();
        Dependency dep = Dependency.withConfiguredAspects(Label.parseAbsolute("//a", ImmutableMap.of()), getTargetConfiguration(), EMPTY, ImmutableMap.<AspectDescriptor, BuildConfiguration>of());
        // Here we're also checking that this doesn't throw an exception. No boom? OK. Good.
        assertThat(dep.getAspects().getAllAspects()).isEmpty();
    }

    @Test
    public void withTransitionAndAspects_BasicAccessors() throws Exception {
        AspectDescriptor simpleAspect = new AspectDescriptor(TestAspects.SIMPLE_ASPECT);
        AspectDescriptor attributeAspect = new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT);
        AspectCollection twoAspects = AspectCollection.createForTests(ImmutableSet.of(simpleAspect, attributeAspect));
        Dependency hostDep = Dependency.withTransitionAndAspects(Label.parseAbsolute("//a", ImmutableMap.of()), INSTANCE, twoAspects);
        assertThat(hostDep.getLabel()).isEqualTo(Label.parseAbsolute("//a", ImmutableMap.of()));
        assertThat(hostDep.hasExplicitConfiguration()).isFalse();
        assertThat(hostDep.getAspects().getAllAspects()).containsExactlyElementsIn(twoAspects.getAllAspects());
        assertThat(hostDep.getTransition().isHostTransition()).isTrue();
        try {
            hostDep.getConfiguration();
            Assert.fail("withTransitionAndAspects-created Dependencies should throw ISE on getConfiguration()");
        } catch (IllegalStateException ex) {
            // good. I knew you would do that.
        }
        try {
            hostDep.getAspectConfiguration(simpleAspect);
            Assert.fail(("withTransitionAndAspects-created Dependencies should throw ISE on " + "getAspectConfiguration()"));
        } catch (IllegalStateException ex) {
            // good. you're so predictable.
        }
        try {
            hostDep.getAspectConfiguration(attributeAspect);
            Assert.fail(("withTransitionAndAspects-created Dependencies should throw ISE on " + "getAspectConfiguration()"));
        } catch (IllegalStateException ex) {
            // good. you're so predictable.
        }
    }

    @Test
    public void withTransitionAndAspects_AllowsEmptyAspectSet() throws Exception {
        update();
        Dependency dep = Dependency.withTransitionAndAspects(Label.parseAbsolute("//a", ImmutableMap.of()), INSTANCE, EMPTY);
        // Here we're also checking that this doesn't throw an exception. No boom? OK. Good.
        assertThat(dep.getAspects().getAllAspects()).isEmpty();
    }

    @Test
    public void factoriesPassNullableTester() throws Exception {
        update();
        new NullPointerTester().setDefault(Label.class, Label.parseAbsolute("//a", ImmutableMap.of())).setDefault(BuildConfiguration.class, getTargetConfiguration()).testAllPublicStaticMethods(Dependency.class);
    }

    @Test
    public void equalsPassesEqualsTester() throws Exception {
        update();
        Label a = Label.parseAbsolute("//a", ImmutableMap.of());
        Label aExplicit = Label.parseAbsolute("//a:a", ImmutableMap.of());
        Label b = Label.parseAbsolute("//b", ImmutableMap.of());
        BuildConfiguration host = getHostConfiguration();
        BuildConfiguration target = getTargetConfiguration();
        AspectDescriptor simpleAspect = new AspectDescriptor(TestAspects.SIMPLE_ASPECT);
        AspectDescriptor attributeAspect = new AspectDescriptor(TestAspects.ATTRIBUTE_ASPECT);
        AspectDescriptor errorAspect = new AspectDescriptor(TestAspects.ERROR_ASPECT);
        AspectCollection twoAspects = AspectCollection.createForTests(simpleAspect, attributeAspect);
        AspectCollection inverseAspects = AspectCollection.createForTests(attributeAspect, simpleAspect);
        AspectCollection differentAspects = AspectCollection.createForTests(attributeAspect, errorAspect);
        AspectCollection noAspects = EMPTY;
        ImmutableMap<AspectDescriptor, BuildConfiguration> twoAspectsHostMap = ImmutableMap.of(simpleAspect, host, attributeAspect, host);
        ImmutableMap<AspectDescriptor, BuildConfiguration> twoAspectsTargetMap = ImmutableMap.of(simpleAspect, target, attributeAspect, target);
        ImmutableMap<AspectDescriptor, BuildConfiguration> differentAspectsHostMap = ImmutableMap.of(attributeAspect, host, errorAspect, host);
        ImmutableMap<AspectDescriptor, BuildConfiguration> differentAspectsTargetMap = ImmutableMap.of(attributeAspect, target, errorAspect, target);
        ImmutableMap<AspectDescriptor, BuildConfiguration> noAspectsMap = ImmutableMap.<AspectDescriptor, BuildConfiguration>of();
        // inverse of base set: transition NONE, label //b, different aspects
        // base set but with transition NONE and label //b
        // base set but with transition NONE and different aspects
        // base set but with transition NONE
        // inverse of base set: transition HOST, label //b, different aspects
        // base set but with transition HOST and label //b
        // base set but with transition HOST and different aspects
        // base set but with transition HOST
        // inverse of base set: //b, target configuration, no aspects
        // base set but with target configuration and no aspects
        // base set but with label //b and no aspects
        // base set but with no aspects
        // inverse of base set: //b, target configuration, different aspects
        // base set but with target configuration and different aspects
        // base set but with label //b and different aspects
        // base set but with label //b and null configuration
        // base set but with label //b and target configuration
        // base set but with different aspects
        // base set but with null configuration
        // base set but with target configuration
        // base set but with label //b
        // base set: //a, host configuration, normal aspect set
        new EqualsTester().addEqualityGroup(Dependency.withConfigurationAndAspects(a, host, twoAspects), Dependency.withConfigurationAndAspects(aExplicit, host, twoAspects), Dependency.withConfigurationAndAspects(a, host, inverseAspects), Dependency.withConfigurationAndAspects(aExplicit, host, inverseAspects), Dependency.withConfiguredAspects(a, host, twoAspects, twoAspectsHostMap), Dependency.withConfiguredAspects(aExplicit, host, twoAspects, twoAspectsHostMap)).addEqualityGroup(Dependency.withConfigurationAndAspects(b, host, twoAspects), Dependency.withConfigurationAndAspects(b, host, inverseAspects), Dependency.withConfiguredAspects(b, host, twoAspects, twoAspectsHostMap)).addEqualityGroup(Dependency.withConfigurationAndAspects(a, target, twoAspects), Dependency.withConfigurationAndAspects(aExplicit, target, twoAspects), Dependency.withConfigurationAndAspects(a, target, inverseAspects), Dependency.withConfigurationAndAspects(aExplicit, target, inverseAspects), Dependency.withConfiguredAspects(a, target, twoAspects, twoAspectsTargetMap), Dependency.withConfiguredAspects(aExplicit, target, twoAspects, twoAspectsTargetMap)).addEqualityGroup(Dependency.withNullConfiguration(a), Dependency.withNullConfiguration(aExplicit)).addEqualityGroup(Dependency.withConfigurationAndAspects(a, host, differentAspects), Dependency.withConfigurationAndAspects(aExplicit, host, differentAspects), Dependency.withConfiguredAspects(a, host, differentAspects, differentAspectsHostMap), Dependency.withConfiguredAspects(aExplicit, host, differentAspects, differentAspectsHostMap)).addEqualityGroup(Dependency.withConfigurationAndAspects(b, target, twoAspects), Dependency.withConfigurationAndAspects(b, target, inverseAspects), Dependency.withConfiguredAspects(b, target, twoAspects, twoAspectsTargetMap)).addEqualityGroup(Dependency.withNullConfiguration(b)).addEqualityGroup(Dependency.withConfigurationAndAspects(b, host, differentAspects), Dependency.withConfiguredAspects(b, host, differentAspects, differentAspectsHostMap)).addEqualityGroup(Dependency.withConfigurationAndAspects(a, target, differentAspects), Dependency.withConfigurationAndAspects(aExplicit, target, differentAspects), Dependency.withConfiguredAspects(a, target, differentAspects, differentAspectsTargetMap), Dependency.withConfiguredAspects(aExplicit, target, differentAspects, differentAspectsTargetMap)).addEqualityGroup(Dependency.withConfigurationAndAspects(b, target, differentAspects), Dependency.withConfiguredAspects(b, target, differentAspects, differentAspectsTargetMap)).addEqualityGroup(Dependency.withConfiguration(a, host), Dependency.withConfiguration(aExplicit, host), Dependency.withConfigurationAndAspects(a, host, noAspects), Dependency.withConfigurationAndAspects(aExplicit, host, noAspects), Dependency.withConfiguredAspects(a, host, noAspects, noAspectsMap), Dependency.withConfiguredAspects(aExplicit, host, noAspects, noAspectsMap)).addEqualityGroup(Dependency.withConfiguration(b, host), Dependency.withConfigurationAndAspects(b, host, noAspects), Dependency.withConfiguredAspects(b, host, noAspects, noAspectsMap)).addEqualityGroup(Dependency.withConfiguration(a, target), Dependency.withConfiguration(aExplicit, target), Dependency.withConfigurationAndAspects(a, target, noAspects), Dependency.withConfigurationAndAspects(aExplicit, target, noAspects), Dependency.withConfiguredAspects(a, target, noAspects, noAspectsMap), Dependency.withConfiguredAspects(aExplicit, target, noAspects, noAspectsMap)).addEqualityGroup(Dependency.withConfiguration(b, target), Dependency.withConfigurationAndAspects(b, target, noAspects), Dependency.withConfiguredAspects(b, target, noAspects, noAspectsMap)).addEqualityGroup(Dependency.withTransitionAndAspects(a, INSTANCE, twoAspects), Dependency.withTransitionAndAspects(aExplicit, INSTANCE, twoAspects), Dependency.withTransitionAndAspects(a, INSTANCE, inverseAspects), Dependency.withTransitionAndAspects(aExplicit, INSTANCE, inverseAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(a, INSTANCE, differentAspects), Dependency.withTransitionAndAspects(aExplicit, INSTANCE, differentAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(b, INSTANCE, twoAspects), Dependency.withTransitionAndAspects(b, INSTANCE, inverseAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(b, INSTANCE, differentAspects), Dependency.withTransitionAndAspects(b, INSTANCE, differentAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(a, NoTransition.INSTANCE, twoAspects), Dependency.withTransitionAndAspects(aExplicit, NoTransition.INSTANCE, twoAspects), Dependency.withTransitionAndAspects(a, NoTransition.INSTANCE, inverseAspects), Dependency.withTransitionAndAspects(aExplicit, NoTransition.INSTANCE, inverseAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(a, NoTransition.INSTANCE, differentAspects), Dependency.withTransitionAndAspects(aExplicit, NoTransition.INSTANCE, differentAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(b, NoTransition.INSTANCE, twoAspects), Dependency.withTransitionAndAspects(b, NoTransition.INSTANCE, inverseAspects)).addEqualityGroup(Dependency.withTransitionAndAspects(b, NoTransition.INSTANCE, differentAspects), Dependency.withTransitionAndAspects(b, NoTransition.INSTANCE, differentAspects)).testEquals();
    }
}

