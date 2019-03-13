/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.concurrency;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.SocketPortAllocation;
import org.pentaho.di.www.TransformationMap;


public class TransformationMapConcurrentTest {
    TransformationMap transformationMap;

    int numberOfSameAllocations = 10;

    int numberOfSameSourceAndTargetSlaveNameAllocations = 40;

    int numberOfDifferentAllocations = 100;

    int numberOfSameHosts = 100;

    int numberOfDeallocateTasks = 100;

    List<TransformationMapConcurrentTest.ConcurrentAllocate> concurrentAllocateTasks;

    List<TransformationMapConcurrentTest.ConcurrentDeallocate> concurrentDeallocateTasks;

    @Test
    public void testConcurrentAllocateServerSocketPort() throws Exception {
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(concurrentAllocateTasks, Collections.emptyList(), new AtomicBoolean(true));
    }

    @Test
    public void testConcurrentAllocateAndDeallocateServerSocketPort() throws Exception {
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(concurrentDeallocateTasks, concurrentAllocateTasks, new AtomicBoolean(true));
    }

    private class ConcurrentAllocate implements Callable<SocketPortAllocation> {
        int portRangeStart;

        String hostname;

        String clusteredRunId;

        String transformationName;

        String sourceSlaveName;

        String sourceStepName;

        String sourceStepCopy;

        String targetSlaveName;

        String targetStepName;

        String targetStepCopy;

        public ConcurrentAllocate(int portRangeStart, String hostname, String clusteredRunId, String transformationName, String sourceSlaveName, String sourceStepName, String sourceStepCopy, String targetSlaveName, String targetStepName, String targetStepCopy) {
            this.portRangeStart = portRangeStart;
            this.hostname = hostname;
            this.clusteredRunId = clusteredRunId;
            this.transformationName = transformationName;
            this.sourceSlaveName = sourceSlaveName;
            this.sourceStepName = sourceStepName;
            this.sourceStepCopy = sourceStepCopy;
            this.targetSlaveName = targetSlaveName;
            this.targetStepName = targetStepName;
            this.targetStepCopy = targetStepCopy;
        }

        @Override
        public SocketPortAllocation call() throws Exception {
            return transformationMap.allocateServerSocketPort(portRangeStart, hostname, clusteredRunId, transformationName, sourceSlaveName, sourceStepName, sourceStepCopy, targetSlaveName, targetStepName, targetStepCopy);
        }
    }

    private class ConcurrentDeallocate implements Callable<Object> {
        int port;

        String hostname;

        CarteObjectEntry entry;

        ConcurrentDeallocate(int port, String hostname, CarteObjectEntry entry) {
            this.port = port;
            this.hostname = hostname;
            this.entry = entry;
        }

        @Override
        public Object call() throws Exception {
            transformationMap.deallocateServerSocketPorts(entry);
            transformationMap.deallocateServerSocketPort(port, hostname);
            return null;
        }
    }
}

