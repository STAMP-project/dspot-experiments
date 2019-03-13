/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.myth.admin.service.log;


import SpringBootTest.WebEnvironment;
import org.dromara.myth.admin.page.CommonPager;
import org.dromara.myth.admin.page.PageParameter;
import org.dromara.myth.admin.query.ConditionQuery;
import org.dromara.myth.admin.service.LogService;
import org.dromara.myth.admin.vo.LogVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * The type Jdbc log service impl test.
 *
 * @author xiaoyu(Myth)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JdbcLogServiceImplTest {
    @Autowired
    private LogService logService;

    /**
     * List by page.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void listByPage() throws Exception {
        ConditionQuery query = new ConditionQuery();
        PageParameter pageParameter = new PageParameter(1, 10);
        query.setPageParameter(pageParameter);
        query.setApplicationName("account-service");
        final CommonPager<LogVO> pager = logService.listByPage(query);
        Assert.assertNotNull(pager.getDataList());
    }
}

