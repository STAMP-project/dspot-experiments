/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall.mysql;


import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;


/**
 * SQLServerWallTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see 
 */
public class MySqlWallTest_comment extends TestCase {
    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCommentAllow(true);
        TestCase.assertTrue(// 
        provider.checkValid("SELECT * FROM T WHERE FID = ? #AND 1"));
        TestCase.assertEquals(1, provider.getTableStats().size());
    }

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCommentAllow(false);
        TestCase.assertTrue(provider.checkValid("/* this is comment */ SELECT id FROM t "));
        TestCase.assertTrue(provider.checkValid("-- this is comment \n SELECT * FROM t"));
        TestCase.assertTrue(provider.checkValid("#this is comment \n SELECT * FROM t"));
        TestCase.assertTrue(provider.checkValid("/*!40101fff*/ select * from t"));
        TestCase.assertFalse(provider.checkValid("select * from t/*!40101fff*/"));
        TestCase.assertTrue(provider.checkValid("SELECT * FROM t where a=1 #this is comment \n and b=1"));
        TestCase.assertTrue(provider.checkValid("SELECT * FROM t where a=1 -- this is comment \n and c=1"));
        TestCase.assertTrue(provider.checkValid("SELECT * FROM t where a=1 /* this is comment */ and d=1"));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 #and c=1 \n and e=1"));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 -- AND c=1 \n and f=1"));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 /* and c=1 */ and g=1"));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 #and c=1 "));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 -- and c=1"));
        TestCase.assertFalse(provider.checkValid("SELECT * FROM t where a=1 /* and c=1 */"));
    }
}

