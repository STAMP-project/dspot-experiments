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
package org.pentaho.di.trans.steps.pentahoreporting;


import StatusType.ERROR;
import java.io.OutputStream;
import java.util.Locale;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;


public class ReportExportTaskTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private MasterReport masterReport;

    private PentahoReportingSwingGuiContext swingGuiContext;

    private String targetPath;

    private boolean createParentFolder;

    @Test(expected = NullPointerException.class)
    public void testExportReportWithNullReport() {
        masterReport = null;
        Mockito.when(swingGuiContext.getLocale()).thenReturn(Locale.US);
        Mockito.when(swingGuiContext.getStatusListener()).thenReturn(Mockito.mock(StatusListener.class));
        Runnable exportTask = new ReportExportTask(masterReport, swingGuiContext, targetPath, createParentFolder) {
            protected ReportProcessor createReportProcessor(OutputStream fout) throws Exception {
                return null;
            }
        };
        Assert.assertNull(exportTask);
    }

    @Test
    public void testExportReportWithSupportedLocale() {
        Mockito.when(masterReport.getConfiguration()).thenReturn(Mockito.mock(Configuration.class));
        Mockito.when(masterReport.getResourceManager()).thenReturn(new ResourceManager());
        Mockito.when(swingGuiContext.getLocale()).thenReturn(Locale.US);
        Mockito.when(swingGuiContext.getStatusListener()).thenReturn(Mockito.mock(StatusListener.class));
        Runnable exportTask = new ReportExportTask(masterReport, swingGuiContext, targetPath, createParentFolder) {
            protected ReportProcessor createReportProcessor(OutputStream fout) throws Exception {
                PdfOutputProcessor outputProcessor = new PdfOutputProcessor(masterReport.getConfiguration(), fout, masterReport.getResourceManager());
                return new org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor(masterReport, outputProcessor);
            }
        };
        Assert.assertNotNull(exportTask);
        exportTask.run();
        Assert.assertThat(swingGuiContext.getStatusType(), IsNot.not(ERROR));
    }

    @Test
    public void testExportReportWithUnsupportedLocale() {
        Mockito.when(masterReport.getConfiguration()).thenReturn(Mockito.mock(Configuration.class));
        Mockito.when(masterReport.getResourceManager()).thenReturn(new ResourceManager());
        Mockito.when(swingGuiContext.getLocale()).thenReturn(Locale.UK);
        Mockito.when(swingGuiContext.getStatusListener()).thenReturn(Mockito.mock(StatusListener.class));
        Runnable exportTask = new ReportExportTask(masterReport, swingGuiContext, targetPath, createParentFolder) {
            protected ReportProcessor createReportProcessor(OutputStream fout) throws Exception {
                PdfOutputProcessor outputProcessor = new PdfOutputProcessor(masterReport.getConfiguration(), fout, masterReport.getResourceManager());
                return new org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor(masterReport, outputProcessor);
            }
        };
        Assert.assertNotNull(exportTask);
        exportTask.run();
        Assert.assertThat(swingGuiContext.getStatusType(), IsNot.not(ERROR));
    }
}

