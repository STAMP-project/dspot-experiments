/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.report;


import ResourceType.DRL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.junit.Assert;
import org.junit.Test;


public class VerifierReportBuilderTest {
    @Test
    public void testHtmlReportTest() throws IOException {
        // Create report
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        VerifierReportWriter writer = VerifierReportWriterFactory.newHTMLReportWriter();
        // Write to disk
        FileOutputStream out = new FileOutputStream("testReport.zip");
        writer.writeReport(out, verifier.getResult());
        // Check the files on disk
        File file = new File("testReport.zip");
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
        // TODO: Check the file content
        // Remove the test file
        file.delete();
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testPlainTextReportTest() throws IOException {
        // TODO:
        Assert.assertTrue(true);
    }

    @Test
    public void testXMLReportTest() throws IOException {
        // TODO:
        Assert.assertTrue(true);
    }

    @Test
    public void testPDFReportTest() throws IOException {
        // TODO:
        Assert.assertTrue(true);
    }
}

