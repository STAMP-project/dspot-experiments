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
package com.alibaba.csp.sentinel.adapter.servlet;


import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Eric Zhao
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
public class CommonFilterTest {
    private static final String HELLO_STR = "Hello!";

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCommonFilterMiscellaneous() throws Exception {
        String url = "/hello";
        this.mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string(CommonFilterTest.HELLO_STR));
        ClusterNode cn = ClusterBuilderSlot.getClusterNode(url);
        Assert.assertNotNull(cn);
        Assert.assertEquals(1, cn.passQps());
        testCommonBlockAndRedirectBlockPage(url, cn);
        // Test for url cleaner.
        testUrlCleaner();
        testCustomOriginParser();
    }
}

