package com.orientechnologies.orient.core.iterator;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Artem Loginov
 */
public class ClassIteratorTest {
    private static final boolean RECREATE_DATABASE = true;

    private static ODatabaseDocumentTx db = null;

    private Set<String> names;

    @Test
    public void testIteratorShouldReuseRecordWithoutNPE() {
        // Use class iterator.
        // browseClass() returns all documents in RecordID order
        // (including subclasses, which shouldn't exist for Person)
        final ORecordIteratorClass<ODocument> personIter = ClassIteratorTest.db.browseClass("Person");
        // Setting this to true causes the bug. Setting to false it works fine.
        personIter.setReuseSameRecord(true);
        int docNum = 0;
        // Explicit iterator loop.
        while (personIter.hasNext()) {
            final ODocument personDoc = personIter.next();
            Assert.assertTrue(names.contains(personDoc.field("First")));
            Assert.assertTrue(names.remove(personDoc.field("First")));
            System.out.printf("Doc %d: %s\n", (docNum++), personDoc.toString());
        } 
        Assert.assertTrue(names.isEmpty());
    }

    @Test
    public void testIteratorShouldReuseRecordWithoutNPEUsingForEach() throws Exception {
        // Use class iterator.
        // browseClass() returns all documents in RecordID order
        // (including subclasses, which shouldn't exist for Person)
        final ORecordIteratorClass<ODocument> personIter = ClassIteratorTest.db.browseClass("Person");
        // Setting this to true causes the bug. Setting to false it works fine.
        personIter.setReuseSameRecord(true);
        // Shorthand iterator loop.
        int docNum = 0;
        for (final ODocument personDoc : personIter) {
            Assert.assertTrue(names.contains(personDoc.field("First")));
            Assert.assertTrue(names.remove(personDoc.field("First")));
            System.out.printf("Doc %d: %s\n", (docNum++), personDoc.toString());
        }
        Assert.assertTrue(names.isEmpty());
    }

    @Test
    public void testDescendentOrderIteratorWithMultipleClusters() throws Exception {
        final OClass personClass = ClassIteratorTest.db.getMetadata().getSchema().getClass("Person");
        // empty old cluster but keep it attached
        personClass.truncate();
        // reload the data in a new 'test' cluster
        int testClusterId = ClassIteratorTest.db.addCluster("test");
        personClass.addClusterId(testClusterId);
        personClass.setClusterSelection(new ODefaultClusterSelectionStrategy());
        personClass.setDefaultClusterId(testClusterId);
        for (String name : names) {
            ClassIteratorTest.createPerson("Person", name);
        }
        // Use descending class iterator.
        final ORecordIteratorClass<ODocument> personIter = new ORecordIteratorClassDescendentOrder<ODocument>(ClassIteratorTest.db, ClassIteratorTest.db, "Person", true);
        personIter.setRange(null, null);// open range

        int docNum = 0;
        // Explicit iterator loop.
        while (personIter.hasNext()) {
            final ODocument personDoc = personIter.next();
            Assert.assertTrue(names.contains(personDoc.field("First")));
            Assert.assertTrue(names.remove(personDoc.field("First")));
            System.out.printf("Doc %d: %s\n", (docNum++), personDoc.toString());
        } 
        Assert.assertTrue(names.isEmpty());
    }

    @Test
    public void testMultipleClusters() throws Exception {
        final OClass personClass = ClassIteratorTest.db.getMetadata().getSchema().createClass("PersonMultipleClusters", 4, null);
        for (String name : names) {
            ClassIteratorTest.createPerson("PersonMultipleClusters", name);
        }
        final ORecordIteratorClass<ODocument> personIter = new ORecordIteratorClass<ODocument>(ClassIteratorTest.db, "PersonMultipleClusters", true);
        int docNum = 0;
        while (personIter.hasNext()) {
            final ODocument personDoc = personIter.next();
            Assert.assertTrue(names.contains(personDoc.field("First")));
            Assert.assertTrue(names.remove(personDoc.field("First")));
            System.out.printf("Doc %d: %s\n", (docNum++), personDoc.toString());
        } 
        Assert.assertTrue(names.isEmpty());
    }
}

