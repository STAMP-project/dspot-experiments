/**
 * Copyright 2009-2014 the original author or authors.
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
package org.springframework.batch.core.scope;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StepScopePerformanceTests implements ApplicationContextAware {
    private Log logger = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    @Test
    public void testVanilla() throws Exception {
        int count = doTest("vanilla", "vanilla");
        logger.info(("Item count: " + count));
    }

    @Test
    public void testProxied() throws Exception {
        int count = doTest("proxied", "proxied");
        logger.info(("Item count: " + count));
    }
}

