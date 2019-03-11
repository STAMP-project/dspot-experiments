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
package org.springframework.security.taglibs.authz;


import Tag.EVAL_BODY_INCLUDE;
import Tag.SKIP_BODY;
import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockPageContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class AccessControlListTagTests {
    AccessControlListTag tag;

    PermissionEvaluator pe;

    MockPageContext pageContext;

    Authentication bob = new TestingAuthenticationToken("bob", "bobspass", "A");

    @Test
    public void bodyIsEvaluatedIfAclGrantsAccess() throws Exception {
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, "READ")).thenReturn(true);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("READ");
        tag.setVar("allowed");
        assertThat(tag.getDomainObject()).isSameAs(domainObject);
        assertThat(tag.getHasPermission()).isEqualTo("READ");
        assertThat(tag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isTrue();
    }

    @Test
    public void childContext() throws Exception {
        ServletContext servletContext = pageContext.getServletContext();
        WebApplicationContext wac = ((WebApplicationContext) (servletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)));
        servletContext.removeAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        servletContext.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, "READ")).thenReturn(true);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("READ");
        tag.setVar("allowed");
        assertThat(tag.getDomainObject()).isSameAs(domainObject);
        assertThat(tag.getHasPermission()).isEqualTo("READ");
        assertThat(tag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isTrue();
    }

    // SEC-2022
    @Test
    public void multiHasPermissionsAreSplit() throws Exception {
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, "READ")).thenReturn(true);
        Mockito.when(pe.hasPermission(bob, domainObject, "WRITE")).thenReturn(true);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("READ,WRITE");
        tag.setVar("allowed");
        assertThat(tag.getDomainObject()).isSameAs(domainObject);
        assertThat(tag.getHasPermission()).isEqualTo("READ,WRITE");
        assertThat(tag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isTrue();
        Mockito.verify(pe).hasPermission(bob, domainObject, "READ");
        Mockito.verify(pe).hasPermission(bob, domainObject, "WRITE");
        Mockito.verifyNoMoreInteractions(pe);
    }

    // SEC-2023
    @Test
    public void hasPermissionsBitMaskSupported() throws Exception {
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, 1)).thenReturn(true);
        Mockito.when(pe.hasPermission(bob, domainObject, 2)).thenReturn(true);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("1,2");
        tag.setVar("allowed");
        assertThat(tag.getDomainObject()).isSameAs(domainObject);
        assertThat(tag.getHasPermission()).isEqualTo("1,2");
        assertThat(tag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isTrue();
        Mockito.verify(pe).hasPermission(bob, domainObject, 1);
        Mockito.verify(pe).hasPermission(bob, domainObject, 2);
        Mockito.verifyNoMoreInteractions(pe);
    }

    @Test
    public void hasPermissionsMixedBitMaskSupported() throws Exception {
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, 1)).thenReturn(true);
        Mockito.when(pe.hasPermission(bob, domainObject, "WRITE")).thenReturn(true);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("1,WRITE");
        tag.setVar("allowed");
        assertThat(tag.getDomainObject()).isSameAs(domainObject);
        assertThat(tag.getHasPermission()).isEqualTo("1,WRITE");
        assertThat(tag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isTrue();
        Mockito.verify(pe).hasPermission(bob, domainObject, 1);
        Mockito.verify(pe).hasPermission(bob, domainObject, "WRITE");
        Mockito.verifyNoMoreInteractions(pe);
    }

    @Test
    public void bodyIsSkippedIfAclDeniesAccess() throws Exception {
        Object domainObject = new Object();
        Mockito.when(pe.hasPermission(bob, domainObject, "READ")).thenReturn(false);
        tag.setDomainObject(domainObject);
        tag.setHasPermission("READ");
        tag.setVar("allowed");
        assertThat(tag.doStartTag()).isEqualTo(SKIP_BODY);
        assertThat(((Boolean) (pageContext.getAttribute("allowed")))).isFalse();
    }
}

