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
package org.pentaho.di.trans.steps.salesforceinput;


import SalesforceInputField.trimTypeCode;
import SalesforceInputField.trimTypeDesc;
import org.junit.Assert;
import org.junit.Test;


public class SalesforceInputFieldTest {
    @Test
    public void testLookups() {
        Assert.assertEquals(0, SalesforceInputField.getTrimTypeByCode(null));
        Assert.assertTrue(((trimTypeCode.length) > 2));
        Assert.assertEquals(0, SalesforceInputField.getTrimTypeByCode("none"));
        Assert.assertEquals(1, SalesforceInputField.getTrimTypeByCode("left"));
        Assert.assertEquals(2, SalesforceInputField.getTrimTypeByCode("right"));
        Assert.assertEquals(3, SalesforceInputField.getTrimTypeByCode("both"));
        Assert.assertEquals(0, SalesforceInputField.getTrimTypeByCode("invalid"));
        Assert.assertEquals(0, SalesforceInputField.getTrimTypeByDesc(null));
        Assert.assertTrue(((trimTypeDesc.length) > 2));
        Assert.assertEquals(0, SalesforceInputField.getTrimTypeByDesc("invalid"));
        Assert.assertEquals("none", SalesforceInputField.getTrimTypeCode((-1)));
        Assert.assertEquals("none", SalesforceInputField.getTrimTypeCode(((trimTypeCode.length) + 1)));
        Assert.assertEquals("none", SalesforceInputField.getTrimTypeCode(0));
        Assert.assertEquals("left", SalesforceInputField.getTrimTypeCode(1));
        Assert.assertEquals("right", SalesforceInputField.getTrimTypeCode(2));
        Assert.assertEquals("both", SalesforceInputField.getTrimTypeCode(3));
        Assert.assertEquals(SalesforceInputField.getTrimTypeDesc(0), SalesforceInputField.getTrimTypeDesc((-1)));
        Assert.assertEquals(SalesforceInputField.getTrimTypeDesc(0), SalesforceInputField.getTrimTypeDesc(((trimTypeCode.length) + 1)));
    }
}

