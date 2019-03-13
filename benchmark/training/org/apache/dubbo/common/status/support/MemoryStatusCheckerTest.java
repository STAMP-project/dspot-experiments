/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.status.support;


import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.status.Status;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class MemoryStatusCheckerTest {
    private static final Logger logger = LoggerFactory.getLogger(MemoryStatusCheckerTest.class);

    @Test
    public void test() throws Exception {
        MemoryStatusChecker statusChecker = new MemoryStatusChecker();
        Status status = statusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.anyOf(Matchers.is(Level.OK), Matchers.is(Level.WARN)));
        MemoryStatusCheckerTest.logger.info(("memory status level: " + (status.getLevel())));
        MemoryStatusCheckerTest.logger.info(("memory status message: " + (status.getMessage())));
    }
}

