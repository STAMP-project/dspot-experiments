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
package org.pentaho.di.trans.steps.salesforce;


import com.sforce.soap.partner.sobject.SObject;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SalesforceRecordValueTest {
    @Test
    public void testClass() {
        SalesforceRecordValue srv = new SalesforceRecordValue(100);
        Assert.assertEquals(100, srv.getRecordIndex());
        Assert.assertNull(srv.getRecordValue());
        Assert.assertFalse(srv.isRecordIndexChanges());
        Assert.assertFalse(srv.isAllRecordsProcessed());
        Assert.assertNull(srv.getDeletionDate());
        srv.setRecordIndex(120);
        Assert.assertEquals(120, srv.getRecordIndex());
        srv.setRecordValue(Mockito.mock(SObject.class));
        Assert.assertNotNull(srv.getRecordValue());
        srv.setAllRecordsProcessed(true);
        Assert.assertTrue(srv.isAllRecordsProcessed());
        srv.setAllRecordsProcessed(false);
        Assert.assertFalse(srv.isRecordIndexChanges());
        srv.setRecordIndexChanges(true);
        Assert.assertTrue(srv.isRecordIndexChanges());
        srv.setRecordIndexChanges(false);
        Assert.assertFalse(srv.isRecordIndexChanges());
        srv.setDeletionDate(new Date());
        Assert.assertNotNull(srv.getDeletionDate());
    }
}

