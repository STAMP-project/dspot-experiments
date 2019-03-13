/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.ftpsput;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.pentaho.di.job.entries.ftpsget.FTPSConnection;


public class JobEntryFTPSPUTTest {
    /**
     * PDI-6868, attempt to set binary mode is after the connection.connect() succeeded.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBinaryModeSetAfterConnectionSuccess() throws Exception {
        JobEntryFTPSPUT job = new JobEntryFTPSPUTTest.JobEntryFTPSPUTCustom();
        FTPSConnection connection = Mockito.mock(FTPSConnection.class);
        InOrder inOrder = Mockito.inOrder(connection);
        job.buildFTPSConnection(connection);
        inOrder.verify(connection).connect();
        inOrder.verify(connection).setBinaryMode(Mockito.anyBoolean());
    }

    class JobEntryFTPSPUTCustom extends JobEntryFTPSPUT {
        @Override
        public boolean isBinaryMode() {
            return true;
        }
    }
}

