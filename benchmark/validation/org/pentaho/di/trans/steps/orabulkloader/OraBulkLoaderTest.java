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
package org.pentaho.di.trans.steps.orabulkloader;


import java.io.File;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * User: Dzmitry Stsiapanau Date: 4/8/14 Time: 1:44 PM
 */
public class OraBulkLoaderTest {
    private StepMockHelper<OraBulkLoaderMeta, OraBulkLoaderData> stepMockHelper;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testCreateCommandLine() throws Exception {
        OraBulkLoader oraBulkLoader = new OraBulkLoader(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        File tmp = File.createTempFile("testCreateCOmmandLine", "tmp");
        tmp.deleteOnExit();
        OraBulkLoaderMeta meta = new OraBulkLoaderMeta();
        meta.setSqlldr(tmp.getAbsolutePath());
        meta.setControlFile(tmp.getAbsolutePath());
        DatabaseMeta dm = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dm.getUsername()).thenReturn("user");
        Mockito.when(dm.getPassword()).thenReturn("Encrypted 2be98afc86aa7f2e4cb298b5eeab387f5");
        meta.setDatabaseMeta(dm);
        String cmd = oraBulkLoader.createCommandLine(meta, true);
        String expected = (((tmp.getAbsolutePath()) + " control='") + (tmp.getAbsolutePath())) + "' userid=user/PENTAHO@";
        Assert.assertEquals("Comandline for oracle bulkloader is not as expected", expected, cmd);
    }
}

