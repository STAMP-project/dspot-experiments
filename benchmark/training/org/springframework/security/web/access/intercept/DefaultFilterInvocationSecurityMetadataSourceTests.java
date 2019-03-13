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
package org.springframework.security.web.access.intercept;


import java.util.Collection;
import java.util.LinkedHashMap;
import org.junit.Test;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Tests {@link DefaultFilterInvocationSecurityMetadataSource}.
 *
 * @author Ben Alex
 */
public class DefaultFilterInvocationSecurityMetadataSourceTests {
    private DefaultFilterInvocationSecurityMetadataSource fids;

    private Collection<ConfigAttribute> def = SecurityConfig.createList("ROLE_ONE");

    @Test
    public void lookupNotRequiringExactMatchSucceedsIfNotMatching() {
        createFids("/secure/super/**", null);
        FilterInvocation fi = createFilterInvocation("/secure/super/somefile.html", null, null, null);
        assertThat(this.fids.getAttributes(fi)).isEqualTo(this.def);
    }

    /**
     * SEC-501. Note that as of 2.0, lower case comparisons are the default for this
     * class.
     */
    @Test
    public void lookupNotRequiringExactMatchSucceedsIfSecureUrlPathContainsUpperCase() {
        createFids("/secure/super/**", null);
        FilterInvocation fi = createFilterInvocation("/secure", "/super/somefile.html", null, null);
        Collection<ConfigAttribute> response = this.fids.getAttributes(fi);
        assertThat(response).isEqualTo(this.def);
    }

    @Test
    public void lookupRequiringExactMatchIsSuccessful() {
        createFids("/SeCurE/super/**", null);
        FilterInvocation fi = createFilterInvocation("/SeCurE/super/somefile.html", null, null, null);
        Collection<ConfigAttribute> response = this.fids.getAttributes(fi);
        assertThat(response).isEqualTo(this.def);
    }

    @Test
    public void lookupRequiringExactMatchWithAdditionalSlashesIsSuccessful() {
        createFids("/someAdminPage.html**", null);
        FilterInvocation fi = createFilterInvocation("/someAdminPage.html", null, "a=/test", null);
        Collection<ConfigAttribute> response = this.fids.getAttributes(fi);
        assertThat(response);// see SEC-161 (it should truncate after ?

        // sign).isEqualTo(def)
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownHttpMethodIsRejected() {
        createFids("/someAdminPage.html**", "UNKNOWN");
    }

    @Test
    public void httpMethodLookupSucceeds() {
        createFids("/somepage**", "GET");
        FilterInvocation fi = createFilterInvocation("/somepage", null, null, "GET");
        Collection<ConfigAttribute> attrs = this.fids.getAttributes(fi);
        assertThat(attrs).isEqualTo(this.def);
    }

    @Test
    public void generalMatchIsUsedIfNoMethodSpecificMatchExists() {
        createFids("/somepage**", null);
        FilterInvocation fi = createFilterInvocation("/somepage", null, null, "GET");
        Collection<ConfigAttribute> attrs = this.fids.getAttributes(fi);
        assertThat(attrs).isEqualTo(this.def);
    }

    @Test
    public void requestWithDifferentHttpMethodDoesntMatch() {
        createFids("/somepage**", "GET");
        FilterInvocation fi = createFilterInvocation("/somepage", null, null, "POST");
        Collection<ConfigAttribute> attrs = this.fids.getAttributes(fi);
        assertThat(attrs).isNull();
    }

    // SEC-1236
    @Test
    public void mixingPatternsWithAndWithoutHttpMethodsIsSupported() throws Exception {
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
        Collection<ConfigAttribute> userAttrs = SecurityConfig.createList("A");
        requestMap.put(new AntPathRequestMatcher("/user/**", null), userAttrs);
        requestMap.put(new AntPathRequestMatcher("/teller/**", "GET"), SecurityConfig.createList("B"));
        this.fids = new DefaultFilterInvocationSecurityMetadataSource(requestMap);
        FilterInvocation fi = createFilterInvocation("/user", null, null, "GET");
        Collection<ConfigAttribute> attrs = this.fids.getAttributes(fi);
        assertThat(attrs).isEqualTo(userAttrs);
    }

    /**
     * Check fixes for SEC-321
     */
    @Test
    public void extraQuestionMarkStillMatches() {
        createFids("/someAdminPage.html*", null);
        FilterInvocation fi = createFilterInvocation("/someAdminPage.html", null, null, null);
        Collection<ConfigAttribute> response = this.fids.getAttributes(fi);
        assertThat(response).isEqualTo(this.def);
        fi = createFilterInvocation("/someAdminPage.html", null, "?", null);
        response = this.fids.getAttributes(fi);
        assertThat(response).isEqualTo(this.def);
    }
}

