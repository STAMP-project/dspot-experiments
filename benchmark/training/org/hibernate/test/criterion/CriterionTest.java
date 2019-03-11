/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criterion;


import AvailableSettings.DIALECT;
import Environment.HBM2DDL_AUTO;
import org.hibernate.Criteria;
import org.hibernate.IrrelevantEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CriterionTest extends BaseUnitTestCase {
    @Test
    public void testIlikeRendering() {
        SessionFactory sf = new Configuration().addAnnotatedClass(IrrelevantEntity.class).setProperty(DIALECT, CriterionTest.IlikeSupportingDialect.class.getName()).setProperty(HBM2DDL_AUTO, "create-drop").buildSessionFactory();
        try {
            final Criteria criteria = sf.openSession().createCriteria(IrrelevantEntity.class);
            final CriteriaQueryTranslator translator = new CriteriaQueryTranslator(((SessionFactoryImplementor) (sf)), ((CriteriaImpl) (criteria)), IrrelevantEntity.class.getName(), "a");
            final Criterion ilikeExpression = Restrictions.ilike("name", "abc");
            final String ilikeExpressionSqlFragment = ilikeExpression.toSqlString(criteria, translator);
            Assert.assertEquals("a.name insensitiveLike ?", ilikeExpressionSqlFragment);
        } finally {
            sf.close();
        }
    }

    @Test
    public void testIlikeMimicing() {
        SessionFactory sf = new Configuration().addAnnotatedClass(IrrelevantEntity.class).setProperty(DIALECT, CriterionTest.NonIlikeSupportingDialect.class.getName()).setProperty(HBM2DDL_AUTO, "create-drop").buildSessionFactory();
        try {
            final Criteria criteria = sf.openSession().createCriteria(IrrelevantEntity.class);
            final CriteriaQueryTranslator translator = new CriteriaQueryTranslator(((SessionFactoryImplementor) (sf)), ((CriteriaImpl) (criteria)), IrrelevantEntity.class.getName(), "a");
            final Criterion ilikeExpression = Restrictions.ilike("name", "abc");
            final String ilikeExpressionSqlFragment = ilikeExpression.toSqlString(criteria, translator);
            Assert.assertEquals("lowLowLow(a.name) like ?", ilikeExpressionSqlFragment);
        } finally {
            sf.close();
        }
    }

    public static class IlikeSupportingDialect extends Dialect {
        @Override
        public boolean supportsCaseInsensitiveLike() {
            return true;
        }

        @Override
        public String getCaseInsensitiveLike() {
            return "insensitiveLike";
        }
    }

    public static class NonIlikeSupportingDialect extends Dialect {
        @Override
        public boolean supportsCaseInsensitiveLike() {
            return false;
        }

        @Override
        public String getCaseInsensitiveLike() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLowercaseFunction() {
            return "lowLowLow";
        }
    }
}

