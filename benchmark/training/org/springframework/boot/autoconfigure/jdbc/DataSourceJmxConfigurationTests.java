/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.jdbc;


import com.zaxxer.hikari.HikariDataSource;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.UUID;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DelegatingDataSource;


/**
 * Tests for {@link DataSourceJmxConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Tadaya Tsuyukubo
 */
public class DataSourceJmxConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withPropertyValues((("spring.datasource.url=" + "jdbc:hsqldb:mem:test-") + (UUID.randomUUID()))).withConfiguration(AutoConfigurations.of(JmxAutoConfiguration.class, DataSourceAutoConfiguration.class));

    @Test
    public void hikariAutoConfiguredCanUseRegisterMBeans() {
        String poolName = UUID.randomUUID().toString();
        this.contextRunner.withPropertyValues("spring.jmx.enabled=true", ("spring.datasource.type=" + (HikariDataSource.class.getName())), ("spring.datasource.name=" + poolName), "spring.datasource.hikari.register-mbeans=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).isRegisterMbeans()).isTrue();
            MBeanServer mBeanServer = context.getBean(.class);
            validateHikariMBeansRegistration(mBeanServer, poolName, true);
        });
    }

    @Test
    public void hikariAutoConfiguredWithoutDataSourceName() throws MalformedObjectNameException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> existingInstances = mBeanServer.queryMBeans(new ObjectName("com.zaxxer.hikari:type=*"), null);
        this.contextRunner.withPropertyValues(("spring.datasource.type=" + (HikariDataSource.class.getName())), "spring.datasource.hikari.register-mbeans=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).isRegisterMbeans()).isTrue();
            // We can't rely on the number of MBeans so we're checking that the
            // pool and pool
            // config MBeans were registered
            assertThat(mBeanServer.queryMBeans(new ObjectName("com.zaxxer.hikari:type=*"), null).size()).isEqualTo(((existingInstances.size()) + 2));
        });
    }

    @Test
    public void hikariAutoConfiguredUsesJmsFlag() {
        String poolName = UUID.randomUUID().toString();
        this.contextRunner.withPropertyValues(("spring.datasource.type=" + (HikariDataSource.class.getName())), "spring.jmx.enabled=false", ("spring.datasource.name=" + poolName), "spring.datasource.hikari.register-mbeans=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).isRegisterMbeans()).isTrue();
            // Hikari can still register mBeans
            validateHikariMBeansRegistration(ManagementFactory.getPlatformMBeanServer(), poolName, true);
        });
    }

    @Test
    public void hikariProxiedCanUseRegisterMBeans() {
        String poolName = UUID.randomUUID().toString();
        this.contextRunner.withUserConfiguration(DataSourceJmxConfigurationTests.DataSourceProxyConfiguration.class).withPropertyValues("spring.jmx.enabled=true", ("spring.datasource.type=" + (HikariDataSource.class.getName())), ("spring.datasource.name=" + poolName), "spring.datasource.hikari.register-mbeans=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            HikariDataSource hikariDataSource = context.getBean(.class).unwrap(.class);
            assertThat(hikariDataSource.isRegisterMbeans()).isTrue();
            MBeanServer mBeanServer = context.getBean(.class);
            validateHikariMBeansRegistration(mBeanServer, poolName, true);
        });
    }

    @Test
    public void tomcatDoesNotExposeMBeanPoolByDefault() {
        this.contextRunner.withPropertyValues(("spring.datasource.type=" + (DataSource.class.getName()))).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void tomcatAutoConfiguredCanExposeMBeanPool() {
        this.contextRunner.withPropertyValues(("spring.datasource.type=" + (DataSource.class.getName())), "spring.datasource.jmx-enabled=true").run(( context) -> {
            assertThat(context).hasBean("dataSourceMBean");
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).createPool().getJmxPool()).isSameAs(context.getBean(.class));
        });
    }

    @Test
    public void tomcatProxiedCanExposeMBeanPool() {
        this.contextRunner.withUserConfiguration(DataSourceJmxConfigurationTests.DataSourceProxyConfiguration.class).withPropertyValues(("spring.datasource.type=" + (DataSource.class.getName())), "spring.datasource.jmx-enabled=true").run(( context) -> {
            assertThat(context).hasBean("dataSourceMBean");
            assertThat(context).getBean("dataSourceMBean").isInstanceOf(.class);
        });
    }

    @Test
    public void tomcatDelegateCanExposeMBeanPool() {
        this.contextRunner.withUserConfiguration(DataSourceJmxConfigurationTests.DataSourceDelegateConfiguration.class).withPropertyValues(("spring.datasource.type=" + (DataSource.class.getName())), "spring.datasource.jmx-enabled=true").run(( context) -> {
            assertThat(context).hasBean("dataSourceMBean");
            assertThat(context).getBean("dataSourceMBean").isInstanceOf(.class);
        });
    }

    @Configuration
    static class DataSourceProxyConfiguration {
        @Bean
        public static DataSourceJmxConfigurationTests.DataSourceBeanPostProcessor dataSourceBeanPostProcessor() {
            return new DataSourceJmxConfigurationTests.DataSourceBeanPostProcessor();
        }
    }

    private static class DataSourceBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof javax.sql.DataSource) {
                return new ProxyFactory(bean).getProxy();
            }
            return bean;
        }
    }

    @Configuration
    static class DataSourceDelegateConfiguration {
        @Bean
        public static DataSourceJmxConfigurationTests.DataSourceBeanPostProcessor dataSourceBeanPostProcessor() {
            return new DataSourceJmxConfigurationTests.DataSourceBeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    if (bean instanceof javax.sql.DataSource) {
                        return new DelegatingDataSource(((javax.sql.DataSource) (bean)));
                    }
                    return bean;
                }
            };
        }
    }
}

