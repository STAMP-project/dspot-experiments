/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.repository.internal;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Assert;
import org.junit.Test;


public class RemoteSnapshotMetadataTest {
    private Locale defaultLocale;

    @Test
    public void gregorianCalendarIsUsed() {
        String dateBefore = RemoteSnapshotMetadataTest.gregorianDate();
        RemoteSnapshotMetadata metadata = new RemoteSnapshotMetadata(new DefaultArtifact("a:b:1-SNAPSHOT"), false);
        metadata.merge(new Metadata());
        String dateAfter = RemoteSnapshotMetadataTest.gregorianDate();
        String ts = metadata.metadata.getVersioning().getSnapshot().getTimestamp();
        String datePart = ts.replaceAll("\\..*", "");
        /* Allow for this test running across midnight */
        Set<String> expected = new HashSet<String>(Arrays.asList(dateBefore, dateAfter));
        Assert.assertTrue(((("Expected " + datePart) + " to be in ") + expected), expected.contains(datePart));
    }
}

