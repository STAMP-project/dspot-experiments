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
package org.apache.hive.minikdc;


import java.io.File;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


public class TestMiniHiveKdc {
    private static File baseDir;

    private MiniHiveKdc miniHiveKdc;

    private HiveConf hiveConf;

    @Test
    public void testLogin() throws Exception {
        String servicePrinc = miniHiveKdc.getHiveServicePrincipal();
        Assert.assertNotNull(servicePrinc);
        miniHiveKdc.loginUser(servicePrinc);
        Assert.assertTrue(UserGroupInformation.isLoginKeytabBased());
        UserGroupInformation ugi = Utils.getUGI();
        Assert.assertEquals(MiniHiveKdc.HIVE_SERVICE_PRINCIPAL, ugi.getShortUserName());
    }
}

