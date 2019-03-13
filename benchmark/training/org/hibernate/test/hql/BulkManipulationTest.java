/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import DialectChecks.HasSelfReferentialForeignKeyBugCheck;
import DialectChecks.SupportsParametersInInsertSelectCheck;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import junit.framework.AssertionFailedError;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jdbc.Work;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.SkipLog;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.hql.Farm.Accreditation.ORGANIC;
import static org.hibernate.test.hql.Farm.Accreditation.SUSTAINABLE;


/**
 * Tests execution of bulk UPDATE/DELETE statements through the new AST parser.
 *
 * @author Steve Ebersole
 */
public class BulkManipulationTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdateWithSubquery() {
        Session s = openSession();
        s.beginTransaction();
        // just checking parsing and syntax...
        s.createQuery("update Human h set h.bodyWeight = h.bodyWeight + (select count(1) from IntegerVersioned)").executeUpdate();
        s.createQuery("update Human h set h.bodyWeight = h.bodyWeight + (select count(1) from IntegerVersioned) where h.description = 'abc'").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testDeleteNonExistentEntity() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("delete NonExistentEntity").executeUpdate();
            Assert.fail("no exception thrown");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException ignore) {
        }
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateNonExistentEntity() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("update NonExistentEntity e set e.someProp = ?").executeUpdate();
            Assert.fail("no exception thrown");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException e) {
        }
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    public void testTempTableGenerationIsolation() throws Throwable {
        Session s = openSession();
        s.beginTransaction();
        Truck truck = new Truck();
        truck.setVin("123t");
        truck.setOwner("Steve");
        s.save(truck);
        // manually flush the session to ensure the insert happens
        s.flush();
        // now issue a bulk delete against Car which should force the temp table to be
        // created.  we need to test to ensure that this does not cause the transaction
        // to be committed...
        s.createQuery("delete from Vehicle").executeUpdate();
        s.getTransaction().rollback();
        s.close();
        s = openSession();
        s.beginTransaction();
        List list = s.createQuery("from Car").list();
        Assert.assertEquals("temp table gen caused premature commit", 0, list.size());
        s.createQuery("delete from Car").executeUpdate();
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    public void testBooleanHandling() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // currently, we need the three different binds because they are different underlying types...
        int count = s.createQuery("update BooleanLiteralEntity set yesNoBoolean = :b1, trueFalseBoolean = :b2, zeroOneBoolean = :b3").setBoolean("b1", true).setBoolean("b2", true).setBoolean("b3", true).executeUpdate();
        Assert.assertEquals(1, count);
        BooleanLiteralEntity entity = ((BooleanLiteralEntity) (s.createQuery("from BooleanLiteralEntity").uniqueResult()));
        Assert.assertTrue(entity.isYesNoBoolean());
        Assert.assertTrue(entity.isTrueFalseBoolean());
        Assert.assertTrue(entity.isZeroOneBoolean());
        s.clear();
        count = s.createQuery("update BooleanLiteralEntity set yesNoBoolean = true, trueFalseBoolean = true, zeroOneBoolean = true").executeUpdate();
        Assert.assertEquals(1, count);
        entity = ((BooleanLiteralEntity) (s.createQuery("from BooleanLiteralEntity").uniqueResult()));
        Assert.assertTrue(entity.isYesNoBoolean());
        Assert.assertTrue(entity.isTrueFalseBoolean());
        Assert.assertTrue(entity.isZeroOneBoolean());
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testSimpleInsert() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("insert into Pickup (id, vin, owner) select id, vin, owner from Car").executeUpdate();
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testSelectWithNamedParamProjection() {
        Session s = openSession();
        try {
            s.createQuery("select :someParameter, id from Car");
            Assert.fail("Should throw an unsupported exception");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException q) {
            // allright
        } finally {
            s.close();
        }
    }

    @Test
    public void testSimpleInsertWithNamedParam() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        org.hibernate.Query q = s.createQuery("insert into Pickup (id, owner, vin) select id, :owner, vin from Car");
        q.setParameter("owner", "owner");
        q.executeUpdate();
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertWithMultipleNamedParams() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        org.hibernate.Query q = s.createQuery("insert into Pickup (id, owner, vin) select :id, owner, :vin from Car");
        q.setParameter("id", 5L);
        q.setParameter("vin", "some");
        q.executeUpdate();
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertWithSubqueriesAndNamedParams() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        org.hibernate.Query q = s.createQuery("insert into Pickup (id, owner, vin) select :id, (select a.description from Animal a where a.description = :description), :vin from Car");
        q.setParameter("id", 5L);
        q.setParameter("description", "Frog");
        q.setParameter("vin", "some");
        q.executeUpdate();
        t.commit();
        t = s.beginTransaction();
        try {
            org.hibernate.Query q1 = s.createQuery("insert into Pickup (id, owner, vin) select :id, (select :description from Animal a where a.description = :description), :vin from Car");
            Assert.fail("Unsupported exception should have been thrown");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException e) {
            Assert.assertTrue(((e.getMessage().indexOf("Use of parameters in subqueries of INSERT INTO DML statements is not supported.")) > (-1)));
        }
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testSimpleInsertTypeMismatchException() {
        Session s = openSession();
        try {
            org.hibernate.Query q = s.createQuery("insert into Pickup (id, owner, vin) select id, :owner, id from Car");
            Assert.fail("Parameter type mismatch but no exception thrown");
        } catch (Throwable throwable) {
            QueryException queryException = ExtraAssertions.assertTyping(QueryException.class, throwable.getCause());
            String m = queryException.getMessage();
            // insertion type [org.hibernate.type.StringType@21e3cc77] and selection type [org.hibernate.type.LongType@7284aa02] at position 2 are not compatible [insert into Pickup (id, owner, vin) select id, :owner, id from org.hibernate.test.hql.Car]
            int st = m.indexOf("org.hibernate.type.StringType");
            int lt = m.indexOf("org.hibernate.type.LongType");
            Assert.assertTrue("type causing error not reported", (st > (-1)));
            Assert.assertTrue("type causing error not reported", (lt > (-1)));
            Assert.assertTrue((lt > st));
            Assert.assertTrue("wrong position of type error reported", ((m.indexOf("position 2")) > (-1)));
        } finally {
            s.close();
        }
    }

    @Test
    public void testSimpleNativeSQLInsert() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        List l = s.createQuery("from Vehicle").list();
        Assert.assertEquals(l.size(), 4);
        s.createSQLQuery("insert into Pickup (id, vin, owner) select id, vin, owner from Car").executeUpdate();
        l = s.createQuery("from Vehicle").list();
        Assert.assertEquals(l.size(), 5);
        t.commit();
        t = s.beginTransaction();
        int deleteCount = s.createSQLQuery("delete from Truck").executeUpdate();
        Assert.assertEquals(1, deleteCount);
        l = s.createQuery("from Vehicle").list();
        Assert.assertEquals(l.size(), 4);
        Car c = ((Car) (s.createQuery("from Car where owner = 'Kirsten'").uniqueResult()));
        c.setOwner("NotKirsten");
        Assert.assertEquals(0, s.getNamedQuery("native-delete-car").setString(1, "Kirsten").executeUpdate());
        Assert.assertEquals(1, s.getNamedQuery("native-delete-car").setString(1, "NotKirsten").executeUpdate());
        Assert.assertEquals(0, s.createSQLQuery("delete from SUV where owner = :owner").setString("owner", "NotThere").executeUpdate());
        Assert.assertEquals(1, s.createSQLQuery("delete from SUV where owner = :owner").setString("owner", "Joe").executeUpdate());
        s.createSQLQuery("delete from Pickup").executeUpdate();
        l = s.createQuery("from Vehicle").list();
        Assert.assertEquals(l.size(), 0);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertWithManyToOne() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("insert into Animal (description, bodyWeight, mother) select description, bodyWeight, mother from Human").executeUpdate();
        t.commit();
        t = s.beginTransaction();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertWithMismatchedTypes() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("insert into Pickup (owner, vin, id) select id, vin, owner from Car").executeUpdate();
            Assert.fail("mismatched types did not error");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException e) {
            // expected result
        }
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertIntoSuperclassPropertiesFails() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("insert into Human (id, bodyWeight) select id, bodyWeight from Lizard").executeUpdate();
            Assert.fail("superclass prop insertion did not error");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException e) {
            // expected result
        }
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Animal where mother is not null").executeUpdate();
        s.createQuery("delete Animal where father is not null").executeUpdate();
        s.createQuery("delete Animal").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertAcrossMappedJoinFails() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("insert into Joiner (name, joinedName) select vin, owner from Car").executeUpdate();
            Assert.fail("mapped-join insertion did not error");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException e) {
            // expected result
        }
        t.commit();
        t = s.beginTransaction();
        s.createQuery("delete Joiner").executeUpdate();
        s.createQuery("delete Vehicle").executeUpdate();
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testInsertWithGeneratedId() {
        // Make sure the env supports bulk inserts with generated ids...
        if (!(supportsBulkInsertIdGeneration(PettingZoo.class))) {
            SkipLog.reportSkip("bulk id generation not supported", "test bulk inserts with generated id and generated timestamp");
            return;
        }
        // create a Zoo
        Zoo zoo = new Zoo();
        zoo.setName("zoo");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.save(zoo);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        int count = s.createQuery("insert into PettingZoo (name) select name from Zoo").executeUpdate();
        t.commit();
        s.close();
        Assert.assertEquals("unexpected insertion count", 1, count);
        s = openSession();
        t = s.beginTransaction();
        PettingZoo pz = ((PettingZoo) (s.createQuery("from PettingZoo").uniqueResult()));
        t.commit();
        s.close();
        Assert.assertEquals(zoo.getName(), pz.getName());
        Assert.assertTrue((!(zoo.getId().equals(pz.getId()))));
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete Zoo").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    public void testInsertWithGeneratedVersionAndId() {
        // Make sure the env supports bulk inserts with generated ids...
        if (!(supportsBulkInsertIdGeneration(IntegerVersioned.class))) {
            SkipLog.reportSkip("bulk id generation not supported", "test bulk inserts with generated id and generated timestamp");
            return;
        }
        Session s = openSession();
        Transaction t = s.beginTransaction();
        IntegerVersioned entity = new IntegerVersioned("int-vers");
        s.save(entity);
        s.createQuery("select id, name, version from IntegerVersioned").list();
        t.commit();
        s.close();
        Long initialId = entity.getId();
        int initialVersion = entity.getVersion();
        s = openSession();
        t = s.beginTransaction();
        int count = s.createQuery("insert into IntegerVersioned ( name ) select name from IntegerVersioned").executeUpdate();
        t.commit();
        s.close();
        Assert.assertEquals("unexpected insertion count", 1, count);
        s = openSession();
        t = s.beginTransaction();
        IntegerVersioned created = ((IntegerVersioned) (s.createQuery("from IntegerVersioned where id <> :initialId").setLong("initialId", initialId.longValue()).uniqueResult()));
        t.commit();
        s.close();
        Assert.assertEquals("version was not seeded", initialVersion, created.getVersion());
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete IntegerVersioned").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = SupportsParametersInInsertSelectCheck.class, comment = "dialect does not support parameter in INSERT ... SELECT")
    public void testInsertWithGeneratedTimestampVersion() {
        // Make sure the env supports bulk inserts with generated ids...
        if (!(supportsBulkInsertIdGeneration(TimestampVersioned.class))) {
            SkipLog.reportSkip("bulk id generation not supported", "test bulk inserts with generated id and generated timestamp");
            return;
        }
        Session s = openSession();
        Transaction t = s.beginTransaction();
        TimestampVersioned entity = new TimestampVersioned("int-vers");
        s.save(entity);
        s.createQuery("select id, name, version from TimestampVersioned").list();
        t.commit();
        s.close();
        Long initialId = entity.getId();
        // Date initialVersion = entity.getVersion();
        s = openSession();
        t = s.beginTransaction();
        int count = s.createQuery("insert into TimestampVersioned ( name ) select name from TimestampVersioned").executeUpdate();
        t.commit();
        s.close();
        Assert.assertEquals("unexpected insertion count", 1, count);
        s = openSession();
        t = s.beginTransaction();
        TimestampVersioned created = ((TimestampVersioned) (s.createQuery("from TimestampVersioned where id <> :initialId").setLong("initialId", initialId.longValue()).uniqueResult()));
        t.commit();
        s.close();
        Assert.assertNotNull(created.getVersion());
        // assertEquals( "version was not seeded", initialVersion, created.getVersion() );
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete TimestampVersioned").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    public void testInsertWithAssignedCompositeId() {
        // this just checks that the query parser detects that we are explicitly inserting a composite id
        Session s = openSession();
        s.beginTransaction();
        // intentionally reversing the order of the composite id properties to make sure that is supported too
        s.createQuery("insert into CompositeIdEntity (key2, someProperty, key1) select a.key2, 'COPY', a.key1 from CompositeIdEntity a").executeUpdate();
        s.createQuery("delete from CompositeIdEntity").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    public void testInsertWithSelectListUsingJoins() {
        // this is just checking parsing and syntax...
        Session s = openSession();
        s.beginTransaction();
        s.createQuery("insert into Animal (description, bodyWeight) select h.description, h.bodyWeight from Human h where h.mother.mother is not null").executeUpdate();
        s.createQuery("insert into Animal (description, bodyWeight) select h.description, h.bodyWeight from Human h join h.mother m where m.mother is not null").executeUpdate();
        s.createQuery("delete from Animal").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testIncorrectSyntax() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            s.createQuery("update Human set Human.description = 'xyz' where Human.id = 1 and Human.description is null");
            Assert.fail("expected failure");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (QueryException expected) {
            // ignore : expected behavior
        }
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testUpdateWithWhereExistsSubquery() {
        // multi-table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Human joe = new Human();
        joe.setName(new Name("Joe", 'Q', "Public"));
        s.save(joe);
        Human doll = new Human();
        doll.setName(new Name("Kyu", 'P', "Doll"));
        doll.setFriends(new ArrayList());
        doll.getFriends().add(joe);
        s.save(doll);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        String updateQryString = "update Human h " + ((((("set h.description = 'updated' " + "where exists (") + "      select f.id ") + "      from h.friends f ") + "      where f.name.last = 'Public' ") + ")");
        int count = s.createQuery(updateQryString).executeUpdate();
        Assert.assertEquals(1, count);
        s.delete(doll);
        s.delete(joe);
        t.commit();
        s.close();
        // single-table (one-to-many & many-to-many) ~~~~~~~~~~~~~~~~~~~~~~~~~~
        s = openSession();
        t = s.beginTransaction();
        SimpleEntityWithAssociation entity = new SimpleEntityWithAssociation();
        SimpleEntityWithAssociation other = new SimpleEntityWithAssociation();
        entity.setName("main");
        other.setName("many-to-many-association");
        entity.getManyToManyAssociatedEntities().add(other);
        entity.addAssociation("one-to-many-association");
        s.save(entity);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        // one-to-many test
        updateQryString = "update SimpleEntityWithAssociation e " + ((((("set e.name = 'updated' " + "where exists (") + "      select a.id ") + "      from e.associatedEntities a ") + "      where a.name = 'one-to-many-association' ") + ")");
        count = s.createQuery(updateQryString).executeUpdate();
        Assert.assertEquals(1, count);
        // many-to-many test
        if (getDialect().supportsSubqueryOnMutatingTable()) {
            updateQryString = "update SimpleEntityWithAssociation e " + ((((("set e.name = 'updated' " + "where exists (") + "      select a.id ") + "      from e.manyToManyAssociatedEntities a ") + "      where a.name = 'many-to-many-association' ") + ")");
            count = s.createQuery(updateQryString).executeUpdate();
            Assert.assertEquals(1, count);
        }
        s.delete(entity.getManyToManyAssociatedEntities().iterator().next());
        s.delete(entity);
        t.commit();
        s.close();
    }

    @Test
    public void testIncrementCounterVersion() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        IntegerVersioned entity = new IntegerVersioned("int-vers");
        s.save(entity);
        t.commit();
        s.close();
        int initialVersion = entity.getVersion();
        s = openSession();
        t = s.beginTransaction();
        int count = s.createQuery("update versioned IntegerVersioned set name = name").executeUpdate();
        Assert.assertEquals("incorrect exec count", 1, count);
        t.commit();
        t = s.beginTransaction();
        entity = ((IntegerVersioned) (s.load(IntegerVersioned.class, entity.getId())));
        Assert.assertEquals("version not incremented", (initialVersion + 1), entity.getVersion());
        s.delete(entity);
        t.commit();
        s.close();
    }

    @Test
    public void testIncrementTimestampVersion() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        TimestampVersioned entity = new TimestampVersioned("ts-vers");
        s.save(entity);
        t.commit();
        s.close();
        Date initialVersion = entity.getVersion();
        synchronized(this) {
            try {
                wait(1500);
            } catch (InterruptedException ie) {
            }
        }
        s = openSession();
        t = s.beginTransaction();
        int count = s.createQuery("update versioned TimestampVersioned set name = name").executeUpdate();
        Assert.assertEquals("incorrect exec count", 1, count);
        t.commit();
        t = s.beginTransaction();
        entity = ((TimestampVersioned) (s.load(TimestampVersioned.class, entity.getId())));
        Assert.assertTrue("version not incremented", entity.getVersion().after(initialVersion));
        s.delete(entity);
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateOnComponent() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Human human = new Human();
        human.setName(new Name("Stevee", 'X', "Ebersole"));
        s.save(human);
        s.flush();
        t.commit();
        String correctName = "Steve";
        t = s.beginTransaction();
        int count = s.createQuery("update Human set name.first = :correction where id = :id").setString("correction", correctName).setLong("id", human.getId().longValue()).executeUpdate();
        Assert.assertEquals("Incorrect update count", 1, count);
        t.commit();
        t = s.beginTransaction();
        s.refresh(human);
        Assert.assertEquals("Update did not execute properly", correctName, human.getName().getFirst());
        s.createQuery("delete Human").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    public void testUpdateOnManyToOne() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("update Animal a set a.mother = null where a.id = 2").executeUpdate();
        if (!((getDialect()) instanceof MySQLDialect)) {
            // MySQL does not support (even un-correlated) subqueries against the update-mutating table
            s.createQuery("update Animal a set a.mother = (from Animal where id = 1) where a.id = 2").executeUpdate();
        }
        t.commit();
        s.close();
    }

    @Test
    public void testUpdateOnImplicitJoinFails() {
        inTransaction(( s) -> {
            try {
                s.createQuery("update Human set mother.name.initial = :initial").setString("initial", "F").executeUpdate();
                fail("update allowed across implicit join");
            } catch ( e) {
                assertTyping(.class, e.getCause());
            } catch ( e) {
            }
        });
    }

    @Test
    public void testUpdateOnDiscriminatorSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update PettingZoo set name = name").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass update count", 1, count);
        t.rollback();
        t = s.beginTransaction();
        count = s.createQuery("update PettingZoo pz set pz.name = pz.name where pz.id = :id").setLong("id", data.pettingZoo.getId().longValue()).executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass update count", 1, count);
        t.rollback();
        t = s.beginTransaction();
        count = s.createQuery("update Zoo as z set z.name = z.name").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass update count", 2, count);
        t.rollback();
        t = s.beginTransaction();
        // TODO : not so sure this should be allowed.  Seems to me that if they specify an alias,
        // property-refs should be required to be qualified.
        count = s.createQuery("update Zoo as z set name = name where id = :id").setLong("id", data.zoo.getId().longValue()).executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass update count", 1, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testUpdateOnAnimal() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update Animal set description = description where description = :desc").setString("desc", data.frog.getDescription()).executeUpdate();
        Assert.assertEquals("Incorrect entity-updated count", 1, count);
        count = s.createQuery("update Animal set description = :newDesc where description = :desc").setString("desc", data.polliwog.getDescription()).setString("newDesc", "Tadpole").executeUpdate();
        Assert.assertEquals("Incorrect entity-updated count", 1, count);
        Animal tadpole = ((Animal) (s.load(Animal.class, data.polliwog.getId())));
        Assert.assertEquals("Update did not take effect", "Tadpole", tadpole.getDescription());
        count = s.createQuery("update Animal set bodyWeight = bodyWeight + :w1 + :w2").setDouble("w1", 1).setDouble("w2", 2).executeUpdate();
        Assert.assertEquals("incorrect count on 'complex' update assignment", count, 6);
        if (!((getDialect()) instanceof MySQLDialect)) {
            // MySQL does not support (even un-correlated) subqueries against the update-mutating table
            s.createQuery("update Animal set bodyWeight = ( select max(bodyWeight) from Animal )").executeUpdate();
        }
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testUpdateOnMammal() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update Mammal set description = description").executeUpdate();
        Assert.assertEquals("incorrect update count against 'middle' of joined-subclass hierarchy", 2, count);
        count = s.createQuery("update Mammal set bodyWeight = 25").executeUpdate();
        Assert.assertEquals("incorrect update count against 'middle' of joined-subclass hierarchy", 2, count);
        if (!((getDialect()) instanceof MySQLDialect)) {
            // MySQL does not support (even un-correlated) subqueries against the update-mutating table
            count = s.createQuery("update Mammal set bodyWeight = ( select max(bodyWeight) from Animal )").executeUpdate();
            Assert.assertEquals("incorrect update count against 'middle' of joined-subclass hierarchy", 2, count);
        }
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testUpdateSetNullUnionSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        // These should reach out into *all* subclass tables...
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update Vehicle set owner = 'Steve'").executeUpdate();
        Assert.assertEquals("incorrect restricted update count", 4, count);
        count = s.createQuery("update Vehicle set owner = null where owner = 'Steve'").executeUpdate();
        Assert.assertEquals("incorrect restricted update count", 4, count);
        try {
            count = s.createQuery("delete Vehicle where owner is null").executeUpdate();
            Assert.assertEquals("incorrect restricted delete count", 4, count);
        } catch (AssertionFailedError afe) {
            if (H2Dialect.class.isInstance(getDialect())) {
                // http://groups.google.com/group/h2-database/t/5548ff9fd3abdb7
                // this is fixed in H2 1.2.140
                count = s.createQuery("delete Vehicle").executeUpdate();
                Assert.assertEquals("incorrect count", 4, count);
            } else {
                throw afe;
            }
        }
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testUpdateSetNullOnDiscriminatorSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update PettingZoo set address.city = null").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        count = s.createQuery("delete Zoo where address.city is null").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        count = s.createQuery("update Zoo set address.city = null").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        count = s.createQuery("delete Zoo where address.city is null").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testUpdateSetNullOnJoinedSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("update Mammal set bodyWeight = null").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 2, count);
        count = s.createQuery("delete Animal where bodyWeight = null").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 2, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteWithSubquery() {
        // setup the test data...
        Session s = openSession();
        s.beginTransaction();
        SimpleEntityWithAssociation owner = new SimpleEntityWithAssociation("myEntity-1");
        owner.addAssociation("assoc-1");
        owner.addAssociation("assoc-2");
        owner.addAssociation("assoc-3");
        s.save(owner);
        SimpleEntityWithAssociation owner2 = new SimpleEntityWithAssociation("myEntity-2");
        owner2.addAssociation("assoc-1");
        owner2.addAssociation("assoc-2");
        owner2.addAssociation("assoc-3");
        owner2.addAssociation("assoc-4");
        s.save(owner2);
        SimpleEntityWithAssociation owner3 = new SimpleEntityWithAssociation("myEntity-3");
        s.save(owner3);
        s.getTransaction().commit();
        s.close();
        // now try the bulk delete
        s = openSession();
        s.beginTransaction();
        int count = s.createQuery("delete SimpleEntityWithAssociation e where size( e.associatedEntities ) = 0 and e.name like '%'").executeUpdate();
        Assert.assertEquals("incorrect delete count", 1, count);
        s.getTransaction().commit();
        s.close();
        // finally, clean up
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete SimpleAssociatedEntity").executeUpdate();
        s.createQuery("delete SimpleEntityWithAssociation").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = HasSelfReferentialForeignKeyBugCheck.class, comment = "self referential FK bug")
    public void testSimpleDeleteOnAnimal() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete from Animal as a where a.id = :id").setLong("id", data.polliwog.getId().longValue()).executeUpdate();
        Assert.assertEquals("Incorrect delete count", 1, count);
        count = s.createQuery("delete Animal where id = :id").setLong("id", data.catepillar.getId().longValue()).executeUpdate();
        Assert.assertEquals("incorrect delete count", 1, count);
        if (getDialect().supportsSubqueryOnMutatingTable()) {
            count = s.createQuery("delete from User u where u not in (select u from User u)").executeUpdate();
            Assert.assertEquals(0, count);
        }
        count = s.createQuery("delete Animal a").executeUpdate();
        Assert.assertEquals("Incorrect delete count", 4, count);
        List list = s.createQuery("select a from Animal as a").list();
        Assert.assertTrue("table not empty", list.isEmpty());
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteOnDiscriminatorSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete PettingZoo").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        count = s.createQuery("delete Zoo").executeUpdate();
        Assert.assertEquals("Incorrect discrim subclass delete count", 1, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteOnJoinedSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Mammal where bodyWeight > 150").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 1, count);
        count = s.createQuery("delete Mammal").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 1, count);
        count = s.createQuery("delete SubMulti").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 0, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteOnMappedJoin() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Joiner where joinedName = :joinedName").setString("joinedName", "joined-name").executeUpdate();
        Assert.assertEquals("Incorrect deletion count on joined subclass", 1, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteUnionSubclassAbstractRoot() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        // These should reach out into *all* subclass tables...
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Vehicle where owner = :owner").setString("owner", "Steve").executeUpdate();
        Assert.assertEquals("incorrect restricted update count", 1, count);
        count = s.createQuery("delete Vehicle").executeUpdate();
        Assert.assertEquals("incorrect update count", 3, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteUnionSubclassConcreteSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        // These should only affect the given table
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Truck where owner = :owner").setString("owner", "Steve").executeUpdate();
        Assert.assertEquals("incorrect restricted update count", 1, count);
        count = s.createQuery("delete Truck").executeUpdate();
        Assert.assertEquals("incorrect update count", 2, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteUnionSubclassLeafSubclass() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        // These should only affect the given table
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Car where owner = :owner").setString("owner", "Kirsten").executeUpdate();
        Assert.assertEquals("incorrect restricted update count", 1, count);
        count = s.createQuery("delete Car").executeUpdate();
        Assert.assertEquals("incorrect update count", 0, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    public void testDeleteWithMetadataWhereFragments() throws Throwable {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        // Note: we are just checking the syntax here...
        s.createQuery("delete from Bar").executeUpdate();
        s.createQuery("delete from Bar where barString = 's'").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    public void testDeleteRestrictedOnManyToOne() {
        BulkManipulationTest.TestData data = new BulkManipulationTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        int count = s.createQuery("delete Animal where mother = :mother").setEntity("mother", data.butterfly).executeUpdate();
        Assert.assertEquals(1, count);
        t.commit();
        s.close();
        data.cleanup();
    }

    @Test
    public void testDeleteSyntaxWithCompositeId() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("delete EntityWithCrazyCompositeKey where id.id = 1 and id.otherId = 2").executeUpdate();
        s.createQuery("delete from EntityWithCrazyCompositeKey where id.id = 1 and id.otherId = 2").executeUpdate();
        s.createQuery("delete from EntityWithCrazyCompositeKey e where e.id.id = 1 and e.id.otherId = 2").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8476")
    public void testManyToManyBulkDelete() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Farm farm1 = new Farm();
        farm1.setName("farm1");
        Crop crop = new Crop();
        crop.setName("crop1");
        farm1.setCrops(new ArrayList());
        farm1.getCrops().add(crop);
        s.save(farm1);
        Farm farm2 = new Farm();
        farm2.setName("farm2");
        farm2.setCrops(new ArrayList());
        farm2.getCrops().add(crop);
        s.save(farm2);
        s.flush();
        try {
            s.createQuery("delete from Farm f where f.name='farm1'").executeUpdate();
            Assert.assertEquals(s.createQuery("from Farm").list().size(), 1);
            s.createQuery("delete from Farm").executeUpdate();
            Assert.assertEquals(s.createQuery("from Farm").list().size(), 0);
        } catch (ConstraintViolationException cve) {
            Assert.fail("The join table was not cleared prior to the bulk delete.");
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1917")
    public void testManyToManyBulkDeleteMultiTable() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Human friend = new Human();
        friend.setName(new Name("Bob", 'B', "Bobbert"));
        s.save(friend);
        Human brett = new Human();
        brett.setName(new Name("Brett", 'E', "Meyer"));
        brett.setFriends(new ArrayList());
        brett.getFriends().add(friend);
        s.save(brett);
        s.flush();
        try {
            // multitable (joined subclass)
            s.createQuery("delete from Human").executeUpdate();
            Assert.assertEquals(s.createQuery("from Human").list().size(), 0);
        } catch (ConstraintViolationException cve) {
            Assert.fail("The join table was not cleared prior to the bulk delete.");
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9282", message = "failed because HHH-9222 was reverted by HHH-9282")
    public void testBulkDeleteOfEntityWithElementCollection() {
        // set up test data
        {
            Session s = openSession();
            s.beginTransaction();
            Farm farm = new Farm();
            farm.setName("Old McDonald Farm 'o the Earth");
            farm.setAccreditations(new HashSet<Farm.Accreditation>());
            farm.getAccreditations().add(ORGANIC);
            farm.getAccreditations().add(SUSTAINABLE);
            s.save(farm);
            s.getTransaction().commit();
            s.close();
        }
        // assertion that accreditations collection table got populated
        {
            Session s = openSession();
            s.beginTransaction();
            s.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("select count(*) from farm_accreditations");
                    Assert.assertTrue(resultSet.next());
                    final int count = resultSet.getInt(1);
                    Assert.assertEquals(2, count);
                }
            });
            s.getTransaction().commit();
            s.close();
        }
        // do delete
        {
            Session s = openSession();
            s.beginTransaction();
            s.createQuery("delete Farm").executeUpdate();
            s.getTransaction().commit();
            s.close();
        }
        // assertion that accreditations collection table got cleaned up
        // if they didn't, the delete should have caused a constraint error, but just to be sure...
        {
            Session s = openSession();
            s.beginTransaction();
            s.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("select count(*) from farm_accreditations");
                    Assert.assertTrue(resultSet.next());
                    final int count = resultSet.getInt(1);
                    Assert.assertEquals(0, count);
                }
            });
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9282", message = "failed because HHH-9222 was reverted by HHH-9282")
    public void testBulkDeleteOfMultiTableEntityWithElementCollection() {
        // set up test data
        {
            Session s = openSession();
            s.beginTransaction();
            Human human = new Human();
            human.setNickNames(new TreeSet());
            human.getNickNames().add("Johnny");
            s.save(human);
            s.getTransaction().commit();
            s.close();
        }
        // assertion that nickname collection table got populated
        {
            Session s = openSession();
            s.beginTransaction();
            s.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("select count(*) from human_nick_names");
                    Assert.assertTrue(resultSet.next());
                    final int count = resultSet.getInt(1);
                    Assert.assertEquals(1, count);
                }
            });
            s.getTransaction().commit();
            s.close();
        }
        // do delete
        {
            Session s = openSession();
            s.beginTransaction();
            s.createQuery("delete Human").executeUpdate();
            s.getTransaction().commit();
            s.close();
        }
        // assertion that nickname collection table got cleaned up
        // if they didn't, the delete should have caused a constraint error, but just to be sure...
        {
            Session s = openSession();
            s.beginTransaction();
            s.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("select count(*) from human_nick_names");
                    Assert.assertTrue(resultSet.next());
                    final int count = resultSet.getInt(1);
                    Assert.assertEquals(0, count);
                }
            });
            s.getTransaction().commit();
            s.close();
        }
    }

    private class TestData {
        private Animal polliwog;

        private Animal catepillar;

        private Animal frog;

        private Animal butterfly;

        private Zoo zoo;

        private Zoo pettingZoo;

        private void prepare() {
            Session s = openSession();
            Transaction txn = s.beginTransaction();
            polliwog = new Animal();
            polliwog.setBodyWeight(12);
            polliwog.setDescription("Polliwog");
            catepillar = new Animal();
            catepillar.setBodyWeight(10);
            catepillar.setDescription("Catepillar");
            frog = new Animal();
            frog.setBodyWeight(34);
            frog.setDescription("Frog");
            polliwog.setFather(frog);
            frog.addOffspring(polliwog);
            butterfly = new Animal();
            butterfly.setBodyWeight(9);
            butterfly.setDescription("Butterfly");
            catepillar.setMother(butterfly);
            butterfly.addOffspring(catepillar);
            s.save(frog);
            s.save(polliwog);
            s.save(butterfly);
            s.save(catepillar);
            Dog dog = new Dog();
            dog.setBodyWeight(200);
            dog.setDescription("dog");
            s.save(dog);
            Cat cat = new Cat();
            cat.setBodyWeight(100);
            cat.setDescription("cat");
            s.save(cat);
            zoo = new Zoo();
            zoo.setName("Zoo");
            Address add = new Address();
            add.setCity("MEL");
            add.setCountry("AU");
            add.setStreet("Main st");
            add.setPostalCode("3000");
            zoo.setAddress(add);
            pettingZoo = new PettingZoo();
            pettingZoo.setName("Petting Zoo");
            Address addr = new Address();
            addr.setCity("Sydney");
            addr.setCountry("AU");
            addr.setStreet("High st");
            addr.setPostalCode("2000");
            pettingZoo.setAddress(addr);
            s.save(zoo);
            s.save(pettingZoo);
            Joiner joiner = new Joiner();
            joiner.setJoinedName("joined-name");
            joiner.setName("name");
            s.save(joiner);
            Car car = new Car();
            car.setVin("123c");
            car.setOwner("Kirsten");
            s.save(car);
            Truck truck = new Truck();
            truck.setVin("123t");
            truck.setOwner("Steve");
            s.save(truck);
            SUV suv = new SUV();
            suv.setVin("123s");
            suv.setOwner("Joe");
            s.save(suv);
            Pickup pickup = new Pickup();
            pickup.setVin("123p");
            pickup.setOwner("Cecelia");
            s.save(pickup);
            BooleanLiteralEntity bool = new BooleanLiteralEntity();
            s.save(bool);
            txn.commit();
            s.close();
        }

        private void cleanup() {
            Session s = openSession();
            Transaction txn = s.beginTransaction();
            // workaround awesome HSQLDB "feature"
            s.createQuery("delete from Animal where mother is not null or father is not null").executeUpdate();
            s.createQuery("delete from Animal").executeUpdate();
            s.createQuery("delete from Zoo").executeUpdate();
            s.createQuery("delete from Joiner").executeUpdate();
            s.createQuery("delete from Vehicle").executeUpdate();
            s.createQuery("delete from BooleanLiteralEntity").executeUpdate();
            txn.commit();
            s.close();
        }
    }
}

