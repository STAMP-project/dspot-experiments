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


import com.google.common.collect.Iterables;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
@RunWith(Parameterized.class)
public class DelegatesPrivateDatabasesTest {
    private static final String DB_NAME = "privateDatabase";

    private final ITransformer delegate;

    private final AbstractMeta meta;

    private final String privateDbsNodeName;

    private final String privateDbsPropertyName;

    public DelegatesPrivateDatabasesTest(ITransformer delegate, AbstractMeta meta, String privateDbsNodeName, String privateDbsPropertyName) {
        this.delegate = delegate;
        this.meta = meta;
        this.privateDbsNodeName = privateDbsNodeName;
        this.privateDbsPropertyName = privateDbsPropertyName;
    }

    @Test
    public void savesNode_IfSetIsNotEmpty() throws Exception {
        meta.setPrivateDatabases(Collections.singleton(DelegatesPrivateDatabasesTest.DB_NAME));
        DataNode dataNode = element2node();
        DataNode dbsNode = dataNode.getNode(privateDbsNodeName);
        Assert.assertNotNull(dbsNode);
        Assert.assertTrue(dbsNode.hasProperty(privateDbsPropertyName));
        Assert.assertEquals(DelegatesPrivateDatabasesTest.DB_NAME, dbsNode.getProperty(privateDbsPropertyName).getString());
    }

    @Test
    public void doesNotSaveNode_IfSetIsNull() throws Exception {
        meta.setPrivateDatabases(null);
        DataNode dataNode = element2node();
        DataNode dbsNode = dataNode.getNode(privateDbsNodeName);
        Assert.assertNull(dbsNode);
    }

    @Test
    public void savesNode_IfSetIsEmpty() throws Exception {
        meta.setPrivateDatabases(Collections.<String>emptySet());
        DataNode dataNode = element2node();
        DataNode dbsNode = dataNode.getNode(privateDbsNodeName);
        Assert.assertNotNull("Even if the set is empty, the node should be saved as an indicator", dbsNode);
        DataNode databaseNode = Iterables.getFirst(dbsNode.getNodes(), null);
        Assert.assertNull(databaseNode);
    }

    @Test
    public void saveAndLoad_SetIsNotEmpty() throws Exception {
        meta.setPrivateDatabases(Collections.singleton(DelegatesPrivateDatabasesTest.DB_NAME));
        AbstractMeta restored = ((AbstractMeta) (delegate.dataNodeToElement(delegate.elementToDataNode(meta))));
        Assert.assertEquals(meta.getPrivateDatabases(), restored.getPrivateDatabases());
    }

    @Test
    public void saveAndLoad_SetIsEmpty() throws Exception {
        meta.setPrivateDatabases(Collections.<String>emptySet());
        AbstractMeta restored = ((AbstractMeta) (delegate.dataNodeToElement(delegate.elementToDataNode(meta))));
        Assert.assertNotNull(restored.getPrivateDatabases());
        Assert.assertTrue(restored.getPrivateDatabases().isEmpty());
    }

    @Test
    public void saveAndLoad_SetIsNull() throws Exception {
        meta.setPrivateDatabases(null);
        AbstractMeta restored = ((AbstractMeta) (delegate.dataNodeToElement(delegate.elementToDataNode(meta))));
        Assert.assertNull(restored.getPrivateDatabases());
    }
}

