/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dirtiness;


import java.io.Serializable;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CustomDirtinessStrategyTest extends BaseCoreFunctionalTestCase {
    private static final String INITIAL_NAME = "thing 1";

    private static final String SUBSEQUENT_NAME = "thing 2";

    @Test
    public void testOnlyCustomStrategy() {
        Session session = openSession();
        session.beginTransaction();
        Long id = ((Long) (session.save(new Thing(CustomDirtinessStrategyTest.INITIAL_NAME))));
        session.getTransaction().commit();
        session.close();
        CustomDirtinessStrategyTest.Strategy.INSTANCE.resetState();
        session = openSession();
        session.beginTransaction();
        Thing thing = ((Thing) (session.get(Thing.class, id)));
        thing.setName(CustomDirtinessStrategyTest.SUBSEQUENT_NAME);
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.canDirtyCheckCount);
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.isDirtyCount);
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.resetDirtyCount);
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.findDirtyCount);
        session = openSession();
        session.beginTransaction();
        thing = ((Thing) (session.get(Thing.class, id)));
        Assert.assertEquals(CustomDirtinessStrategyTest.SUBSEQUENT_NAME, thing.getName());
        session.delete(thing);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testCustomStrategyWithFlushInterceptor() {
        Session session = openSession();
        session.beginTransaction();
        Long id = ((Long) (session.save(new Thing(CustomDirtinessStrategyTest.INITIAL_NAME))));
        session.getTransaction().commit();
        session.close();
        CustomDirtinessStrategyTest.Strategy.INSTANCE.resetState();
        session = sessionWithInterceptor().openSession();
        session.beginTransaction();
        Thing thing = ((Thing) (session.get(Thing.class, id)));
        thing.setName(CustomDirtinessStrategyTest.SUBSEQUENT_NAME);
        session.getTransaction().commit();
        session.close();
        // As we used an interceptor, the custom strategy should have been called twice to find dirty properties
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.canDirtyCheckCount);
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.isDirtyCount);
        Assert.assertEquals(1, CustomDirtinessStrategyTest.Strategy.INSTANCE.resetDirtyCount);
        Assert.assertEquals(2, CustomDirtinessStrategyTest.Strategy.INSTANCE.findDirtyCount);
        session = openSession();
        session.beginTransaction();
        thing = ((Thing) (session.get(Thing.class, id)));
        Assert.assertEquals(CustomDirtinessStrategyTest.SUBSEQUENT_NAME, thing.getName());
        session.delete(thing);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testOnlyCustomStrategyConsultedOnNonDirty() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        Long id = ((Long) (session.save(new Thing(CustomDirtinessStrategyTest.INITIAL_NAME))));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        Thing thing = ((Thing) (session.get(Thing.class, id)));
        // lets change the name
        thing.setName(CustomDirtinessStrategyTest.SUBSEQUENT_NAME);
        Assert.assertTrue(CustomDirtinessStrategyTest.Strategy.INSTANCE.isDirty(thing, null, null));
        // but fool the dirty map
        thing.changedValues.clear();
        Assert.assertFalse(CustomDirtinessStrategyTest.Strategy.INSTANCE.isDirty(thing, null, null));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        thing = ((Thing) (session.get(Thing.class, id)));
        Assert.assertEquals(CustomDirtinessStrategyTest.INITIAL_NAME, thing.getName());
        session.createQuery("delete Thing").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public static class Strategy implements CustomEntityDirtinessStrategy {
        public static final CustomDirtinessStrategyTest.Strategy INSTANCE = new CustomDirtinessStrategyTest.Strategy();

        int canDirtyCheckCount = 0;

        @Override
        public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
            (canDirtyCheckCount)++;
            System.out.println("canDirtyCheck called");
            return Thing.class.isInstance(entity);
        }

        int isDirtyCount = 0;

        @Override
        public boolean isDirty(Object entity, EntityPersister persister, Session session) {
            (isDirtyCount)++;
            System.out.println("isDirty called");
            return !(Thing.class.cast(entity).changedValues.isEmpty());
        }

        int resetDirtyCount = 0;

        @Override
        public void resetDirty(Object entity, EntityPersister persister, Session session) {
            (resetDirtyCount)++;
            System.out.println("resetDirty called");
            Thing.class.cast(entity).changedValues.clear();
        }

        int findDirtyCount = 0;

        @Override
        public void findDirty(final Object entity, EntityPersister persister, Session session, DirtyCheckContext dirtyCheckContext) {
            (findDirtyCount)++;
            System.out.println("findDirty called");
            dirtyCheckContext.doDirtyChecking(new AttributeChecker() {
                @Override
                public boolean isDirty(AttributeInformation attributeInformation) {
                    return Thing.class.cast(entity).changedValues.containsKey(attributeInformation.getName());
                }
            });
        }

        void resetState() {
            canDirtyCheckCount = 0;
            isDirtyCount = 0;
            resetDirtyCount = 0;
            findDirtyCount = 0;
        }
    }

    public static class OnFlushDirtyInterceptor extends EmptyInterceptor {
        private static CustomDirtinessStrategyTest.OnFlushDirtyInterceptor INSTANCE = new CustomDirtinessStrategyTest.OnFlushDirtyInterceptor();

        @Override
        public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
            // Tell Hibernate ORM we did change the entity state, which should trigger another dirty check
            return true;
        }
    }
}

