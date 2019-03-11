/**
 * Copyright 2019 http://www.hswebframework.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hswebframework.web.authorization.starter;


import DataAccessConfig.DefaultType.OWN_CREATED;
import Permission.ACTION_QUERY;
import java.util.Arrays;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.entity.authorization.ActionEntity;
import org.hswebframework.web.entity.authorization.DataAccessEntity;
import org.hswebframework.web.entity.authorization.PermissionEntity;
import org.hswebframework.web.service.authorization.PermissionService;
import org.hswebframework.web.tests.SimpleWebApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * TODO ????
 *
 * @author zhouhao
 */
public class PermissionTests extends SimpleWebApplicationTests {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Test
    public void testCRUD() throws Exception {
        Assert.assertTrue(sqlExecutor.tableExists("s_permission"));
        DataAccessEntity dataAccessEntity = new DataAccessEntity();
        dataAccessEntity.setType(OWN_CREATED);
        dataAccessEntity.setAction(ACTION_QUERY);
        dataAccessEntity.setDescribe("???????????");
        PermissionEntity entity = permissionService.createEntity();
        entity.setStatus(((byte) (1)));
        entity.setName("??");
        entity.setActions(Arrays.asList(new ActionEntity("C")));
        entity.setId("test");
        String id = permissionService.insert(entity);
        Assert.assertNotNull(id);
        PermissionEntity data = permissionService.selectByPk("test");
        Assert.assertEquals(data.getId(), entity.getId());
        Assert.assertEquals(data.getName(), entity.getName());
        Assert.assertEquals(data.getStatus(), entity.getStatus());
        data.setName("????");
        permissionService.updateByPk(data.getId(), data);
        PermissionEntity data2 = permissionService.selectByPk("test");
        Assert.assertEquals(data2.getName(), data.getName());
        permissionService.deleteByPk("test");
        Assert.assertTrue(((permissionService.selectByPk("test")) == null));
    }
}

