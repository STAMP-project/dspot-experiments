/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.table;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ObservableTableDisplayTest {
    private TableDisplay tableDisplay;

    private int rowId = 0;

    private int colId = 0;

    @Test
    public void setDoubleClickAction_doubleClickActionIsNotNull() throws Exception {
        // when
        tableDisplay.setDoubleClickAction(getClosure(this::sumAllElements));
        // then
        Assertions.assertThat(tableDisplay.hasDoubleClickAction()).isTrue();
        Assertions.assertThat(tableDisplay.getDoubleClickTag()).isNull();
    }

    @Test
    public void addContextMenuItem_contextMenuItemActionIsNotNull() throws Exception {
        // when
        tableDisplay.addContextMenuItem("negate", getClosure(this::negateValue));
        // then
        Assertions.assertThat(tableDisplay.getContextMenuItems()).isNotEmpty();
        Assertions.assertThat(tableDisplay.getContextMenuTags()).isEmpty();
    }

    @Test
    public void fireDoubleClick_shouldExecuteDoubleClickAction() throws Exception {
        // given
        int result = sumAllElements(getRowValues(rowId, tableDisplay), colId);
        tableDisplay.setDoubleClickAction(getClosure(this::sumAllElements));
        // when
        tableDisplay.fireDoubleClick(new ArrayList<Object>(Arrays.asList(rowId, colId)), null);
        // then
        Assertions.assertThat(tableDisplay.getValues().get(rowId).get(colId)).isEqualTo(result);
    }

    @Test
    public void fireContextMenuItem_shouldExecuteDoubleClickAction() throws Exception {
        // given
        int result = negateValue(getRowValues(rowId, tableDisplay), colId);
        tableDisplay.addContextMenuItem("negate", getClosure(this::negateValue));
        // when
        tableDisplay.fireContextMenuClick("negate", new ArrayList<Object>(Arrays.asList(rowId, colId)), null);
        // then
        Assertions.assertThat(tableDisplay.getValues().get(rowId).get(colId)).isEqualTo(result);
    }

    private interface TableActionTest {
        int execute(List<Integer> rowValues, int colId);
    }
}

