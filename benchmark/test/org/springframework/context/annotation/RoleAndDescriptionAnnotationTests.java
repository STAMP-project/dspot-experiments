/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.context.annotation;


import BeanDefinition.ROLE_APPLICATION;
import BeanDefinition.ROLE_INFRASTRUCTURE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.role.ComponentWithRole;
import org.springframework.context.annotation.role.ComponentWithoutRole;


/**
 * Tests the use of the @Role and @Description annotation on @Bean methods and @Component classes.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class RoleAndDescriptionAnnotationTests {
    @Test
    public void onBeanMethod() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(RoleAndDescriptionAnnotationTests.Config.class);
        ctx.refresh();
        Assert.assertThat("Expected bean to have ROLE_APPLICATION", ctx.getBeanDefinition("foo").getRole(), CoreMatchers.is(ROLE_APPLICATION));
        Assert.assertThat(ctx.getBeanDefinition("foo").getDescription(), CoreMatchers.is(((Object) (null))));
        Assert.assertThat("Expected bean to have ROLE_INFRASTRUCTURE", ctx.getBeanDefinition("bar").getRole(), CoreMatchers.is(ROLE_INFRASTRUCTURE));
        Assert.assertThat(ctx.getBeanDefinition("bar").getDescription(), CoreMatchers.is("A Bean method with a role"));
    }

    @Test
    public void onComponentClass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentWithoutRole.class, ComponentWithRole.class);
        ctx.refresh();
        Assert.assertThat("Expected bean to have ROLE_APPLICATION", ctx.getBeanDefinition("componentWithoutRole").getRole(), CoreMatchers.is(ROLE_APPLICATION));
        Assert.assertThat(ctx.getBeanDefinition("componentWithoutRole").getDescription(), CoreMatchers.is(((Object) (null))));
        Assert.assertThat("Expected bean to have ROLE_INFRASTRUCTURE", ctx.getBeanDefinition("componentWithRole").getRole(), CoreMatchers.is(ROLE_INFRASTRUCTURE));
        Assert.assertThat(ctx.getBeanDefinition("componentWithRole").getDescription(), CoreMatchers.is("A Component with a role"));
    }

    @Test
    public void viaComponentScanning() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("org.springframework.context.annotation.role");
        ctx.refresh();
        Assert.assertThat("Expected bean to have ROLE_APPLICATION", ctx.getBeanDefinition("componentWithoutRole").getRole(), CoreMatchers.is(ROLE_APPLICATION));
        Assert.assertThat(ctx.getBeanDefinition("componentWithoutRole").getDescription(), CoreMatchers.is(((Object) (null))));
        Assert.assertThat("Expected bean to have ROLE_INFRASTRUCTURE", ctx.getBeanDefinition("componentWithRole").getRole(), CoreMatchers.is(ROLE_INFRASTRUCTURE));
        Assert.assertThat(ctx.getBeanDefinition("componentWithRole").getDescription(), CoreMatchers.is("A Component with a role"));
    }

    @Configuration
    static class Config {
        @Bean
        public String foo() {
            return "foo";
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        @Description("A Bean method with a role")
        public String bar() {
            return "bar";
        }
    }
}

