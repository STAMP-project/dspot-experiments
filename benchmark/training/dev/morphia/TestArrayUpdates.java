package dev.morphia;


import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestArrayUpdates extends TestBase {
    @Test
    public void testStudents() {
        getMorphia().map(TestArrayUpdates.Student.class);
        final Datastore datastore = getDs();
        datastore.ensureIndexes();
        datastore.save(new TestArrayUpdates.Student(1L, new TestArrayUpdates.Grade(80, Collections.singletonMap("name", "Homework")), new TestArrayUpdates.Grade(90, Collections.singletonMap("name", "Test"))));
        Query<TestArrayUpdates.Student> testQuery = datastore.find(TestArrayUpdates.Student.class).field("_id").equal(1L).field("grades.data.name").equal("Test");
        Assert.assertNotNull(testQuery.find(new FindOptions().limit(1)).tryNext());
        UpdateOperations<TestArrayUpdates.Student> operations = datastore.createUpdateOperations(TestArrayUpdates.Student.class);
        operations.set("grades.$.data.name", "Makeup Test");
        datastore.update(testQuery, operations);
        Assert.assertNull(testQuery.find(new FindOptions().limit(1)).tryNext());
        Assert.assertNotNull(datastore.find(TestArrayUpdates.Student.class).field("_id").equal(1L).field("grades.data.name").equal("Makeup Test").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testUpdatesWithArrayIndexPosition() {
        getMorphia().map(TestArrayUpdates.Student.class);
        final Datastore datastore = getDs();
        datastore.ensureIndexes();
        datastore.save(new TestArrayUpdates.Student(1L, new TestArrayUpdates.Grade(80, Collections.singletonMap("name", "Homework")), new TestArrayUpdates.Grade(90, Collections.singletonMap("name", "Test"))));
        Query<TestArrayUpdates.Student> testQuery = datastore.find(TestArrayUpdates.Student.class).field("_id").equal(1L).field("grades.data.name").equal("Test");
        Assert.assertNotNull(testQuery.find(new FindOptions().limit(1)).tryNext());
        // Update the second element. Array indexes are zero-based.
        UpdateOperations<TestArrayUpdates.Student> operations = datastore.createUpdateOperations(TestArrayUpdates.Student.class);
        operations.set("grades.1.data.name", "Makeup Test");
        datastore.update(testQuery, operations);
        Assert.assertNull(testQuery.find(new FindOptions().limit(1)).tryNext());
        Assert.assertNotNull(datastore.find(TestArrayUpdates.Student.class).field("_id").equal(1L).field("grades.data.name").equal("Makeup Test").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testUpdates() {
        TestArrayUpdates.BatchData theBatch = new TestArrayUpdates.BatchData();
        theBatch.files.add(new TestArrayUpdates.Files(0, "fileName1", "fileHash1"));
        theBatch.files.add(new TestArrayUpdates.Files(0, "fileName2", "fileHash2"));
        getDs().save(theBatch);
        ObjectId id = theBatch.id;
        theBatch = new TestArrayUpdates.BatchData();
        theBatch.files.add(new TestArrayUpdates.Files(0, "fileName3", "fileHash3"));
        theBatch.files.add(new TestArrayUpdates.Files(0, "fileName4", "fileHash4"));
        getDs().save(theBatch);
        ObjectId id2 = theBatch.id;
        UpdateOperations<TestArrayUpdates.BatchData> updateOperations = getDs().createUpdateOperations(TestArrayUpdates.BatchData.class).set("files.$.fileHash", "new hash");
        getDs().update(getDs().find(TestArrayUpdates.BatchData.class).filter("_id", id).filter("files.fileName", "fileName1"), updateOperations);
        TestArrayUpdates.BatchData data = getDs().find(TestArrayUpdates.BatchData.class).filter("_id", id).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals("new hash", data.files.get(0).fileHash);
        Assert.assertEquals("fileHash2", data.files.get(1).fileHash);
        data = getDs().find(TestArrayUpdates.BatchData.class).filter("_id", id2).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals("fileHash3", data.files.get(0).fileHash);
        Assert.assertEquals("fileHash4", data.files.get(1).fileHash);
    }

    @Entity
    public static class BatchData {
        @Id
        private ObjectId id;

        @Embedded
        private List<TestArrayUpdates.Files> files = new ArrayList<TestArrayUpdates.Files>();

        @Override
        public String toString() {
            return String.format("BatchData{id=%s, files=%s}", id, files);
        }
    }

    @Embedded
    public static class Files {
        private int position;

        private String fileName = "";

        private String fileHash = "";

        public Files() {
        }

        public Files(final int pos, final String fileName, final String fileHash) {
            this.position = pos;
            this.fileName = fileName;
            this.fileHash = fileHash;
        }

        @Override
        public String toString() {
            return String.format("Files{fileHash='%s', fileName='%s', position=%d}", fileHash, fileName, position);
        }
    }

    @Entity
    public static class Student {
        @Id
        private long id;

        private List<TestArrayUpdates.Grade> grades;

        public Student() {
        }

        public Student(final long id, final TestArrayUpdates.Grade... grades) {
            this.id = id;
            this.grades = Arrays.asList(grades);
        }

        @Override
        public String toString() {
            return (("id: " + (id)) + ", grades: ") + (grades);
        }
    }

    @Embedded
    public static class Grade {
        private int marks;

        @Property("d")
        private Map<String, String> data;

        public Grade() {
        }

        public Grade(final int marks, final Map<String, String> data) {
            this.marks = marks;
            this.data = data;
        }

        @Override
        public String toString() {
            return (("marks: " + (marks)) + ", data: ") + (data);
        }
    }
}

