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
package org.pentaho.di.trans.steps.exceloutput;


import java.util.HashMap;
import junit.framework.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Test for case using template file
 *
 * @author Pavel Sakun
 */
public class ExcelOutputTemplateTest {
    private static StepMockHelper<ExcelOutputMeta, ExcelOutputData> helper;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testExceptionClosingWorkbook() throws Exception {
        ExcelOutput excelOutput = new ExcelOutput(ExcelOutputTemplateTest.helper.stepMeta, ExcelOutputTemplateTest.helper.stepDataInterface, 0, ExcelOutputTemplateTest.helper.transMeta, ExcelOutputTemplateTest.helper.trans);
        ExcelOutputMeta meta = createStepMeta();
        excelOutput.init(meta, ExcelOutputTemplateTest.helper.initStepDataInterface);
        Assert.assertEquals("Step init error.", 0, excelOutput.getErrors());
        ExcelOutputTemplateTest.helper.initStepDataInterface.formats = new HashMap();
        excelOutput.dispose(meta, ExcelOutputTemplateTest.helper.initStepDataInterface);
        Assert.assertEquals("Step dispose error", 0, excelOutput.getErrors());
    }
}

