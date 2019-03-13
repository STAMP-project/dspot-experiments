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
package org.pentaho.di.ui.trans.steps.tableoutput;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.row.RowMetaInterface;


public class TableOutputDialogTest {
    private static RowMetaInterface filled;

    private static RowMetaInterface empty;

    private static String[] sample = new String[]{ "1", "2", "3" };

    @Test
    public void validationRowMetaTest() throws Exception {
        Method m = TableOutputDialog.class.getDeclaredMethod("isValidRowMeta", RowMetaInterface.class);
        m.setAccessible(true);
        Object result1 = m.invoke(null, TableOutputDialogTest.filled);
        Object result2 = m.invoke(null, TableOutputDialogTest.empty);
        Assert.assertTrue(Boolean.parseBoolean((result1 + "")));
        Assert.assertFalse(Boolean.parseBoolean((result2 + "")));
    }

    @Test
    public void isConnectionSupportedValidTest() {
        isConnectionSupportedTest(true);
    }

    @Test
    public void isConnectionSupportedInvalidTest() {
        isConnectionSupportedTest(false);
    }
}

