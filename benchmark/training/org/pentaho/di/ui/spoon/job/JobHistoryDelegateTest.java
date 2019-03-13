/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon.job;


import JobHistoryDelegate.JobHistoryLogTab;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogTableField;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.spoon.Spoon;


public class JobHistoryDelegateTest {
    @Test
    public void getColumnMappings() {
        TableView view = Mockito.mock(TableView.class);
        Mockito.doReturn(getColumnInfo()).when(view).getColumns();
        JobHistoryDelegate.JobHistoryLogTab model = Mockito.mock(JobHistoryLogTab.class);
        setInternalState(model, "logDisplayTableView", view);
        setInternalState(model, "logTableFields", getLogTableFields());
        JobHistoryDelegate history = new JobHistoryDelegate(Mockito.mock(Spoon.class), Mockito.mock(JobGraph.class));
        Map<String, Integer> map = history.getColumnMappings(model);
        Assert.assertEquals(0, ((int) (map.get("COLUMN_1"))));
        Assert.assertEquals(1, ((int) (map.get("COLUMN_2"))));
        Assert.assertEquals(2, ((int) (map.get("COLUMN_3"))));
        Assert.assertEquals(4, ((int) (map.get("COLUMN_5"))));
        Assert.assertEquals(5, ((int) (map.get("COLUMN_6"))));
    }

    @Test
    public void getValueMetaForStringColumn() {
        JobHistoryDelegate history = new JobHistoryDelegate(Mockito.mock(Spoon.class), Mockito.mock(JobGraph.class));
        ValueMetaInterface valueMeta = history.getValueMetaForColumn(getColumnInfo(), new LogTableField("COLUMN 2", "COLUMN_2", null));
        Assert.assertEquals("COLUMN_2", valueMeta.getName());
        Assert.assertThat(valueMeta, CoreMatchers.instanceOf(ValueMetaString.class));
    }

    @Test
    public void getValueMetaForIntegerColumn() {
        JobHistoryDelegate history = new JobHistoryDelegate(Mockito.mock(Spoon.class), Mockito.mock(JobGraph.class));
        ValueMetaInterface valueMeta = history.getValueMetaForColumn(getColumnInfo(), new LogTableField("COLUMN 5", "COLUMN_5", null));
        Assert.assertEquals("COLUMN_5", valueMeta.getName());
        Assert.assertThat(valueMeta, CoreMatchers.instanceOf(ValueMetaInteger.class));
    }
}

