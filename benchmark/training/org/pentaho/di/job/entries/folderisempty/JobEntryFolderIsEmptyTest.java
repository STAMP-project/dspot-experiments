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
package org.pentaho.di.job.entries.folderisempty;


import Const.KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;


public class JobEntryFolderIsEmptyTest {
    private Job job;

    private JobEntryFolderIsEmpty entry;

    private String emptyDir;

    private String nonEmptyDir;

    @Test
    public void testSetNrErrorsSuccess() throws Exception {
        entry.setFoldername(emptyDir);
        Result result = entry.execute(new Result(), 0);
        Assert.assertTrue("For empty folder result should be true", result.getResult());
        Assert.assertEquals("There should be no errors", 0, result.getNrErrors());
    }

    @Test
    public void testSetNrErrorsNewBehaviorFail() throws Exception {
        entry.setFoldername(nonEmptyDir);
        Result result = entry.execute(new Result(), 0);
        Assert.assertFalse("For non-empty folder result should be false", result.getResult());
        Assert.assertEquals("There should be still no errors", 0, result.getNrErrors());
    }

    @Test
    public void testSetNrErrorsOldBehaviorFail() throws Exception {
        entry.setFoldername(nonEmptyDir);
        entry.setVariable(KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES, "Y");
        Result result = entry.execute(new Result(), 0);
        Assert.assertFalse("For non-empty folder result should be false", result.getResult());
        Assert.assertEquals("According to old behaviour there should be an error", 1, result.getNrErrors());
    }
}

