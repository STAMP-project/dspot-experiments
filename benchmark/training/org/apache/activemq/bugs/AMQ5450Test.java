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
package org.apache.activemq.bugs;


import java.util.HashMap;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ5450Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ5450Test.class);

    private static final int maxFileLength = (1024 * 1024) * 32;

    private static final String POSTFIX_DESTINATION_NAME = ".dlq";

    private static final String DESTINATION_NAME = "test" + (AMQ5450Test.POSTFIX_DESTINATION_NAME);

    private static final String DESTINATION_NAME_2 = "2.test" + (AMQ5450Test.POSTFIX_DESTINATION_NAME);

    private static final String DESTINATION_NAME_3 = "3.2.test" + (AMQ5450Test.POSTFIX_DESTINATION_NAME);

    private static final String[] DESTS = new String[]{ AMQ5450Test.DESTINATION_NAME, AMQ5450Test.DESTINATION_NAME_2, AMQ5450Test.DESTINATION_NAME_3, AMQ5450Test.DESTINATION_NAME, AMQ5450Test.DESTINATION_NAME };

    BrokerService broker;

    private HashMap<Object, PersistenceAdapter> adapters = new HashMap();

    @Test
    public void testPostFixMatch() throws Exception {
        doTestPostFixMatch(false);
    }

    @Test
    public void testPostFixCompositeMatch() throws Exception {
        doTestPostFixMatch(true);
    }
}

