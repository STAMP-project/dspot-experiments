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
package com.alibaba.csp.sentinel.adapter.servletmethod;


import SpringBootTest.WebEnvironment;
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
 * @author Roger Law
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CommonFilterMethodTest {
    private static final String HELLO_STR = "Hello!";

    private static final String HELLO_POST_STR = "Hello Post!";

    private static final String GET = "GET";

    private static final String POST = "POST";

    private static final String COLON = ":";

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCommonFilterMiscellaneous() throws Exception {
        String url = "/hello";
        this.mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string(CommonFilterMethodTest.HELLO_STR));
        ClusterNode cnGet = ClusterBuilderSlot.getClusterNode((((CommonFilterMethodTest.GET) + (CommonFilterMethodTest.COLON)) + url));
        Assert.assertNotNull(cnGet);
        Assert.assertEquals(1, cnGet.passQps());
        ClusterNode cnPost = ClusterBuilderSlot.getClusterNode((((CommonFilterMethodTest.POST) + (CommonFilterMethodTest.COLON)) + url));
        Assert.assertNull(cnPost);
        this.mvc.perform(post(url)).andExpect(status().isOk()).andExpect(content().string(CommonFilterMethodTest.HELLO_POST_STR));
        cnPost = ClusterBuilderSlot.getClusterNode((((CommonFilterMethodTest.POST) + (CommonFilterMethodTest.COLON)) + url));
        Assert.assertNotNull(cnPost);
        Assert.assertEquals(1, cnPost.passQps());
        testCommonBlockAndRedirectBlockPage(url, cnGet, cnPost);
    }
}

