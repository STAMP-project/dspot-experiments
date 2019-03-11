/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.spi;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for making sure that any set entity entry extra state is propagated from temporary to final entity entries.
 *
 * @author Gunnar Morling
 */
@RequiresDialectFeature(value = SupportsIdentityColumns.class, jiraKey = "HHH-9918")
public class ExtraStateTest extends BaseCoreFunctionalTestCase {
    /**
     * Storing it as a field so it can be accessed from the entity setter.
     */
    private Session session;

    @Test
    @TestForIssue(jiraKey = "HHH-9451")
    public void shouldMaintainExtraStateWhenUsingIdentityIdGenerationStrategy() {
        session = openSession();
        session.getTransaction().begin();
        ExtraStateTest.ChineseTakeawayRestaurant mrKim = new ExtraStateTest.ChineseTakeawayRestaurant();
        mrKim.setGobelinStars(3);
        // As a side-effect, the id setter will populate the test extra state
        session.persist(mrKim);
        session.getTransaction().commit();
        ExtraStateTest.TestExtraState extraState = getEntityEntry(mrKim).getExtraState(ExtraStateTest.TestExtraState.class);
        Assert.assertNotNull("Test extra state was not propagated from temporary to final entity entry", extraState);
        Assert.assertEquals(311, extraState.getValue());
        session.close();
    }

    @Entity
    @Table(name = "ChineseTakeawayRestaurant")
    public class ChineseTakeawayRestaurant {
        private long id;

        private int gobelinStars;

        public ChineseTakeawayRestaurant() {
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        public long getId() {
            return id;
        }

        /**
         * Sets the test extra state as a side effect
         */
        public void setId(long id) {
            getEntityEntry(this).addExtraState(new ExtraStateTest.TestExtraState(311));
            this.id = id;
        }

        public int getGobelinStars() {
            return gobelinStars;
        }

        public void setGobelinStars(int gobelinStars) {
            this.gobelinStars = gobelinStars;
        }
    }

    private static class TestExtraState implements EntityEntryExtraState {
        private final long value;

        public TestExtraState(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        @Override
        public void addExtraState(EntityEntryExtraState extraState) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
            throw new UnsupportedOperationException();
        }
    }
}

