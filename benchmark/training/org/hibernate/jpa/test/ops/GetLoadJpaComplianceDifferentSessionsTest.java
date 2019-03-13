/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import AvailableSettings.HBM2DDL_AUTO;
import AvailableSettings.JPA_PROXY_COMPLIANCE;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12273")
public class GetLoadJpaComplianceDifferentSessionsTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9856")
    public void testReattachEntityToSessionWithJpaComplianceProxy() {
        final Integer _workloadId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Workload workload = new Workload();
            workload.load = 123;
            workload.name = "Package";
            entityManager.persist(workload);
            return workload.getId();
        });
        Workload _workload = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.getReference(.class, _workloadId);
        });
        Map settings = buildSettings();
        settings.put(JPA_PROXY_COMPLIANCE, Boolean.TRUE.toString());
        settings.put(HBM2DDL_AUTO, "none");
        EntityManagerFactory newEntityManagerFactory = Bootstrap.getEntityManagerFactoryBuilder(new BaseEntityManagerFunctionalTestCase.TestingPersistenceUnitDescriptorImpl(getClass().getSimpleName()), settings).build();
        try {
            TransactionUtil.doInJPA(() -> newEntityManagerFactory, ( entityManager) -> {
                entityManager.unwrap(.class).update(_workload);
                _workload.getId();
            });
        } finally {
            newEntityManagerFactory.close();
        }
        Assert.assertEquals("Package", _workload.getName());
    }
}

