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


import java.nio.file.Paths;
import org.apache.hadoop.fs.FileSystem;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.processors.igfs.IgfsEx;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Ensures that sampling is really turned on/off.
 */
public class IgniteHadoopFileSystemLoggerStateSelfTest extends IgfsCommonAbstractTest {
    /**
     * IGFS.
     */
    private IgfsEx igfs;

    /**
     * File system.
     */
    private FileSystem fs;

    /**
     * Whether logging is enabled in FS configuration.
     */
    private boolean logging;

    /**
     * whether sampling is enabled.
     */
    private Boolean sampling;

    /**
     * When logging is disabled and sampling is not set no-op logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingDisabledSamplingNotSet() throws Exception {
        startUp();
        assert !(logEnabled());
    }

    /**
     * When logging is enabled and sampling is not set file logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingEnabledSamplingNotSet() throws Exception {
        logging = true;
        startUp();
        assert logEnabled();
    }

    /**
     * When logging is disabled and sampling is disabled no-op logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingDisabledSamplingDisabled() throws Exception {
        sampling = false;
        startUp();
        assert !(logEnabled());
    }

    /**
     * When logging is enabled and sampling is disabled no-op logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingEnabledSamplingDisabled() throws Exception {
        logging = true;
        sampling = false;
        startUp();
        assert !(logEnabled());
    }

    /**
     * When logging is disabled and sampling is enabled file logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingDisabledSamplingEnabled() throws Exception {
        sampling = true;
        startUp();
        assert logEnabled();
    }

    /**
     * When logging is enabled and sampling is enabled file logger must be used.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLoggingEnabledSamplingEnabled() throws Exception {
        logging = true;
        sampling = true;
        startUp();
        assert logEnabled();
    }

    /**
     * Ensure sampling change through API causes changes in logging on subsequent client connections.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSamplingChange() throws Exception {
        // Start with sampling not set.
        startUp();
        assert !(logEnabled());
        fs.close();
        // "Not set" => true transition.
        igfs.globalSampling(true);
        fs = fileSystem();
        assert logEnabled();
        fs.close();
        // True => "not set" transition.
        igfs.globalSampling(null);
        fs = fileSystem();
        assert !(logEnabled());
        // "Not-set" => false transition.
        igfs.globalSampling(false);
        fs = fileSystem();
        assert !(logEnabled());
        fs.close();
        // False => "not=set" transition.
        igfs.globalSampling(null);
        fs = fileSystem();
        assert !(logEnabled());
        fs.close();
        // True => false transition.
        igfs.globalSampling(true);
        igfs.globalSampling(false);
        fs = fileSystem();
        assert !(logEnabled());
        fs.close();
        // False => true transition.
        igfs.globalSampling(true);
        fs = fileSystem();
        assert logEnabled();
    }

    /**
     * Ensure that log directory is set to IGFS when client FS connects.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testLogDirectory() throws Exception {
        startUp();
        assertEquals(Paths.get(U.getIgniteHome()).normalize().toString(), igfs.clientLogDirectory());
    }
}

