/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.pinterest.secor.io;


import com.pinterest.secor.common.LogFilePath;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.io.impl.DelimitedTextFileReaderWriterFactory;
import com.pinterest.secor.io.impl.SequenceFileReaderWriterFactory;
import com.pinterest.secor.util.ReflectionUtil;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URI;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.compress.GzipCodec;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Test the file readers and writers
 *
 * @author Praveen Murugesan (praveen@uber.com)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ java.io.FileSystem.class, DelimitedTextFileReaderWriterFactory.class, SequenceFile.class, SequenceFileReaderWriterFactory.class, GzipCodec.class, FileInputStream.class, FileOutputStream.class })
public class FileReaderWriterFactoryTest extends TestCase {
    private static final String DIR = "/some_parent_dir/some_topic/some_partition/some_other_partition";

    private static final String BASENAME = "10_0_00000000000000000100";

    private static final String PATH = ((FileReaderWriterFactoryTest.DIR) + "/") + (FileReaderWriterFactoryTest.BASENAME);

    private static final String PATH_GZ = (((FileReaderWriterFactoryTest.DIR) + "/") + (FileReaderWriterFactoryTest.BASENAME)) + ".gz";

    private LogFilePath mLogFilePath;

    private LogFilePath mLogFilePathGz;

    private SecorConfig mConfig;

    public void testSequenceFileReader() throws Exception {
        setupSequenceFileReaderConfig();
        mockSequenceFileWriter(false);
        ReflectionUtil.createFileReader(mConfig.getFileReaderWriterFactory(), mLogFilePath, null, mConfig);
        // Verify that the method has been called exactly once (the default).
        PowerMockito.verifyStatic();
        org.apache.hadoop.fs.FileSystem.get(Mockito.any(URI.class), Mockito.any(Configuration.class));
        mockSequenceFileWriter(true);
        ReflectionUtil.createFileWriter(mConfig.getFileReaderWriterFactory(), mLogFilePathGz, new GzipCodec(), mConfig);
        // Verify that the method has been called exactly once (the default).
        PowerMockito.verifyStatic();
        org.apache.hadoop.fs.FileSystem.get(Mockito.any(URI.class), Mockito.any(Configuration.class));
    }

    public void testSequenceFileWriter() throws Exception {
        setupSequenceFileReaderConfig();
        mockSequenceFileWriter(false);
        FileWriter writer = ReflectionUtil.createFileWriter(mConfig.getFileReaderWriterFactory(), mLogFilePath, null, mConfig);
        // Verify that the method has been called exactly once (the default).
        PowerMockito.verifyStatic();
        org.apache.hadoop.fs.FileSystem.get(Mockito.any(URI.class), Mockito.any(Configuration.class));
        assert (getLength()) == 123L;
        mockSequenceFileWriter(true);
        writer = ReflectionUtil.createFileWriter(mConfig.getFileReaderWriterFactory(), mLogFilePathGz, new GzipCodec(), mConfig);
        // Verify that the method has been called exactly once (the default).
        PowerMockito.verifyStatic();
        org.apache.hadoop.fs.FileSystem.get(Mockito.any(URI.class), Mockito.any(Configuration.class));
        assert (getLength()) == 12L;
    }

    public void testDelimitedTextFileWriter() throws Exception {
        setupDelimitedTextFileWriterConfig();
        mockDelimitedTextFileWriter(false);
        FileWriter writer = ((FileWriter) (ReflectionUtil.createFileWriter(mConfig.getFileReaderWriterFactory(), mLogFilePath, null, mConfig)));
        assert (getLength()) == 0L;
        mockDelimitedTextFileWriter(true);
        writer = ((FileWriter) (ReflectionUtil.createFileWriter(mConfig.getFileReaderWriterFactory(), mLogFilePathGz, new GzipCodec(), mConfig)));
        assert (getLength()) == 0L;
    }

    public void testDelimitedTextFileReader() throws Exception {
        setupDelimitedTextFileWriterConfig();
        mockDelimitedTextFileWriter(false);
        ReflectionUtil.createFileReader(mConfig.getFileReaderWriterFactory(), mLogFilePath, null, mConfig);
        mockDelimitedTextFileWriter(true);
        ReflectionUtil.createFileReader(mConfig.getFileReaderWriterFactory(), mLogFilePathGz, new GzipCodec(), mConfig);
    }
}

