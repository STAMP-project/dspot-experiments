/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;


public class DatabaseDelegateTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private PurRepository mockPurRepository;

    private DatabaseDelegate dbDelegate;

    @Test
    public void testExtraOptionEscapeWithInvalidCharInDatabaseType() throws KettleException {
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dbMeta.getPluginId()).thenReturn("pluginId");
        Mockito.when(dbMeta.getAccessTypeDesc()).thenReturn("Native");
        Mockito.when(dbMeta.getHostname()).thenReturn("AS/400Host");
        Mockito.when(dbMeta.getDatabaseName()).thenReturn("mainframeTable");
        Mockito.when(dbMeta.getDatabasePortNumberString()).thenReturn("1234");
        Mockito.when(dbMeta.getUsername()).thenReturn("testUser");
        Mockito.when(dbMeta.getPassword()).thenReturn("123");
        Mockito.when(dbMeta.getServername()).thenReturn("as400.dot.com");
        Mockito.when(dbMeta.getDataTablespace()).thenReturn("tableSpace");
        Mockito.when(dbMeta.getIndexTablespace()).thenReturn("123");
        // Create an extra options that has an unsupported character like '/'
        Properties extraOptions = new Properties();
        extraOptions.setProperty("EXTRA_OPTION_AS/400.optionExtraOption", "true");
        Mockito.when(dbMeta.getAttributes()).thenReturn(extraOptions);
        IUnifiedRepository purRepo = Mockito.mock(IUnifiedRepository.class);
        Mockito.when(purRepo.getReservedChars()).thenReturn(Arrays.asList(new Character[]{ '/' }));
        Mockito.when(mockPurRepository.getUnderlyingRepository()).thenReturn(purRepo);
        DataNode escapedAttributes = dbDelegate.elementToDataNode(dbMeta);
        // Should only be one option in list
        for (Iterator<DataNode> iter = escapedAttributes.getNodes().iterator(); iter.hasNext();) {
            DataNode options = iter.next();
            Assert.assertTrue("Invalid escaped extra options", options.hasProperty("EXTRA_OPTION_AS%2F400.optionExtraOption"));
            Assert.assertFalse("Should not contain un-escaped option", options.hasProperty("EXTRA_OPTION_AS/400.optionExtraOption"));
        }
    }

    @Test
    public void testExtraOptionUnescapeWithInvalidCharInDatabaseType() throws KettleException {
        DataNode mockDataNode = Mockito.mock(DataNode.class);
        DataNode unescapedExtraOptions = new DataNode("options");
        unescapedExtraOptions.setProperty("EXTRA_OPTION_AS%2F400.optionExtraOption", true);
        Mockito.when(mockDataNode.getNode("attributes")).thenReturn(unescapedExtraOptions);
        DatabaseMeta unescapedDbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(unescapedDbMeta.getAttributes()).thenReturn(new Properties());
        dbDelegate.dataNodeToElement(mockDataNode, unescapedDbMeta);
        Assert.assertEquals("true", unescapedDbMeta.getAttributes().getProperty("EXTRA_OPTION_AS/400.optionExtraOption"));
    }
}

