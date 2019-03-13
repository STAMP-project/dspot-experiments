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
package org.pentaho.di.core;


import CheckResultInterface.TYPE_RESULT_NONE;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static CheckResultInterface.TYPE_RESULT_ERROR;


public class CheckResultTest {
    @Test
    public void testClass() {
        final int type = TYPE_RESULT_ERROR;
        final String text = "some text";
        final String sourceMetaName = "meta name";
        final CheckResultSourceInterface sourceMeta = Mockito.mock(CheckResultSourceInterface.class);
        final String errorCode = "error code";
        CheckResult cr = new CheckResult();
        Assert.assertEquals(TYPE_RESULT_NONE, cr.getType());
        Assert.assertTrue((((cr.getTypeDesc()) != null) && (cr.getTypeDesc().isEmpty())));
        cr.setType(type);
        Assert.assertEquals(type, cr.getType());
        Assert.assertTrue(cr.getText().isEmpty());
        cr.setText(text);
        Assert.assertSame(text, cr.getText());
        Assert.assertNull(null, cr.getSourceInfo());
        Assert.assertNull(cr.getErrorCode());
        cr.setErrorCode(errorCode);
        Assert.assertSame(errorCode, cr.getErrorCode());
        Mockito.when(sourceMeta.getName()).thenReturn(sourceMetaName);
        cr = new CheckResult(type, text, sourceMeta);
        Assert.assertSame(sourceMeta, cr.getSourceInfo());
        Assert.assertTrue((((cr.getTypeDesc()) != null) && (!(cr.getTypeDesc().isEmpty()))));
        final String stringValue = String.format("%s: %s (%s)", cr.getTypeDesc(), text, sourceMetaName);
        Assert.assertEquals(stringValue, cr.toString());
        cr = new CheckResult(type, errorCode, text, sourceMeta);
        Assert.assertSame(errorCode, cr.getErrorCode());
    }
}

