package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.executor.MatchPrefetchStep;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class OMatchStatementExecutionNewTest {
    private static String DB_STORAGE = "memory";

    private static String DB_NAME = "OMatchStatementExecutionNewTest";

    static ODatabaseDocumentTx db;

    @Test
    public void testSimple() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person} return person");
        printExecutionPlan(qResult);
        for (int i = 0; i < 6; i++) {
            OResult item = qResult.next();
            Assert.assertTrue(((item.getPropertyNames().size()) == 1));
            OElement person = OMatchStatementExecutionNewTest.db.load(((ORID) (item.getProperty("person"))));
            String name = person.getProperty("name");
            Assert.assertTrue(name.startsWith("n"));
        }
        qResult.close();
    }

    @Test
    public void testSimpleWhere() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person");
        for (int i = 0; i < 2; i++) {
            OResult item = qResult.next();
            Assert.assertTrue(((item.getPropertyNames().size()) == 1));
            OElement personId = OMatchStatementExecutionNewTest.db.load(((ORID) (item.getProperty("person"))));
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(((name.equals("n1")) || (name.equals("n2"))));
        }
        qResult.close();
    }

    @Test
    public void testSimpleLimit() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit 1");
        Assert.assertTrue(qResult.hasNext());
        qResult.next();
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testSimpleLimit2() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit -1");
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(qResult.hasNext());
            qResult.next();
        }
        qResult.close();
    }

    @Test
    public void testSimpleLimit3() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit 3");
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(qResult.hasNext());
            qResult.next();
        }
        qResult.close();
    }

    @Test
    public void testSimpleUnnamedParams() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person, where: (name = ? or name = ?)} return person", "n1", "n2");
        printExecutionPlan(qResult);
        for (int i = 0; i < 2; i++) {
            OResult item = qResult.next();
            Assert.assertTrue(((item.getPropertyNames().size()) == 1));
            OElement person = OMatchStatementExecutionNewTest.db.load(((ORID) (item.getProperty("person"))));
            String name = person.getProperty("name");
            Assert.assertTrue(((name.equals("n1")) || (name.equals("n2"))));
        }
        qResult.close();
    }

    @Test
    public void testCommonFriends() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testCommonFriendsPatterns() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $patterns)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testPattens() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $patterns");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals(1, item.getPropertyNames().size());
        Assert.assertEquals("friend", item.getPropertyNames().iterator().next());
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testPaths() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $paths");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals(3, item.getPropertyNames().size());
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testElements() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $elements");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testPathElements() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $pathElements");
        printExecutionPlan(qResult);
        Set<String> expected = new HashSet<>();
        expected.add("n1");
        expected.add("n2");
        expected.add("n4");
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(qResult.hasNext());
            OResult item = qResult.next();
            expected.remove(item.getProperty("name"));
        }
        Assert.assertFalse(qResult.hasNext());
        Assert.assertTrue(expected.isEmpty());
        qResult.close();
    }

    @Test
    public void testCommonFriendsMatches() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $matches)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testCommonFriendsArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testCommonFriendsArrowsPatterns() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return $patterns)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testCommonFriends2() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testCommonFriends2Arrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnMethod() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name.toUpperCase(Locale.ENGLISH) as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("N2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnMethodArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name.toUpperCase(Locale.ENGLISH) as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("N2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnExpression() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name + ' ' +friend.name as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2 n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnExpressionArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name + ' ' +friend.name as name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2 n2", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnDefaultAlias() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("friend.name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testReturnDefaultAliasArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n2", item.getProperty("friend.name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testFriendsOfFriends() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend').out('Friend'){as:friend} return $matches)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n4", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testFriendsOfFriendsArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{}-Friend->{as:friend} return $matches)");
        Assert.assertTrue(qResult.hasNext());
        OResult item = qResult.next();
        Assert.assertEquals("n4", item.getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testFriendsOfFriends2() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1'), as: me}.both('Friend').both('Friend'){as:friend, where: ($matched.me != $currentMatch)} return $matches)");
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        while (qResult.hasNext()) {
            Assert.assertNotEquals(getProperty("name"), "n1");
        } 
        qResult.close();
    }

    @Test
    public void testFriendsOfFriends2Arrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1'), as: me}-Friend-{}-Friend-{as:friend, where: ($matched.me != $currentMatch)} return $matches)");
        Assert.assertTrue(qResult.hasNext());
        while (qResult.hasNext()) {
            Assert.assertNotEquals(getProperty("name"), "n1");
        } 
        qResult.close();
    }

    @Test
    public void testFriendsWithName() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1' and 1 + 1 = 2)}.out('Friend'){as:friend, where:(name = 'n2' and 1 + 1 = 2)} return friend)");
        Assert.assertTrue(qResult.hasNext());
        Assert.assertEquals("n2", getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testFriendsWithNameArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1' and 1 + 1 = 2)}-Friend->{as:friend, where:(name = 'n2' and 1 + 1 = 2)} return friend)");
        Assert.assertTrue(qResult.hasNext());
        Assert.assertEquals("n2", getProperty("name"));
        Assert.assertFalse(qResult.hasNext());
        qResult.close();
    }

    @Test
    public void testWhile() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 1)} return friend)");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 2), where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 4), where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend)");
        Assert.assertEquals(6, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend limit 3)");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend) limit 3");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
    }

    @Test
    public void testWhileArrows() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 1)} return friend)");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 2), where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 4), where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: (true) } return friend)");
        Assert.assertEquals(6, size(qResult));
        qResult.close();
    }

    @Test
    public void testMaxDepth() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1, where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1 } return friend)");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 0 } return friend)");
        Assert.assertEquals(1, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1, where: ($depth > 0) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
    }

    @Test
    public void testMaxDepthArrow() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1, where: ($depth=1) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1 } return friend)");
        Assert.assertEquals(3, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 0 } return friend)");
        Assert.assertEquals(1, size(qResult));
        qResult.close();
        qResult = OMatchStatementExecutionNewTest.db.query("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1, where: ($depth > 0) } return friend)");
        Assert.assertEquals(2, size(qResult));
        qResult.close();
    }

    @Test
    public void testManager() {
        // the manager of a person is the manager of the department that person belongs to.
        // if that department does not have a direct manager, climb up the hierarchy until you find one
        Assert.assertEquals("c", getManager("p10").field("name"));
        Assert.assertEquals("c", getManager("p12").field("name"));
        Assert.assertEquals("b", getManager("p6").field("name"));
        Assert.assertEquals("b", getManager("p11").field("name"));
        Assert.assertEquals("c", getManagerArrows("p10").field("name"));
        Assert.assertEquals("c", getManagerArrows("p12").field("name"));
        Assert.assertEquals("b", getManagerArrows("p6").field("name"));
        Assert.assertEquals("b", getManagerArrows("p11").field("name"));
    }

    @Test
    public void testManager2() {
        // the manager of a person is the manager of the department that person belongs to.
        // if that department does not have a direct manager, climb up the hierarchy until you find one
        Assert.assertEquals("c", getManager2("p10").getProperty("name"));
        Assert.assertEquals("c", getManager2("p12").getProperty("name"));
        Assert.assertEquals("b", getManager2("p6").getProperty("name"));
        Assert.assertEquals("b", getManager2("p11").getProperty("name"));
        Assert.assertEquals("c", getManager2Arrows("p10").getProperty("name"));
        Assert.assertEquals("c", getManager2Arrows("p12").getProperty("name"));
        Assert.assertEquals("b", getManager2Arrows("p6").getProperty("name"));
        Assert.assertEquals("b", getManager2Arrows("p11").getProperty("name"));
    }

    @Test
    public void testManaged() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        OResultSet managedByA = getManagedBy("a");
        Assert.assertTrue(managedByA.hasNext());
        OResult item = managedByA.next();
        Assert.assertFalse(managedByA.hasNext());
        Assert.assertEquals("p1", item.getProperty("name"));
        managedByA.close();
        OResultSet managedByB = getManagedBy("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult id = managedByB.next();
            String name = id.getProperty("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testManagedArrows() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        OResultSet managedByA = getManagedByArrows("a");
        Assert.assertTrue(managedByA.hasNext());
        OResult item = managedByA.next();
        Assert.assertFalse(managedByA.hasNext());
        Assert.assertEquals("p1", item.getProperty("name"));
        managedByA.close();
        OResultSet managedByB = getManagedByArrows("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult id = managedByB.next();
            String name = id.getProperty("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testManaged2() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        OResultSet managedByA = getManagedBy2("a");
        Assert.assertTrue(managedByA.hasNext());
        OResult item = managedByA.next();
        Assert.assertFalse(managedByA.hasNext());
        Assert.assertEquals("p1", item.getProperty("name"));
        managedByA.close();
        OResultSet managedByB = getManagedBy2("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult id = managedByB.next();
            String name = id.getProperty("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testManaged2Arrows() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        OResultSet managedByA = getManagedBy2Arrows("a");
        Assert.assertTrue(managedByA.hasNext());
        OResult item = managedByA.next();
        Assert.assertFalse(managedByA.hasNext());
        Assert.assertEquals("p1", item.getProperty("name"));
        managedByA.close();
        OResultSet managedByB = getManagedBy2Arrows("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult id = managedByB.next();
            String name = id.getProperty("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testTriangle1() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("  .out('TriangleE'){as: friend2}");
        query.append("  .out('TriangleE'){as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTriangle1Arrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)} -TriangleE-> {as: friend2} -TriangleE-> {as: friend3},");
        query.append("{class:TriangleV, as: friend1} -TriangleE-> {as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTriangle2Old() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){class:TriangleV, as: friend2, where: (uid = 1)}");
        query.append("  .out('TriangleE'){as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        OElement friend1 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend1"))));
        OElement friend2 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend2"))));
        OElement friend3 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend3"))));
        Assert.assertEquals(0, friend1.<Object>getProperty("uid"));
        Assert.assertEquals(1, friend2.<Object>getProperty("uid"));
        Assert.assertEquals(2, friend3.<Object>getProperty("uid"));
        result.close();
    }

    @Test
    public void testTriangle2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){class:TriangleV, as: friend2, where: (uid = 1)}");
        query.append("  .out('TriangleE'){as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){as: friend3}");
        query.append("return $patterns");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        OElement friend1 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend1"))));
        OElement friend2 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend2"))));
        OElement friend3 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend3"))));
        Assert.assertEquals(0, friend1.<Object>getProperty("uid"));
        Assert.assertEquals(1, friend2.<Object>getProperty("uid"));
        Assert.assertEquals(2, friend3.<Object>getProperty("uid"));
        result.close();
    }

    @Test
    public void testTriangle2Arrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{class:TriangleV, as: friend2, where: (uid = 1)}");
        query.append("  -TriangleE->{as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        OElement friend1 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend1"))));
        OElement friend2 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend2"))));
        OElement friend3 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend3"))));
        Assert.assertEquals(0, friend1.<Object>getProperty("uid"));
        Assert.assertEquals(1, friend2.<Object>getProperty("uid"));
        Assert.assertEquals(2, friend3.<Object>getProperty("uid"));
        result.close();
    }

    @Test
    public void testTriangle3() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{as: friend2}");
        query.append("  -TriangleE->{as: friend3, where: (uid = 2)},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTriangle4() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){as: friend2, where: (uid = 1)}");
        query.append("  .out('TriangleE'){as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .out('TriangleE'){as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTriangle4Arrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{as: friend2, where: (uid = 1)}");
        query.append("  -TriangleE->{as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  -TriangleE->{as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTriangleWithEdges4() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .outE('TriangleE').inV(){as: friend2, where: (uid = 1)}");
        query.append("  .outE('TriangleE').inV(){as: friend3},");
        query.append("{class:TriangleV, as: friend1}");
        query.append("  .outE('TriangleE').inV(){as: friend3}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testCartesianProduct() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where:(uid = 1)},");
        query.append("{class:TriangleV, as: friend2, where:(uid = 2 or uid = 3)}");
        query.append("return $matches");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            OResult doc = result.next();
            OElement friend1 = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("friend1"))));
            Assert.assertEquals(friend1.<Object>getProperty("uid"), 1);
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testNoPrefetch() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one}");
        query.append("return $patterns");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        result.getExecutionPlan().ifPresent(( x) -> x.getSteps().stream().filter(( y) -> y instanceof MatchPrefetchStep).forEach(( prefetchStepFound) -> Assert.fail()));
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue(result.hasNext());
            result.next();
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testCartesianProductLimit() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where:(uid = 1)},");
        query.append("{class:TriangleV, as: friend2, where:(uid = 2 or uid = 3)}");
        query.append("return $matches LIMIT 1");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult d = result.next();
        OElement friend1 = OMatchStatementExecutionNewTest.db.load(((ORID) (d.getProperty("friend1"))));
        Assert.assertEquals(friend1.<Object>getProperty("uid"), 1);
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testArrayNumber() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Object foo = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("foo"))));
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof OVertex));
        result.close();
    }

    @Test
    public void testArraySingleSelectors2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0,1] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        Object foo = doc.getProperty("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
        result.close();
    }

    @Test
    public void testArrayRangeSelectors1() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..1] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        Object foo = doc.getProperty("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(1, ((List) (foo)).size());
        result.close();
    }

    @Test
    public void testArrayRange2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..2] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        Object foo = doc.getProperty("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
        result.close();
    }

    @Test
    public void testArrayRange3() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..3] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        Object foo = doc.getProperty("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
        result.close();
    }

    @Test
    public void testConditionInSquareBrackets() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[uid = 2] as foo");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        Object foo = doc.getProperty("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(1, ((List) (foo)).size());
        OVertex resultVertex = ((OVertex) (((List) (foo)).get(0)));
        Assert.assertEquals(2, resultVertex.<Object>getProperty("uid"));
        result.close();
    }

    @Test
    public void testIndexedEdge() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)}");
        query.append(".out('IndexedEdge'){class:IndexedVertex, as: two, where: (uid = 1)}");
        query.append("return one, two");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testIndexedEdgeArrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)}");
        query.append("-IndexedEdge->{class:IndexedVertex, as: two, where: (uid = 1)}");
        query.append("return one, two");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testJson() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'uuid':one.uid}");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("uuid"));
        result.close();
    }

    @Test
    public void testJson2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'sub': {'uuid':one.uid}}");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub.uuid"));
        result.close();
    }

    @Test
    public void testJson3() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'sub': [{'uuid':one.uid}]}");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub[0].uuid"));
        result.close();
    }

    @Test
    public void testUnique() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return DISTINCT one, two");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertFalse(result.hasNext());
        query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return DISTINCT one.uid, two.uid");
        result.close();
        result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub[0].uuid"));
    }

    @Test
    public void testNotUnique() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return one, two");
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query.toString());
        printExecutionPlan(result);
        Assert.assertTrue(result.hasNext());
        OResult doc = result.next();
        Assert.assertTrue(result.hasNext());
        doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
        query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return one.uid, two.uid");
        result = OMatchStatementExecutionNewTest.db.query(query.toString());
        Assert.assertTrue(result.hasNext());
        doc = result.next();
        Assert.assertTrue(result.hasNext());
        doc = result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub[0].uuid"));
    }

    @Test
    public void testManagedElements() {
        OResultSet managedByB = getManagedElements("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("b");
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult doc = managedByB.next();
            String name = doc.getProperty("name");
            names.add(name);
        }
        Assert.assertFalse(managedByB.hasNext());
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testManagedPathElements() {
        OResultSet managedByB = getManagedPathElements("b");
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("department1");
        expectedNames.add("department3");
        expectedNames.add("department4");
        expectedNames.add("department8");
        expectedNames.add("b");
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(managedByB.hasNext());
            OResult doc = managedByB.next();
            String name = doc.getProperty("name");
            names.add(name);
        }
        Assert.assertFalse(managedByB.hasNext());
        Assert.assertEquals(expectedNames, names);
        managedByB.close();
    }

    @Test
    public void testOptional() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person} -NonExistingEdge-> {as:b, optional:true} return person, b.name");
        printExecutionPlan(qResult);
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(qResult.hasNext());
            OResult doc = qResult.next();
            Assert.assertTrue(((doc.getPropertyNames().size()) == 2));
            OElement person = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("person"))));
            String name = person.getProperty("name");
            Assert.assertTrue(name.startsWith("n"));
        }
    }

    @Test
    public void testOptional2() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query("match {class:Person, as: person} --> {as:b, optional:true, where:(nonExisting = 12)} return person, b.name");
        for (int i = 0; i < 6; i++) {
            Assert.assertTrue(qResult.hasNext());
            OResult doc = qResult.next();
            Assert.assertTrue(((doc.getPropertyNames().size()) == 2));
            OElement person = OMatchStatementExecutionNewTest.db.load(((ORID) (doc.getProperty("person"))));
            String name = person.getProperty("name");
            Assert.assertTrue(name.startsWith("n"));
        }
    }

    @Test
    public void testOptional3() throws Exception {
        OResultSet qResult = OMatchStatementExecutionNewTest.db.query(("select friend.name as name, b from (" + ((("match {class:Person, as:a, where:(name = 'n1' and 1 + 1 = 2)}.out('Friend'){as:friend, where:(name = 'n2' and 1 + 1 = 2)}," + "{as:a}.out(){as:b, where:(nonExisting = 12), optional:true},") + "{as:friend}.out(){as:b, optional:true}") + " return friend, b)")));
        printExecutionPlan(qResult);
        Assert.assertTrue(qResult.hasNext());
        OResult doc = qResult.next();
        Assert.assertEquals("n2", doc.getProperty("name"));
        Assert.assertNull(doc.getProperty("b"));
        Assert.assertFalse(qResult.hasNext());
    }

    @Test
    public void testOrderByAsc() {
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE CLASS testOrderByAsc EXTENDS V")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByAsc SET name = 'bbb'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByAsc SET name = 'zzz'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByAsc SET name = 'aaa'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByAsc SET name = 'ccc'")).execute();
        String query = "MATCH { class: testOrderByAsc, as:a} RETURN a.name as name order by name asc";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("aaa", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("bbb", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("ccc", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("zzz", getProperty("name"));
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testOrderByDesc() {
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE CLASS testOrderByDesc EXTENDS V")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByDesc SET name = 'bbb'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByDesc SET name = 'zzz'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByDesc SET name = 'aaa'")).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL("CREATE VERTEX testOrderByDesc SET name = 'ccc'")).execute();
        String query = "MATCH { class: testOrderByDesc, as:a} RETURN a.name as name order by name desc";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("zzz", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("ccc", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("bbb", getProperty("name"));
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("aaa", getProperty("name"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testNestedProjections() {
        String clazz = "testNestedProjections";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb', surname = 'ccc'"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a:{name}, 'x' ";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        OResult a = item.getProperty("a");
        Assert.assertEquals("bbb", a.getProperty("name"));
        Assert.assertNull(a.getProperty("surname"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testAggregate() {
        String clazz = "testAggregate";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 1"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 2"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 3"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb', num = 4"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb', num = 5"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb', num = 6"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a.name as a, max(a.num) as maxNum group by a.name order by a.name";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("aaa", item.getProperty("a"));
        Assert.assertEquals(3, ((int) (item.getProperty("maxNum"))));
        Assert.assertTrue(result.hasNext());
        item = result.next();
        Assert.assertEquals("bbb", item.getProperty("a"));
        Assert.assertEquals(6, ((int) (item.getProperty("maxNum"))));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testOrderByOutOfProjAsc() {
        String clazz = "testOrderByOutOfProjAsc";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 0, num2 = 1"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 1, num2 = 2"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 2, num2 = 3"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a.name as name, a.num as num order by a.num2 asc";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals("aaa", item.getProperty("name"));
            Assert.assertEquals(i, ((int) (item.getProperty("num"))));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testOrderByOutOfProjDesc() {
        String clazz = "testOrderByOutOfProjDesc";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 0, num2 = 1"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 1, num2 = 2"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', num = 2, num2 = 3"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a.name as name, a.num as num order by a.num2 desc";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        for (int i = 2; i >= 0; i--) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertEquals("aaa", item.getProperty("name"));
            Assert.assertEquals(i, ((int) (item.getProperty("num"))));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testUnwind() {
        String clazz = "testUnwind";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa', coll = [1, 2]"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb', coll = [3, 4]"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a.name as name, a.coll as num unwind num";
        int sum = 0;
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            sum += ((int) (item.getProperty("num")));
        }
        Assert.assertFalse(result.hasNext());
        result.close();
        Assert.assertEquals(10, sum);
    }

    @Test
    public void testSkip() {
        String clazz = "testSkip";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ccc'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ddd'"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a} RETURN a.name as name skip 1 limit 2";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("bbb", item.getProperty("name"));
        Assert.assertTrue(result.hasNext());
        item = result.next();
        Assert.assertEquals("ccc", item.getProperty("name"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testDepthAlias() {
        String clazz = "testDepthAlias";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ccc'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ddd'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'aaa') TO (SELECT FROM ") + clazz) + " WHERE name = 'bbb')"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'bbb') TO (SELECT FROM ") + clazz) + " WHERE name = 'ccc')"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'ccc') TO (SELECT FROM ") + clazz) + " WHERE name = 'ddd')"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a, where:(name = 'aaa')} --> {as:b, while:($depth<10), depthAlias: xy} RETURN a.name as name, b.name as bname, xy";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Object depth = item.getProperty("xy");
            Assert.assertTrue((depth instanceof Integer));
            Assert.assertEquals("aaa", item.getProperty("name"));
            switch (((int) (depth))) {
                case 0 :
                    Assert.assertEquals("aaa", item.getProperty("bname"));
                    break;
                case 1 :
                    Assert.assertEquals("bbb", item.getProperty("bname"));
                    break;
                case 2 :
                    Assert.assertEquals("ccc", item.getProperty("bname"));
                    break;
                case 3 :
                    Assert.assertEquals("ddd", item.getProperty("bname"));
                    break;
                default :
                    Assert.fail();
            }
            sum += ((int) (depth));
        }
        Assert.assertEquals(sum, 6);
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testPathAlias() {
        String clazz = "testPathAlias";
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE CLASS " + clazz) + " EXTENDS V"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'aaa'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'bbb'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ccc'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((("CREATE VERTEX " + clazz) + " SET name = 'ddd'"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'aaa') TO (SELECT FROM ") + clazz) + " WHERE name = 'bbb')"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'bbb') TO (SELECT FROM ") + clazz) + " WHERE name = 'ccc')"))).execute();
        OMatchStatementExecutionNewTest.db.command(new OCommandSQL((((("CREATE EDGE E FROM (SELECT FROM " + clazz) + " WHERE name = 'ccc') TO (SELECT FROM ") + clazz) + " WHERE name = 'ddd')"))).execute();
        String query = ("MATCH { class: " + clazz) + ", as:a, where:(name = 'aaa')} --> {as:b, while:($depth<10), pathAlias: xy} RETURN a.name as name, b.name as bname, xy";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Object path = item.getProperty("xy");
            Assert.assertTrue((path instanceof List));
            List<OIdentifiable> thePath = ((List<OIdentifiable>) (path));
            String bname = item.getProperty("bname");
            if (bname.equals("aaa")) {
                Assert.assertEquals(0, thePath.size());
            } else
                if (bname.equals("aaa")) {
                    Assert.assertEquals(1, thePath.size());
                    Assert.assertEquals("bbb", getProperty("name"));
                } else
                    if (bname.equals("ccc")) {
                        Assert.assertEquals(2, thePath.size());
                        Assert.assertEquals("bbb", getProperty("name"));
                        Assert.assertEquals("ccc", getProperty("name"));
                    } else
                        if (bname.equals("ddd")) {
                            Assert.assertEquals(3, thePath.size());
                            Assert.assertEquals("bbb", getProperty("name"));
                            Assert.assertEquals("ccc", getProperty("name"));
                            Assert.assertEquals("ddd", getProperty("name"));
                        }



        }
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testClusterTarget() {
        String clazz = "testClusterTarget";
        OMatchStatementExecutionNewTest.db.command((("CREATE CLASS " + clazz) + " EXTENDS V")).close();
        OMatchStatementExecutionNewTest.db.command((((("ALTER CLASS " + clazz) + " ADDCLUSTER ") + (clazz.toLowerCase())) + "_one")).close();
        OMatchStatementExecutionNewTest.db.command((((("ALTER CLASS " + clazz) + " ADDCLUSTER ") + (clazz.toLowerCase())) + "_two")).close();
        OMatchStatementExecutionNewTest.db.command((((("ALTER CLASS " + clazz) + " ADDCLUSTER ") + (clazz.toLowerCase())) + "_three")).close();
        OVertex v1 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v1.setProperty("name", "one");
        v1.save(((clazz.toLowerCase()) + "_one"));
        OVertex vx = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        vx.setProperty("name", "onex");
        vx.save(((clazz.toLowerCase()) + "_one"));
        OVertex v2 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v2.setProperty("name", "two");
        v2.save(((clazz.toLowerCase()) + "_two"));
        OVertex v3 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v3.setProperty("name", "three");
        v3.save(((clazz.toLowerCase()) + "_three"));
        v1.addEdge(v2).save();
        v2.addEdge(v3).save();
        v1.addEdge(v3).save();
        String query = ((("MATCH { cluster: " + (clazz.toLowerCase())) + "_one, as:a} --> {as:b, cluster:") + (clazz.toLowerCase())) + "_two} RETURN a.name as aname, b.name as bname";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        OResult item = result.next();
        Assert.assertEquals("one", item.getProperty("aname"));
        Assert.assertEquals("two", item.getProperty("bname"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testNegativePattern() {
        String clazz = "testNegativePattern";
        OMatchStatementExecutionNewTest.db.command((("CREATE CLASS " + clazz) + " EXTENDS V")).close();
        OVertex v1 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v1.setProperty("name", "a");
        v1.save();
        OVertex v2 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v2.setProperty("name", "b");
        v2.save();
        OVertex v3 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v3.setProperty("name", "c");
        v3.save();
        v1.addEdge(v2).save();
        v2.addEdge(v3).save();
        String query = ("MATCH { class:" + clazz) + ", as:a} --> {as:b} --> {as:c}, ";
        query += " NOT {as:a} --> {as:c}";
        query += " RETURN $patterns";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testNegativePattern2() {
        String clazz = "testNegativePattern2";
        OMatchStatementExecutionNewTest.db.command((("CREATE CLASS " + clazz) + " EXTENDS V")).close();
        OVertex v1 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v1.setProperty("name", "a");
        v1.save();
        OVertex v2 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v2.setProperty("name", "b");
        v2.save();
        OVertex v3 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v3.setProperty("name", "c");
        v3.save();
        v1.addEdge(v2).save();
        v2.addEdge(v3).save();
        v1.addEdge(v3).save();
        String query = ("MATCH { class:" + clazz) + ", as:a} --> {as:b} --> {as:c}, ";
        query += " NOT {as:a} --> {as:c}";
        query += " RETURN $patterns";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testNegativePattern3() {
        String clazz = "testNegativePattern3";
        OMatchStatementExecutionNewTest.db.command((("CREATE CLASS " + clazz) + " EXTENDS V")).close();
        OVertex v1 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v1.setProperty("name", "a");
        v1.save();
        OVertex v2 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v2.setProperty("name", "b");
        v2.save();
        OVertex v3 = OMatchStatementExecutionNewTest.db.newVertex(clazz);
        v3.setProperty("name", "c");
        v3.save();
        v1.addEdge(v2).save();
        v2.addEdge(v3).save();
        v1.addEdge(v3).save();
        String query = ("MATCH { class:" + clazz) + ", as:a} --> {as:b} --> {as:c}, ";
        query += " NOT {as:a} --> {as:c, where:(name <> 'c')}";
        query += " RETURN $patterns";
        OResultSet result = OMatchStatementExecutionNewTest.db.query(query);
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

