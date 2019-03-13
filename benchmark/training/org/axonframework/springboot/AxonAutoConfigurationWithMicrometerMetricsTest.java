/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.springboot;


import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.axonframework.micrometer.GlobalMetricRegistry;
import org.axonframework.micrometer.MetricsConfigurerModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@ContextConfiguration
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, WebClientAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class })
@RunWith(SpringRunner.class)
public class AxonAutoConfigurationWithMicrometerMetricsTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private GlobalMetricRegistry globalMetricRegistry;

    @Autowired
    private MetricsConfigurerModule metricsConfigurerModule;

    @Autowired(required = false)
    private MetricRegistry dropwizardMetricRegistry;

    @Autowired(required = false)
    private GlobalMetricRegistry metricsModuleGlobalMetricRegistry;

    @Autowired(required = false)
    private MetricsConfigurerModule metricsModuleMetricsConfigurerModule;

    @Test
    public void testContextInitialization() {
        Assert.assertNotNull(applicationContext);
        Assert.assertNotNull(applicationContext.getBean(MeterRegistry.class));
        Assert.assertNotNull(meterRegistry);
        Assert.assertTrue(applicationContext.containsBean("globalMetricRegistry"));
        Assert.assertNotNull(applicationContext.getBean(GlobalMetricRegistry.class));
        Assert.assertEquals(GlobalMetricRegistry.class, globalMetricRegistry.getClass());
        Assert.assertTrue(applicationContext.containsBean("metricsConfigurerModule"));
        Assert.assertNotNull(applicationContext.getBean(MetricsConfigurerModule.class));
        Assert.assertEquals(MetricsConfigurerModule.class, metricsConfigurerModule.getClass());
        Assert.assertNull(dropwizardMetricRegistry);
        Assert.assertNull(metricsModuleGlobalMetricRegistry);
        Assert.assertNull(metricsModuleMetricsConfigurerModule);
    }
}

