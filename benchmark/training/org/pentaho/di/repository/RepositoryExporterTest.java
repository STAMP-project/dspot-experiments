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
package org.pentaho.di.repository;


import ExportFeedback.Status.REJECTED;
import RepositoryExporter.ExportType.ALL;
import RepositoryExporter.ExportType.JOBS;
import RepositoryExporter.ExportType.TRANS;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.vfs2.FileObject;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class RepositoryExporterTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Mock
    Repository repository;

    @Mock
    LogChannelInterface log;

    @Mock
    private RepositoryDirectoryInterface root;

    private FileObject fileObject;

    private String xmlFileName;

    /**
     * Test that jobs can be exported with feedback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExportJobsWithFeedback() throws Exception {
        RepositoryExporter exporter = new RepositoryExporter(repository);
        List<ExportFeedback> feedback = exporter.exportAllObjectsWithFeedback(null, xmlFileName, root, JOBS.toString());
        Assert.assertEquals("Feedback contains all items recorded", 2, feedback.size());
        ExportFeedback fb = feedback.get(1);
        Assert.assertEquals("Job1 was exproted", "job1", fb.getItemName());
        Assert.assertEquals("Repository path for Job1 is specified", "path", fb.getItemPath());
        String res = this.validateXmlFile(fileObject.getContent().getInputStream(), "//job1");
        Assert.assertEquals("Export xml contains exported job xml", "found", res);
    }

    /**
     * Test that transformations can be exported with feedback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExportTransformationsWithFeedback() throws Exception {
        RepositoryExporter exporter = new RepositoryExporter(repository);
        List<ExportFeedback> feedback = exporter.exportAllObjectsWithFeedback(null, xmlFileName, root, TRANS.toString());
        Assert.assertEquals("Feedback contains all items recorded", 2, feedback.size());
        ExportFeedback fb = feedback.get(1);
        Assert.assertEquals("Job1 was exproted", "trans1", fb.getItemName());
        Assert.assertEquals("Repository path for Job1 is specified", "path", fb.getItemPath());
        String res = this.validateXmlFile(fileObject.getContent().getInputStream(), "//trans1");
        Assert.assertEquals("Export xml contains exported job xml", "found", res);
    }

    /**
     * Test that we can have some feedback on rule violations
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testExportAllRulesViolation() throws Exception {
        RepositoryExporter exporter = new RepositoryExporter(repository);
        exporter.setImportRulesToValidate(getImportRules());
        List<ExportFeedback> feedback = exporter.exportAllObjectsWithFeedback(null, xmlFileName, root, ALL.toString());
        Assert.assertEquals("Feedback contains all items recorded", 3, feedback.size());
        for (ExportFeedback feed : feedback) {
            if (feed.isSimpleString()) {
                continue;
            }
            Assert.assertEquals(("all items rejected: " + (feed.toString())), REJECTED, feed.getStatus());
        }
        Assert.assertTrue("Export file is deleted", (!(fileObject.exists())));
    }

    /**
     * PDI-7734 - EE Repository export with Rules: When it fails, no UI feedback is given and the file is incomplete
     * <p/>
     * this tests bachward compatibility mode when attempt to export repository is called from code that does not support
     * feddbacks.
     *
     * @throws KettleException
     * 		
     */
    @Test(expected = KettleException.class)
    public void testExportAllExceptionThrown() throws KettleException {
        RepositoryExporter exporter = new RepositoryExporter(repository);
        exporter.setImportRulesToValidate(getImportRules());
        try {
            exporter.exportAllObjects(null, xmlFileName, root, ALL.toString());
        } catch (KettleException e) {
            // some debugging palce e.getStackTrace();
            throw e;
        }
    }
}

