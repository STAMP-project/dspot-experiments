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


import java.io.File;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.igfs.common.IgfsLogger;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.util.typedef.internal.SB;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Grid IGFS client logger test.
 */
public class IgniteHadoopFileSystemLoggerSelfTest extends IgfsCommonAbstractTest {
    /**
     * Path string.
     */
    private static final String PATH_STR = "/dir1/dir2/file;test";

    /**
     * Path string with escaped semicolons.
     */
    private static final String PATH_STR_ESCAPED = IgniteHadoopFileSystemLoggerSelfTest.PATH_STR.replace(';', '~');

    /**
     * Path.
     */
    private static final IgfsPath PATH = new IgfsPath(IgniteHadoopFileSystemLoggerSelfTest.PATH_STR);

    /**
     * IGFS name.
     */
    private static final String IGFS_NAME = "igfs";

    /**
     * Log file path.
     */
    private static final String LOG_DIR = U.getIgniteHome();

    /**
     * Endpoint address.
     */
    private static final String ENDPOINT = "localhost:10500";

    /**
     * Log file name.
     */
    private static final String LOG_FILE = ((((((IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR) + (File.separator)) + "igfs-log-") + (IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME)) + "-") + (U.jvmPid())) + ".csv";

    /**
     * Ensure correct static loggers creation/removal as well as file creation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateDelete() throws Exception {
        IgfsLogger log = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        IgfsLogger sameLog0 = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        // Loggers for the same endpoint must be the same object.
        assert log == sameLog0;
        IgfsLogger otherLog = IgfsLogger.logger(("other" + (IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT)), IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        // Logger for another endpoint must be different.
        assert log != otherLog;
        otherLog.close();
        log.logDelete(IgniteHadoopFileSystemLoggerSelfTest.PATH, false);
        log.close();
        File logFile = new File(IgniteHadoopFileSystemLoggerSelfTest.LOG_FILE);
        // When there are multiple loggers, closing one must not force flushing.
        assert !(logFile.exists());
        IgfsLogger sameLog1 = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        assert sameLog0 == sameLog1;
        sameLog0.close();
        assert !(logFile.exists());
        sameLog1.close();
        // When we cloe the last logger, it must flush data to disk.
        assert logFile.exists();
        logFile.delete();
        IgfsLogger sameLog2 = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        // This time we expect new logger instance to be created.
        assert sameLog0 != sameLog2;
        sameLog2.close();
        // As we do not add any records to the logger, we do not expect flushing.
        assert !(logFile.exists());
    }

    /**
     * Test read operations logging.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLogRead() throws Exception {
        IgfsLogger log = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        log.logOpen(1, IgniteHadoopFileSystemLoggerSelfTest.PATH, 2, 3L);
        log.logRandomRead(1, 4L, 5);
        log.logSeek(1, 6L);
        log.logSkip(1, 7L);
        log.logMark(1, 8L);
        log.logReset(1);
        log.logCloseIn(1, 9L, 10L, 11);
        log.close();
        checkLog(new SB().a((((((((((((((U.jvmPid()) + (d())) + (TYPE_OPEN_IN)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d())) + 1) + (d())) + 2) + (d())) + 3) + (d(14)))).toString(), new SB().a(((((((((((U.jvmPid()) + (d())) + (TYPE_RANDOM_READ)) + (d(3))) + 1) + (d(7))) + 4) + (d())) + 5) + (d(8)))).toString(), new SB().a(((((((((U.jvmPid()) + (d())) + (TYPE_SEEK)) + (d(3))) + 1) + (d(7))) + 6) + (d(9)))).toString(), new SB().a(((((((((U.jvmPid()) + (d())) + (TYPE_SKIP)) + (d(3))) + 1) + (d(9))) + 7) + (d(7)))).toString(), new SB().a(((((((((U.jvmPid()) + (d())) + (TYPE_MARK)) + (d(3))) + 1) + (d(10))) + 8) + (d(6)))).toString(), new SB().a(((((((U.jvmPid()) + (d())) + (TYPE_RESET)) + (d(3))) + 1) + (d(16)))).toString(), new SB().a(((((((((((((U.jvmPid()) + (d())) + (TYPE_CLOSE_IN)) + (d(3))) + 1) + (d(11))) + 9) + (d())) + 10) + (d())) + 11) + (d(3)))).toString());
    }

    /**
     * Test write operations logging.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLogWrite() throws Exception {
        IgfsLogger log = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        log.logCreate(1, IgniteHadoopFileSystemLoggerSelfTest.PATH, true, 2, new Integer(3).shortValue(), 4L);
        log.logAppend(2, IgniteHadoopFileSystemLoggerSelfTest.PATH, 8);
        log.logCloseOut(2, 9L, 10L, 11);
        log.close();
        checkLog(new SB().a((((((((((((((((((((U.jvmPid()) + (d())) + (TYPE_OPEN_OUT)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d())) + 1) + (d())) + 2) + (d(2))) + 0) + (d())) + 1) + (d())) + 3) + (d())) + 4) + (d(10)))).toString(), new SB().a((((((((((((((U.jvmPid()) + (d())) + (TYPE_OPEN_OUT)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d())) + 2) + (d())) + 8) + (d(2))) + 1) + (d(13)))).toString(), new SB().a(((((((((((((U.jvmPid()) + (d())) + (TYPE_CLOSE_OUT)) + (d(3))) + 2) + (d(11))) + 9) + (d())) + 10) + (d())) + 11) + (d(3)))).toString());
    }

    /**
     * Test miscellaneous operations logging.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("TooBroadScope")
    @Test
    public void testLogMisc() throws Exception {
        IgfsLogger log = IgfsLogger.logger(IgniteHadoopFileSystemLoggerSelfTest.ENDPOINT, IgniteHadoopFileSystemLoggerSelfTest.IGFS_NAME, IgniteHadoopFileSystemLoggerSelfTest.LOG_DIR, 10);
        String newFile = "/dir3/file.test";
        String file1 = "/dir3/file1.test";
        String file2 = "/dir3/file1.test";
        log.logMakeDirectory(IgniteHadoopFileSystemLoggerSelfTest.PATH);
        log.logRename(IgniteHadoopFileSystemLoggerSelfTest.PATH, new IgfsPath(newFile));
        log.logListDirectory(IgniteHadoopFileSystemLoggerSelfTest.PATH, new String[]{ file1, file2 });
        log.logDelete(IgniteHadoopFileSystemLoggerSelfTest.PATH, false);
        log.close();
        checkLog(new SB().a((((((((U.jvmPid()) + (d())) + (TYPE_DIR_MAKE)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d(17)))).toString(), new SB().a((((((((((U.jvmPid()) + (d())) + (TYPE_RENAME)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d(15))) + newFile) + (d(2)))).toString(), new SB().a(((((((((((U.jvmPid()) + (d())) + (TYPE_DIR_LIST)) + (d())) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d(17))) + file1) + (DELIM_FIELD_VAL)) + file2)).toString(), new SB().a((((((((((U.jvmPid()) + (d())) + (TYPE_DELETE)) + (d(1))) + (IgniteHadoopFileSystemLoggerSelfTest.PATH_STR_ESCAPED)) + (d())) + (d(16))) + 0) + (d()))).toString());
    }
}

