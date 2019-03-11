package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class OMatchStatementExecutionTest {
    private static String DB_STORAGE = "memory";

    private static String DB_NAME = "OMatchStatementExecutionTest";

    static ODatabaseDocumentTx db;

    @Test
    public void testSimple() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person} return person")).execute();
        Assert.assertEquals(6, qResult.size());
        for (ODocument doc : qResult) {
            Assert.assertTrue(((doc.fieldNames().length) == 1));
            OIdentifiable personId = doc.field("person");
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(name.startsWith("n"));
        }
    }

    @Test
    public void testSimpleWhere() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person")).execute();
        Assert.assertEquals(2, qResult.size());
        for (ODocument doc : qResult) {
            Assert.assertTrue(((doc.fieldNames().length) == 1));
            OIdentifiable personId = doc.field("person");
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(((name.equals("n1")) || (name.equals("n2"))));
        }
    }

    @Test
    public void testSimpleLimit() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
    }

    @Test
    public void testSimpleLimit2() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit -1")).execute();
        Assert.assertEquals(2, qResult.size());
    }

    @Test
    public void testSimpleLimit3() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person, where: (name = 'n1' or name = 'n2')} return person limit 3")).execute();
        Assert.assertEquals(2, qResult.size());
    }

    @Test
    public void testSimpleUnnamedParams() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person, where: (name = ? or name = ?)} return person")).execute("n1", "n2");
        Assert.assertEquals(2, qResult.size());
        for (ODocument doc : qResult) {
            Assert.assertTrue(((doc.fieldNames().length) == 1));
            OIdentifiable personId = doc.field("person");
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(((name.equals("n1")) || (name.equals("n2"))));
        }
    }

    @Test
    public void testCommonFriends() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return $matches)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testCommonFriendsArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return $matches)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testCommonFriends2() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testCommonFriends2Arrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testReturnMethod() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name.toUpperCase(Locale.ENGLISH) as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("N2", field("name"));
    }

    @Test
    public void testReturnMethodArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name.toUpperCase(Locale.ENGLISH) as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("N2", field("name"));
    }

    @Test
    public void testReturnExpression() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name + ' ' +friend.name as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2 n2", field("name"));
    }

    @Test
    public void testReturnExpressionArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name + ' ' +friend.name as name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2 n2", field("name"));
    }

    @Test
    public void testReturnDefaultAlias() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}.both('Friend'){as:friend}.both('Friend'){class: Person, where:(name = 'n4')} return friend.name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", qResult.get(0).getProperty("friend.name"));
    }

    @Test
    public void testReturnDefaultAliasArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, where:(name = 'n1')}-Friend-{as:friend}-Friend-{class: Person, where:(name = 'n4')} return friend.name")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", qResult.get(0).getProperty("friend.name"));
    }

    @Test
    public void testFriendsOfFriends() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend').out('Friend'){as:friend} return $matches)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n4", field("name"));
    }

    @Test
    public void testFriendsOfFriendsArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{}-Friend->{as:friend} return $matches)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n4", field("name"));
    }

    @Test
    public void testFriendsOfFriends2() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1'), as: me}.both('Friend').both('Friend'){as:friend, where: ($matched.me != $currentMatch)} return $matches)")).execute();
        for (ODocument doc : qResult) {
            Assert.assertNotEquals(doc.field("name"), "n1");
        }
    }

    @Test
    public void testFriendsOfFriends2Arrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1'), as: me}-Friend-{}-Friend-{as:friend, where: ($matched.me != $currentMatch)} return $matches)")).execute();
        for (ODocument doc : qResult) {
            Assert.assertNotEquals(doc.field("name"), "n1");
        }
    }

    @Test
    public void testFriendsWithName() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1' and 1 + 1 = 2)}.out('Friend'){as:friend, where:(name = 'n2' and 1 + 1 = 2)} return friend)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testFriendsWithNameArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1' and 1 + 1 = 2)}-Friend->{as:friend, where:(name = 'n2' and 1 + 1 = 2)} return friend)")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testWhile() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 1)} return friend)")).execute();
        Assert.assertEquals(3, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 2), where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: ($depth < 4), where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend)")).execute();
        Assert.assertEquals(6, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend limit 3)")).execute();
        Assert.assertEquals(3, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, while: (true) } return friend) limit 3")).execute();
        Assert.assertEquals(3, qResult.size());
    }

    @Test
    public void testWhileArrows() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 1)} return friend)")).execute();
        Assert.assertEquals(3, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 2), where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: ($depth < 4), where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, while: (true) } return friend)")).execute();
        Assert.assertEquals(6, qResult.size());
    }

    @Test
    public void testMaxDepth() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1, where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1 } return friend)")).execute();
        Assert.assertEquals(3, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 0 } return friend)")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}.out('Friend'){as:friend, maxDepth: 1, where: ($depth > 0) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
    }

    @Test
    public void testMaxDepthArrow() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1, where: ($depth=1) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1 } return friend)")).execute();
        Assert.assertEquals(3, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 0 } return friend)")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select friend.name as name from (match {class:Person, where:(name = 'n1')}-Friend->{as:friend, maxDepth: 1, where: ($depth > 0) } return friend)")).execute();
        Assert.assertEquals(2, qResult.size());
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
        Assert.assertEquals("c", getManager2("p10").field("name"));
        Assert.assertEquals("c", getManager2("p12").field("name"));
        Assert.assertEquals("b", getManager2("p6").field("name"));
        Assert.assertEquals("b", getManager2("p11").field("name"));
        Assert.assertEquals("c", getManager2Arrows("p10").field("name"));
        Assert.assertEquals("c", getManager2Arrows("p12").field("name"));
        Assert.assertEquals("b", getManager2Arrows("p6").field("name"));
        Assert.assertEquals("b", getManager2Arrows("p11").field("name"));
    }

    @Test
    public void testManaged() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        List<OIdentifiable> managedByA = getManagedBy("a");
        Assert.assertEquals(1, managedByA.size());
        Assert.assertEquals("p1", field("name"));
        List<OIdentifiable> managedByB = getManagedBy("b");
        Assert.assertEquals(5, managedByB.size());
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
    }

    @Test
    public void testManagedArrows() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        List<OIdentifiable> managedByA = getManagedByArrows("a");
        Assert.assertEquals(1, managedByA.size());
        Assert.assertEquals("p1", field("name"));
        List<OIdentifiable> managedByB = getManagedByArrows("b");
        Assert.assertEquals(5, managedByB.size());
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
    }

    @Test
    public void testManaged2() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        List<OIdentifiable> managedByA = getManagedBy2("a");
        Assert.assertEquals(1, managedByA.size());
        Assert.assertEquals("p1", field("name"));
        List<OIdentifiable> managedByB = getManagedBy2("b");
        Assert.assertEquals(5, managedByB.size());
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
    }

    @Test
    public void testManaged2Arrows() {
        // people managed by a manager are people who belong to his department or people who belong to sub-departments without a manager
        List<OIdentifiable> managedByA = getManagedBy2Arrows("a");
        Assert.assertEquals(1, managedByA.size());
        Assert.assertEquals("p1", field("name"));
        List<OIdentifiable> managedByB = getManagedBy2Arrows("b");
        Assert.assertEquals(5, managedByB.size());
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
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
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testTriangle1Arrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)} -TriangleE-> {as: friend2} -TriangleE-> {as: friend3},");
        query.append("{class:TriangleV, as: friend1} -TriangleE-> {as: friend3}");
        query.append("return $matches");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
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
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = result.get(0);
        ODocument friend1 = getRecord();
        ODocument friend2 = getRecord();
        ODocument friend3 = getRecord();
        Assert.assertEquals(0, friend1.<Object>field("uid"));
        Assert.assertEquals(1, friend2.<Object>field("uid"));
        Assert.assertEquals(2, friend3.<Object>field("uid"));
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
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = result.get(0);
        ODocument friend1 = getRecord();
        ODocument friend2 = getRecord();
        ODocument friend3 = getRecord();
        Assert.assertEquals(0, friend1.<Object>field("uid"));
        Assert.assertEquals(1, friend2.<Object>field("uid"));
        Assert.assertEquals(2, friend3.<Object>field("uid"));
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
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = result.get(0);
        ODocument friend1 = getRecord();
        ODocument friend2 = getRecord();
        ODocument friend3 = getRecord();
        Assert.assertEquals(0, friend1.<Object>field("uid"));
        Assert.assertEquals(1, friend2.<Object>field("uid"));
        Assert.assertEquals(2, friend3.<Object>field("uid"));
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
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
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
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
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
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
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
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testCartesianProduct() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where:(uid = 1)},");
        query.append("{class:TriangleV, as: friend2, where:(uid = 2 or uid = 3)}");
        query.append("return $matches");
        List<OIdentifiable> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(2, result.size());
        for (OIdentifiable d : result) {
            Assert.assertEquals(<Object>field("uid"), 1);
        }
    }

    @Test
    public void testCartesianProductLimit() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where:(uid = 1)},");
        query.append("{class:TriangleV, as: friend2, where:(uid = 2 or uid = 3)}");
        query.append("return $matches LIMIT 1");
        List<OIdentifiable> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        for (OIdentifiable d : result) {
            Assert.assertEquals(<Object>field("uid"), 1);
        }
    }

    @Test
    public void testArrayNumber() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof OVertex));
    }

    @Test
    public void testArraySingleSelectors2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0,1] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
    }

    @Test
    public void testArrayRangeSelectors1() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..1] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(1, ((List) (foo)).size());
    }

    @Test
    public void testArrayRange2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..2] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
    }

    @Test
    public void testArrayRange3() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[0..3] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(2, ((List) (foo)).size());
    }

    @Test
    public void testConditionInSquareBrackets() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:TriangleV, as: friend1, where: (uid = 0)}");
        query.append("return friend1.out('TriangleE')[uid = 2] as foo");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        ODocument doc = ((ODocument) (result.get(0)));
        Object foo = doc.field("foo");
        Assert.assertNotNull(foo);
        Assert.assertTrue((foo instanceof List));
        Assert.assertEquals(1, ((List) (foo)).size());
        OVertex resultVertex = ((OVertex) (((List) (foo)).get(0)));
        Assert.assertEquals(2, resultVertex.<Object>getProperty("uid"));
    }

    @Test
    public void testIndexedEdge() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)}");
        query.append(".out('IndexedEdge'){class:IndexedVertex, as: two, where: (uid = 1)}");
        query.append("return one, two");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testIndexedEdgeArrows() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)}");
        query.append("-IndexedEdge->{class:IndexedVertex, as: two, where: (uid = 1)}");
        query.append("return one, two");
        List<?> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testJson() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'uuid':one.uid}");
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("uuid"));
    }

    @Test
    public void testJson2() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'sub': {'uuid':one.uid}}");
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub.uuid"));
    }

    @Test
    public void testJson3() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:IndexedVertex, as: one, where: (uid = 0)} ");
        query.append("return {'name':'foo', 'sub': [{'uuid':one.uid}]}");
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub[0].uuid"));
    }

    @Test
    public void testUnique() {
        StringBuilder query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return one, two");
        List<ODocument> result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        query = new StringBuilder();
        query.append("match ");
        query.append("{class:DiamondV, as: one, where: (uid = 0)}.out('DiamondE').out('DiamondE'){as: two} ");
        query.append("return one.uid, two.uid");
        result = OMatchStatementExecutionTest.db.command(new OCommandSQL(query.toString())).execute();
        Assert.assertEquals(1, result.size());
        // ODocument doc = result.get(0);
        // assertEquals("foo", doc.field("name"));
        // assertEquals(0, doc.field("sub[0].uuid"));
    }

    @Test
    public void testManagedElements() {
        List<OIdentifiable> managedByB = getManagedElements("b");
        Assert.assertEquals(6, managedByB.size());
        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("b");
        expectedNames.add("p2");
        expectedNames.add("p3");
        expectedNames.add("p6");
        expectedNames.add("p7");
        expectedNames.add("p11");
        Set<String> names = new HashSet<String>();
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
    }

    @Test
    public void testManagedPathElements() {
        List<OIdentifiable> managedByB = getManagedPathElements("b");
        Assert.assertEquals(10, managedByB.size());
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
        for (OIdentifiable id : managedByB) {
            ODocument doc = id.getRecord();
            String name = doc.field("name");
            names.add(name);
        }
        Assert.assertEquals(expectedNames, names);
    }

    @Test
    public void testOptional() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person} -NonExistingEdge-> {as:b, optional:true} return person, b.name")).execute();
        Assert.assertEquals(6, qResult.size());
        for (ODocument doc : qResult) {
            Assert.assertTrue(((doc.fieldNames().length) == 2));
            OIdentifiable personId = doc.field("person");
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(name.startsWith("n"));
        }
    }

    @Test
    public void testOptional2() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("match {class:Person, as: person} --> {as:b, optional:true, where:(nonExisting = 12)} return person, b.name")).execute();
        Assert.assertEquals(6, qResult.size());
        for (ODocument doc : qResult) {
            Assert.assertTrue(((doc.fieldNames().length) == 2));
            OIdentifiable personId = doc.field("person");
            ODocument person = personId.getRecord();
            String name = person.field("name");
            Assert.assertTrue(name.startsWith("n"));
        }
    }

    @Test
    public void testOptional3() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL(("select friend.name as name from (" + ((("match {class:Person, as:a, where:(name = 'n1' and 1 + 1 = 2)}.out('Friend'){as:friend, where:(name = 'n2' and 1 + 1 = 2)}," + "{as:a}.out(){as:b, where:(nonExisting = 12), optional:true},") + "{as:friend}.out(){as:b, optional:true}") + " return friend)")))).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertEquals("n2", field("name"));
    }

    @Test
    public void testAliasesWithSubquery() throws Exception {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("select from ( match {class:Person, as:A} return A.name as namexx ) limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
        Assert.assertNotNull(field("namexx"));
        Assert.assertTrue(field("namexx").toString().startsWith("n"));
    }

    @Test
    public void testEvalInReturn() {
        // issue #6606
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testEvalInReturn EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE PROPERTY testEvalInReturn.name String")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testEvalInReturn SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testEvalInReturn SET name = 'bar'")).execute();
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: testEvalInReturn, as: p} RETURN if(eval(\"p.name = \'foo\'\"), 1, 2) AS b")).execute();
        Assert.assertEquals(2, qResult.size());
        int sum = 0;
        for (ODocument doc : qResult) {
            sum += ((Number) (doc.field("b"))).intValue();
        }
        Assert.assertEquals(3, sum);
        // check that it still removes duplicates (standard behavior for MATCH)
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: testEvalInReturn, as: p} RETURN if(eval(\"p.name = \'foo\'\"), \'foo\', \'foo\') AS b")).execute();
        Assert.assertEquals(1, qResult.size());
    }

    @Test
    public void testCheckClassAsCondition() {
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCheckClassAsCondition EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCheckClassAsCondition1 EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCheckClassAsCondition2 EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCheckClassAsCondition SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCheckClassAsCondition1 SET name = 'bar'")).execute();
        for (int i = 0; i < 5; i++) {
            OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCheckClassAsCondition2 SET name = 'baz'")).execute();
        }
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE EDGE E FROM (select from testCheckClassAsCondition where name = 'foo') to (select from testCheckClassAsCondition1)")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE EDGE E FROM (select from testCheckClassAsCondition where name = 'foo') to (select from testCheckClassAsCondition2)")).execute();
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: testCheckClassAsCondition, as: p} -E- {class: testCheckClassAsCondition1, as: q} RETURN $elements")).execute();
        Assert.assertEquals(2, qResult.size());
    }

    @Test
    public void testInstanceof() {
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, as: p, where: ($currentMatch instanceof 'Person')} return $elements limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, as: p, where: ($currentMatch instanceof 'V')} return $elements limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, as: p, where: (not ($currentMatch instanceof 'Person'))} return $elements limit 1")).execute();
        Assert.assertEquals(0, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, where: (name = 'n1')}.out(){as:p, where: ($currentMatch instanceof 'Person')} return $elements limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, where: (name = 'n1')}.out(){as:p, where: ($currentMatch instanceof 'Person' and '$currentMatch' <> '@this')} return $elements limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
        qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: Person, where: (name = 'n1')}.out(){as:p, where: ( not ($currentMatch instanceof 'Person'))} return $elements limit 1")).execute();
        Assert.assertEquals(0, qResult.size());
    }

    @Test
    public void testBigEntryPoint() {
        // issue #6890
        OSchema schema = OMatchStatementExecutionTest.db.getMetadata().getSchema();
        schema.createClass("testBigEntryPoint1");
        schema.createClass("testBigEntryPoint2");
        for (int i = 0; i < 1000; i++) {
            ODocument doc = OMatchStatementExecutionTest.db.newInstance("testBigEntryPoint1");
            doc.field("a", i);
            doc.save();
        }
        ODocument doc = OMatchStatementExecutionTest.db.newInstance("testBigEntryPoint2");
        doc.field("b", "b");
        doc.save();
        List<ODocument> qResult = OMatchStatementExecutionTest.db.command(new OCommandSQL("MATCH {class: testBigEntryPoint1, as: a}, {class: testBigEntryPoint2, as: b} return $elements limit 1")).execute();
        Assert.assertEquals(1, qResult.size());
    }

    @Test
    public void testMatched1() {
        // issue #6931
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Foo EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Bar EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Baz EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Far EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Foo_Bar EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Bar_Baz EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testMatched1_Foo_Far EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testMatched1_Foo SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testMatched1_Bar SET name = 'bar'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testMatched1_Baz SET name = 'baz'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testMatched1_Far SET name = 'far'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE EDGE testMatched1_Foo_Bar FROM (SELECT FROM testMatched1_Foo) TO (SELECT FROM testMatched1_Bar)")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE EDGE testMatched1_Bar_Baz FROM (SELECT FROM testMatched1_Bar) TO (SELECT FROM testMatched1_Baz)")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE EDGE testMatched1_Foo_Far FROM (SELECT FROM testMatched1_Foo) TO (SELECT FROM testMatched1_Far)")).execute();
        List result = OMatchStatementExecutionTest.db.query(new OSQLSynchQuery(("MATCH \n" + ((("{class: testMatched1_Foo, as: foo}.out(\'testMatched1_Foo_Bar\') {as: bar}, \n" + "{class: testMatched1_Bar,as: bar}.out(\'testMatched1_Bar_Baz\') {as: baz}, \n") + "{class: testMatched1_Foo,as: foo}.out(\'testMatched1_Foo_Far\') {where: ($matched.baz IS null),as: far}\n") + "RETURN $matches"))));
        Assert.assertTrue(result.isEmpty());
        result = OMatchStatementExecutionTest.db.query(new OSQLSynchQuery(("MATCH \n" + ((("{class: testMatched1_Foo, as: foo}.out(\'testMatched1_Foo_Bar\') {as: bar}, \n" + "{class: testMatched1_Bar,as: bar}.out(\'testMatched1_Bar_Baz\') {as: baz}, \n") + "{class: testMatched1_Foo,as: foo}.out(\'testMatched1_Foo_Far\') {where: ($matched.baz IS not null),as: far}\n") + "RETURN $matches"))));
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testDependencyOrdering1() {
        // issue #6931
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Foo EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Bar EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Baz EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Far EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Foo_Bar EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Bar_Baz EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testDependencyOrdering1_Foo_Far EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testDependencyOrdering1_Foo SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testDependencyOrdering1_Bar SET name = 'bar'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testDependencyOrdering1_Baz SET name = 'baz'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testDependencyOrdering1_Far SET name = 'far'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testDependencyOrdering1_Foo_Bar FROM (" + "SELECT FROM testDependencyOrdering1_Foo) TO (SELECT FROM testDependencyOrdering1_Bar)"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testDependencyOrdering1_Bar_Baz FROM (" + "SELECT FROM testDependencyOrdering1_Bar) TO (SELECT FROM testDependencyOrdering1_Baz)"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testDependencyOrdering1_Foo_Far FROM (" + "SELECT FROM testDependencyOrdering1_Foo) TO (SELECT FROM testDependencyOrdering1_Far)"))).execute();
        // The correct but non-obvious execution order here is:
        // foo, bar, far, baz
        // This is a test to ensure that the query scheduler resolves dependencies correctly,
        // even if they are unusual or contrived.
        List result = OMatchStatementExecutionTest.db.query(new OSQLSynchQuery(("MATCH {\n" + (((((((((((((("    class: testDependencyOrdering1_Foo,\n" + "    as: foo\n") + "}.out(\'testDependencyOrdering1_Foo_Far\') {\n") + "    optional: true,\n") + "    where: ($matched.bar IS NOT null),\n") + "    as: far\n") + "}, {\n") + "    as: foo\n") + "}.out(\'testDependencyOrdering1_Foo_Bar\') {\n") + "    where: ($matched.foo IS NOT null),\n") + "    as: bar\n") + "}.out(\'testDependencyOrdering1_Bar_Baz\') {\n") + "    where: ($matched.far IS NOT null),\n") + "    as: baz\n") + "} RETURN $matches"))));
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testCircularDependency() {
        // issue #6931
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Foo EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Bar EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Baz EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Far EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Foo_Bar EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Bar_Baz EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCircularDependency_Foo_Far EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCircularDependency_Foo SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCircularDependency_Bar SET name = 'bar'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCircularDependency_Baz SET name = 'baz'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCircularDependency_Far SET name = 'far'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCircularDependency_Foo_Bar FROM (" + "SELECT FROM testCircularDependency_Foo) TO (SELECT FROM testCircularDependency_Bar)"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCircularDependency_Bar_Baz FROM (" + "SELECT FROM testCircularDependency_Bar) TO (SELECT FROM testCircularDependency_Baz)"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCircularDependency_Foo_Far FROM (" + "SELECT FROM testCircularDependency_Foo) TO (SELECT FROM testCircularDependency_Far)"))).execute();
        // The circular dependency here is:
        // - far depends on baz
        // - baz depends on bar
        // - bar depends on far
        OSQLSynchQuery query = new OSQLSynchQuery(("MATCH {\n" + ((((((((((((("    class: testCircularDependency_Foo,\n" + "    as: foo\n") + "}.out(\'testCircularDependency_Foo_Far\') {\n") + "    where: ($matched.baz IS NOT null),\n") + "    as: far\n") + "}, {\n") + "    as: foo\n") + "}.out(\'testCircularDependency_Foo_Bar\') {\n") + "    where: ($matched.far IS NOT null),\n") + "    as: bar\n") + "}.out(\'testCircularDependency_Bar_Baz\') {\n") + "    where: ($matched.bar IS NOT null),\n") + "    as: baz\n") + "} RETURN $matches")));
        try {
            OMatchStatementExecutionTest.db.query(query);
            Assert.fail();
        } catch (OCommandExecutionException x) {
            // passed the test
        }
    }

    @Test
    public void testUndefinedAliasDependency() {
        // issue #6931
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testUndefinedAliasDependency_Foo EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testUndefinedAliasDependency_Bar EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testUndefinedAliasDependency_Foo_Bar EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testUndefinedAliasDependency_Foo SET name = 'foo'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testUndefinedAliasDependency_Bar SET name = 'bar'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testUndefinedAliasDependency_Foo_Bar FROM (" + "SELECT FROM testUndefinedAliasDependency_Foo) TO (SELECT FROM testUndefinedAliasDependency_Bar)"))).execute();
        // "bar" in the following query declares a dependency on the alias "baz", which doesn't exist.
        OSQLSynchQuery query = new OSQLSynchQuery(("MATCH {\n" + ((((("    class: testUndefinedAliasDependency_Foo,\n" + "    as: foo\n") + "}.out(\'testUndefinedAliasDependency_Foo_Bar\') {\n") + "    where: ($matched.baz IS NOT null),\n") + "    as: bar\n") + "} RETURN $matches")));
        try {
            OMatchStatementExecutionTest.db.query(query);
            Assert.fail();
        } catch (OCommandExecutionException x) {
            // passed the test
        }
    }

    @Test
    public void testCyclicDeepTraversal() {
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCyclicDeepTraversalV EXTENDS V")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE CLASS testCyclicDeepTraversalE EXTENDS E")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCyclicDeepTraversalV SET name = 'a'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCyclicDeepTraversalV SET name = 'b'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCyclicDeepTraversalV SET name = 'c'")).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL("CREATE VERTEX testCyclicDeepTraversalV SET name = 'z'")).execute();
        // a -> b -> z
        // z -> c -> a
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCyclicDeepTraversalE from" + "(select from testCyclicDeepTraversalV where name = 'a') to (select from testCyclicDeepTraversalV where name = 'b')"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCyclicDeepTraversalE from" + "(select from testCyclicDeepTraversalV where name = 'b') to (select from testCyclicDeepTraversalV where name = 'z')"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCyclicDeepTraversalE from" + "(select from testCyclicDeepTraversalV where name = 'z') to (select from testCyclicDeepTraversalV where name = 'c')"))).execute();
        OMatchStatementExecutionTest.db.command(new OCommandSQL(("CREATE EDGE testCyclicDeepTraversalE from" + "(select from testCyclicDeepTraversalV where name = 'c') to (select from testCyclicDeepTraversalV where name = 'a')"))).execute();
        OSQLSynchQuery query = new OSQLSynchQuery(("MATCH {\n" + (((((((((((("    class: testCyclicDeepTraversalV,\n" + "    as: foo,\n") + "    where: (name = \'a\')\n") + "}.out() {\n") + "    while: ($depth < 2),\n") + "    where: (name = \'z\'),\n") + "    as: bar\n") + "}, {\n") + "    as: bar\n") + "}.out() {\n") + "    while: ($depth < 2),\n") + "    as: foo\n") + "} RETURN $patterns")));
        List<?> result = OMatchStatementExecutionTest.db.query(query);
        Assert.assertEquals(1, result.size());
    }
}

