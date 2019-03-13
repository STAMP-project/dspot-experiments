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
package org.apache.camel.example.cdi.osgi;


import ServiceStatus.Started;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.api.management.mbean.ManagedRouteMBean;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CdiOsgiIT {
    @Inject
    private CamelContext context;

    @Inject
    private SessionFactory sessionFactory;

    @Test
    public void testRouteStatus() {
        Assert.assertThat("Route status is incorrect!", context.getRouteController().getRouteStatus("consumer-route"), IsEqual.equalTo(Started));
    }

    @Test
    public void testExchangesCompleted() throws Exception {
        ManagedRouteMBean route = context.getManagedRoute(context.getRoute("consumer-route").getId(), ManagedRouteMBean.class);
        Assert.assertThat("Number of exchanges completed is incorrect!", route.getExchangesCompleted(), IsEqual.equalTo(1L));
    }

    @Test
    public void testExecuteCommands() throws Exception {
        Session session = sessionFactory.create(System.in, System.out, System.err);
        session.execute("camel:context-list");
        session.execute("camel:route-list");
        session.execute("camel:route-info consumer-route");
    }
}

