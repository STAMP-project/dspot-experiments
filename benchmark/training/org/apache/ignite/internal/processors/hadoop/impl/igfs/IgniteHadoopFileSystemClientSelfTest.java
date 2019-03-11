/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.igfs;


import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.igfs.common.IgfsLogger;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test interaction between a IGFS client and a IGFS server.
 */
public class IgniteHadoopFileSystemClientSelfTest extends IgfsCommonAbstractTest {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(IgniteHadoopFileSystemClientSelfTest.class);

    /**
     * Test output stream deferred exception (GG-4440).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOutputStreamDeferredException() throws Exception {
        final byte[] data = "test".getBytes();
        try {
            switchHandlerErrorFlag(true);
            HadoopIgfs client = new HadoopIgfsOutProc("127.0.0.1", 10500, "igfs", IgniteHadoopFileSystemClientSelfTest.LOG, null);
            client.handshake(null);
            IgfsPath path = new IgfsPath("/test1.file");
            HadoopIgfsStreamDelegate delegate = client.create(path, true, false, 1, 1024, null);
            final HadoopIgfsOutputStream igfsOut = new HadoopIgfsOutputStream(delegate, IgniteHadoopFileSystemClientSelfTest.LOG, IgfsLogger.disabledLogger(), 0);
            // This call should return fine as exception is thrown for the first time.
            igfsOut.write(data);
            U.sleep(500);
            // This call should throw an IO exception.
            GridTestUtils.assertThrows(null, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    igfsOut.write(data);
                    return null;
                }
            }, IOException.class, "Failed to write data to server (test).");
        } finally {
            switchHandlerErrorFlag(false);
        }
    }
}

