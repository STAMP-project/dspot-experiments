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
package org.springframework.security.ldap;


import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;


@RunWith(MockitoJUnitRunner.class)
public class SpringSecurityLdapTemplateTests {
    @Mock
    private DirContext ctx;

    @Captor
    private ArgumentCaptor<SearchControls> searchControls;

    @Mock
    private NamingEnumeration<SearchResult> resultsEnum;

    @Mock
    private SearchResult searchResult;

    // SEC-2405
    @Test
    public void searchForSingleEntryInternalAllowsReferrals() throws Exception {
        String base = "";
        String filter = "";
        String searchResultName = "ldap://example.com/dc=springframework,dc=org";
        Object[] params = new Object[]{  };
        DirContextAdapter searchResultObject = Mockito.mock(DirContextAdapter.class);
        Mockito.when(ctx.search(ArgumentMatchers.any(DistinguishedName.class), ArgumentMatchers.eq(filter), ArgumentMatchers.eq(params), searchControls.capture())).thenReturn(resultsEnum);
        Mockito.when(resultsEnum.hasMore()).thenReturn(true, false);
        Mockito.when(resultsEnum.next()).thenReturn(searchResult);
        Mockito.when(searchResult.getObject()).thenReturn(searchResultObject);
        SpringSecurityLdapTemplate.searchForSingleEntryInternal(ctx, Mockito.mock(SearchControls.class), base, filter, params);
        assertThat(searchControls.getValue().getReturningObjFlag()).isTrue();
    }
}

