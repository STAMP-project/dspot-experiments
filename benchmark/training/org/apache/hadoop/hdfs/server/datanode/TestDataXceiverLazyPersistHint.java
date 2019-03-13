/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;


import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentCaptor;


/**
 * Mock-based unit test to verify that the DataXceiver correctly handles the
 * LazyPersist hint from clients.
 */
public class TestDataXceiverLazyPersistHint {
    @Rule
    public Timeout timeout = new Timeout(300000);

    private enum PeerLocality {

        LOCAL,
        REMOTE;}

    private enum NonLocalLazyPersist {

        ALLOWED,
        NOT_ALLOWED;}

    /**
     * Ensure that the correct hint is passed to the block receiver when
     * the client is local.
     */
    @Test
    public void testWithLocalClient() throws IOException {
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        DataXceiver xceiver = TestDataXceiverLazyPersistHint.makeStubDataXceiver(TestDataXceiverLazyPersistHint.PeerLocality.LOCAL, TestDataXceiverLazyPersistHint.NonLocalLazyPersist.NOT_ALLOWED, captor);
        for (Boolean lazyPersistSetting : Arrays.asList(true, false)) {
            issueWriteBlockCall(xceiver, lazyPersistSetting);
            MatcherAssert.assertThat(captor.getValue(), Is.is(lazyPersistSetting));
        }
    }

    /**
     * Ensure that hint is always false when the client is remote.
     */
    @Test
    public void testWithRemoteClient() throws IOException {
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        DataXceiver xceiver = TestDataXceiverLazyPersistHint.makeStubDataXceiver(TestDataXceiverLazyPersistHint.PeerLocality.REMOTE, TestDataXceiverLazyPersistHint.NonLocalLazyPersist.NOT_ALLOWED, captor);
        for (Boolean lazyPersistSetting : Arrays.asList(true, false)) {
            issueWriteBlockCall(xceiver, lazyPersistSetting);
            MatcherAssert.assertThat(captor.getValue(), Is.is(false));
        }
    }

    /**
     * Ensure that the correct hint is passed to the block receiver when
     * the client is remote AND dfs.datanode.allow.non.local.lazy.persist
     * is set to true.
     */
    @Test
    public void testOverrideWithRemoteClient() throws IOException {
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        DataXceiver xceiver = TestDataXceiverLazyPersistHint.makeStubDataXceiver(TestDataXceiverLazyPersistHint.PeerLocality.REMOTE, TestDataXceiverLazyPersistHint.NonLocalLazyPersist.ALLOWED, captor);
        for (Boolean lazyPersistSetting : Arrays.asList(true, false)) {
            issueWriteBlockCall(xceiver, lazyPersistSetting);
            MatcherAssert.assertThat(captor.getValue(), Is.is(lazyPersistSetting));
        }
    }
}

