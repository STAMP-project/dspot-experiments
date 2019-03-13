package org.baeldung.customscope;


import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TenantScopeIntegrationTest {
    @Test
    public final void whenRegisterScopeAndBeans_thenContextContainsFooAndBar() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        try {
            ctx.register(TenantScopeConfig.class);
            ctx.register(TenantBeansConfig.class);
            ctx.refresh();
            TenantBean foo = ((TenantBean) (ctx.getBean("foo", TenantBean.class)));
            foo.sayHello();
            TenantBean bar = ((TenantBean) (ctx.getBean("bar", TenantBean.class)));
            bar.sayHello();
            Map<String, TenantBean> foos = ctx.getBeansOfType(TenantBean.class);
            Assert.assertThat(foo, CoreMatchers.not(CoreMatchers.equalTo(bar)));
            Assert.assertThat(foos.size(), CoreMatchers.equalTo(2));
            Assert.assertTrue(foos.containsValue(foo));
            Assert.assertTrue(foos.containsValue(bar));
            BeanDefinition fooDefinition = ctx.getBeanDefinition("foo");
            BeanDefinition barDefinition = ctx.getBeanDefinition("bar");
            Assert.assertThat(fooDefinition.getScope(), CoreMatchers.equalTo("tenant"));
            Assert.assertThat(barDefinition.getScope(), CoreMatchers.equalTo("tenant"));
        } finally {
            ctx.close();
        }
    }

    @Test
    public final void whenComponentScan_thenContextContainsFooAndBar() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        try {
            ctx.scan("org.baeldung.customscope");
            ctx.refresh();
            TenantBean foo = ((TenantBean) (ctx.getBean("foo", TenantBean.class)));
            foo.sayHello();
            TenantBean bar = ((TenantBean) (ctx.getBean("bar", TenantBean.class)));
            bar.sayHello();
            Map<String, TenantBean> foos = ctx.getBeansOfType(TenantBean.class);
            Assert.assertThat(foo, CoreMatchers.not(CoreMatchers.equalTo(bar)));
            Assert.assertThat(foos.size(), CoreMatchers.equalTo(2));
            Assert.assertTrue(foos.containsValue(foo));
            Assert.assertTrue(foos.containsValue(bar));
            BeanDefinition fooDefinition = ctx.getBeanDefinition("foo");
            BeanDefinition barDefinition = ctx.getBeanDefinition("bar");
            Assert.assertThat(fooDefinition.getScope(), CoreMatchers.equalTo("tenant"));
            Assert.assertThat(barDefinition.getScope(), CoreMatchers.equalTo("tenant"));
        } finally {
            ctx.close();
        }
    }
}

