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
package org.pentaho.di.trans.steps.joinrows;


import LogLevel.ROWLEVEL;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.trans.RowStepCollector;
import org.pentaho.di.trans.step.StepMetaInterface;


/**
 *
 *
 * @author Denis Mashukov
 */
public class JoinRowsTest {
    private StepMetaInterface meta;

    private JoinRowsData data;

    /**
     * BACKLOG-8520 Check that method call does't throw an error NullPointerException.
     */
    @Test
    public void checkThatMethodPerformedWithoutError() throws Exception {
        getJoinRows().dispose(meta, data);
    }

    @Test
    public void disposeDataFiles() throws Exception {
        File mockFile1 = Mockito.mock(File.class);
        File mockFile2 = Mockito.mock(File.class);
        data.file = new File[]{ null, mockFile1, mockFile2 };
        getJoinRows().dispose(meta, data);
        Mockito.verify(mockFile1, Mockito.times(1)).delete();
        Mockito.verify(mockFile2, Mockito.times(1)).delete();
    }

    @Test
    public void testJoinRowsStep() throws Exception {
        JoinRowsMeta joinRowsMeta = new JoinRowsMeta();
        joinRowsMeta.setMainStepname("main step name");
        joinRowsMeta.setPrefix("out");
        joinRowsMeta.setCacheSize(3);
        JoinRowsData joinRowsData = new JoinRowsData();
        JoinRows joinRows = getJoinRows();
        joinRows.getTrans().setRunning(true);
        joinRows.init(joinRowsMeta, joinRowsData);
        List<RowSet> rowSets = new ArrayList<>();
        rowSets.add(getRowSetWithData(3, "main --", true));
        rowSets.add(getRowSetWithData(3, "secondary --", false));
        joinRows.setInputRowSets(rowSets);
        RowStepCollector rowStepCollector = new RowStepCollector();
        joinRows.addRowListener(rowStepCollector);
        joinRows.getLogChannel().setLogLevel(ROWLEVEL);
        KettleLogStore.init();
        while (true) {
            if (!(joinRows.processRow(joinRowsMeta, joinRowsData))) {
                break;
            }
        } 
        rowStepCollector.getRowsWritten();
        // since we have data join of two row sets with size 3 then we must have 9 written rows
        Assert.assertEquals(9, rowStepCollector.getRowsWritten().size());
        Assert.assertEquals(6, rowStepCollector.getRowsRead().size());
        Object[][] expectedResult = createExpectedResult();
        List<Object[]> rowWritten = rowStepCollector.getRowsWritten().stream().map(RowMetaAndData::getData).collect(Collectors.toList());
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(Arrays.equals(expectedResult[i], rowWritten.get(i)));
        }
    }
}

