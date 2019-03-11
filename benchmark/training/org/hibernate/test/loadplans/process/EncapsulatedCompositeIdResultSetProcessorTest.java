/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.process;


import LockOptions.NONE;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.Session;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EncapsulatedCompositeIdResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSimpleCompositeId() throws Exception {
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeIdResultSetProcessorTest.Parent parent = new EncapsulatedCompositeIdResultSetProcessorTest.Parent();
        parent.id = new EncapsulatedCompositeIdResultSetProcessorTest.ParentPK();
        parent.id.firstName = "Joe";
        parent.id.lastName = "Blow";
        session.save(parent);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeIdResultSetProcessorTest.Parent parentGotten = ((EncapsulatedCompositeIdResultSetProcessorTest.Parent) (session.get(EncapsulatedCompositeIdResultSetProcessorTest.Parent.class, parent.id)));
        Assert.assertEquals(parent, parentGotten);
        session.getTransaction().commit();
        session.close();
        final List results = getResults(sessionFactory().getEntityPersister(EncapsulatedCompositeIdResultSetProcessorTest.Parent.class.getName()), new EncapsulatedCompositeIdResultSetProcessorTest.Callback() {
            @Override
            public void bind(PreparedStatement ps) throws SQLException {
                ps.setString(1, "Joe");
                ps.setString(2, "Blow");
            }

            @Override
            public QueryParameters getQueryParameters() {
                return new QueryParameters();
            }
        });
        Assert.assertEquals(1, results.size());
        Object result = results.get(0);
        Assert.assertNotNull(result);
        EncapsulatedCompositeIdResultSetProcessorTest.Parent parentWork = ExtraAssertions.assertTyping(EncapsulatedCompositeIdResultSetProcessorTest.Parent.class, result);
        Assert.assertEquals(parent, parentWork);
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete Parent").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testCompositeIdWithKeyManyToOne() throws Exception {
        final String cardId = "ace-of-spades";
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeIdResultSetProcessorTest.Card card = new EncapsulatedCompositeIdResultSetProcessorTest.Card(cardId);
        final EncapsulatedCompositeIdResultSetProcessorTest.CardField cardField = new EncapsulatedCompositeIdResultSetProcessorTest.CardField(card, 1);
        session.persist(card);
        session.persist(cardField);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeIdResultSetProcessorTest.Card cardProxy = ((EncapsulatedCompositeIdResultSetProcessorTest.Card) (session.load(EncapsulatedCompositeIdResultSetProcessorTest.Card.class, cardId)));
        final EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK cardFieldPK = new EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK(cardProxy, 1);
        EncapsulatedCompositeIdResultSetProcessorTest.CardField cardFieldGotten = ((EncapsulatedCompositeIdResultSetProcessorTest.CardField) (session.get(EncapsulatedCompositeIdResultSetProcessorTest.CardField.class, cardFieldPK)));
        // assertEquals( card, cardGotten );
        session.getTransaction().commit();
        session.close();
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(EncapsulatedCompositeIdResultSetProcessorTest.CardField.class.getName());
        final List results = getResults(entityPersister, new EncapsulatedCompositeIdResultSetProcessorTest.Callback() {
            @Override
            public void bind(PreparedStatement ps) throws SQLException {
                ps.setString(1, cardField.primaryKey.card.id);
                ps.setInt(2, cardField.primaryKey.fieldNumber);
            }

            @Override
            public QueryParameters getQueryParameters() {
                QueryParameters qp = new QueryParameters();
                qp.setPositionalParameterTypes(new Type[]{ entityPersister.getIdentifierType() });
                qp.setPositionalParameterValues(new Object[]{ cardFieldPK });
                qp.setOptionalObject(null);
                qp.setOptionalEntityName(entityPersister.getEntityName());
                qp.setOptionalId(cardFieldPK);
                qp.setLockOptions(NONE);
                return qp;
            }
        });
        Assert.assertEquals(1, results.size());
        Object result = results.get(0);
        Assert.assertNotNull(result);
        EncapsulatedCompositeIdResultSetProcessorTest.CardField cardFieldWork = ExtraAssertions.assertTyping(EncapsulatedCompositeIdResultSetProcessorTest.CardField.class, result);
        Assert.assertEquals(cardFieldGotten, cardFieldWork);
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete CardField").executeUpdate();
        session.createQuery("delete Card").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private interface Callback {
        void bind(PreparedStatement ps) throws SQLException;

        QueryParameters getQueryParameters();
    }

    @Entity(name = "Parent")
    public static class Parent {
        @EmbeddedId
        public EncapsulatedCompositeIdResultSetProcessorTest.ParentPK id;

        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof EncapsulatedCompositeIdResultSetProcessorTest.Parent))
                return false;

            final EncapsulatedCompositeIdResultSetProcessorTest.Parent parent = ((EncapsulatedCompositeIdResultSetProcessorTest.Parent) (o));
            if (!(id.equals(parent.id)))
                return false;

            return true;
        }

        public int hashCode() {
            return id.hashCode();
        }
    }

    @Embeddable
    public static class ParentPK implements Serializable {
        private String firstName;

        private String lastName;

        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof EncapsulatedCompositeIdResultSetProcessorTest.ParentPK))
                return false;

            final EncapsulatedCompositeIdResultSetProcessorTest.ParentPK parentPk = ((EncapsulatedCompositeIdResultSetProcessorTest.ParentPK) (o));
            if (!(firstName.equals(parentPk.firstName)))
                return false;

            if (!(lastName.equals(parentPk.lastName)))
                return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = firstName.hashCode();
            result = (29 * result) + (lastName.hashCode());
            return result;
        }
    }

    @Entity(name = "CardField")
    public static class CardField implements Serializable {
        @EmbeddedId
        private EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK primaryKey;

        CardField(EncapsulatedCompositeIdResultSetProcessorTest.Card card, int fieldNumber) {
            this.primaryKey = new EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK(card, fieldNumber);
        }

        CardField() {
        }

        public EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK getPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK primaryKey) {
            this.primaryKey = primaryKey;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EncapsulatedCompositeIdResultSetProcessorTest.CardField cardField = ((EncapsulatedCompositeIdResultSetProcessorTest.CardField) (o));
            if ((primaryKey) != null ? !(primaryKey.equals(cardField.primaryKey)) : (cardField.primaryKey) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return (primaryKey) != null ? primaryKey.hashCode() : 0;
        }
    }

    @Embeddable
    public static class CardFieldPK implements Serializable {
        @ManyToOne(optional = false)
        private EncapsulatedCompositeIdResultSetProcessorTest.Card card;

        private int fieldNumber;

        public CardFieldPK(EncapsulatedCompositeIdResultSetProcessorTest.Card card, int fieldNumber) {
            this.card = card;
            this.fieldNumber = fieldNumber;
        }

        CardFieldPK() {
        }

        public EncapsulatedCompositeIdResultSetProcessorTest.Card getCard() {
            return card;
        }

        public void setCard(EncapsulatedCompositeIdResultSetProcessorTest.Card card) {
            this.card = card;
        }

        public int getFieldNumber() {
            return fieldNumber;
        }

        public void setFieldNumber(int fieldNumber) {
            this.fieldNumber = fieldNumber;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK that = ((EncapsulatedCompositeIdResultSetProcessorTest.CardFieldPK) (o));
            if ((fieldNumber) != (that.fieldNumber)) {
                return false;
            }
            if ((card) != null ? !(card.equals(that.card)) : (that.card) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = ((card) != null) ? card.hashCode() : 0;
            result = (31 * result) + (fieldNumber);
            return result;
        }
    }

    @Entity(name = "Card")
    public static class Card implements Serializable {
        @Id
        private String id;

        public Card(String id) {
            this();
            this.id = id;
        }

        Card() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EncapsulatedCompositeIdResultSetProcessorTest.Card card = ((EncapsulatedCompositeIdResultSetProcessorTest.Card) (o));
            if (!(id.equals(card.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}

