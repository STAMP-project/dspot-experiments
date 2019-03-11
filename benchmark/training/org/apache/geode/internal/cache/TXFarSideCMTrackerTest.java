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
package org.apache.geode.internal.cache;


import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;


public class TXFarSideCMTrackerTest {
    private TXCommitMessage message;

    private TXFarSideCMTracker tracker;

    private Map txInProcess;

    private final Object trackerKey = new Object();

    @Test
    public void findTxInProgressReturnsTrueIfMessageIsProcessing() {
        Mockito.when(message.isProcessing()).thenReturn(true);
        boolean found = tracker.foundTxInProgress(message);
        assertThat(found).isTrue();
    }

    @Test
    public void findTxInProgressReturnsFalseIfMessageIsNotProcessing() {
        Mockito.when(message.isProcessing()).thenReturn(false);
        boolean found = tracker.foundTxInProgress(message);
        assertThat(found).isFalse();
    }

    @Test
    public void findTxInProgressReturnsFalseIfMessageIsNull() {
        boolean found = tracker.foundTxInProgress(null);
        assertThat(found).isFalse();
    }

    @Test
    public void commitProcessReceivedIfFoundInProgress() {
        TXFarSideCMTracker spy = Mockito.spy(tracker);
        Mockito.doReturn(txInProcess).when(spy).getTxInProgress();
        Mockito.when(txInProcess.get(trackerKey)).thenReturn(message);
        Mockito.doReturn(true).when(spy).foundTxInProgress(message);
        boolean received = spy.commitProcessReceived(trackerKey);
        assertThat(received).isTrue();
    }

    @Test
    public void commitProcessReceivedIfFoundFromHistory() {
        TXFarSideCMTracker spy = Mockito.spy(tracker);
        Mockito.doReturn(txInProcess).when(spy).getTxInProgress();
        Mockito.when(txInProcess.get(trackerKey)).thenReturn(message);
        Mockito.doReturn(false).when(spy).foundTxInProgress(message);
        Mockito.doReturn(true).when(spy).foundFromHistory(trackerKey);
        boolean received = spy.commitProcessReceived(trackerKey);
        assertThat(received).isTrue();
    }

    @Test
    public void commitProcessNotReceivedIfFoundMessageIsNotProcessing() {
        TXFarSideCMTracker spy = Mockito.spy(tracker);
        Mockito.doReturn(txInProcess).when(spy).getTxInProgress();
        Mockito.when(txInProcess.get(trackerKey)).thenReturn(message);
        Mockito.doReturn(false).when(spy).foundTxInProgress(message);
        Mockito.doReturn(false).when(spy).foundFromHistory(trackerKey);
        Mockito.when(message.isProcessing()).thenReturn(false);
        boolean received = spy.commitProcessReceived(trackerKey);
        assertThat(received).isFalse();
        Mockito.verify(message).setDontProcess();
    }

    @Test
    public void commitProcessNotReceivedIfMessageNotFoundInTxProcessMapAndNotFoundFromHistory() {
        TXFarSideCMTracker spy = Mockito.spy(tracker);
        Mockito.doReturn(txInProcess).when(spy).getTxInProgress();
        Mockito.when(txInProcess.get(trackerKey)).thenReturn(null);
        Mockito.doReturn(false).when(spy).foundFromHistory(trackerKey);
        boolean received = spy.commitProcessReceived(trackerKey);
        assertThat(received).isFalse();
    }
}

