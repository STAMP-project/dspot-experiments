/**
 * Copyright (C) 2018 the original author or authors.
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
package org.springframework.cloud.alicloud.acm.endpoint;


import com.alibaba.edas.acm.ConfigService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.MethodProxy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.alicloud.acm.AcmAutoConfiguration;
import org.springframework.cloud.alicloud.acm.AcmPropertySourceRepository;
import org.springframework.cloud.alicloud.acm.refresh.AcmRefreshHistory;
import org.springframework.cloud.alicloud.context.acm.AcmContextBootstrapConfiguration;
import org.springframework.cloud.alicloud.context.acm.AcmProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author xiaojing
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ ConfigService.class })
@SpringBootTest(classes = AcmEndpointTests.TestConfig.class, properties = { "spring.application.name=test-name", "spring.cloud.alicloud.acm.server-list=127.0.0.1", "spring.cloud.alicloud.acm.server-port=8848", "spring.cloud.alicloud.acm.file-extension=properties" }, webEnvironment = NONE)
public class AcmEndpointTests {
    static {
        try {
            Method method = PowerMockito.method(ConfigService.class, "getConfig", String.class, String.class, long.class);
            MethodProxy.proxy(method, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (("test-name.properties".equals(args[0])) && ("DEFAULT_GROUP".equals(args[1]))) {
                        return "user.name=hello\nuser.age=12";
                    }
                    return "";
                }
            });
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @Autowired
    private AcmProperties properties;

    @Autowired
    private AcmRefreshHistory refreshHistory;

    @Autowired
    private AcmPropertySourceRepository propertySourceRepository;

    @Autowired
    private AcmPropertySourceRepository acmPropertySourceRepository;

    @Test
    public void contextLoads() throws Exception {
        checkoutEndpoint();
        checkoutAcmHealthIndicator();
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration({ AcmEndpointAutoConfiguration.class, AcmAutoConfiguration.class, AcmContextBootstrapConfiguration.class })
    public static class TestConfig {}
}

