/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
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
 */
package org.pentaho.di.trans.steps.gpbulkloader;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class GPBulkLoaderTest {
    private GPBulkLoader loader;

    private StepMockHelper<GPBulkLoaderMeta, GPBulkLoaderData> mockHelper;

    @Test
    public void testInputFileSurroundedBySingleQuotes() throws Exception {
        String datafile = "test-data-file";
        loader = new GPBulkLoader(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        DatabaseMeta dbMetaMock = Mockito.mock(DatabaseMeta.class);
        Mockito.doReturn("").when(dbMetaMock).getQuotedSchemaTableCombination(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn("").when(dbMetaMock).quoteField(ArgumentMatchers.anyString());
        GPBulkLoaderMeta meta = new GPBulkLoaderMeta();
        meta.setLoadAction("");
        meta.setFieldStream(new String[]{ "" });
        meta.setFieldTable(new String[]{ "" });
        meta.setDatabaseMeta(dbMetaMock);
        meta.setDataFile(datafile);
        String actual = loader.getControlFileContents(meta, null, null);
        int first = actual.indexOf(datafile);
        if (first > 0) {
            if (((actual.charAt((first - 1))) != '\'') || ((actual.charAt((first + (datafile.length())))) != '\'')) {
                Assert.fail(("Datafile name is not surrounded by single quotes. Actual control file: " + actual));
            }
        } else {
            Assert.fail(("Datafile name not found in control file. Actual control file: " + actual));
        }
    }
}

