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
package org.pentaho.di.repository.pur.metastore;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.pur.PurRepository;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreAttribute;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreElement;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;


public class PurRepositoryMetaStoreTest {
    private PurRepositoryMetaStore metaStore;

    private PurRepository purRepository;

    private IUnifiedRepository pur;

    private RepositoryFile namespacesFolder;

    @Test
    public void testDataNodeConversion() throws Exception {
        IMetaStoreElement expected = new MemoryMetaStoreElement();
        expected.setName("parent");
        expected.addChild(new MemoryMetaStoreAttribute("date", new Date()));
        expected.addChild(new MemoryMetaStoreAttribute("long", 32L));
        expected.addChild(new MemoryMetaStoreAttribute("double", 3.2));
        expected.addChild(new MemoryMetaStoreAttribute("string", "value"));
        MemoryMetaStoreAttribute collection = new MemoryMetaStoreAttribute("collection", "collection-value");
        for (int i = 0; i < 10; i++) {
            collection.addChild(new MemoryMetaStoreAttribute(("key-" + i), ("value-" + i)));
        }
        expected.addChild(collection);
        DataNode dataNode = new DataNode("test");
        metaStore.elementToDataNode(expected, dataNode);
        IMetaStoreElement verify = new MemoryMetaStoreElement();
        metaStore.dataNodeToElement(dataNode, verify);
        Assert.assertEquals(expected.getName(), verify.getName());
        PurRepositoryMetaStoreTest.validate(expected, verify);
    }
}

