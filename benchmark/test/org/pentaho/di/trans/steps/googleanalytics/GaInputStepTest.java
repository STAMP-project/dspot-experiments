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
package org.pentaho.di.trans.steps.googleanalytics;


import Analytics.Data.Ga.Get;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class GaInputStepTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void getNextDataEntry_WithPaging() throws Exception {
        final int recordsCount = 30;
        final String stepName = "GaInputStepTest";
        StepMeta stepMeta = new StepMeta(stepName, stepName, new GaInputStepMeta());
        Trans trans = Mockito.mock(Trans.class);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.when(transMeta.findStep(stepName)).thenReturn(stepMeta);
        GaInputStepData data = new GaInputStepData();
        GaInputStep step = new GaInputStep(stepMeta, data, 0, transMeta, trans);
        FieldUtils.writeField(FieldUtils.getField(GaInputStep.class, "data", true), step, data, true);
        Analytics.Data.Ga.Get mockQuery = prepareMockQuery(recordsCount);
        step = Mockito.spy(step);
        Mockito.doReturn(mockQuery).when(step).getQuery(ArgumentMatchers.any(Analytics.class));
        for (int i = 0; i < recordsCount; i++) {
            List<String> next = step.getNextDataEntry();
            Assert.assertEquals(Integer.toString((i + 1)), next.get(0));
        }
        Assert.assertNull(step.getNextDataEntry());
    }

    private static class MockQueryAssistant {
        private final int recordsCount;

        private Integer startIndex;

        private Integer limit;

        public MockQueryAssistant(int recordsCount) {
            this.recordsCount = recordsCount;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public GaData execute() {
            GaData result = new GaData();
            result.setTotalResults(recordsCount);
            result.setItemsPerPage(limit);
            List<List<String>> rows = new ArrayList<List<String>>();
            int start = ((startIndex) == null) ? 1 : startIndex;
            int end = Math.min((start + ((limit) == null ? 1000 : limit)), ((recordsCount) + 1));
            while (start < end) {
                rows.add(Collections.singletonList(Integer.toString(start)));
                start++;
            } 
            result.setRows(rows);
            return result;
        }
    }
}

