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
package org.pentaho.di.trans.steps.xsdvalidator;


import org.apache.commons.vfs2.FileObject;
import org.junit.Test;


public class XsdValidatorIntTest {
    private static final String RAMDIR = "ram://" + (XsdValidatorIntTest.class.getSimpleName());

    private static final String TEST_FILES_DIR = "src/test/resources/xsdvalidator/";

    private static FileObject schemaRamFile = null;

    private static FileObject dataRamFile = null;

    @Test
    public void testVfsInputFiles() throws Exception {
        testVfsFileTypes(getDataRamFile().getURL().toString(), getSchemaRamFile().getURL().toString(), true);
        testVfsFileTypes(getDataRamFile().getURL().toString(), getSchemaFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "schema.xsd")), true);
        testVfsFileTypes(getDataFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "data.xml")), getSchemaRamFile().getURL().toString(), true);
        testVfsFileTypes(getDataFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "data.xml")), getSchemaFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "schema.xsd")), true);
        testVfsFileTypes(getDataFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "xsd_issue/bad.xml")), getSchemaFileUrl(((XsdValidatorIntTest.TEST_FILES_DIR) + "xsd_issue/cbc-xml-schema-v1.0/CbcXML_v1.0.xsd")), true);
    }
}

