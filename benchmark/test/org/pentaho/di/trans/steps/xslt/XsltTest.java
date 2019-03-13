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
package org.pentaho.di.trans.steps.xslt;


import junit.framework.TestCase;


public class XsltTest extends TestCase {
    private static final String TEST1_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message>Yep, it worked!</message>";

    private static final String TEST1_XSL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((("<xsl:stylesheet version = \"1.0\" xmlns:xsl = \"http://www.w3.org/1999/XSL/Transform\">" + "<xsl:output method = \"text\" encoding = \"UTF-8\"/>") + "<!--simply copy the message to the result tree -->") + "<xsl:template match = \"/\">") + "<xsl:value-of select = \"message\"/>") + "</xsl:template>") + "</xsl:stylesheet>");

    private static final String TEST1_FNAME = "template.xsl";

    /**
     * Test case for XSLT step, getting the filename from a field, JAXP factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt1() throws Exception {
        String fileName = writeInputFile();
        runTestWithParams("XML", "result", true, true, "filename", fileName, "JAXP");
    }

    /**
     * Test case for XSLT step, getting the filename from a field, SAXON factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt2() throws Exception {
        String fileName = writeInputFile();
        runTestWithParams("XML", "result", true, true, "filename", fileName, "SAXON");
    }

    /**
     * Test case for XSLT step, getting the XSL from a field, JAXP factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt3() throws Exception {
        runTestWithParams("XML", "result", true, false, "XSL", "", "JAXP");
    }

    /**
     * Test case for XSLT step, getting the XSL from a field, SAXON factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt4() throws Exception {
        runTestWithParams("XML", "result", true, false, "XSL", "", "SAXON");
    }

    /**
     * Test case for XSLT step, getting the XSL from a file, JAXP factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt5() throws Exception {
        String fileName = writeInputFile();
        runTestWithParams("XML", "result", false, false, "filename", fileName, "JAXP");
    }

    /**
     * Test case for XSLT step, getting the XSL from a file, SAXON factory
     *
     * @throws Exception
     * 		Upon any exception
     */
    public void testXslt6() throws Exception {
        String fileName = writeInputFile();
        runTestWithParams("XML", "result", false, false, "filename", fileName, "SAXON");
    }
}

