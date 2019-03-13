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
package org.apache.camel.component.bean;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Header;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Test inheritance of parameter binding annotations from superclasses and
 * interfaces.
 */
public class BeanWithAnnotationInheritedTest extends ContextTestSupport {
    @Test
    public void testWithAnnotationsFromOneInterface() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("x1y1");
        template.requestBody("direct:in1", "whatever");
        mock.assertIsSatisfied();
    }

    @Test
    public void testWithAnnotationsFromTwoInterfaces() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("x2y2");
        template.requestBody("direct:in2", "whatever");
        mock.assertIsSatisfied();
    }

    @Test
    public void testWithAnnotationsFromSuperclassAndInterface() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("x3y3");
        template.requestBody("direct:in3", "whatever");
        mock.assertIsSatisfied();
    }

    @Test
    public void testWithAnnotationsFromImplementationClassAndInterface() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("x4y4");
        template.requestBody("direct:in4", "whatever");
        mock.assertIsSatisfied();
    }

    @Test
    public void testWithAnnotationsFromOneInterfaceInheritedByProxy() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("x5y5");
        template.requestBody("direct:in5", "whatever");
        mock.assertIsSatisfied();
    }

    private interface I1 {
        String m1(@Header("foo")
        String h1, @Header("bar")
        String h2);

        String m2(@Header("foo")
        String h1, String h2);
    }

    private interface I2 {
        String m2(String h1, @Header("bar")
        String h2);

        String m3(@Header("foo")
        String h1, String h2);

        String m4(@Header("foo")
        String h1, String h2);
    }

    private abstract static class A implements BeanWithAnnotationInheritedTest.I2 {
        public String m3(String h1, @Header("bar")
        String h2) {
            return h1 + h2;
        }
    }

    private static class B extends BeanWithAnnotationInheritedTest.A implements BeanWithAnnotationInheritedTest.I1 {
        public String m1(String h1, String h2) {
            return h1 + h2;
        }

        public String m2(String h1, String h2) {
            return h1 + h2;
        }

        public String m4(String h1, @Header("bar")
        String h2) {
            return h1 + h2;
        }
    }
}

