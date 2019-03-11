/**
 * * Copyright 2019 http://www.hswebframework.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.hswebframework.web.starter;


import java.sql.Connection;
import java.util.Collections;
import org.hswebframework.ezorm.rdb.RDBDatabase;
import org.hswebframework.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.starter.init.SystemInitialize;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO ????
 *
 * @author zhouhao
 */
public class InstallTests {
    SqlExecutor sqlExecutor;

    RDBDatabase database;

    Connection connection;

    @Test
    public void testVersion() {
        SystemVersion version = new SystemVersion();
        version.setVersion("3.0.0");
        SystemVersion version2 = new SystemVersion();
        version2.setVersion("3.0.1");
        SystemVersion version4 = new SystemVersion();
        version4.setVersion("3.0.2");
        Assert.assertEquals(version.compareTo(version2), (-1));
        Assert.assertEquals(version.compareTo(version4), (-1));
    }

    @Test
    public void testInstall() throws Exception {
        SystemVersion version = new SystemVersion();
        version.setName("test");
        version.setVersion("3.0.0");
        SystemInitialize systemInitialize = new SystemInitialize(sqlExecutor, database, version);
        systemInitialize.setExcludeTables(Collections.singletonList("s_user_test"));
        systemInitialize.init();
        systemInitialize.install();
        // List systems = database.getTable("s_system").createQuery().list();
        // System.out.println(JSON.toJSONString(systems, SerializerFeature.PrettyFormat));
    }
}

