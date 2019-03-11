/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.work.fragment;


import org.apache.drill.exec.proto.BitControl.FragmentStatus;
import org.apache.drill.exec.proto.UserBitShared.FragmentState;
import org.apache.drill.exec.rpc.control.ControlTunnel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FragmentStatusReporterTest {
    private FragmentStatusReporter statusReporter;

    private ControlTunnel foremanTunnel;

    @Test
    public void testStateChanged() throws Exception {
        for (FragmentState state : FragmentState.values()) {
            try {
                statusReporter.stateChanged(state);
                if (state == (FragmentState.FAILED)) {
                    Assert.fail(("Expected exception: " + (IllegalStateException.class.getName())));
                }
            } catch (IllegalStateException e) {
                if (state != (FragmentState.FAILED)) {
                    Assert.fail(("Unexpected exception: " + (e.toString())));
                }
            }
        }
        /* exclude SENDING and FAILED */
        Mockito.verify(foremanTunnel, Mockito.times(((FragmentState.values().length) - 2))).sendFragmentStatus(ArgumentMatchers.any(FragmentStatus.class));
    }

    @Test
    public void testFail() throws Exception {
        statusReporter.fail(null);
        Mockito.verify(foremanTunnel).sendFragmentStatus(ArgumentMatchers.any(FragmentStatus.class));
    }

    @Test
    public void testClose() throws Exception {
        statusReporter.close();
        Mockito.verifyZeroInteractions(foremanTunnel);
    }

    @Test
    public void testCloseClosed() throws Exception {
        statusReporter.close();
        statusReporter.close();
        Mockito.verifyZeroInteractions(foremanTunnel);
    }

    @Test
    public void testStateChangedAfterClose() throws Exception {
        statusReporter.stateChanged(FragmentState.RUNNING);
        Mockito.verify(foremanTunnel).sendFragmentStatus(ArgumentMatchers.any(FragmentStatus.class));
        statusReporter.close();
        statusReporter.stateChanged(FragmentState.CANCELLATION_REQUESTED);
        Mockito.verify(foremanTunnel).sendFragmentStatus(ArgumentMatchers.any(FragmentStatus.class));
    }
}

