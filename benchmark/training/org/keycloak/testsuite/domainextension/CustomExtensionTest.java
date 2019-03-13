/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.domainextension;


import java.util.List;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CustomExtensionTest extends AbstractKeycloakTest {
    @Test
    public void testDomainExtension() throws Exception {
        companyResource().createCompany("foo", buildCompany("foo-company"));
        companyResource().createCompany("foo", buildCompany("bar-company"));
        companyResource().createCompany("master", buildCompany("master-company"));
        List<CompanyRepresentation> fooCompanies = companyResource().getCompanies("foo");
        List<CompanyRepresentation> masterCompanies = companyResource().getCompanies("master");
        assertCompanyNames(fooCompanies, "foo-company", "bar-company");
        assertCompanyNames(masterCompanies, "master-company");
        companyResource().deleteAllCompanies("foo");
        companyResource().deleteAllCompanies("master");
    }
}

