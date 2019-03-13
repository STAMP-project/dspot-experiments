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
package org.apache.camel.dataformat.beanio;


import java.util.List;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class BeanIOSplitterCustomBeanReaderErrorHandlerTest extends CamelTestSupport {
    // START SNIPPET: e2
    private static final String FIXED_DATA = (((("Joe,Smith,Developer,75000,10012009" + (LS)) + "Jane,Doe,Architect,80000,01152008") + (LS)) + "Jon,Anderson,Manager,85000,03182007") + (LS);

    // END SNIPPET: e2
    private static final String FIXED_FAIL_DATA = (((("Joe,Smith,Developer,75000,10012009" + (LS)) + "Jane,Doe,Architect,80000,01152008") + (LS)) + "Jon,Anderson,Manager,XXX,03182007") + (LS);

    @Test
    public void testSplit() throws Exception {
        List<Employee> employees = getEmployees();
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedBodiesReceived(employees);
        template.sendBody("direct:unmarshal", BeanIOSplitterCustomBeanReaderErrorHandlerTest.FIXED_DATA);
        mock.assertIsSatisfied();
    }

    @Test
    public void testSplitFail() throws Exception {
        // there should be 1 splitted that failed we get also
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedMessageCount(3);
        mock.message(0).body().isInstanceOf(Employee.class);
        mock.message(1).body().isInstanceOf(Employee.class);
        mock.message(2).body().isInstanceOf(MyErrorDto.class);
        template.sendBody("direct:unmarshal", BeanIOSplitterCustomBeanReaderErrorHandlerTest.FIXED_FAIL_DATA);
        mock.assertIsSatisfied();
        assertEquals("employee", mock.getReceivedExchanges().get(2).getIn().getBody(MyErrorDto.class).getRecord());
    }
}

