/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.intercept.method.aopalliance;


import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.security.ITargetObject;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;


/**
 * Tests for SEC-428 (and SEC-1204).
 *
 * @author Luke Taylor
 * @author Ben Alex
 */
public class MethodSecurityInterceptorWithAopConfigTests {
    static final String AUTH_PROVIDER_XML = "<authentication-manager>" + (((((("    <authentication-provider>" + "        <user-service>") + "            <user name='bob' password='bobspassword' authorities='ROLE_USER,ROLE_ADMIN' />") + "            <user name='bill' password='billspassword' authorities='ROLE_USER' />") + "        </user-service>") + "    </authentication-provider>") + "</authentication-manager>");

    static final String ACCESS_MANAGER_XML = "<b:bean id='accessDecisionManager' class='org.springframework.security.access.vote.AffirmativeBased'>" + ((("   <b:constructor-arg>" + "       <b:list><b:bean class='org.springframework.security.access.vote.RoleVoter'/></b:list>") + "   </b:constructor-arg>") + "</b:bean>");

    static final String TARGET_BEAN_AND_INTERCEPTOR = "<b:bean id='target' class='org.springframework.security.TargetObject'/>" + (((((((("<b:bean id='securityInterceptor' class='org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor' autowire='byType' >" + "     <b:property name='securityMetadataSource'>") + "         <method-security-metadata-source>") + "             <protect method='org.springframework.security.ITargetObject.makeLower*' access='ROLE_A'/>") + "             <protect method='org.springframework.security.ITargetObject.makeUpper*' access='ROLE_A'/>") + "             <protect method='org.springframework.security.ITargetObject.computeHashCode*' access='ROLE_B'/>") + "         </method-security-metadata-source>") + "     </b:property>") + "</b:bean>");

    private AbstractXmlApplicationContext appContext;

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void securityInterceptorIsAppliedWhenUsedWithAopConfig() {
        setContext((((("<aop:config>" + (("     <aop:pointcut id='targetMethods' expression='execution(* org.springframework.security.TargetObject.*(..))'/>" + "     <aop:advisor advice-ref='securityInterceptor' pointcut-ref='targetMethods' />") + "</aop:config>")) + (MethodSecurityInterceptorWithAopConfigTests.TARGET_BEAN_AND_INTERCEPTOR)) + (MethodSecurityInterceptorWithAopConfigTests.AUTH_PROVIDER_XML)) + (MethodSecurityInterceptorWithAopConfigTests.ACCESS_MANAGER_XML)));
        ITargetObject target = ((ITargetObject) (appContext.getBean("target")));
        // Check both against interface and class
        try {
            target.makeLowerCase("TEST");
            fail("AuthenticationCredentialsNotFoundException expected");
        } catch (AuthenticationCredentialsNotFoundException expected) {
        }
        target.makeUpperCase("test");
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void securityInterceptorIsAppliedWhenUsedWithBeanNameAutoProxyCreator() {
        setContext((((("<b:bean id='autoProxyCreator' class='org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator'>" + ((((((((((("   <b:property name='interceptorNames'>" + "       <b:list>") + "          <b:value>securityInterceptor</b:value>") + "       </b:list>") + "   </b:property>") + "   <b:property name='beanNames'>") + "       <b:list>") + "          <b:value>target</b:value>") + "       </b:list>") + "   </b:property>") + "   <b:property name='proxyTargetClass' value='false'/>") + "</b:bean>")) + (MethodSecurityInterceptorWithAopConfigTests.TARGET_BEAN_AND_INTERCEPTOR)) + (MethodSecurityInterceptorWithAopConfigTests.AUTH_PROVIDER_XML)) + (MethodSecurityInterceptorWithAopConfigTests.ACCESS_MANAGER_XML)));
        ITargetObject target = ((ITargetObject) (appContext.getBean("target")));
        try {
            target.makeLowerCase("TEST");
            fail("AuthenticationCredentialsNotFoundException expected");
        } catch (AuthenticationCredentialsNotFoundException expected) {
        }
        target.makeUpperCase("test");
    }
}

