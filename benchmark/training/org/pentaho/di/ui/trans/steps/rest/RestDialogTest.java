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
package org.pentaho.di.ui.trans.steps.rest;


import RestMeta.HTTP_METHOD_DELETE;
import RestMeta.HTTP_METHOD_GET;
import RestMeta.HTTP_METHOD_HEAD;
import RestMeta.HTTP_METHOD_OPTIONS;
import RestMeta.HTTP_METHOD_PATCH;
import RestMeta.HTTP_METHOD_POST;
import RestMeta.HTTP_METHOD_PUT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;


public class RestDialogTest {
    private Label bodyl = Mockito.mock(Label.class);

    private ComboVar body = Mockito.mock(ComboVar.class);

    private ComboVar type = Mockito.mock(ComboVar.class);

    private Label paramsl = Mockito.mock(Label.class);

    private TableView params = Mockito.mock(TableView.class);

    private Button paramsb = Mockito.mock(Button.class);

    private Label matrixl = Mockito.mock(Label.class);

    private TableView matrix = Mockito.mock(TableView.class);

    private Button matrixb = Mockito.mock(Button.class);

    private ComboVar method = Mockito.mock(ComboVar.class);

    private RestDialog dialog = Mockito.mock(RestDialog.class);

    @Test
    public void testSetMethod_GET() {
        Mockito.doReturn(HTTP_METHOD_GET).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(body, Mockito.times(1)).setEnabled(false);
        Mockito.verify(type, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(params, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(false);
    }

    @Test
    public void testSetMethod_POST() {
        Mockito.doReturn(HTTP_METHOD_POST).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(body, Mockito.times(1)).setEnabled(true);
        Mockito.verify(type, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(params, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(true);
    }

    @Test
    public void testSetMethod_PUT() {
        Mockito.doReturn(HTTP_METHOD_PUT).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(body, Mockito.times(1)).setEnabled(true);
        Mockito.verify(type, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(params, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(true);
    }

    @Test
    public void testSetMethod_PATCH() {
        Mockito.doReturn(HTTP_METHOD_PATCH).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(body, Mockito.times(1)).setEnabled(true);
        Mockito.verify(type, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(params, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(true);
    }

    @Test
    public void testSetMethod_DELETE() {
        Mockito.doReturn(HTTP_METHOD_DELETE).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(body, Mockito.times(1)).setEnabled(false);
        Mockito.verify(type, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(params, Mockito.times(1)).setEnabled(true);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(true);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(true);
    }

    @Test
    public void testSetMethod_OPTIONS() {
        Mockito.doReturn(HTTP_METHOD_OPTIONS).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(body, Mockito.times(1)).setEnabled(false);
        Mockito.verify(type, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(params, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(false);
    }

    @Test
    public void testSetMethod_HEAD() {
        Mockito.doReturn(HTTP_METHOD_HEAD).when(method).getText();
        dialog.setMethod();
        Mockito.verify(bodyl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(body, Mockito.times(1)).setEnabled(false);
        Mockito.verify(type, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(params, Mockito.times(1)).setEnabled(false);
        Mockito.verify(paramsb, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixl, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrix, Mockito.times(1)).setEnabled(false);
        Mockito.verify(matrixb, Mockito.times(1)).setEnabled(false);
    }
}

