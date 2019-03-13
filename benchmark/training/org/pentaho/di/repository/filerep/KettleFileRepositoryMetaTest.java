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
package org.pentaho.di.repository.filerep;


import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.RepositoriesMeta;


/**
 * Created by bmorrise on 4/26/16.
 */
public class KettleFileRepositoryMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String NAME = "Name";

    public static final String DESCRIPTION = "Description";

    public static final String THIS_IS_THE_PATH = "/this/is/the/path";

    public static final String JSON_OUTPUT = "{\"isDefault\":true,\"displayName\":\"Name\",\"showHiddenFolders\":true," + ("\"description\":\"Description\",\"location\":\"\\/this\\/is\\/the\\/path\",\"id\":\"KettleFileRepository\"," + "\"doNotModify\":true}");

    private RepositoriesMeta repositoriesMeta = Mockito.mock(RepositoriesMeta.class);

    KettleFileRepositoryMeta kettleFileRepositoryMeta;

    @Test
    public void testPopulate() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("displayName", KettleFileRepositoryMetaTest.NAME);
        properties.put("showHiddenFolders", true);
        properties.put("description", KettleFileRepositoryMetaTest.DESCRIPTION);
        properties.put("location", KettleFileRepositoryMetaTest.THIS_IS_THE_PATH);
        properties.put("doNotModify", true);
        properties.put("isDefault", true);
        kettleFileRepositoryMeta.populate(properties, repositoriesMeta);
        Assert.assertEquals(KettleFileRepositoryMetaTest.NAME, kettleFileRepositoryMeta.getName());
        Assert.assertEquals(true, kettleFileRepositoryMeta.isHidingHiddenFiles());
        Assert.assertEquals(KettleFileRepositoryMetaTest.DESCRIPTION, kettleFileRepositoryMeta.getDescription());
        Assert.assertEquals(KettleFileRepositoryMetaTest.THIS_IS_THE_PATH, kettleFileRepositoryMeta.getBaseDirectory());
        Assert.assertEquals(true, kettleFileRepositoryMeta.isReadOnly());
        Assert.assertEquals(true, kettleFileRepositoryMeta.isDefault());
    }

    @Test
    public void testToJSONString() {
        kettleFileRepositoryMeta.setName(KettleFileRepositoryMetaTest.NAME);
        kettleFileRepositoryMeta.setHidingHiddenFiles(true);
        kettleFileRepositoryMeta.setDescription(KettleFileRepositoryMetaTest.DESCRIPTION);
        kettleFileRepositoryMeta.setBaseDirectory(KettleFileRepositoryMetaTest.THIS_IS_THE_PATH);
        kettleFileRepositoryMeta.setReadOnly(true);
        kettleFileRepositoryMeta.setDefault(true);
        JSONObject json = kettleFileRepositoryMeta.toJSONObject();
        Assert.assertEquals(KettleFileRepositoryMetaTest.JSON_OUTPUT, json.toString());
    }
}

