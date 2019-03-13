/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.reflection;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-11924")
public class ElementCollectionConverterTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testConverterIsAppliedToElementCollection() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Company company = new Company();
            company.setId(1L);
            Organization org1 = new Organization();
            org1.setOrganizationId("ACME");
            company.getOrganizations().add(org1);
            session.persist(company);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String organizationId = ((String) (session.createNativeQuery("select organizations from Company_organizations").getSingleResult()));
            assertEquals("ORG-ACME", organizationId);
            Company company = session.find(.class, 1L);
            assertEquals(1, company.getOrganizations().size());
            assertEquals("ACME", company.getOrganizations().get(0).getOrganizationId());
        });
    }
}

