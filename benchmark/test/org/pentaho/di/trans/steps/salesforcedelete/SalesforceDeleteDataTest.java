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
package org.pentaho.di.trans.steps.salesforcedelete;


import org.junit.Assert;
import org.junit.Test;


public class SalesforceDeleteDataTest {
    @Test
    public void testConstructor() {
        SalesforceDeleteData data = new SalesforceDeleteData();
        Assert.assertNull(data.inputRowMeta);
        Assert.assertNull(data.outputRowMeta);
        Assert.assertNull(data.deleteResult);
        Assert.assertNull(data.deleteId);
        Assert.assertNull(data.outputBuffer);
        Assert.assertEquals(0, data.iBufferPos);
        Assert.assertEquals((-1), data.indexOfKeyField);
    }
}

