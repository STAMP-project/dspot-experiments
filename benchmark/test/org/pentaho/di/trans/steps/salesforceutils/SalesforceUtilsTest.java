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
package org.pentaho.di.trans.steps.salesforceutils;


import java.lang.reflect.Constructor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class SalesforceUtilsTest {
    private static LogChannelInterface logMock;

    private String inputFieldName;

    private String expectedFieldName;

    @Test
    public void testNoInstances() {
        Constructor<?>[] methods = SalesforceUtils.class.getConstructors();
        for (int i = 0; i < (methods.length); i++) {
            Assert.assertFalse(methods[i].isAccessible());
        }
    }

    @Test
    public void testFieldWithExtIdYes_StandartObject() {
        inputFieldName = "Account:ExtID_AccountId__c/Account";
        expectedFieldName = "AccountId";
        String fieldToNullName = SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Assert.assertEquals(expectedFieldName, fieldToNullName);
    }

    @Test
    public void testFieldWithExtIdNo_StandartObject() {
        inputFieldName = "AccountId";
        expectedFieldName = "AccountId";
        String fieldToNullName = SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, false);
        Assert.assertEquals(expectedFieldName, fieldToNullName);
    }

    @Test
    public void testFieldWithExtIdYes_CustomObject() {
        inputFieldName = "ParentObject__c:Name/ParentObjectId__r";
        expectedFieldName = "ParentObjectId__c";
        String fieldToNullName = SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Assert.assertEquals(expectedFieldName, fieldToNullName);
    }

    @Test
    public void testFieldWithExtIdNo_CustomObject() {
        inputFieldName = "ParentObjectId__c";
        expectedFieldName = "ParentObjectId__c";
        String fieldToNullName = SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, false);
        Assert.assertEquals(expectedFieldName, fieldToNullName);
    }

    @Test
    public void testFieldWithExtIdYesButNameInIncorrectSyntax_StandartObject() {
        Mockito.when(SalesforceUtilsTest.logMock.isDebug()).thenReturn(true);
        inputFieldName = "Account";
        expectedFieldName = inputFieldName;
        String fieldToNullName = SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Assert.assertEquals(expectedFieldName, fieldToNullName);
    }

    @Test
    public void testIncorrectExternalKeySyntaxWarnIsLoggedInDebugMode() {
        Mockito.when(SalesforceUtilsTest.logMock.isDebug()).thenReturn(true);
        inputFieldName = "AccountId";
        Mockito.verify(SalesforceUtilsTest.logMock, Mockito.never()).logDebug(ArgumentMatchers.anyString());
        SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Mockito.verify(SalesforceUtilsTest.logMock).logDebug("The field has incorrect external key syntax: AccountId. Syntax for external key should be : object:externalId/lookupField. Trying to use fieldToNullName=AccountId.");
    }

    @Test
    public void testIncorrectExternalKeySyntaxWarnIsNotLoggedInNotDebugMode() {
        Mockito.when(SalesforceUtilsTest.logMock.isDebug()).thenReturn(false);
        inputFieldName = "AccountId";
        Mockito.verify(SalesforceUtilsTest.logMock, Mockito.never()).logDebug(ArgumentMatchers.anyString());
        SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Mockito.verify(SalesforceUtilsTest.logMock, Mockito.never()).logDebug("The field has incorrect external key syntax: AccountId. Syntax for external key should be : object:externalId/lookupField. Trying to use fieldToNullName=AccountId.");
    }

    @Test
    public void testFinalNullFieldNameIsLoggedInDebugMode_StandartObject() {
        Mockito.when(SalesforceUtilsTest.logMock.isDebug()).thenReturn(true);
        inputFieldName = "Account:ExtID_AccountId__c/Account";
        Mockito.verify(SalesforceUtilsTest.logMock, Mockito.never()).logDebug(ArgumentMatchers.anyString());
        SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Mockito.verify(SalesforceUtilsTest.logMock).logDebug("fieldToNullName=AccountId");
    }

    @Test
    public void testFinalNullFieldNameIsLoggedInDebugMode_CustomObject() {
        Mockito.when(SalesforceUtilsTest.logMock.isDebug()).thenReturn(true);
        inputFieldName = "ParentObject__c:Name/ParentObjectId__r";
        Mockito.verify(SalesforceUtilsTest.logMock, Mockito.never()).logDebug(ArgumentMatchers.anyString());
        SalesforceUtils.getFieldToNullName(SalesforceUtilsTest.logMock, inputFieldName, true);
        Mockito.verify(SalesforceUtilsTest.logMock).logDebug("fieldToNullName=ParentObjectId__c");
    }
}

