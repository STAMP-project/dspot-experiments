/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.ftp;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class JobEntryFTPTest {
    private Job job;

    private JobEntryFTP entry;

    private String existingDir;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testFixedExistingTargetDir() throws Exception {
        entry.setTargetDirectory(existingDir);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("For existing folder should be true", result.getResult());
        Assert.assertEquals("There should be no errors", 0, result.getNrErrors());
    }

    @Test
    public void testFixedNonExistingTargetDir() throws Exception {
        entry.setTargetDirectory((((existingDir) + (File.separator)) + "sub"));
        Result result = entry.execute(new Result(), 0);
        Assert.assertFalse("For non existing folder should be false", result.getResult());
        Assert.assertTrue("There should be errors", (0 != (result.getNrErrors())));
    }

    @Test
    public void testVariableExistingTargetDir() throws Exception {
        entry.setTargetDirectory("${Internal.Job.Filename.Directory}");
        entry.setVariable("Internal.Job.Filename.Directory", existingDir);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("For existing folder should be true", result.getResult());
        Assert.assertEquals("There should be no errors", 0, result.getNrErrors());
    }

    @Test
    public void testVariableNonExistingTargetDir() throws Exception {
        entry.setTargetDirectory("${Internal.Job.Filename.Directory}/Worg");
        entry.setVariable("Internal.Job.Filename.Directory", (((existingDir) + (File.separator)) + "sub"));
        Result result = entry.execute(new Result(), 0);
        Assert.assertFalse("For non existing folder should be false", result.getResult());
        Assert.assertTrue("There should be errors", (0 != (result.getNrErrors())));
    }

    @Test
    public void testProtocolVariableExistingTargetDir() throws Exception {
        entry.setTargetDirectory("${Internal.Job.Filename.Directory}");
        entry.setVariable("Internal.Job.Filename.Directory", ("file://" + (existingDir)));
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("For existing folder should be true", result.getResult());
        Assert.assertEquals("There should be no errors", 0, result.getNrErrors());
    }

    @Test
    public void testPtotocolVariableNonExistingTargetDir() throws Exception {
        entry.setTargetDirectory("${Internal.Job.Filename.Directory}/Worg");
        entry.setVariable("Internal.Job.Filename.Directory", ((("file://" + (existingDir)) + (File.separator)) + "sub"));
        Result result = entry.execute(new Result(), 0);
        Assert.assertFalse("For non existing folder should be false", result.getResult());
        Assert.assertTrue("There should be errors", (0 != (result.getNrErrors())));
    }

    @Test
    public void testTargetFilenameNoDateTime() throws Exception {
        File destFolder = tempFolder.newFolder("pdi5558");
        destFolder.deleteOnExit();
        JobEntryFTP entry = new JobEntryFTP();
        entry.setTargetDirectory(destFolder.getAbsolutePath());
        entry.setAddDateBeforeExtension(false);
        Assert.assertNull(entry.returnTargetFilename(null));
        Assert.assertEquals((((destFolder.getAbsolutePath()) + (Const.FILE_SEPARATOR)) + "testFile"), entry.returnTargetFilename("testFile"));
        Assert.assertEquals((((destFolder.getAbsolutePath()) + (Const.FILE_SEPARATOR)) + "testFile.txt"), entry.returnTargetFilename("testFile.txt"));
    }

    @Test
    public void testTargetFilenameWithDateTime() throws Exception {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat HHmmssSSS = new SimpleDateFormat("HHmmssSSS");
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        File destFolder = tempFolder.newFolder("pdi5558");
        destFolder.deleteOnExit();
        String destFolderName = destFolder.getAbsolutePath();
        JobEntryFTP entry = new JobEntryFTP();
        entry.setTargetDirectory(destFolderName);
        entry.setAddDateBeforeExtension(true);
        // Test Date-Only
        entry.setDateInFilename(true);
        Assert.assertNull(entry.returnTargetFilename(null));
        Assert.assertEquals("Test Add Date without file extension", (((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (yyyyMMdd.format(new Date()))), entry.returnTargetFilename("testFile"));
        Assert.assertEquals("Test Add Date with file extension", ((((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (yyyyMMdd.format(new Date()))) + ".txt"), entry.returnTargetFilename("testFile.txt"));
        // Test Date-and-Time
        entry.setTimeInFilename(true);
        String beforeString = (((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (yyyyMMddHHmmssSSS.format(new Date()))) + ".txt";
        String actualValue = entry.returnTargetFilename("testFile.txt");
        String afterString = (((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (yyyyMMddHHmmssSSS.format(new Date()))) + ".txt";
        Pattern expectedFormat = Pattern.compile(((Pattern.quote(((((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (yyyyMMdd.format(new Date()))) + "_"))) + "([\\d]{9})\\.txt"));
        Assert.assertTrue("Output file matches expected format", expectedFormat.matcher(actualValue).matches());
        Assert.assertTrue("The actual time is not too early for test run", ((actualValue.compareTo(beforeString)) >= 0));
        Assert.assertTrue("The actual time is not too late for test run", ((actualValue.compareTo(afterString)) <= 0));
        // Test Time-Only
        entry.setDateInFilename(false);
        beforeString = (((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (HHmmssSSS.format(new Date()))) + ".txt";
        actualValue = entry.returnTargetFilename("testFile.txt");
        afterString = (((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_") + (HHmmssSSS.format(new Date()))) + ".txt";
        expectedFormat = Pattern.compile(((Pattern.quote(((destFolderName + (Const.FILE_SEPARATOR)) + "testFile_"))) + "([\\d]{9})\\.txt"));
        Assert.assertTrue("Output file matches expected format", expectedFormat.matcher(actualValue).matches());
        Assert.assertTrue("The actual time is not too early for test run", ((actualValue.compareTo(beforeString)) >= 0));
        Assert.assertTrue("The actual time is not too late for test run", ((actualValue.compareTo(afterString)) <= 0));
    }
}

