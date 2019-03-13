/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.snapshot;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ParallelSnapshotFileMapperTest {
    private static final int PORT = 1234;

    private static final String BASE_LOCATION = "/test/snapshot";

    private static final String FILE_TYPE = ".gfd";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SnapshotFileMapper mapper;

    @Test
    public void mapExportPathWithIpv4() throws UnknownHostException {
        InternalDistributedMember member = Mockito.mock(InternalDistributedMember.class);
        Mockito.when(member.getInetAddress()).thenReturn(InetAddress.getByName("127.0.0.1"));
        Mockito.when(member.getPort()).thenReturn(ParallelSnapshotFileMapperTest.PORT);
        File mappedFile = mapper.mapExportPath(member, new File(((ParallelSnapshotFileMapperTest.BASE_LOCATION) + (ParallelSnapshotFileMapperTest.FILE_TYPE))));
        File expectedFile = new File(((((ParallelSnapshotFileMapperTest.BASE_LOCATION) + "-") + 1270011234) + (ParallelSnapshotFileMapperTest.FILE_TYPE)));
        Assert.assertEquals(expectedFile, mappedFile);
    }

    @Test
    public void mapExportPathWithIpv6() throws UnknownHostException {
        InternalDistributedMember member = Mockito.mock(InternalDistributedMember.class);
        Mockito.when(member.getInetAddress()).thenReturn(InetAddress.getByName("2001:db8::2"));
        Mockito.when(member.getPort()).thenReturn(ParallelSnapshotFileMapperTest.PORT);
        File mappedFile = mapper.mapExportPath(member, new File(((ParallelSnapshotFileMapperTest.BASE_LOCATION) + (ParallelSnapshotFileMapperTest.FILE_TYPE))));
        // db8 == db800000
        File expectedFile = new File(((((ParallelSnapshotFileMapperTest.BASE_LOCATION) + "-") + "2001db80000021234") + (ParallelSnapshotFileMapperTest.FILE_TYPE)));
        Assert.assertEquals(expectedFile, mappedFile);
    }

    @Test
    public void mapImportReturnsUnchangedPath() {
        File file = new File(((ParallelSnapshotFileMapperTest.BASE_LOCATION) + (ParallelSnapshotFileMapperTest.FILE_TYPE)));
        File[] mappedFiles = mapper.mapImportPath(null, file);
        Assert.assertEquals(file, mappedFiles[0]);
    }

    @Test
    public void filesWithoutCorrectExtensionGiveUsefulException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        mapper.mapExportPath(null, new File(ParallelSnapshotFileMapperTest.BASE_LOCATION));
    }
}

