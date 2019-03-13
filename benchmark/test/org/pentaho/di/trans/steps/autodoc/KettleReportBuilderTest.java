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
package org.pentaho.di.trans.steps.autodoc;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LoggingObjectInterface;


public class KettleReportBuilderTest {
    @Test
    public void createReport() throws Exception {
        LoggingObjectInterface log = Mockito.mock(LoggingObjectInterface.class);
        AutoDocOptionsInterface options = Mockito.mock(AutoDocOptionsInterface.class);
        Mockito.when(options.isIncludingImage()).thenReturn(Boolean.TRUE);
        KettleReportBuilder builder = new KettleReportBuilder(log, Collections.<ReportSubjectLocation>emptyList(), "", options);
        builder.createReport();
        Assert.assertNotNull(builder.getReport());
        Assert.assertNotNull(builder.getReport().getDataFactory());
        Assert.assertNotNull(builder.getReport().getReportHeader());
        Assert.assertNotNull(builder.getReport().getReportFooter());
        Assert.assertNotNull(builder.getReport().getRootGroup());
        Assert.assertNotNull(builder.getReport().getPageDefinition());
        Assert.assertTrue(((builder.getReport().getExpressions().size()) > 0));
    }
}

