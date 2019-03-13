/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.config.method;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.BusinessService;
import org.springframework.security.access.annotation.ExpressionProtectedBusinessServiceImpl;
import org.springframework.security.access.intercept.AfterInvocationProviderManager;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.prepost.PostInvocationAdviceProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.ConfigTestUtils;
import org.springframework.security.config.PostProcessedMockUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.util.FieldUtils;


/**
 *
 *
 * @author Ben Alex
 * @author Luke Taylor
 */
public class GlobalMethodSecurityBeanDefinitionParserTests {
    private final UsernamePasswordAuthenticationToken bob = new UsernamePasswordAuthenticationToken("bob", "bobspassword");

    private AbstractXmlApplicationContext appContext;

    private BusinessService target;

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void targetShouldPreventProtectedMethodInvocationWithNoContext() {
        loadContext();
        target.someUserMethod1();
    }

    @Test
    public void targetShouldAllowProtectedMethodInvocationWithCorrectRole() {
        loadContext();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "password");
        SecurityContextHolder.getContext().setAuthentication(token);
        target.someUserMethod1();
        // SEC-1213. Check the order
        Advisor[] advisors = getAdvisors();
        assertThat(advisors).hasSize(1);
        assertThat(getOrder()).isEqualTo(1001);
    }

    @Test(expected = AccessDeniedException.class)
    public void targetShouldPreventProtectedMethodInvocationWithIncorrectRole() {
        loadContext();
        TestingAuthenticationToken token = new TestingAuthenticationToken("Test", "Password", "ROLE_SOMEOTHERROLE");
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);
        target.someAdminMethod();
    }

    @Test
    public void doesntInterfereWithBeanPostProcessing() {
        setContext(("<b:bean id='myUserService' class='org.springframework.security.config.PostProcessedMockUserDetailsService'/>" + (((("<global-method-security />" + "<authentication-manager>") + "   <authentication-provider user-service-ref='myUserService'/>") + "</authentication-manager>") + "<b:bean id='beanPostProcessor' class='org.springframework.security.config.MockUserServiceBeanPostProcessor'/>")));
        PostProcessedMockUserDetailsService service = ((PostProcessedMockUserDetailsService) (appContext.getBean("myUserService")));
        assertThat(service.getPostProcessorWasHere()).isEqualTo("Hello from the post processor!");
    }

    @Test(expected = AccessDeniedException.class)
    public void worksWithAspectJAutoproxy() {
        setContext(("<global-method-security>" + ((((((("  <protect-pointcut expression='execution(* org.springframework.security.config.*Service.*(..))'" + "       access='ROLE_SOMETHING' />") + "</global-method-security>") + "<b:bean id='myUserService' class='org.springframework.security.config.PostProcessedMockUserDetailsService'/>") + "<aop:aspectj-autoproxy />") + "<authentication-manager>") + "   <authentication-provider user-service-ref='myUserService'/>") + "</authentication-manager>")));
        UserDetailsService service = ((UserDetailsService) (appContext.getBean("myUserService")));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password", AuthorityUtils.createAuthorityList("ROLE_SOMEOTHERROLE"));
        SecurityContextHolder.getContext().setAuthentication(token);
        service.loadUserByUsername("notused");
    }

    @Test
    public void supportsMethodArgumentsInPointcut() {
        setContext((("<b:bean id='target' class='org.springframework.security.access.annotation.BusinessServiceImpl'/>" + ((("<global-method-security>" + "   <protect-pointcut expression='execution(* org.springframework.security.access.annotation.BusinessService.someOther(String))' access='ROLE_ADMIN'/>") + "   <protect-pointcut expression='execution(* org.springframework.security.access.annotation.BusinessService.*(..))' access='ROLE_USER'/>") + "</global-method-security>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password"));
        target = ((BusinessService) (appContext.getBean("target")));
        // someOther(int) should not be matched by someOther(String), but should require
        // ROLE_USER
        target.someOther(0);
        try {
            // String version should required admin role
            target.someOther("somestring");
            fail("Expected AccessDeniedException");
        } catch (AccessDeniedException expected) {
        }
    }

    @Test
    public void supportsBooleanPointcutExpressions() {
        setContext((("<b:bean id='target' class='org.springframework.security.access.annotation.BusinessServiceImpl'/>" + ((((("<global-method-security>" + "   <protect-pointcut expression=") + "     'execution(* org.springframework.security.access.annotation.BusinessService.*(..)) ") + "       and not execution(* org.springframework.security.access.annotation.BusinessService.someOther(String)))' ") + "               access='ROLE_USER'/>") + "</global-method-security>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        target = ((BusinessService) (appContext.getBean("target")));
        // String method should not be protected
        target.someOther("somestring");
        // All others should require ROLE_USER
        try {
            target.someOther(0);
            fail("Expected AuthenticationCredentialsNotFoundException");
        } catch (AuthenticationCredentialsNotFoundException expected) {
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password"));
        target.someOther(0);
    }

    @Test(expected = BeanDefinitionParsingException.class)
    public void duplicateElementCausesError() {
        setContext(("<global-method-security />" + "<global-method-security />"));
    }

    // SEC-936
    @Test(expected = AccessDeniedException.class)
    public void worksWithoutTargetOrClass() {
        setContext((("<global-method-security secured-annotations='enabled'/>" + ((("<b:bean id='businessService' class='org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean'>" + "    <b:property name='serviceUrl' value='http://localhost:8080/SomeService'/>") + "    <b:property name='serviceInterface' value='org.springframework.security.access.annotation.BusinessService'/>") + "</b:bean>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Test", "Password", AuthorityUtils.createAuthorityList("ROLE_SOMEOTHERROLE"));
        SecurityContextHolder.getContext().setAuthentication(token);
        target = ((BusinessService) (appContext.getBean("businessService")));
        target.someUserMethod1();
    }

    // Expression configuration tests
    @SuppressWarnings("unchecked")
    @Test
    public void expressionVoterAndAfterInvocationProviderUseSameExpressionHandlerInstance() throws Exception {
        setContext(("<global-method-security pre-post-annotations='enabled'/>" + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        AffirmativeBased adm = ((AffirmativeBased) (appContext.getBeansOfType(AffirmativeBased.class).values().toArray()[0]));
        List voters = ((List) (FieldUtils.getFieldValue(adm, "decisionVoters")));
        PreInvocationAuthorizationAdviceVoter mev = ((PreInvocationAuthorizationAdviceVoter) (voters.get(0)));
        MethodSecurityMetadataSourceAdvisor msi = ((MethodSecurityMetadataSourceAdvisor) (appContext.getBeansOfType(MethodSecurityMetadataSourceAdvisor.class).values().toArray()[0]));
        AfterInvocationProviderManager pm = ((AfterInvocationProviderManager) (getAfterInvocationManager()));
        PostInvocationAdviceProvider aip = ((PostInvocationAdviceProvider) (pm.getProviders().get(0)));
        assertThat(FieldUtils.getFieldValue(mev, "preAdvice.expressionHandler")).isSameAs(FieldUtils.getFieldValue(aip, "postAdvice.expressionHandler"));
    }

    @Test(expected = AccessDeniedException.class)
    public void accessIsDeniedForHasRoleExpression() {
        setContext((("<global-method-security pre-post-annotations='enabled'/>" + "<b:bean id='target' class='org.springframework.security.access.annotation.ExpressionProtectedBusinessServiceImpl'/>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        target = ((BusinessService) (appContext.getBean("target")));
        target.someAdminMethod();
    }

    @Test
    public void beanNameExpressionPropertyIsSupported() {
        setContext((("<global-method-security pre-post-annotations='enabled' proxy-target-class='true'/>" + ((("<b:bean id='number' class='java.lang.Integer'>" + "    <b:constructor-arg value='1294'/>") + "</b:bean>") + "<b:bean id='target' class='org.springframework.security.access.annotation.ExpressionProtectedBusinessServiceImpl'/>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        ExpressionProtectedBusinessServiceImpl target = ((ExpressionProtectedBusinessServiceImpl) (appContext.getBean("target")));
        target.methodWithBeanNamePropertyAccessExpression("x");
    }

    @Test
    public void preAndPostFilterAnnotationsWorkWithLists() {
        setContext((("<global-method-security pre-post-annotations='enabled'/>" + "<b:bean id='target' class='org.springframework.security.access.annotation.ExpressionProtectedBusinessServiceImpl'/>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        target = ((BusinessService) (appContext.getBean("target")));
        List<String> arg = new ArrayList<>();
        arg.add("joe");
        arg.add("bob");
        arg.add("sam");
        List<?> result = target.methodReturningAList(arg);
        // Expression is (filterObject == name or filterObject == 'sam'), so "joe" should
        // be gone after pre-filter
        // PostFilter should remove sam from the return object
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("bob");
    }

    @Test
    public void prePostFilterAnnotationWorksWithArrays() {
        setContext((("<global-method-security pre-post-annotations='enabled'/>" + "<b:bean id='target' class='org.springframework.security.access.annotation.ExpressionProtectedBusinessServiceImpl'/>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        target = ((BusinessService) (appContext.getBean("target")));
        Object[] arg = new String[]{ "joe", "bob", "sam" };
        Object[] result = target.methodReturningAnArray(arg);
        assertThat(result).hasSize(1);
        assertThat(result[0]).isEqualTo("bob");
    }

    // SEC-1392
    @Test
    public void customPermissionEvaluatorIsSupported() throws Exception {
        setContext((("<global-method-security pre-post-annotations='enabled'>" + ((((("   <expression-handler ref='expressionHandler'/>" + "</global-method-security>") + "<b:bean id='expressionHandler' class='org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler'>") + "   <b:property name='permissionEvaluator' ref='myPermissionEvaluator'/>") + "</b:bean>") + "<b:bean id='myPermissionEvaluator' class='org.springframework.security.config.method.TestPermissionEvaluator'/>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
    }

    // SEC-1450
    @Test(expected = AuthenticationException.class)
    @SuppressWarnings("unchecked")
    public void genericsAreMatchedByProtectPointcut() throws Exception {
        setContext((("<b:bean id='target' class='org.springframework.security.config.method.GlobalMethodSecurityBeanDefinitionParserTests$ConcreteFoo'/>" + (("<global-method-security>" + "   <protect-pointcut expression='execution(* org..*Foo.foo(..))' access='ROLE_USER'/>") + "</global-method-security>")) + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        GlobalMethodSecurityBeanDefinitionParserTests.Foo foo = ((GlobalMethodSecurityBeanDefinitionParserTests.Foo) (appContext.getBean("target")));
        foo.foo(new SecurityConfig("A"));
    }

    // SEC-1448
    @Test
    @SuppressWarnings("unchecked")
    public void genericsMethodArgumentNamesAreResolved() throws Exception {
        setContext((((("<b:bean id='target' class='" + (GlobalMethodSecurityBeanDefinitionParserTests.ConcreteFoo.class.getName())) + "'/>") + "<global-method-security pre-post-annotations='enabled'/>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        GlobalMethodSecurityBeanDefinitionParserTests.Foo foo = ((GlobalMethodSecurityBeanDefinitionParserTests.Foo) (appContext.getBean("target")));
        foo.foo(new SecurityConfig("A"));
    }

    @Test
    public void runAsManagerIsSetCorrectly() throws Exception {
        StaticApplicationContext parent = new StaticApplicationContext();
        MutablePropertyValues props = new MutablePropertyValues();
        props.addPropertyValue("key", "blah");
        parent.registerSingleton("runAsMgr", RunAsManagerImpl.class, props);
        parent.refresh();
        setContext(("<global-method-security run-as-manager-ref='runAsMgr'/>" + (ConfigTestUtils.AUTH_PROVIDER_XML)), parent);
        RunAsManagerImpl ram = ((RunAsManagerImpl) (appContext.getBean("runAsMgr")));
        MethodSecurityMetadataSourceAdvisor msi = ((MethodSecurityMetadataSourceAdvisor) (appContext.getBeansOfType(MethodSecurityMetadataSourceAdvisor.class).values().toArray()[0]));
        assertThat(ram).isSameAs(FieldUtils.getFieldValue(msi.getAdvice(), "runAsManager"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void supportsExternalMetadataSource() throws Exception {
        setContext(((((((((("<b:bean id='target' class='" + (GlobalMethodSecurityBeanDefinitionParserTests.ConcreteFoo.class.getName())) + "'/>") + "<method-security-metadata-source id='mds'>") + "      <protect method='") + (GlobalMethodSecurityBeanDefinitionParserTests.Foo.class.getName())) + ".foo' access='ROLE_ADMIN'/>") + "</method-security-metadata-source>") + "<global-method-security pre-post-annotations='enabled' metadata-source-ref='mds'/>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        // External MDS should take precedence over PreAuthorize
        SecurityContextHolder.getContext().setAuthentication(bob);
        GlobalMethodSecurityBeanDefinitionParserTests.Foo foo = ((GlobalMethodSecurityBeanDefinitionParserTests.Foo) (appContext.getBean("target")));
        try {
            foo.foo(new SecurityConfig("A"));
            fail("Bob can't invoke admin methods");
        } catch (AccessDeniedException expected) {
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "password"));
        foo.foo(new SecurityConfig("A"));
    }

    @Test
    public void supportsCustomAuthenticationManager() throws Exception {
        setContext((((((((((((("<b:bean id='target' class='" + (GlobalMethodSecurityBeanDefinitionParserTests.ConcreteFoo.class.getName())) + "'/>") + "<method-security-metadata-source id='mds'>") + "      <protect method='") + (GlobalMethodSecurityBeanDefinitionParserTests.Foo.class.getName())) + ".foo' access='ROLE_ADMIN'/>") + "</method-security-metadata-source>") + "<global-method-security pre-post-annotations='enabled' metadata-source-ref='mds' authentication-manager-ref='customAuthMgr'/>") + "<b:bean id='customAuthMgr' class='org.springframework.security.config.method.GlobalMethodSecurityBeanDefinitionParserTests$CustomAuthManager'>") + "      <b:constructor-arg value='authManager'/>") + "</b:bean>") + (ConfigTestUtils.AUTH_PROVIDER_XML)));
        SecurityContextHolder.getContext().setAuthentication(bob);
        GlobalMethodSecurityBeanDefinitionParserTests.Foo foo = ((GlobalMethodSecurityBeanDefinitionParserTests.Foo) (appContext.getBean("target")));
        try {
            foo.foo(new SecurityConfig("A"));
            fail("Bob can't invoke admin methods");
        } catch (AccessDeniedException expected) {
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "password"));
        foo.foo(new SecurityConfig("A"));
    }

    static class CustomAuthManager implements ApplicationContextAware , AuthenticationManager {
        private String beanName;

        private AuthenticationManager authenticationManager;

        CustomAuthManager(String beanName) {
            this.beanName = beanName;
        }

        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return authenticationManager.authenticate(authentication);
        }

        /* (non-Javadoc)

        @see
        org.springframework.context.ApplicationContextAware#setApplicationContext(org
        .springframework.context.ApplicationContext)
         */
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.authenticationManager = applicationContext.getBean(beanName, AuthenticationManager.class);
        }
    }

    interface Foo<T extends ConfigAttribute> {
        void foo(T action);
    }

    public static class ConcreteFoo implements GlobalMethodSecurityBeanDefinitionParserTests.Foo<SecurityConfig> {
        @PreAuthorize("#action.attribute == 'A'")
        public void foo(SecurityConfig action) {
        }
    }
}

