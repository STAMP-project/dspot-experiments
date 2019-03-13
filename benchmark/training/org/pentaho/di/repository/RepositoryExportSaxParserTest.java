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
package org.pentaho.di.repository;


import java.io.File;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.test.util.XXEUtils;
import org.xml.sax.SAXParseException;


public class RepositoryExportSaxParserTest {
    private static final String PKG = "org/pentaho/di/repository/";

    private static final String REPOSITORY_FILE = "test_repo";

    private static final String DIR_WITH_SPECIFIC_CHARS = "\u30a2\u30ff\u30cf";

    private static final String BASE_TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static final File TEMP_DIR_WITH_REP_FILE = new File(RepositoryExportSaxParserTest.BASE_TEMP_DIR, RepositoryExportSaxParserTest.DIR_WITH_SPECIFIC_CHARS);

    private RepositoryExportSaxParser repExpSAXParser;

    private RepositoryImportFeedbackInterface repImpPgDlg = Mockito.mock(RepositoryImportFeedbackInterface.class);

    private RepositoryImporter repImpMock = Mockito.mock(RepositoryImporter.class);

    @Test
    public void testNoExceptionOccurs_WhenNameContainsJapaneseCharacters() throws Exception {
        repExpSAXParser = new RepositoryExportSaxParser(getRepositoryFile().getCanonicalPath(), repImpPgDlg);
        try {
            repExpSAXParser.parse(repImpMock);
        } catch (Exception e) {
            Assert.fail(("No exception is expected But occured: " + e));
        }
    }

    @Test(expected = SAXParseException.class)
    public void exceptionIsThrownWhenParsingXmlWithBigAmountOfExternalEntities() throws Exception {
        File file = createTmpFile(XXEUtils.MALICIOUS_XML);
        repExpSAXParser = new RepositoryExportSaxParser(file.getAbsolutePath(), null);
        repExpSAXParser.parse(repImpMock);
    }
}

