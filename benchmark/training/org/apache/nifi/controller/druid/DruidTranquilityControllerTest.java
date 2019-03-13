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
package org.apache.nifi.controller.druid;


import DruidTranquilityController.AGGREGATOR_JSON;
import DruidTranquilityController.DATASOURCE;
import DruidTranquilityController.DIMENSIONS_LIST;
import DruidTranquilityController.ZOOKEEPER_CONNECTION_STRING;
import com.metamx.tranquility.tranquilizer.Tranquilizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.api.druid.DruidTranquilityService;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.Mockito;


public class DruidTranquilityControllerTest {
    private TestRunner runner;

    private DruidTranquilityControllerTest.MockDruidTranquilityController service;

    @Test
    public void testValid() {
        runner.assertNotValid(service);
        runner.setProperty(service, DATASOURCE, "test");
        runner.assertNotValid(service);
        runner.setProperty(service, ZOOKEEPER_CONNECTION_STRING, "localhost:2181");
        runner.assertNotValid(service);
        runner.setProperty(service, AGGREGATOR_JSON, "[{\"type\": \"count\", \"name\": \"count\"}]");
        runner.assertNotValid(service);
        runner.setProperty(service, DIMENSIONS_LIST, "dim1,dim2");
        runner.assertValid(service);
    }

    public static class MockDruidTranquilityController extends DruidTranquilityController {
        Tranquilizer t = Mockito.mock(Tranquilizer.class);

        CuratorFramework c = Mockito.mock(CuratorFramework.class);

        @Override
        public Tranquilizer getTranquilizer() {
            return t;
        }

        @Override
        CuratorFramework getCurator(String zkConnectString) {
            return c;
        }
    }

    public static class TestControllerServiceProcessor extends AbstractProcessor {
        static final PropertyDescriptor CLIENT_SERVICE = new PropertyDescriptor.Builder().name("Client Service").description("DruidTranquilityService").identifiesControllerService(DruidTranquilityService.class).required(true).build();

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        }

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
            propertyDescriptors.add(DruidTranquilityControllerTest.TestControllerServiceProcessor.CLIENT_SERVICE);
            return propertyDescriptors;
        }
    }
}

