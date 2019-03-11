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
package org.apache.geode.internal.process;


import java.util.concurrent.TimeUnit;
import org.apache.geode.lang.AttachAPINotFoundException;
import org.junit.Test;


/**
 * Unit tests for {@link FileProcessController}.
 */
public class FileProcessControllerTest {
    private FileProcessController controller;

    private FileControllerParameters mockParameters;

    private int pid;

    private long timeout;

    private TimeUnit units;

    @Test
    public void pidLessThanOne_throwsIllegalArgumentException() throws Exception {
        pid = 0;
        assertThatThrownBy(() -> new FileProcessController(mockParameters, pid, timeout, units)).isInstanceOf(IllegalArgumentException.class).hasMessage((("Invalid pid '" + (pid)) + "' specified"));
    }

    @Test
    public void getProcessId_returnsPid() throws Exception {
        assertThat(controller.getProcessId()).isEqualTo(pid);
    }

    @Test
    public void checkPidSupport_throwsAttachAPINotFoundException() throws Exception {
        assertThatThrownBy(() -> controller.checkPidSupport()).isInstanceOf(AttachAPINotFoundException.class);
    }

    @Test
    public void statusTimeoutMillis_defaultsToOneMinute() throws Exception {
        FileProcessController controller = new FileProcessController(mockParameters, pid);
        assertThat(controller.getStatusTimeoutMillis()).isEqualTo(FileProcessController.DEFAULT_STATUS_TIMEOUT_MILLIS);
    }

    @Test
    public void timeoutLessThanZero_throwsIllegalArgumentException() throws Exception {
        timeout = -1;
        assertThatThrownBy(() -> new FileProcessController(mockParameters, pid, timeout, units)).isInstanceOf(IllegalArgumentException.class).hasMessage((("Invalid timeout '" + (timeout)) + "' specified"));
    }
}

