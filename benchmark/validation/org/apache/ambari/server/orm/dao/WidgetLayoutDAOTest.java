/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.orm.dao;


import com.google.inject.Injector;
import java.util.List;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.WidgetLayoutEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * WidgetLayoutDAO unit tests.
 */
public class WidgetLayoutDAOTest {
    private static Injector injector;

    private WidgetLayoutDAO widgetLayoutDAO;

    private WidgetDAO widgetDAO;

    OrmTestHelper helper;

    Long clusterId;

    @Test
    public void testFindByCluster() {
        createRecords();
        Assert.assertEquals(0, widgetLayoutDAO.findByCluster(99999).size());
        Assert.assertEquals(2, widgetLayoutDAO.findByCluster(clusterId).size());
    }

    @Test
    public void testFindBySectionName() {
        createRecords();
        Assert.assertEquals(0, widgetLayoutDAO.findBySectionName("non existing").size());
        List<WidgetLayoutEntity> widgetLayoutEntityList1 = widgetLayoutDAO.findBySectionName("section0");
        List<WidgetLayoutEntity> widgetLayoutEntityList2 = widgetLayoutDAO.findBySectionName("section1");
        Assert.assertEquals(1, widgetLayoutEntityList1.size());
        Assert.assertEquals(1, widgetLayoutEntityList2.size());
        Assert.assertEquals(3, widgetLayoutEntityList1.get(0).getListWidgetLayoutUserWidgetEntity().size());
    }

    @Test
    public void testFindAll() {
        createRecords();
        Assert.assertEquals(2, widgetLayoutDAO.findAll().size());
    }
}

