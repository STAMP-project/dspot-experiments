/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.resource;


import ProcessState.FAILED;
import ProcessState.SUCCEEDED;
import java.util.Map;
import org.easymock.classextension.EasyMock;
import org.geoserver.wps.ProcessListener;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geotools.coverage.grid.GridCoverage2D;
import org.junit.Test;


public class CoverageResourceListenerTest {
    private WPSResourceManager resourceManager;

    private ProcessListener listener;

    private ExecutionStatus status;

    private Map<String, Object> inputs;

    @Test
    public void testCheckInputWhenSucceeded() {
        // expected addResource to be called twice
        this.resourceManager.addResource(EasyMock.<GridCoverageResource>anyObject());
        expectLastCall().times(2);
        replay(this.resourceManager);
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.inputs.put("coverageA", createMock(GridCoverage2D.class));
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.inputs.put("coverageB", createMock(GridCoverage2D.class));
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.inputs.put("string", "testString");
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.inputs.put("integer", 1);
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.status.setPhase(SUCCEEDED);
        this.listener.succeeded(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        // verify that addResource was called twice
        verify(this.resourceManager);
    }

    @Test
    public void testCheckInputWhenFailed() {
        // expected addResource to be called once
        this.resourceManager.addResource(EasyMock.<GridCoverageResource>anyObject());
        expectLastCall().once();
        replay(this.resourceManager);
        // failure loading second coverage
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.inputs.put("coverageA", createMock(GridCoverage2D.class));
        this.listener.progress(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        this.status.setPhase(FAILED);
        this.listener.failed(new org.geoserver.wps.ProcessEvent(this.status, this.inputs));
        // verify that addResource was called once
        verify(this.resourceManager);
    }
}

