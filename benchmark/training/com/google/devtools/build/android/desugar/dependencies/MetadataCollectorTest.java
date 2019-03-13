/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.desugar.dependencies;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.android.desugar.proto.DesugarDeps.Dependency;
import com.google.devtools.build.android.desugar.proto.DesugarDeps.DesugarDepsInfo;
import com.google.devtools.build.android.desugar.proto.DesugarDeps.InterfaceDetails;
import com.google.devtools.build.android.desugar.proto.DesugarDeps.InterfaceWithCompanion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link MetadataCollector}.
 */
@RunWith(JUnit4.class)
public class MetadataCollectorTest {
    @Test
    public void testEmptyAvoidsOutput() {
        assertThat(new MetadataCollector(false).toByteArray()).isNull();
    }

    @Test
    public void testAssumeCompanionClass() throws Exception {
        MetadataCollector collector = new MetadataCollector(false);
        collector.assumeCompanionClass("a", "b$$CC");
        collector.assumeCompanionClass("b", "b$$CC");
        collector.assumeCompanionClass("a", "a$$CC");
        DesugarDepsInfo info = extractProto(collector);
        assertThat(info.getAssumePresentList()).containsExactly(Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).setTarget(MetadataCollectorTest.wrapType("b$$CC")).build(), Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("b")).setTarget(MetadataCollectorTest.wrapType("b$$CC")).build(), Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).setTarget(MetadataCollectorTest.wrapType("a$$CC")).build());
    }

    @Test
    public void testMissingImplementedInterface() throws Exception {
        MetadataCollector collector = new MetadataCollector(true);
        collector.missingImplementedInterface("a", "b");
        collector.missingImplementedInterface("a", "c");
        collector.missingImplementedInterface("c", "b");
        DesugarDepsInfo info = extractProto(collector);
        assertThat(info.getMissingInterfaceList()).containsExactly(Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).setTarget(MetadataCollectorTest.wrapType("b")).build(), Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).setTarget(MetadataCollectorTest.wrapType("c")).build(), Dependency.newBuilder().setOrigin(MetadataCollectorTest.wrapType("c")).setTarget(MetadataCollectorTest.wrapType("b")).build());
    }

    @Test
    public void testRecordExtendedInterfaces() throws Exception {
        MetadataCollector collector = new MetadataCollector(false);
        collector.recordExtendedInterfaces("a", "b", "c");
        collector.recordExtendedInterfaces("b");
        collector.recordExtendedInterfaces("c", "d");
        DesugarDepsInfo info = extractProto(collector);
        assertThat(info.getInterfaceWithSupertypesList()).containsExactly(InterfaceDetails.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).addAllExtendedInterface(ImmutableList.of(MetadataCollectorTest.wrapType("b"), MetadataCollectorTest.wrapType("c"))).build(), InterfaceDetails.newBuilder().setOrigin(MetadataCollectorTest.wrapType("c")).addAllExtendedInterface(ImmutableList.of(MetadataCollectorTest.wrapType("d"))).build());
    }

    @Test
    public void testRecordDefaultMethods() throws Exception {
        MetadataCollector collector = new MetadataCollector(false);
        collector.recordDefaultMethods("a", 0);
        collector.recordDefaultMethods("b", 1);
        DesugarDepsInfo info = extractProto(collector);
        assertThat(info.getInterfaceWithCompanionList()).containsExactly(InterfaceWithCompanion.newBuilder().setOrigin(MetadataCollectorTest.wrapType("a")).setNumDefaultMethods(0).build(), InterfaceWithCompanion.newBuilder().setOrigin(MetadataCollectorTest.wrapType("b")).setNumDefaultMethods(1).build());
    }
}

