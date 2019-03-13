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
package org.pentaho.di.core.gui;


import Const.KETTLE_MAX_JOB_TRACKER_SIZE;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class JobTrackerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    // PDI-11389 Number of job trackers should be limited by KETTLE_MAX_JOB_TRACKER_SIZE
    @Test
    public void testAddJobTracker() throws Exception {
        final String old = System.getProperty(KETTLE_MAX_JOB_TRACKER_SIZE);
        final Integer maxTestSize = 30;
        try {
            System.setProperty(KETTLE_MAX_JOB_TRACKER_SIZE, maxTestSize.toString());
            JobMeta jobMeta = Mockito.mock(JobMeta.class);
            JobTracker jobTracker = new JobTracker(jobMeta);
            for (int n = 0; n < (maxTestSize * 2); n++) {
                jobTracker.addJobTracker(Mockito.mock(JobTracker.class));
            }
            Assert.assertTrue("More JobTrackers than allowed were added", ((jobTracker.getTotalNumberOfItems()) <= maxTestSize));
        } finally {
            if (old == null) {
                System.clearProperty(KETTLE_MAX_JOB_TRACKER_SIZE);
            } else {
                System.setProperty(KETTLE_MAX_JOB_TRACKER_SIZE, old);
            }
        }
    }

    @Test
    public void findJobTracker_EntryNameIsNull() {
        JobTracker jobTracker = JobTrackerTest.createTracker();
        jobTracker.addJobTracker(JobTrackerTest.createTracker());
        JobEntryCopy copy = JobTrackerTest.createEntryCopy(null);
        Assert.assertNull(jobTracker.findJobTracker(copy));
    }

    @Test
    public void findJobTracker_EntryNameNotFound() {
        JobTracker jobTracker = JobTrackerTest.createTracker();
        for (int i = 0; i < 3; i++) {
            jobTracker.addJobTracker(JobTrackerTest.createTracker(Integer.toString(i), 1));
        }
        JobEntryCopy copy = JobTrackerTest.createEntryCopy("not match");
        Assert.assertNull(jobTracker.findJobTracker(copy));
    }

    @Test
    public void findJobTracker_EntryNameFound() {
        JobTracker jobTracker = JobTrackerTest.createTracker();
        JobTracker[] children = new JobTracker[]{ JobTrackerTest.createTracker("0", 1), JobTrackerTest.createTracker("1", 1), JobTrackerTest.createTracker("2", 1) };
        for (JobTracker child : children) {
            jobTracker.addJobTracker(child);
        }
        JobEntryCopy copy = JobTrackerTest.createEntryCopy("1");
        Assert.assertEquals(children[1], jobTracker.findJobTracker(copy));
    }
}

