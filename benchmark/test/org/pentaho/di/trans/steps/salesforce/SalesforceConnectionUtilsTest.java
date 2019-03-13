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


import SalesforceConnectionUtils.recordsFilterCode.length;
import SalesforceConnectionUtils.recordsFilterDesc;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static SalesforceConnectionUtils.recordsFilterCode;
import static SalesforceConnectionUtils.recordsFilterDesc;


public class SalesforceConnectionUtilsTest {
    @Test
    public void testLookups() {
        Assert.assertEquals(length, SalesforceConnectionUtils.recordsFilterDesc.length);
        Assert.assertEquals(recordsFilterCode[0], SalesforceConnectionUtils.getRecordsFilterCode((-1)));
        Assert.assertEquals(recordsFilterCode[0], SalesforceConnectionUtils.getRecordsFilterCode(((recordsFilterDesc.length) + 1)));
        Assert.assertEquals(recordsFilterDesc[0], SalesforceConnectionUtils.getRecordsFilterDesc((-1)));
        Assert.assertEquals(recordsFilterDesc[0], SalesforceConnectionUtils.getRecordsFilterDesc(((recordsFilterDesc.length) + 1)));
        Assert.assertEquals(0, SalesforceConnectionUtils.getRecordsFilterByCode(null));
        Assert.assertEquals(1, SalesforceConnectionUtils.getRecordsFilterByCode(recordsFilterCode[1]));
        Assert.assertEquals(0, SalesforceConnectionUtils.getRecordsFilterByCode(UUID.randomUUID().toString()));
        Assert.assertEquals(0, SalesforceConnectionUtils.getRecordsFilterByDesc(null));
        Assert.assertEquals(1, SalesforceConnectionUtils.getRecordsFilterByDesc(recordsFilterCode[1]));
        Assert.assertEquals(0, SalesforceConnectionUtils.getRecordsFilterByDesc(UUID.randomUUID().toString()));
    }
}

