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
package org.pentaho.di.trans.steps.excelwriter;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class ExcelWriterStep_FormulaRecalculationTest {
    private ExcelWriterStep step;

    private ExcelWriterStepData data;

    private StepMockHelper<ExcelWriterStepMeta, StepDataInterface> mockHelper;

    @Test
    public void forcesToRecalculate_Sxssf_PropertyIsSet() throws Exception {
        forcesToRecalculate_Sxssf("Y", true);
    }

    @Test
    public void forcesToRecalculate_Sxssf_PropertyIsCleared() throws Exception {
        forcesToRecalculate_Sxssf("N", false);
    }

    @Test
    public void forcesToRecalculate_Sxssf_PropertyIsNotSet() throws Exception {
        forcesToRecalculate_Sxssf(null, false);
    }

    @Test
    public void forcesToRecalculate_Hssf() throws Exception {
        data.wb = new HSSFWorkbook();
        data.wb.createSheet("sheet1");
        data.wb.createSheet("sheet2");
        step.recalculateAllWorkbookFormulas();
        if (!(data.wb.getForceFormulaRecalculation())) {
            int sheets = data.wb.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                Sheet sheet = data.wb.getSheetAt(i);
                Assert.assertTrue(((("Sheet #" + i) + ": ") + (sheet.getSheetName())), sheet.getForceFormulaRecalculation());
            }
        }
    }
}

