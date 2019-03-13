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
package org.pentaho.di.job.entries.filesexist;


import Const.KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class JobEntryFilesExistTest {
    private Job job;

    private JobEntryFilesExist entry;

    private String existingFile1;

    private String existingFile2;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSetNrErrorsNewBehaviorFalseResult() throws Exception {
        // this tests fix for PDI-10270
        entry.setArguments(new String[]{ "nonExistingFile.ext" });
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Entry should fail", res.getResult());
        Assert.assertEquals("Files not found. Result is false. But... No of errors should be zero", 0, res.getNrErrors());
    }

    @Test
    public void testSetNrErrorsOldBehaviorFalseResult() throws Exception {
        // this tests backward compatibility settings for PDI-10270
        entry.setArguments(new String[]{ "nonExistingFile1.ext", "nonExistingFile2.ext" });
        entry.setVariable(KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES, "Y");
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Entry should fail", res.getResult());
        Assert.assertEquals("Files not found. Result is false. And... Number of errors should be the same as number of not found files", entry.getArguments().length, res.getNrErrors());
    }

    @Test
    public void testExecuteWithException() throws Exception {
        entry.setArguments(new String[]{ null });
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Entry should fail", res.getResult());
        Assert.assertEquals("File with wrong name was specified. One error should be reported", 1, res.getNrErrors());
    }

    @Test
    public void testExecuteSuccess() throws Exception {
        entry.setArguments(new String[]{ existingFile1, existingFile2 });
        Result res = entry.execute(new Result(), 0);
        Assert.assertTrue("Entry failed", res.getResult());
    }

    @Test
    public void testExecuteFail() throws Exception {
        entry.setArguments(new String[]{ existingFile1, existingFile2, "nonExistingFile1.ext", "nonExistingFile2.ext" });
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Entry should fail", res.getResult());
    }
}

