/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.serialization.impl;


import StringUtil.LINE_SEPARATOR;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests a common compatibility issue: when a (Identified)DataSerializable class first
 * becomes Versioned, it might miss checking input stream for UNKNOWN version (which is
 * the version of an incoming stream from a previous-version member) instead of using
 * in.getVersion.isUnknownOrLessThan(CURRENT).
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*", "javax.security.*", "javax.management.*" })
@PrepareForTest(Version.class)
@Category({ QuickTest.class, ParallelTest.class })
public class VersionedInCurrentVersionTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Set<Class<? extends Versioned>> versionedInCurrentVersion;

    @Test
    public void testNewVersionedClass_doesNotInvokeLessThan_whenReadingData() {
        List<Class<? extends Versioned>> failures = new ArrayList<Class<? extends Versioned>>();
        for (Class<? extends Versioned> versionedClass : versionedInCurrentVersion) {
            Versioned instance = createInstance(versionedClass);
            if (instance == null) {
                // may occur when there is no default constructor
                continue;
            }
            DataSerializable dataSerializable = ((DataSerializable) (instance));
            Version spy = PowerMockito.spy(Versions.CURRENT_CLUSTER_VERSION);
            ObjectDataInput mockInput = Mockito.spy(ObjectDataInput.class);
            Mockito.when(mockInput.getVersion()).thenReturn(spy);
            try {
                dataSerializable.readData(mockInput);
            } catch (Throwable t) {
                EmptyStatement.ignore(t);
            } finally {
                try {
                    Mockito.verify(spy).isLessThan(Versions.CURRENT_CLUSTER_VERSION);
                    failures.add(versionedClass);
                } catch (Throwable t) {
                    // expected when Version.isLessThan() was not invoked
                }
            }
        }
        if (!(failures.isEmpty())) {
            StringBuilder failMessageBuilder = new StringBuilder();
            for (Class<? extends Versioned> failedClass : failures) {
                failMessageBuilder.append(LINE_SEPARATOR).append(failedClass.getName()).append(" invoked in.getVersion().isLessThan(CURRENT_CLUSTER_VERSION) while reading data");
            }
            Assert.fail(failMessageBuilder.toString());
        }
    }
}

