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
package org.pentaho.di.job.entry;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.job.JobMeta;


public class JobEntryCopyTest {
    private static final String ATTRIBUTE_GROUP = "aGroupName";

    private static final String ATTRIBUTE_KEY = "someKey";

    private static final String ATTRIBUTE_VALUE = "aValue";

    private JobEntryCopy originJobEntry;

    private JobEntryCopy copyJobEntry;

    private JobEntryInterface originEntry;

    @Test
    public void testReplaceMetaCloneEntryOfOrigin() throws Exception {
        copyJobEntry.replaceMeta(originJobEntry);
        Assert.assertNotSame("Entry of origin and copy JobEntry should be different objects: ", copyJobEntry.getEntry(), originJobEntry.getEntry());
    }

    @Test
    public void testReplaceMetaDoesNotChangeEntryOfOrigin() throws Exception {
        copyJobEntry.replaceMeta(originJobEntry);
        Assert.assertEquals("hasChanged in Entry of origin JobEntry should not be changed. ", false, originJobEntry.getEntry().hasChanged());
    }

    @Test
    public void testReplaceMetaChangesEntryOfCopy() throws Exception {
        copyJobEntry.replaceMeta(originJobEntry);
        Assert.assertEquals("hasChanged in Entry of copy JobEntry should be changed. ", true, copyJobEntry.getEntry().hasChanged());
    }

    @Test
    public void testSetParentMeta() throws Exception {
        JobMeta meta = Mockito.mock(JobMeta.class);
        originJobEntry.setParentJobMeta(meta);
        Assert.assertEquals(meta, originEntry.getParentJobMeta());
    }

    @Test
    public void testCloneClonesAttributesMap() throws Exception {
        JobEntryCopy clonedJobEntry = ((JobEntryCopy) (originJobEntry.clone()));
        Assert.assertNotNull(clonedJobEntry.getAttributesMap());
        Assert.assertEquals(originJobEntry.getAttribute(JobEntryCopyTest.ATTRIBUTE_GROUP, JobEntryCopyTest.ATTRIBUTE_KEY), clonedJobEntry.getAttribute(JobEntryCopyTest.ATTRIBUTE_GROUP, JobEntryCopyTest.ATTRIBUTE_KEY));
    }

    @Test
    public void testCloneClearsObjectId() throws Exception {
        JobEntryCopy clonedJobEntry = ((JobEntryCopy) (originJobEntry.clone()));
        Assert.assertNull(clonedJobEntry.getObjectId());
    }

    @Test
    public void testDeepCloneClonesAttributesMap() throws Exception {
        JobEntryCopy deepClonedJobEntry = ((JobEntryCopy) (originJobEntry.clone_deep()));
        Assert.assertNotNull(deepClonedJobEntry.getAttributesMap());
        Assert.assertEquals(originJobEntry.getAttribute(JobEntryCopyTest.ATTRIBUTE_GROUP, JobEntryCopyTest.ATTRIBUTE_KEY), deepClonedJobEntry.getAttribute(JobEntryCopyTest.ATTRIBUTE_GROUP, JobEntryCopyTest.ATTRIBUTE_KEY));
    }

    @Test
    public void testDeepCloneClearsObjectId() throws Exception {
        JobEntryCopy deepClonedJobEntry = ((JobEntryCopy) (originJobEntry.clone_deep()));
        Assert.assertNull(deepClonedJobEntry.getObjectId());
    }
}

