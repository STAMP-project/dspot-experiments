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


import RepositoryObjectType.CLUSTER_SCHEMA;
import RepositoryObjectType.DATABASE;
import RepositoryObjectType.PARTITION_SCHEMA;
import RepositoryObjectType.SLAVE_SERVER;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.shared.SharedObjectInterface;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class PurRepository_SharedObjects_IT extends PurRepositoryTestBase {
    public PurRepository_SharedObjects_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void loadClusters() throws Exception {
        testLoadSharedObjects(PurRepository_SharedObjects_IT.cluster());
    }

    @Test
    public void loadDatabases() throws Exception {
        testLoadSharedObjects(PurRepository_SharedObjects_IT.database());
    }

    @Test
    public void loadSlaves() throws Exception {
        testLoadSharedObjects(PurRepository_SharedObjects_IT.slaveServer());
    }

    @Test
    public void loadPartitions() throws Exception {
        testLoadSharedObjects(PurRepository_SharedObjects_IT.partition());
    }

    @Test
    public void clusterIsRemovedFromCacheOnDelete() throws Exception {
        testElementIsRemovedFromCacheOnDelete(PurRepository_SharedObjects_IT.cluster(), new PurRepository_SharedObjects_IT.Remover() {
            @Override
            public void deleteFromRepository(RepositoryElementInterface element) throws KettleException {
                purRepository.deleteClusterSchema(element.getObjectId());
            }
        });
    }

    @Test
    public void databaseIsRemovedFromCacheOnDelete() throws Exception {
        testElementIsRemovedFromCacheOnDelete(PurRepository_SharedObjects_IT.database(), new PurRepository_SharedObjects_IT.Remover() {
            @Override
            public void deleteFromRepository(RepositoryElementInterface element) throws KettleException {
                purRepository.deleteDatabaseMeta(element.getName());
            }
        });
    }

    @Test
    public void slaveIsRemovedFromCacheOnDelete() throws Exception {
        testElementIsRemovedFromCacheOnDelete(PurRepository_SharedObjects_IT.slaveServer(), new PurRepository_SharedObjects_IT.Remover() {
            @Override
            public void deleteFromRepository(RepositoryElementInterface element) throws KettleException {
                purRepository.deleteSlave(element.getObjectId());
            }
        });
    }

    @Test
    public void partitionIsRemovedFromCacheOnDelete() throws Exception {
        testElementIsRemovedFromCacheOnDelete(PurRepository_SharedObjects_IT.partition(), new PurRepository_SharedObjects_IT.Remover() {
            @Override
            public void deleteFromRepository(RepositoryElementInterface element) throws KettleException {
                purRepository.deletePartitionSchema(element.getObjectId());
            }
        });
    }

    @Test
    public void loadAllShared() throws Exception {
        ClusterSchema cluster = PurRepository_SharedObjects_IT.cluster();
        DatabaseMeta database = PurRepository_SharedObjects_IT.database();
        SlaveServer slaveServer = PurRepository_SharedObjects_IT.slaveServer();
        PartitionSchema partition = PurRepository_SharedObjects_IT.partition();
        purRepository.save(cluster, null, null);
        purRepository.save(database, null, null);
        purRepository.save(slaveServer, null, null);
        purRepository.save(partition, null, null);
        Map<RepositoryObjectType, List<? extends SharedObjectInterface>> map = PurRepository_SharedObjects_IT.map();
        purRepository.readSharedObjects(map, CLUSTER_SCHEMA, DATABASE, SLAVE_SERVER, PARTITION_SCHEMA);
        RepositoryElementInterface[] saved = new RepositoryElementInterface[]{ cluster, database, slaveServer, partition };
        Assert.assertEquals(saved.length, map.size());
        for (RepositoryElementInterface sharedObject : saved) {
            List<? extends SharedObjectInterface> list = map.get(sharedObject.getRepositoryElementType());
            Assert.assertEquals(1, list.size());
            Assert.assertEquals(sharedObject, list.get(0));
        }
    }

    private interface Remover {
        void deleteFromRepository(RepositoryElementInterface element) throws KettleException;
    }
}

