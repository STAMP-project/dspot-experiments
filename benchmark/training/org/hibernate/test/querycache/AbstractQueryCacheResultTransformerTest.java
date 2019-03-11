/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;


import CriteriaSpecification.ALIAS_TO_ENTITY_MAP;
import CriteriaSpecification.INNER_JOIN;
import CriteriaSpecification.LEFT_JOIN;
import FetchMode.JOIN;
import FetchMode.SELECT;
import Transformers.TO_LIST;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public abstract class AbstractQueryCacheResultTransformerTest extends BaseCoreFunctionalTestCase {
    private Student yogiExpected;

    private Student shermanExpected;

    private CourseMeeting courseMeetingExpected1;

    private CourseMeeting courseMeetingExpected2;

    private Course courseExpected;

    private Enrolment yogiEnrolmentExpected;

    private Enrolment shermanEnrolmentExpected;

    protected abstract class CriteriaExecutor extends AbstractQueryCacheResultTransformerTest.QueryExecutor {
        protected abstract Criteria getCriteria(Session s) throws Exception;

        @Override
        protected Object getResults(Session s, boolean isSingleResult) throws Exception {
            Criteria criteria = getCriteria(s).setCacheable(((getQueryCacheMode()) != (CacheMode.IGNORE))).setCacheMode(getQueryCacheMode());
            return isSingleResult ? criteria.uniqueResult() : criteria.list();
        }
    }

    protected abstract class HqlExecutor extends AbstractQueryCacheResultTransformerTest.QueryExecutor {
        protected abstract Query getQuery(Session s);

        @Override
        protected Object getResults(Session s, boolean isSingleResult) {
            Query query = getQuery(s).setCacheable(((getQueryCacheMode()) != (CacheMode.IGNORE))).setCacheMode(getQueryCacheMode());
            return isSingleResult ? query.uniqueResult() : query.list();
        }
    }

    protected abstract class QueryExecutor {
        public Object execute(boolean isSingleResult) throws Exception {
            Session s = openSession();
            Transaction t = s.beginTransaction();
            Object result = null;
            try {
                result = getResults(s, isSingleResult);
                t.commit();
            } catch (Exception ex) {
                t.rollback();
                throw ex;
            } finally {
                s.close();
            }
            return result;
        }

        protected abstract Object getResults(Session s, boolean isSingleResult) throws Exception;
    }

    protected interface ResultChecker {
        void check(Object results);
    }

    @Test
    public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            @Override
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "p", LEFT_JOIN).createAlias("s.addresses", "a", LEFT_JOIN).setResultTransformer(ALIAS_TO_ENTITY_MAP).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            @Override
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.preferredCourse p left join s.addresses a order by s.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(3, resultList.size());
                Map yogiMap1 = ((Map) (resultList.get(0)));
                Assert.assertEquals(3, yogiMap1.size());
                Map yogiMap2 = ((Map) (resultList.get(1)));
                Assert.assertEquals(3, yogiMap2.size());
                Map shermanMap = ((Map) (resultList.get(2)));
                Assert.assertEquals(3, shermanMap.size());
                Assert.assertEquals(yogiExpected, yogiMap1.get("s"));
                Assert.assertEquals(courseExpected, yogiMap1.get("p"));
                Address yogiAddress1 = ((Address) (yogiMap1.get("a")));
                Assert.assertEquals(yogiExpected.getAddresses().get(yogiAddress1.getAddressType()), yogiMap1.get("a"));
                Assert.assertEquals(yogiExpected, yogiMap2.get("s"));
                Assert.assertEquals(courseExpected, yogiMap2.get("p"));
                Address yogiAddress2 = ((Address) (yogiMap2.get("a")));
                Assert.assertEquals(yogiExpected.getAddresses().get(yogiAddress2.getAddressType()), yogiMap2.get("a"));
                Assert.assertSame(yogiMap1.get("s"), yogiMap2.get("s"));
                Assert.assertSame(yogiMap1.get("p"), yogiMap2.get("p"));
                Assert.assertFalse(yogiAddress1.getAddressType().equals(yogiAddress2.getAddressType()));
                Assert.assertEquals(shermanExpected, shermanMap.get("s"));
                Assert.assertEquals(shermanExpected.getPreferredCourse(), shermanMap.get("p"));
                Assert.assertNull(shermanMap.get("a"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            @Override
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.addresses", "a", LEFT_JOIN).setResultTransformer(ALIAS_TO_ENTITY_MAP).createCriteria("s.preferredCourse", INNER_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            @Override
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.addresses a left join s.preferredCourse order by s.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap1 = ((Map) (resultList.get(0)));
                Assert.assertEquals(2, yogiMap1.size());
                Map yogiMap2 = ((Map) (resultList.get(1)));
                Assert.assertEquals(2, yogiMap2.size());
                Assert.assertEquals(yogiExpected, yogiMap1.get("s"));
                Address yogiAddress1 = ((Address) (yogiMap1.get("a")));
                Assert.assertEquals(yogiExpected.getAddresses().get(yogiAddress1.getAddressType()), yogiMap1.get("a"));
                Assert.assertEquals(yogiExpected, yogiMap2.get("s"));
                Address yogiAddress2 = ((Address) (yogiMap2.get("a")));
                Assert.assertEquals(yogiExpected.getAddresses().get(yogiAddress2.getAddressType()), yogiMap2.get("a"));
                Assert.assertSame(yogiMap1.get("s"), yogiMap2.get("s"));
                Assert.assertFalse(yogiAddress1.getAddressType().equals(yogiAddress2.getAddressType()));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testEntityWithNonLazyOneToManyUnique() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Course.class);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Course");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Course));
                Assert.assertEquals(courseExpected, results);
                Assert.assertTrue(Hibernate.isInitialized(courseExpected.getCourseMeetings()));
                Assert.assertEquals(courseExpected.getCourseMeetings(), courseExpected.getCourseMeetings());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testEntityWithNonLazyManyToOneList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(CourseMeeting.class).addOrder(Order.asc("id.day"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            protected Query getQuery(Session s) {
                return s.createQuery("from CourseMeeting order by id.day");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(courseMeetingExpected1, resultList.get(0));
                Assert.assertEquals(courseMeetingExpected2, resultList.get(1));
                Assert.assertTrue(Hibernate.isInitialized(((CourseMeeting) (resultList.get(0))).getCourse()));
                Assert.assertTrue(Hibernate.isInitialized(((CourseMeeting) (resultList.get(1))).getCourse()));
                Assert.assertEquals(courseExpected, ((CourseMeeting) (resultList.get(0))).getCourse());
                Assert.assertEquals(courseExpected, ((CourseMeeting) (resultList.get(1))).getCourse());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testEntityWithLazyAssnUnique() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").add(Restrictions.eq("studentNumber", shermanExpected.getStudentNumber()));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s where s.studentNumber = :studentNumber").setParameter("studentNumber", shermanExpected.getStudentNumber());
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Student));
                Assert.assertEquals(shermanExpected, results);
                Assert.assertNotNull(((Student) (results)).getEnrolments());
                Assert.assertFalse(Hibernate.isInitialized(((Student) (results)).getEnrolments()));
                Assert.assertNull(((Student) (results)).getPreferredCourse());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testEntityWithLazyAssnList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class).addOrder(Order.asc("studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student order by studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNotNull(((Student) (resultList.get(0))).getEnrolments());
                Assert.assertNotNull(((Student) (resultList.get(0))).getPreferredCourse());
                Assert.assertNotNull(((Student) (resultList.get(1))).getEnrolments());
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
                Assert.assertFalse(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                Assert.assertFalse(Hibernate.isInitialized(((Student) (resultList.get(0))).getPreferredCourse()));
                Assert.assertFalse(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testEntityWithUnaliasedJoinFetchedLazyOneToManySingleElementList() throws Exception {
        // unaliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").setFetchMode("enrolments", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.enrolments order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNotNull(((Student) (resultList.get(0))).getEnrolments());
                Assert.assertNotNull(((Student) (resultList.get(1))).getEnrolments());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (resultList.get(0))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (resultList.get(1))).getEnrolments());
                }
            }
        };
        runTest(hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinListCriteria() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "pc", Criteria.LEFT_JOIN).setFetchMode("enrolments", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                // The following fails for criteria due to HHH-3524
                // assertEquals( yogiExpected.getPreferredCourse(), ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Student) (resultList.get(0))).getPreferredCourse().getCourseCode());
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (resultList.get(0))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (resultList.get(1))).getEnrolments());
                }
            }
        };
        runTest(null, criteriaExecutor, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiObjects[1]);
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertNull(shermanObjects[1]);
                Assert.assertNull(((Student) (shermanObjects[0])).getPreferredCourse());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[0])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[0])).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanObjects[0])).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanObjects[0])).getEnrolments());
                }
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinWithOwnerAndPropProjectedList() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlSelectNewMapExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s, s.name from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiExpected.getName(), yogiObjects[1]);
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertEquals(shermanExpected.getName(), shermanObjects[1]);
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[0])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[0])).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanObjects[0])).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanObjects[0])).getEnrolments());
                }
            }
        };
        runTest(hqlSelectNewMapExecutor, null, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinWithPropAndOwnerProjectedList() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlSelectNewMapExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s.name, s from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getName(), yogiObjects[0]);
                Assert.assertEquals(yogiExpected, yogiObjects[1]);
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getName(), shermanObjects[0]);
                Assert.assertEquals(shermanExpected, shermanObjects[1]);
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[1])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[1])).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanObjects[1])).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanObjects[1])).getEnrolments());
                }
            }
        };
        runTest(hqlSelectNewMapExecutor, null, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinWithOwnerAndAliasedJoinedProjectedListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s, pc from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Course) (yogiObjects[1])).getCourseCode());
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertNull(shermanObjects[1]);
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiObjects[1]);
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[0])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[0])).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanObjects[0])).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanObjects[0])).getEnrolments());
                }
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testJoinWithFetchJoinWithAliasedJoinedAndOwnerProjectedListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlSelectNewMapExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select pc, s from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[1]);
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Course) (yogiObjects[0])).getCourseCode());
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[1]);
                Assert.assertNull(shermanObjects[0]);
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiObjects[0]);
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[1])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[1])).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanObjects[1])).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanObjects[1])).getEnrolments());
                }
            }
        };
        runTest(hqlSelectNewMapExecutor, null, checker, false);
    }

    @Test
    public void testEntityWithAliasedJoinFetchedLazyOneToManySingleElementListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.enrolments e order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Student) (resultList.get(0))).getPreferredCourse().getCourseCode());
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (resultList.get(0))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (resultList.get(1))).getEnrolments());
                }
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testEntityWithSelectFetchedLazyOneToManySingleElementListCriteria() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").setFetchMode("enrolments", SELECT).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNotNull(((Student) (resultList.get(0))).getEnrolments());
                Assert.assertFalse(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                Assert.assertNotNull(((Student) (resultList.get(1))).getEnrolments());
                Assert.assertFalse(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
            }
        };
        runTest(null, criteriaExecutorUnaliased, checker, false);
    }

    @Test
    public void testEntityWithJoinFetchedLazyOneToManyMultiAndNullElementList() throws Exception {
        // unaliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").setFetchMode("addresses", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.addresses order by s.studentNumber");
            }
        };
        // aliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased1 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.addresses", "a", Criteria.LEFT_JOIN).setFetchMode("addresses", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased2 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.addresses", "a", Criteria.LEFT_JOIN).setFetchMode("a", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased3 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.addresses", "a", Criteria.LEFT_JOIN).setFetchMode("addresses", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased4 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.addresses", "a", Criteria.LEFT_JOIN).setFetchMode("a", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorAliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.addresses a order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(3, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertSame(resultList.get(0), resultList.get(1));
                Assert.assertEquals(shermanExpected, resultList.get(2));
                Assert.assertNotNull(((Student) (resultList.get(0))).getAddresses());
                Assert.assertNotNull(((Student) (resultList.get(1))).getAddresses());
                Assert.assertNotNull(((Student) (resultList.get(2))).getAddresses());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getAddresses()));
                    Assert.assertEquals(yogiExpected.getAddresses(), ((Student) (resultList.get(0))).getAddresses());
                    Assert.assertTrue(((Student) (resultList.get(2))).getAddresses().isEmpty());
                }
            }
        };
        runTest(hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
        runTest(hqlExecutorAliased, criteriaExecutorAliased1, checker, false);
        runTest(null, criteriaExecutorAliased2, checker, false);
        runTest(null, criteriaExecutorAliased3, checker, false);
        runTest(null, criteriaExecutorAliased4, checker, false);
    }

    @Test
    public void testEntityWithJoinFetchedLazyManyToOneList() throws Exception {
        // unaliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").setFetchMode("preferredCourse", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.preferredCourse order by s.studentNumber");
            }
        };
        // aliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased1 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "pCourse", Criteria.LEFT_JOIN).setFetchMode("preferredCourse", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased2 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "pCourse", Criteria.LEFT_JOIN).setFetchMode("pCourse", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased3 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.preferredCourse", "pCourse", Criteria.LEFT_JOIN).setFetchMode("preferredCourse", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased4 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.preferredCourse", "pCourse", Criteria.LEFT_JOIN).setFetchMode("pCourse", JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorAliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join fetch s.preferredCourse pCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Student) (resultList.get(0))).getPreferredCourse().getCourseCode());
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
            }
        };
        runTest(hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
        runTest(hqlExecutorAliased, criteriaExecutorAliased1, checker, false);
        runTest(null, criteriaExecutorAliased2, checker, false);
        runTest(null, criteriaExecutorAliased3, checker, false);
        runTest(null, criteriaExecutorAliased4, checker, false);
    }

    @Test
    public void testEntityWithJoinFetchedLazyManyToOneUsingProjectionList() throws Exception {
        // unaliased
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "s", Criteria.LEFT_JOIN).setFetchMode("student", JOIN).setFetchMode("student.preferredCourse", JOIN).setProjection(Projections.projectionList().add(Projections.property("s.name")).add(Projections.property("e.student"))).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s.name, s from Enrolment e left join e.student s left join fetch s.preferredCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(yogiExpected.getName(), yogiObjects[0]);
                Assert.assertEquals(shermanExpected.getName(), shermanObjects[0]);
                // The following fails for criteria due to HHH-1425
                // assertEquals( yogiExpected, yogiObjects[ 1 ] );
                // assertEquals( shermanExpected, shermanObjects[ 1 ] );
                Assert.assertEquals(yogiExpected.getStudentNumber(), ((Student) (yogiObjects[1])).getStudentNumber());
                Assert.assertEquals(shermanExpected.getStudentNumber(), ((Student) (shermanObjects[1])).getStudentNumber());
                if (areDynamicNonLazyAssociationsChecked()) {
                    // The following fails for criteria due to HHH-1425
                    // assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() ) );
                    // assertEquals( yogiExpected.getPreferredCourse(),  ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() );
                    // assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() ) );
                    // assertEquals( shermanExpected.getPreferredCourse(),  ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() );
                }
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyOneToManySingleElementListCriteria() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.enrolments", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased1 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.enrolments", "e", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased2 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.enrolments", "e", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertNotNull(((Student) (resultList.get(0))).getEnrolments());
                Assert.assertNotNull(((Student) (resultList.get(1))).getEnrolments());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (resultList.get(0))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(1))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (resultList.get(1))).getEnrolments());
                }
            }
        };
        runTest(null, criteriaExecutorUnaliased, checker, false);
        runTest(null, criteriaExecutorAliased1, checker, false);
        runTest(null, criteriaExecutorAliased2, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyOneToManyMultiAndNullListCriteria() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.addresses", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased1 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.addresses", "a", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased2 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.addresses", "a", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(3, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertSame(resultList.get(0), resultList.get(1));
                Assert.assertEquals(shermanExpected, resultList.get(2));
                Assert.assertNotNull(((Student) (resultList.get(0))).getAddresses());
                Assert.assertNotNull(((Student) (resultList.get(2))).getAddresses());
                Assert.assertNotNull(((Student) (resultList.get(1))).getAddresses());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (resultList.get(0))).getAddresses()));
                    Assert.assertEquals(yogiExpected.getAddresses(), ((Student) (resultList.get(0))).getAddresses());
                    Assert.assertTrue(((Student) (resultList.get(2))).getAddresses().isEmpty());
                }
            }
        };
        runTest(null, criteriaExecutorUnaliased, checker, false);
        runTest(null, criteriaExecutorAliased1, checker, false);
        runTest(null, criteriaExecutorAliased2, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyManyToOneListCriteria() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.preferredCourse", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased1 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createCriteria("s.preferredCourse", "p", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutorAliased2 = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use RootEntityTransformer by default
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "p", Criteria.LEFT_JOIN).addOrder(Order.asc("s.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiExpected, resultList.get(0));
                Assert.assertEquals(shermanExpected, resultList.get(1));
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Student) (resultList.get(0))).getPreferredCourse().getCourseCode());
                Assert.assertNull(((Student) (resultList.get(1))).getPreferredCourse());
            }
        };
        runTest(null, criteriaExecutorUnaliased, checker, false);
        runTest(null, criteriaExecutorAliased1, checker, false);
        runTest(null, criteriaExecutorAliased2, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyOneToManySingleElementListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.enrolments order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorAliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.enrolments e order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertTrue(((resultList.get(0)) instanceof Object[]));
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiEnrolmentExpected, yogiObjects[1]);
                Assert.assertTrue(((resultList.get(0)) instanceof Object[]));
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertEquals(shermanEnrolmentExpected, shermanObjects[1]);
            }
        };
        runTest(hqlExecutorUnaliased, null, checker, false);
        runTest(hqlExecutorAliased, null, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyOneToManyMultiAndNullListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.addresses order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorAliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("from Student s left join s.addresses a order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(3, resultList.size());
                Assert.assertTrue(((resultList.get(0)) instanceof Object[]));
                Object[] yogiObjects1 = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects1[0]);
                Address address1 = ((Address) (yogiObjects1[1]));
                Assert.assertEquals(yogiExpected.getAddresses().get(address1.getAddressType()), address1);
                Object[] yogiObjects2 = ((Object[]) (resultList.get(1)));
                Assert.assertSame(yogiObjects1[0], yogiObjects2[0]);
                Address address2 = ((Address) (yogiObjects2[1]));
                Assert.assertEquals(yogiExpected.getAddresses().get(address2.getAddressType()), address2);
                Assert.assertFalse(address1.getAddressType().equals(address2.getAddressType()));
                Object[] shermanObjects = ((Object[]) (resultList.get(2)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertNull(shermanObjects[1]);
            }
        };
        runTest(hqlExecutorUnaliased, null, checker, false);
        runTest(hqlExecutorAliased, null, checker, false);
    }

    @Test
    public void testEntityWithJoinedLazyManyToOneListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorUnaliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            protected Query getQuery(Session s) {
                // should use RootEntityTransformer by default
                return s.createQuery("from Student s left join s.preferredCourse order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutorAliased = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            protected Query getQuery(Session s) {
                // should use RootEntityTransformer by default
                return s.createQuery("from Student s left join s.preferredCourse p order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiObjects[1]);
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanObjects[0]);
                Assert.assertNull(shermanObjects[1]);
            }
        };
        runTest(hqlExecutorUnaliased, null, checker, false);
        runTest(hqlExecutorAliased, null, checker, false);
    }

    @Test
    public void testAliasToEntityMapOneProjectionList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.property("e.student").as("student")).addOrder(Order.asc("e.studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student as student from Enrolment e order by e.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(1, yogiMap.size());
                Assert.assertEquals(1, shermanMap.size());
                // TODO: following are initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
                // assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
                Assert.assertTrue(((yogiMap.get("student")) instanceof Student));
                Assert.assertTrue(((shermanMap.get("student")) instanceof Student));
                Assert.assertEquals(yogiExpected.getStudentNumber(), ((Student) (yogiMap.get("student"))).getStudentNumber());
                Assert.assertEquals(shermanExpected.getStudentNumber(), ((Student) (shermanMap.get("student"))).getStudentNumber());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToEntityMapMultiProjectionList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.projectionList().add(Property.forName("e.student"), "student").add(Property.forName("e.semester"), "semester").add(Property.forName("e.year"), "year").add(Property.forName("e.course"), "course")).addOrder(Order.asc("studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student as student, e.semester as semester, e.year as year, e.course as course from Enrolment e order by e.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(4, yogiMap.size());
                Assert.assertEquals(4, shermanMap.size());
                Assert.assertTrue(((yogiMap.get("student")) instanceof Student));
                Assert.assertTrue(((shermanMap.get("student")) instanceof Student));
                // TODO: following are initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
                // assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
                Assert.assertEquals(yogiExpected.getStudentNumber(), ((Student) (yogiMap.get("student"))).getStudentNumber());
                Assert.assertEquals(shermanExpected.getStudentNumber(), ((Student) (shermanMap.get("student"))).getStudentNumber());
                Assert.assertEquals(yogiEnrolmentExpected.getSemester(), yogiMap.get("semester"));
                Assert.assertEquals(yogiEnrolmentExpected.getYear(), yogiMap.get("year"));
                Assert.assertEquals(courseExpected, yogiMap.get("course"));
                Assert.assertEquals(shermanEnrolmentExpected.getSemester(), shermanMap.get("semester"));
                Assert.assertEquals(shermanEnrolmentExpected.getYear(), shermanMap.get("year"));
                Assert.assertEquals(courseExpected, shermanMap.get("course"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToEntityMapMultiProjectionWithNullAliasList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.projectionList().add(Property.forName("e.student"), "student").add(Property.forName("e.semester")).add(Property.forName("e.year")).add(Property.forName("e.course"), "course")).addOrder(Order.asc("e.studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student as student, e.semester, e.year, e.course as course from Enrolment e order by e.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Map shermanMap = ((Map) (resultList.get(1)));
                // TODO: following are initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
                // assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
                Assert.assertTrue(((yogiMap.get("student")) instanceof Student));
                Assert.assertEquals(yogiExpected.getStudentNumber(), ((Student) (yogiMap.get("student"))).getStudentNumber());
                Assert.assertEquals(shermanExpected.getStudentNumber(), ((Student) (shermanMap.get("student"))).getStudentNumber());
                Assert.assertNull(yogiMap.get("semester"));
                Assert.assertNull(yogiMap.get("year"));
                Assert.assertEquals(courseExpected, yogiMap.get("course"));
                Assert.assertNull(shermanMap.get("semester"));
                Assert.assertNull(shermanMap.get("year"));
                Assert.assertEquals(courseExpected, shermanMap.get("course"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToEntityMapMultiAggregatedPropProjectionSingleResult() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class).setProjection(Projections.projectionList().add(Projections.min("studentNumber").as("minStudentNumber")).add(Projections.max("studentNumber").as("maxStudentNumber"))).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Map));
                Map resultMap = ((Map) (results));
                Assert.assertEquals(2, resultMap.size());
                Assert.assertEquals(yogiExpected.getStudentNumber(), resultMap.get("minStudentNumber"));
                Assert.assertEquals(shermanExpected.getStudentNumber(), resultMap.get("maxStudentNumber"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testOneNonEntityProjectionUnique() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.property("e.semester")).add(Restrictions.eq("e.studentNumber", shermanEnrolmentExpected.getStudentNumber()));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.semester from Enrolment e where e.studentNumber = :studentNumber").setParameter("studentNumber", shermanEnrolmentExpected.getStudentNumber());
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Short));
                Assert.assertEquals(Short.valueOf(shermanEnrolmentExpected.getSemester()), results);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testOneNonEntityProjectionList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.property("e.semester")).addOrder(Order.asc("e.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.semester from Enrolment e order by e.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertEquals(yogiEnrolmentExpected.getSemester(), resultList.get(0));
                Assert.assertEquals(shermanEnrolmentExpected.getSemester(), resultList.get(1));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testListElementsProjectionList() throws Exception {
        /* CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
        protected Criteria getCriteria(Session s) {
        // should use PassThroughTransformer by default
        return s.createCriteria( Student.class, "s" )
        .createCriteria( "s.secretCodes" )
        .setProjection( Projections.property( "s.secretCodes" ) )
        .addOrder( Order.asc( "s.studentNumber") );
        }
        };
         */
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select elements(s.secretCodes) from Student s");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(3, resultList.size());
                Assert.assertTrue(resultList.contains(yogiExpected.getSecretCodes().get(0)));
                Assert.assertTrue(resultList.contains(shermanExpected.getSecretCodes().get(0)));
                Assert.assertTrue(resultList.contains(shermanExpected.getSecretCodes().get(1)));
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testOneEntityProjectionUnique() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class).setProjection(Projections.property("student")).add(Restrictions.eq("studentNumber", Long.valueOf(yogiExpected.getStudentNumber())));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student from Enrolment e where e.studentNumber = :studentNumber").setParameter("studentNumber", Long.valueOf(yogiExpected.getStudentNumber()));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Student));
                Student student = ((Student) (results));
                // TODO: following is initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( student ) );
                Assert.assertEquals(yogiExpected.getStudentNumber(), student.getStudentNumber());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testOneEntityProjectionList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            // should use PassThroughTransformer by default
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.property("e.student")).addOrder(Order.asc("e.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student from Enrolment e order by e.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                // TODO: following is initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( resultList.get( 0 ) ) );
                // assertFalse( Hibernate.isInitialized( resultList.get( 1 ) ) );
                Assert.assertEquals(yogiExpected.getStudentNumber(), ((Student) (resultList.get(0))).getStudentNumber());
                Assert.assertEquals(shermanExpected.getStudentNumber(), ((Student) (resultList.get(1))).getStudentNumber());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiEntityProjectionUnique() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class).setProjection(Projections.projectionList().add(Property.forName("student")).add(Property.forName("semester")).add(Property.forName("year")).add(Property.forName("course"))).add(Restrictions.eq("studentNumber", Long.valueOf(shermanEnrolmentExpected.getStudentNumber())));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student, e.semester, e.year, e.course from Enrolment e  where e.studentNumber = :studentNumber").setParameter("studentNumber", shermanEnrolmentExpected.getStudentNumber());
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Object[]));
                Object[] shermanObjects = ((Object[]) (results));
                Assert.assertEquals(4, shermanObjects.length);
                Assert.assertNotNull(shermanObjects[0]);
                Assert.assertTrue(((shermanObjects[0]) instanceof Student));
                // TODO: following is initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
                Assert.assertEquals(shermanEnrolmentExpected.getSemester(), ((Short) (shermanObjects[1])).shortValue());
                Assert.assertEquals(shermanEnrolmentExpected.getYear(), ((Short) (shermanObjects[2])).shortValue());
                Assert.assertTrue((!((shermanObjects[3]) instanceof HibernateProxy)));
                Assert.assertTrue(((shermanObjects[3]) instanceof Course));
                Assert.assertEquals(courseExpected, shermanObjects[3]);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testMultiEntityProjectionList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.projectionList().add(Property.forName("e.student")).add(Property.forName("e.semester")).add(Property.forName("e.year")).add(Property.forName("e.course"))).addOrder(Order.asc("e.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student, e.semester, e.year, e.course from Enrolment e order by e.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(4, yogiObjects.length);
                // TODO: following is initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
                // assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
                Assert.assertTrue(((yogiObjects[0]) instanceof Student));
                Assert.assertTrue(((shermanObjects[0]) instanceof Student));
                Assert.assertEquals(yogiEnrolmentExpected.getSemester(), ((Short) (yogiObjects[1])).shortValue());
                Assert.assertEquals(yogiEnrolmentExpected.getYear(), ((Short) (yogiObjects[2])).shortValue());
                Assert.assertEquals(courseExpected, yogiObjects[3]);
                Assert.assertEquals(shermanEnrolmentExpected.getSemester(), ((Short) (shermanObjects[1])).shortValue());
                Assert.assertEquals(shermanEnrolmentExpected.getYear(), ((Short) (shermanObjects[2])).shortValue());
                Assert.assertTrue(((shermanObjects[3]) instanceof Course));
                Assert.assertEquals(courseExpected, shermanObjects[3]);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiEntityProjectionAliasedList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                // should use PassThroughTransformer by default
                return s.createCriteria(Enrolment.class, "e").setProjection(Projections.projectionList().add(Property.forName("e.student").as("st")).add(Property.forName("e.semester").as("sem")).add(Property.forName("e.year").as("yr")).add(Property.forName("e.course").as("c"))).addOrder(Order.asc("e.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select e.student as st, e.semester as sem, e.year as yr, e.course as c from Enrolment e order by e.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Object[] yogiObjects = ((Object[]) (resultList.get(0)));
                Object[] shermanObjects = ((Object[]) (resultList.get(1)));
                Assert.assertEquals(4, yogiObjects.length);
                // TODO: following is initialized for hql and uninitialied for criteria; why?
                // assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
                // assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
                Assert.assertTrue(((yogiObjects[0]) instanceof Student));
                Assert.assertTrue(((shermanObjects[0]) instanceof Student));
                Assert.assertEquals(yogiEnrolmentExpected.getSemester(), ((Short) (yogiObjects[1])).shortValue());
                Assert.assertEquals(yogiEnrolmentExpected.getYear(), ((Short) (yogiObjects[2])).shortValue());
                Assert.assertEquals(courseExpected, yogiObjects[3]);
                Assert.assertEquals(shermanEnrolmentExpected.getSemester(), ((Short) (shermanObjects[1])).shortValue());
                Assert.assertEquals(shermanEnrolmentExpected.getYear(), ((Short) (shermanObjects[2])).shortValue());
                Assert.assertTrue(((shermanObjects[3]) instanceof Course));
                Assert.assertEquals(courseExpected, shermanObjects[3]);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testSingleAggregatedPropProjectionSingleResult() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class).setProjection(Projections.min("studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select min( e.studentNumber ) from Enrolment e");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Long));
                Assert.assertEquals(Long.valueOf(yogiExpected.getStudentNumber()), results);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testMultiAggregatedPropProjectionSingleResult() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class).setProjection(Projections.projectionList().add(Projections.min("studentNumber").as("minStudentNumber")).add(Projections.max("studentNumber").as("maxStudentNumber")));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Object[]));
                Object[] resultObjects = ((Object[]) (results));
                Assert.assertEquals(Long.valueOf(yogiExpected.getStudentNumber()), resultObjects[0]);
                Assert.assertEquals(Long.valueOf(shermanExpected.getStudentNumber()), resultObjects[1]);
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, true);
    }

    @Test
    public void testAliasToBeanDtoOneArgList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "st").createAlias("e.course", "co").setProjection(Projections.property("st.name").as("studentName")).addOrder(Order.asc("st.studentNumber")).setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select st.name as studentName from Student st order by st.studentNumber").setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO dto = ((StudentDTO) (resultList.get(0)));
                Assert.assertNull(dto.getDescription());
                Assert.assertEquals(yogiExpected.getName(), dto.getName());
                dto = ((StudentDTO) (resultList.get(1)));
                Assert.assertNull(dto.getDescription());
                Assert.assertEquals(shermanExpected.getName(), dto.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToBeanDtoMultiArgList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "st").createAlias("e.course", "co").setProjection(Projections.projectionList().add(Property.forName("st.name").as("studentName")).add(Property.forName("co.description").as("courseDescription"))).addOrder(Order.asc("e.studentNumber")).setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber").setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO dto = ((StudentDTO) (resultList.get(0)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(yogiExpected.getName(), dto.getName());
                dto = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(shermanExpected.getName(), dto.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiProjectionListThenApplyAliasToBean() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "st").createAlias("e.course", "co").setProjection(Projections.projectionList().add(Property.forName("st.name")).add(Property.forName("co.description"))).addOrder(Order.asc("e.studentNumber"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                ResultTransformer transformer = Transformers.aliasToBean(StudentDTO.class);
                String[] aliases = new String[]{ "studentName", "courseDescription" };
                for (int i = 0; i < (resultList.size()); i++) {
                    resultList.set(i, transformer.transformTuple(((Object[]) (resultList.get(i))), aliases));
                }
                Assert.assertEquals(2, resultList.size());
                StudentDTO dto = ((StudentDTO) (resultList.get(0)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(yogiExpected.getName(), dto.getName());
                dto = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(shermanExpected.getName(), dto.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToBeanDtoLiteralArgList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "st").createAlias("e.course", "co").setProjection(Projections.projectionList().add(Property.forName("st.name").as("studentName")).add(Projections.sqlProjection("'lame description' as courseDescription", new String[]{ "courseDescription" }, new Type[]{ StandardBasicTypes.STRING }))).addOrder(Order.asc("e.studentNumber")).setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select st.name as studentName, 'lame description' as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber").setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO dto = ((StudentDTO) (resultList.get(0)));
                Assert.assertEquals("lame description", dto.getDescription());
                Assert.assertEquals(yogiExpected.getName(), dto.getName());
                dto = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals("lame description", dto.getDescription());
                Assert.assertEquals(shermanExpected.getName(), dto.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testAliasToBeanDtoWithNullAliasList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Enrolment.class, "e").createAlias("e.student", "st").createAlias("e.course", "co").setProjection(Projections.projectionList().add(Property.forName("st.name").as("studentName")).add(Property.forName("st.studentNumber")).add(Property.forName("co.description").as("courseDescription"))).addOrder(Order.asc("e.studentNumber")).setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber").setResultTransformer(Transformers.aliasToBean(StudentDTO.class));
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO dto = ((StudentDTO) (resultList.get(0)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(yogiExpected.getName(), dto.getName());
                dto = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals(courseExpected.getDescription(), dto.getDescription());
                Assert.assertEquals(shermanExpected.getName(), dto.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testOneSelectNewNoAliasesList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) throws Exception {
                return s.createCriteria(Student.class, "s").setProjection(Projections.property("s.name")).addOrder(Order.asc("s.studentNumber")).setResultTransformer(new AliasToBeanConstructorResultTransformer(getConstructor()));
            }

            private Constructor getConstructor() throws NoSuchMethodException {
                return StudentDTO.class.getConstructor(PersonName.class);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO yogi = ((StudentDTO) (resultList.get(0)));
                Assert.assertNull(yogi.getDescription());
                Assert.assertEquals(yogiExpected.getName(), yogi.getName());
                StudentDTO sherman = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getName(), sherman.getName());
                Assert.assertNull(sherman.getDescription());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testOneSelectNewAliasesList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) throws Exception {
                return s.createCriteria(Student.class, "s").setProjection(Projections.property("s.name").as("name")).addOrder(Order.asc("s.studentNumber")).setResultTransformer(new AliasToBeanConstructorResultTransformer(getConstructor()));
            }

            private Constructor getConstructor() throws NoSuchMethodException {
                return StudentDTO.class.getConstructor(PersonName.class);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                StudentDTO yogi = ((StudentDTO) (resultList.get(0)));
                Assert.assertNull(yogi.getDescription());
                Assert.assertEquals(yogiExpected.getName(), yogi.getName());
                StudentDTO sherman = ((StudentDTO) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getName(), sherman.getName());
                Assert.assertNull(sherman.getDescription());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectNewList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) throws Exception {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Property.forName("s.studentNumber").as("studentNumber")).add(Property.forName("s.name").as("name"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(new AliasToBeanConstructorResultTransformer(getConstructor()));
            }

            private Constructor getConstructor() throws NoSuchMethodException {
                return Student.class.getConstructor(long.class, PersonName.class);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Student yogi = ((Student) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getStudentNumber(), yogi.getStudentNumber());
                Assert.assertEquals(yogiExpected.getName(), yogi.getName());
                Student sherman = ((Student) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getStudentNumber(), sherman.getStudentNumber());
                Assert.assertEquals(shermanExpected.getName(), sherman.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectNewWithLiteralList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) throws Exception {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Projections.sqlProjection("555 as studentNumber", new String[]{ "studentNumber" }, new Type[]{ StandardBasicTypes.LONG })).add(Property.forName("s.name").as("name"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(new AliasToBeanConstructorResultTransformer(getConstructor()));
            }

            private Constructor getConstructor() throws NoSuchMethodException {
                return Student.class.getConstructor(long.class, PersonName.class);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new Student(555L, s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Student yogi = ((Student) (resultList.get(0)));
                Assert.assertEquals(555L, yogi.getStudentNumber());
                Assert.assertEquals(yogiExpected.getName(), yogi.getName());
                Student sherman = ((Student) (resultList.get(1)));
                Assert.assertEquals(555L, sherman.getStudentNumber());
                Assert.assertEquals(shermanExpected.getName(), sherman.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectNewListList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Property.forName("s.studentNumber").as("studentNumber")).add(Property.forName("s.name").as("name"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(TO_LIST);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new list(s.studentNumber, s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                List yogiList = ((List) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getStudentNumber(), yogiList.get(0));
                Assert.assertEquals(yogiExpected.getName(), yogiList.get(1));
                List shermanList = ((List) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getStudentNumber(), shermanList.get(0));
                Assert.assertEquals(shermanExpected.getName(), shermanList.get(1));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectNewMapUsingAliasesList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Property.forName("s.studentNumber").as("sNumber")).add(Property.forName("s.name").as("sName"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getStudentNumber(), yogiMap.get("sNumber"));
                Assert.assertEquals(yogiExpected.getName(), yogiMap.get("sName"));
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getStudentNumber(), shermanMap.get("sNumber"));
                Assert.assertEquals(shermanExpected.getName(), shermanMap.get("sName"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectNewMapUsingAliasesWithFetchJoinList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "pc", Criteria.LEFT_JOIN).setFetchMode("enrolments", JOIN).addOrder(Order.asc("s.studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlSelectNewMapExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new map(s as s, pc as pc) from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiMap.get("s"));
                Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiMap.get("pc"));
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanMap.get("s"));
                Assert.assertNull(shermanMap.get("pc"));
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiMap.get("s"))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiMap.get("s"))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanMap.get("s"))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanMap.get("s"))).getEnrolments());
                }
            }
        };
        runTest(hqlSelectNewMapExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMultiSelectAliasToEntityMapUsingAliasesWithFetchJoinList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.preferredCourse", "pc", Criteria.LEFT_JOIN).setFetchMode("enrolments", JOIN).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlAliasToEntityMapExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s as s, pc as pc from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Assert.assertEquals(yogiExpected, yogiMap.get("s"));
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Course) (yogiMap.get("pc"))).getCourseCode());
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(shermanExpected, shermanMap.get("s"));
                Assert.assertNull(shermanMap.get("pc"));
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiMap.get("pc"));
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiMap.get("s"))).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiMap.get("s"))).getEnrolments());
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (shermanMap.get("s"))).getEnrolments()));
                    Assert.assertEquals(shermanExpected.getEnrolments(), ((Student) (shermanMap.get("s"))).getEnrolments());
                }
            }
        };
        runTest(hqlAliasToEntityMapExecutor, null, checker, false);
    }

    @Test
    public void testMultiSelectUsingImplicitJoinWithFetchJoinListHql() throws Exception {
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select s as s, s.preferredCourse as pc from Student s left join fetch s.enrolments");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                Assert.assertTrue((results instanceof Object[]));
                Object[] yogiObjects = ((Object[]) (results));
                Assert.assertEquals(2, yogiObjects.length);
                Assert.assertEquals(yogiExpected, yogiObjects[0]);
                Assert.assertEquals(yogiExpected.getPreferredCourse().getCourseCode(), ((Course) (yogiObjects[1])).getCourseCode());
                if (areDynamicNonLazyAssociationsChecked()) {
                    Assert.assertEquals(yogiExpected.getPreferredCourse(), yogiObjects[1]);
                    Assert.assertTrue(Hibernate.isInitialized(((Student) (yogiObjects[0])).getEnrolments()));
                    Assert.assertEquals(yogiExpected.getEnrolments(), ((Student) (yogiObjects[0])).getEnrolments());
                }
            }
        };
        runTest(hqlExecutor, null, checker, true);
    }

    @Test
    public void testSelectNewMapUsingAliasesList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Property.forName("s.studentNumber").as("sNumber")).add(Property.forName("s.name").as("sName"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Map yogiMap = ((Map) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getStudentNumber(), yogiMap.get("sNumber"));
                Assert.assertEquals(yogiExpected.getName(), yogiMap.get("sName"));
                Map shermanMap = ((Map) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getStudentNumber(), shermanMap.get("sNumber"));
                Assert.assertEquals(shermanExpected.getName(), shermanMap.get("sName"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testSelectNewEntityConstructorList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").setProjection(Projections.projectionList().add(Property.forName("s.studentNumber").as("studentNumber")).add(Property.forName("s.name").as("name"))).addOrder(Order.asc("s.studentNumber")).setResultTransformer(new AliasToBeanConstructorResultTransformer(getConstructor()));
            }

            private Constructor getConstructor() {
                Type studentNametype = sessionFactory().getEntityPersister(Student.class.getName()).getPropertyType("name");
                return ReflectHelper.getConstructor(Student.class, new Type[]{ StandardBasicTypes.LONG, studentNametype });
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Student yogi = ((Student) (resultList.get(0)));
                Assert.assertEquals(yogiExpected.getStudentNumber(), yogi.getStudentNumber());
                Assert.assertEquals(yogiExpected.getName(), yogi.getName());
                Student sherman = ((Student) (resultList.get(1)));
                Assert.assertEquals(shermanExpected.getStudentNumber(), sherman.getStudentNumber());
                Assert.assertEquals(shermanExpected.getName(), sherman.getName());
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMapKeyList() throws Exception {
        AbstractQueryCacheResultTransformerTest.CriteriaExecutor criteriaExecutor = new AbstractQueryCacheResultTransformerTest.CriteriaExecutor() {
            protected Criteria getCriteria(Session s) {
                return s.createCriteria(Student.class, "s").createAlias("s.addresses", "a").setProjection(Projections.property("a.addressType"));
            }
        };
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select key(s.addresses) from Student s");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertTrue(resultList.contains("home"));
                Assert.assertTrue(resultList.contains("work"));
            }
        };
        runTest(hqlExecutor, criteriaExecutor, checker, false);
    }

    @Test
    public void testMapValueList() throws Exception {
        /* CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
        protected Criteria getCriteria(Session s) {
        return s.createCriteria( Student.class, "s" )
        .createAlias( "s.addresses", "a" )
        .setProjection( Projections.property( "s.addresses" ));
        }
        };
         */
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select value(s.addresses) from Student s");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertTrue(resultList.contains(yogiExpected.getAddresses().get("home")));
                Assert.assertTrue(resultList.contains(yogiExpected.getAddresses().get("work")));
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testMapEntryList() throws Exception {
        /* CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
        protected Criteria getCriteria(Session s) {
        return s.createCriteria( Student.class, "s" )
        .createAlias( "s.addresses", "a" )
        .setProjection(
        Projections.projectionList()
        .add( Projections.property( "a.addressType" ) )
        .add( Projections.property( "s.addresses" ).as( "a" ) );
        )
        }
        };
         */
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select entry(s.addresses) from Student s");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Iterator it = resultList.iterator();
                Assert.assertTrue(((resultList.get(0)) instanceof Map.Entry));
                Map.Entry entry = ((Map.Entry) (it.next()));
                if ("home".equals(entry.getKey())) {
                    Assert.assertTrue(yogiExpected.getAddresses().get("home").equals(entry.getValue()));
                    entry = ((Map.Entry) (it.next()));
                    Assert.assertTrue(yogiExpected.getAddresses().get("work").equals(entry.getValue()));
                } else {
                    Assert.assertTrue("work".equals(entry.getKey()));
                    Assert.assertTrue(yogiExpected.getAddresses().get("work").equals(entry.getValue()));
                    entry = ((Map.Entry) (it.next()));
                    Assert.assertTrue(yogiExpected.getAddresses().get("home").equals(entry.getValue()));
                }
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }

    @Test
    public void testMapElementsList() throws Exception {
        /* CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
        protected Criteria getCriteria(Session s) {
        return s.createCriteria( Student.class, "s" )
        .createAlias( "s.addresses", "a", Criteria.INNER_JOIN )
        .setProjection( Projections.property( "s.addresses" ) );
        }
        };
         */
        AbstractQueryCacheResultTransformerTest.HqlExecutor hqlExecutor = new AbstractQueryCacheResultTransformerTest.HqlExecutor() {
            public Query getQuery(Session s) {
                return s.createQuery("select elements(a) from Student s inner join s.addresses a");
            }
        };
        AbstractQueryCacheResultTransformerTest.ResultChecker checker = new AbstractQueryCacheResultTransformerTest.ResultChecker() {
            public void check(Object results) {
                List resultList = ((List) (results));
                Assert.assertEquals(2, resultList.size());
                Assert.assertTrue(resultList.contains(yogiExpected.getAddresses().get("home")));
                Assert.assertTrue(resultList.contains(yogiExpected.getAddresses().get("work")));
            }
        };
        runTest(hqlExecutor, null, checker, false);
    }
}

