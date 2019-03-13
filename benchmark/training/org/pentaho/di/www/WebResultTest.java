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
package org.pentaho.di.www;


import WebResult.OK;
import WebResult.STRING_ERROR;
import WebResult.STRING_OK;
import WebResult.XML_TAG;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleXMLException;


public class WebResultTest {
    @Test
    public void testStatics() {
        Assert.assertEquals("webresult", XML_TAG);
        Assert.assertEquals("OK", STRING_OK);
        Assert.assertEquals("ERROR", STRING_ERROR);
        Assert.assertNotNull(OK);
        Assert.assertEquals("OK", OK.getResult());
        Assert.assertNull(OK.getMessage());
        Assert.assertNull(OK.getId());
    }

    @Test
    public void testConstructors() {
        String expectedResult = UUID.randomUUID().toString();
        WebResult result = new WebResult(expectedResult);
        Assert.assertEquals(expectedResult, result.getResult());
        String expectedMessage = UUID.randomUUID().toString();
        result = new WebResult(expectedResult, expectedMessage);
        Assert.assertEquals(expectedResult, result.getResult());
        Assert.assertEquals(expectedMessage, result.getMessage());
        String expectedId = UUID.randomUUID().toString();
        result = new WebResult(expectedResult, expectedMessage, expectedId);
        Assert.assertEquals(expectedResult, result.getResult());
        Assert.assertEquals(expectedMessage, result.getMessage());
        Assert.assertEquals(expectedId, result.getId());
    }

    @Test
    public void testSerialization() throws KettleXMLException {
        WebResult original = new WebResult(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        String xml = original.getXML();
        WebResult copy = WebResult.fromXMLString(xml);
        Assert.assertNotNull(copy);
        Assert.assertNotSame(original, copy);
        Assert.assertEquals(original.getResult(), copy.getResult());
        Assert.assertEquals(original.getMessage(), copy.getMessage());
        Assert.assertEquals(original.getId(), copy.getId());
    }

    @Test
    public void testSetters() {
        WebResult result = new WebResult("");
        Assert.assertEquals("", result.getResult());
        result.setMessage("fakeMessage");
        Assert.assertEquals("fakeMessage", result.getMessage());
        result.setResult("fakeResult");
        Assert.assertEquals("fakeResult", result.getResult());
        result.setId("fakeId");
        Assert.assertEquals("fakeId", result.getId());
    }
}

