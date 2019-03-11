/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class BeanValidationDisabledTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testListeners() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("12"));
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.persist(ch);
            s.flush();
        } catch (ConstraintViolationException e) {
            Assert.fail("invalid object should not be validated");
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testDDLDisabled() {
        PersistentClass classMapping = metadata().getEntityBinding(Address.class.getName());
        Column countryColumn = ((Column) (classMapping.getProperty("country").getColumnIterator().next()));
        Assert.assertTrue("DDL constraints are applied", countryColumn.isNullable());
    }
}

