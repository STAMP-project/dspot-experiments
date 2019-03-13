/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.access.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import org.junit.Test;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.sec2150.MethodInvocationFactory;
import org.springframework.security.access.intercept.method.MockMethodInvocation;
import org.springframework.security.core.GrantedAuthority;


/**
 * Tests for
 * {@link org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource}
 *
 * @author Mark St.Godard
 * @author Joe Scalise
 * @author Ben Alex
 * @author Luke Taylor
 */
public class SecuredAnnotationSecurityMetadataSourceTests {
    // ~ Instance fields
    // ================================================================================================
    private SecuredAnnotationSecurityMetadataSource mds = new SecuredAnnotationSecurityMetadataSource();

    // ~ Methods
    // ========================================================================================================
    @Test
    public void genericsSuperclassDeclarationsAreIncludedWhenSubclassesOverride() {
        Method method = null;
        try {
            method = SecuredAnnotationSecurityMetadataSourceTests.DepartmentServiceImpl.class.getMethod("someUserMethod3", new Class[]{ SecuredAnnotationSecurityMetadataSourceTests.Department.class });
        } catch (NoSuchMethodException unexpected) {
            fail("Should be a superMethod called 'someUserMethod3' on class!");
        }
        Collection<ConfigAttribute> attrs = mds.findAttributes(method, SecuredAnnotationSecurityMetadataSourceTests.DepartmentServiceImpl.class);
        assertThat(attrs).isNotNull();
        // expect 1 attribute
        assertThat(((attrs.size()) == 1)).as("Did not find 1 attribute").isTrue();
        // should have 1 SecurityConfig
        for (ConfigAttribute sc : attrs) {
            assertThat(sc.getAttribute()).as("Found an incorrect role").isEqualTo("ROLE_ADMIN");
        }
        Method superMethod = null;
        try {
            superMethod = SecuredAnnotationSecurityMetadataSourceTests.DepartmentServiceImpl.class.getMethod("someUserMethod3", new Class[]{ Entity.class });
        } catch (NoSuchMethodException unexpected) {
            fail("Should be a superMethod called 'someUserMethod3' on class!");
        }
        Collection<ConfigAttribute> superAttrs = this.mds.findAttributes(superMethod, SecuredAnnotationSecurityMetadataSourceTests.DepartmentServiceImpl.class);
        assertThat(superAttrs).isNotNull();
        // This part of the test relates to SEC-274
        // expect 1 attribute
        assertThat(superAttrs).as("Did not find 1 attribute").hasSize(1);
        // should have 1 SecurityConfig
        for (ConfigAttribute sc : superAttrs) {
            assertThat(sc.getAttribute()).as("Found an incorrect role").isEqualTo("ROLE_ADMIN");
        }
    }

    @Test
    public void classLevelAttributesAreFound() {
        Collection<ConfigAttribute> attrs = this.mds.findAttributes(BusinessService.class);
        assertThat(attrs).isNotNull();
        // expect 1 annotation
        assertThat(attrs).hasSize(1);
        // should have 1 SecurityConfig
        SecurityConfig sc = ((SecurityConfig) (attrs.toArray()[0]));
        assertThat(sc.getAttribute()).isEqualTo("ROLE_USER");
    }

    @Test
    public void methodLevelAttributesAreFound() {
        Method method = null;
        try {
            method = BusinessService.class.getMethod("someUserAndAdminMethod", new Class[]{  });
        } catch (NoSuchMethodException unexpected) {
            fail("Should be a method called 'someUserAndAdminMethod' on class!");
        }
        Collection<ConfigAttribute> attrs = this.mds.findAttributes(method, BusinessService.class);
        // expect 2 attributes
        assertThat(attrs).hasSize(2);
        boolean user = false;
        boolean admin = false;
        // should have 2 SecurityConfigs
        for (ConfigAttribute sc : attrs) {
            assertThat(sc).isInstanceOf(SecurityConfig.class);
            if (sc.getAttribute().equals("ROLE_USER")) {
                user = true;
            } else
                if (sc.getAttribute().equals("ROLE_ADMIN")) {
                    admin = true;
                }

        }
        // expect to have ROLE_USER and ROLE_ADMIN
        assertThat(user).isEqualTo(admin).isTrue();
    }

    // SEC-1491
    @Test
    public void customAnnotationAttributesAreFound() throws Exception {
        SecuredAnnotationSecurityMetadataSource mds = new SecuredAnnotationSecurityMetadataSource(new SecuredAnnotationSecurityMetadataSourceTests.CustomSecurityAnnotationMetadataExtractor());
        Collection<ConfigAttribute> attrs = mds.findAttributes(SecuredAnnotationSecurityMetadataSourceTests.CustomAnnotatedService.class);
        assertThat(attrs).containsOnly(SecuredAnnotationSecurityMetadataSourceTests.SecurityEnum.ADMIN);
    }

