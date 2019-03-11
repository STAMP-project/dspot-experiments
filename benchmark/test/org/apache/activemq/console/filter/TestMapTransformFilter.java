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
package org.apache.activemq.console.filter;


import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.console.util.AmqMessagesUtil;


public class TestMapTransformFilter extends TestCase {
    private static final Object[][] testData = new Object[][]{ new Object[]{ new byte[]{ 1, 2, 3, 4 } }, new Object[]{ new int[]{ 1, 2, 3, 4 } }, new Object[]{ new long[]{ 1, 2, 3, 4 } }, new Object[]{ new double[]{ 1, 2, 3, 4 } }, new Object[]{ new float[]{ 1, 2, 3, 4 } }, new Object[]{ new char[]{ '1', '2', '3', '4' } }, new Object[]{ new Integer[]{ 1, 2, 3, 4 } }, new Object[]{ new Byte[]{ 1, 2, 3, 4 } }, new Object[]{ new Long[]{ 1L, 2L, 3L, 4L } }, new Object[]{ new Double[]{ 1.0, 2.0, 3.0, 4.0 } }, new Object[]{ new Float[]{ 1.0F, 2.0F, 3.0F, 4.0F } }, new Object[]{ new Character[]{ '1', '2', '3', '4' } }, new Object[]{ new String[]{ "abc", "def" } }, new Object[]{ new int[]{ 1 } }, new Object[]{ new int[]{  } }, new Object[]{ "abc" }, new Object[]{ ((byte) (1)) }, new Object[]{ ((int) (1)) }, new Object[]{ ((long) (1)) }, new Object[]{ ((double) (1.0)) }, new Object[]{ ((float) (1.0F)) }, new Object[]{ ((char) ('1')) } };

    public void testFetDisplayString() {
        MapTransformFilter filter = new MapTransformFilter(null);
        for (Object[] objectArray : TestMapTransformFilter.testData) {
            filter.getDisplayString(objectArray[0]);
        }
    }

    public void testOriginaDest() throws Exception {
        MapTransformFilter filter = new MapTransformFilter(null);
        ActiveMQMessage mqMessage = new ActiveMQMessage();
        mqMessage.setOriginalDestination(new ActiveMQQueue("O"));
        TestCase.assertTrue(filter.transformToMap(mqMessage).containsKey(((AmqMessagesUtil.JMS_MESSAGE_CUSTOM_PREFIX) + "OriginalDestination")));
    }
}

