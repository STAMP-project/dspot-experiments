/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.enums;


import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static org.hibernate.test.enums.UnspecifiedEnumTypeEntity.E1.X;
import static org.hibernate.test.enums.UnspecifiedEnumTypeEntity.E2.A;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7780")
@RequiresDialect(H2Dialect.class)
public class UnspecifiedEnumTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEnumTypeDiscovery() {
        Session session = openSession();
        session.beginTransaction();
        UnspecifiedEnumTypeEntity entity = new UnspecifiedEnumTypeEntity(X, A);
        session.persist(entity);
        session.getTransaction().commit();
        session.close();
    }
}