    @Test
    public void annotatedAnnotationAtClassLevelIsDetected() throws Exception {
        MockMethodInvocation annotatedAtClassLevel = new MockMethodInvocation(new SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotationAtClassLevel(), SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid.class, "doSomething", List.class);
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtClassLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(attrs).extracting("attribute").containsOnly("CUSTOM");
    }

    @Test
    public void annotatedAnnotationAtInterfaceLevelIsDetected() throws Exception {
        MockMethodInvocation annotatedAtInterfaceLevel = new MockMethodInvocation(new SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotationAtInterfaceLevel(), SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid2.class, "doSomething", List.class);
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtInterfaceLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(attrs).extracting("attribute").containsOnly("CUSTOM");
    }

    @Test
    public void annotatedAnnotationAtMethodLevelIsDetected() throws Exception {
        MockMethodInvocation annotatedAtMethodLevel = new MockMethodInvocation(new SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotationAtMethodLevel(), SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid.class, "doSomething", List.class);
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtMethodLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(attrs).extracting("attribute").containsOnly("CUSTOM");
    }

    @Test
    public void proxyFactoryInterfaceAttributesFound() throws Exception {
        MockMethodInvocation mi = MethodInvocationFactory.createSec2150MethodInvocation();
        Collection<ConfigAttribute> attributes = mds.getAttributes(mi);
        assertThat(attributes).hasSize(1);
        assertThat(attributes).extracting("attribute").containsOnly("ROLE_PERSON");
    }

    // Inner classes
    class Department extends Entity {
        public Department(String name) {
            super(name);
        }
    }

    interface DepartmentService extends BusinessService {
        @Secured({ "ROLE_USER" })
        SecuredAnnotationSecurityMetadataSourceTests.Department someUserMethod3(SecuredAnnotationSecurityMetadataSourceTests.Department dept);
    }

    @SuppressWarnings("serial")
    class DepartmentServiceImpl extends BusinessServiceImpl<SecuredAnnotationSecurityMetadataSourceTests.Department> implements SecuredAnnotationSecurityMetadataSourceTests.DepartmentService {
        @Secured({ "ROLE_ADMIN" })
        public SecuredAnnotationSecurityMetadataSourceTests.Department someUserMethod3(final SecuredAnnotationSecurityMetadataSourceTests.Department dept) {
            return super.someUserMethod3(dept);
        }
    }

    // SEC-1491 Related classes. PoC for custom annotation with enum value.
    @SecuredAnnotationSecurityMetadataSourceTests.CustomSecurityAnnotation(SecuredAnnotationSecurityMetadataSourceTests.SecurityEnum.ADMIN)
    interface CustomAnnotatedService {}

    class CustomAnnotatedServiceImpl implements SecuredAnnotationSecurityMetadataSourceTests.CustomAnnotatedService {}

    enum SecurityEnum implements ConfigAttribute , GrantedAuthority {

        ADMIN,
        USER;
        public String getAttribute() {
            return toString();
        }

        public String getAuthority() {
            return toString();
        }
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomSecurityAnnotation {
        SecuredAnnotationSecurityMetadataSourceTests.SecurityEnum[] value();
    }

    class CustomSecurityAnnotationMetadataExtractor implements AnnotationMetadataExtractor<SecuredAnnotationSecurityMetadataSourceTests.CustomSecurityAnnotation> {
        public Collection<? extends ConfigAttribute> extractAttributes(SecuredAnnotationSecurityMetadataSourceTests.CustomSecurityAnnotation securityAnnotation) {
            SecuredAnnotationSecurityMetadataSourceTests.SecurityEnum[] values = securityAnnotation.value();
            return EnumSet.copyOf(Arrays.asList(values));
        }
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Secured("CUSTOM")
    public @interface AnnotatedAnnotation {}

    public static interface ReturnVoid {
        public void doSomething(List<?> param);
    }

    @SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotation
    public static interface ReturnVoid2 {
        public void doSomething(List<?> param);
    }

    @SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotation
    public static class AnnotatedAnnotationAtClassLevel implements SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid {
        public void doSomething(List<?> param) {
        }
    }

    public static class AnnotatedAnnotationAtInterfaceLevel implements SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid2 {
        public void doSomething(List<?> param) {
        }
    }

    public static class AnnotatedAnnotationAtMethodLevel implements SecuredAnnotationSecurityMetadataSourceTests.ReturnVoid {
        @SecuredAnnotationSecurityMetadataSourceTests.AnnotatedAnnotation
        public void doSomething(List<?> param) {
        }
    }
}

