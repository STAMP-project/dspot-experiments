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
package org.pentaho.di.ui.trans.steps.googleanalytics;


import Analytics.Data.Ga.Get;
import GaInputStepMeta.FIELD_DATA_SOURCE_TABLE_ID;
import GaInputStepMeta.FIELD_DATA_SOURCE_TABLE_NAME;
import GaInputStepMeta.FIELD_TYPE_DATA_SOURCE_FIELD;
import GaInputStepMeta.FIELD_TYPE_DATA_SOURCE_PROPERTY;
import GaInputStepMeta.FIELD_TYPE_DIMENSION;
import GaInputStepMeta.FIELD_TYPE_METRIC;
import GaInputStepMeta.PROPERTY_DATA_SOURCE_ACCOUNT_NAME;
import GaInputStepMeta.PROPERTY_DATA_SOURCE_PROFILE_ID;
import GaInputStepMeta.PROPERTY_DATA_SOURCE_WEBPROP_ID;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import java.util.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.ui.core.widget.TableView;


/**
 *
 *
 * @author Pavel Sakun
 */
@RunWith(MockitoJUnitRunner.class)
public class GaInputStepDialogTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Mock
    Get query;

    @Mock
    TableItem tableItem;

    @Mock
    Table table;

    @Mock
    TableView tableView;

    @Mock
    GaInputStepDialog dialog;

    List<ColumnHeaders> headers;

    @Test
    public void testGetFields() throws Exception {
        dialog.getFields();
        // Verify we do not read more data that needed
        Mockito.verify(query).setMaxResults(1);
        // Verify we're reading fields correctly and filling that info into table
        Mockito.verify(table, Mockito.times(19)).getItem(ArgumentMatchers.anyInt());
        Mockito.verify(tableItem, Mockito.times(7)).setText(1, FIELD_TYPE_DIMENSION);
        Mockito.verify(tableItem, Mockito.times(7)).setText(1, FIELD_TYPE_METRIC);
        Mockito.verify(tableItem, Mockito.times(3)).setText(1, FIELD_TYPE_DATA_SOURCE_PROPERTY);
        Mockito.verify(tableItem, Mockito.times(2)).setText(1, FIELD_TYPE_DATA_SOURCE_FIELD);
        for (ColumnHeaders header : headers) {
            Mockito.verify(tableItem, Mockito.times(1)).setText(2, header.getName());
            Mockito.verify(tableItem, Mockito.times(1)).setText(3, header.getName());
        }
        Mockito.verify(tableItem, Mockito.times(1)).setText(2, PROPERTY_DATA_SOURCE_PROFILE_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(3, PROPERTY_DATA_SOURCE_PROFILE_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(2, PROPERTY_DATA_SOURCE_WEBPROP_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(3, PROPERTY_DATA_SOURCE_WEBPROP_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(2, PROPERTY_DATA_SOURCE_ACCOUNT_NAME);
        Mockito.verify(tableItem, Mockito.times(1)).setText(3, PROPERTY_DATA_SOURCE_ACCOUNT_NAME);
        Mockito.verify(tableItem, Mockito.times(1)).setText(2, FIELD_DATA_SOURCE_TABLE_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(3, FIELD_DATA_SOURCE_TABLE_ID);
        Mockito.verify(tableItem, Mockito.times(1)).setText(2, FIELD_DATA_SOURCE_TABLE_NAME);
        Mockito.verify(tableItem, Mockito.times(1)).setText(3, FIELD_DATA_SOURCE_TABLE_NAME);
        Mockito.verify(tableItem, Mockito.times(1)).setText(4, ValueMetaBase.getTypeDesc(TYPE_DATE));
        Mockito.verify(tableItem, Mockito.times(5)).setText(4, ValueMetaBase.getTypeDesc(TYPE_INTEGER));
        Mockito.verify(tableItem, Mockito.times(6)).setText(4, ValueMetaBase.getTypeDesc(TYPE_NUMBER));
        Mockito.verify(tableItem, Mockito.times(7)).setText(4, ValueMetaBase.getTypeDesc(TYPE_STRING));
    }
}

