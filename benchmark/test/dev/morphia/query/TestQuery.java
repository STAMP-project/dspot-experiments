package dev.morphia.query;


import CollationStrength.SECONDARY;
import CursorType.Tailable;
import com.jayway.awaitility.Awaitility;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoInternalException;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Collation;
import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.TestDatastore;
import dev.morphia.TestMapper;
import dev.morphia.annotations.CappedAt;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PrePersist;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.ReferenceTest;
import dev.morphia.testmodel.Hotel;
import dev.morphia.testmodel.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.bson.types.CodeWScope;
import org.bson.types.ObjectId;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;


/**
 *
 *
 * @author Scott Hernandez
 */
@SuppressWarnings({ "unchecked", "unused" })
public class TestQuery extends TestBase {
    @Test
    @SuppressWarnings("deprecation")
    public void batchSize() {
        QueryImpl<TestQuery.Photo> query = ((QueryImpl<TestQuery.Photo>) (getDs().find(TestQuery.Photo.class).batchSize(42)));
        Assert.assertEquals(42, query.getBatchSize());
        Assert.assertEquals(42, query.getOptions().getBatchSize());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void cursorTimeOut() {
        QueryImpl<TestQuery.Photo> query = ((QueryImpl<TestQuery.Photo>) (getDs().find(TestQuery.Photo.class).enableCursorTimeout()));
        Assert.assertFalse(query.getOptions().isNoCursorTimeout());
        query.disableCursorTimeout();
        Assert.assertTrue(query.getOptions().isNoCursorTimeout());
    }

    @Test
    public void genericMultiKeyValueQueries() {
        getMorphia().map(TestQuery.GenericKeyValue.class);
        getDs().ensureIndexes(TestQuery.GenericKeyValue.class);
        final TestQuery.GenericKeyValue<String> value = new TestQuery.GenericKeyValue<String>();
        final List<Object> keys = Arrays.<Object>asList("key1", "key2");
        value.key = keys;
        getDs().save(value);
        final Query<TestQuery.GenericKeyValue> query = getDs().find(TestQuery.GenericKeyValue.class).field("key").hasAnyOf(keys);
        Assert.assertTrue(query.toString().replaceAll("\\s", "").contains("{\"$in\":[\"key1\",\"key2\"]"));
        Assert.assertEquals(query.find(new FindOptions().limit(1)).tryNext().id, value.id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void maxScan() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        Assert.assertEquals(2, TestBase.toList(getDs().find(TestQuery.Pic.class).maxScan(2).find()).size());
        Assert.assertEquals(2, getDs().find(TestQuery.Pic.class).asList(new FindOptions().modifier("$maxScan", 2)).size());
        Assert.assertEquals(4, TestBase.toList(getDs().find(TestQuery.Pic.class).find()).size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void maxTime() {
        Query<TestQuery.ContainsRenamedFields> query = getDs().find(TestQuery.ContainsRenamedFields.class).maxTime(15, MINUTES);
        Assert.assertEquals(900, getMaxTime(TimeUnit.SECONDS));
    }

    @Test
    public void multiKeyValueQueries() {
        getMorphia().map(TestQuery.KeyValue.class);
        getDs().ensureIndexes(TestQuery.KeyValue.class);
        final TestQuery.KeyValue value = new TestQuery.KeyValue();
        final List<Object> keys = Arrays.<Object>asList("key1", "key2");
        value.key = keys;
        getDs().save(value);
        final Query<TestQuery.KeyValue> query = getDs().find(TestQuery.KeyValue.class).field("key").hasAnyOf(keys);
        Assert.assertTrue(query.toString().replaceAll("\\s", "").contains("{\"$in\":[\"key1\",\"key2\"]"));
        Assert.assertEquals(query.find(new FindOptions().limit(1)).tryNext().id, value.id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void oldReadPreference() {
        QueryImpl<TestQuery.Photo> query = ((QueryImpl<TestQuery.Photo>) (getDs().find(TestQuery.Photo.class).queryNonPrimary()));
        Assert.assertEquals(ReadPreference.secondaryPreferred(), query.getOptions().getReadPreference());
        query.queryPrimaryOnly();
        Assert.assertEquals(ReadPreference.primary(), query.getOptions().getReadPreference());
    }

    @Test
    public void referenceKeys() {
        final TestQuery.ReferenceKey key1 = new TestQuery.ReferenceKey("key1");
        getDs().save(Arrays.asList(key1, new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        final TestQuery.ReferenceKeyValue value = new TestQuery.ReferenceKeyValue();
        value.id = key1;
        final Key<TestQuery.ReferenceKeyValue> key = getDs().save(value);
        final TestQuery.ReferenceKeyValue byKey = getDs().getByKey(TestQuery.ReferenceKeyValue.class, key);
        Assert.assertEquals(value.id, byKey.id);
    }

    @Test
    public void snapshot() {
        Assume.assumeTrue(serverIsAtMostVersion(3.6));
        getDs().find(TestQuery.Photo.class).find(new FindOptions().modifier("$snapshot", true)).tryNext();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void snapshotOld() {
        Assume.assumeTrue(serverIsAtMostVersion(3.6));
        QueryImpl<TestQuery.Photo> query = ((QueryImpl<TestQuery.Photo>) (getDs().find(TestQuery.Photo.class).enableSnapshotMode()));
        Assert.assertTrue(query.getOptions().getModifiers().containsField("$snapshot"));
        query.find(new FindOptions().limit(1)).tryNext();
        query.disableSnapshotMode();
        Assert.assertFalse(query.getOptions().getModifiers().containsField("$snapshot"));
    }

    @Test
    public void testAliasedFieldSort() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(3, 8), new Rectangle(6, 10), new Rectangle(10, 10), new Rectangle(10, 1)));
        Rectangle r1 = getDs().find(Rectangle.class).order("w").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(1, r1.getWidth(), 0);
        r1 = getDs().find(Rectangle.class).order("-w").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(10, r1.getWidth(), 0);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAliasedFieldSortOld() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(3, 8), new Rectangle(6, 10), new Rectangle(10, 10), new Rectangle(10, 1)));
        Rectangle r1 = getDs().find(Rectangle.class).limit(1).order("w").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(1, r1.getWidth(), 0);
        r1 = getDs().find(Rectangle.class).limit(1).order("-w").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(10, r1.getWidth(), 0);
    }

    @Test
    public void testCaseVariants() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").contains("PIC").count());
        Assert.assertEquals(4, getDs().find(TestQuery.Pic.class).field("name").containsIgnoreCase("PIC").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").equal("PIC1").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").equalIgnoreCase("PIC1").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").endsWith("C1").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").endsWithIgnoreCase("C1").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").startsWith("PIC").count());
        Assert.assertEquals(4, getDs().find(TestQuery.Pic.class).field("name").startsWithIgnoreCase("PIC").count());
    }

    @Test
    public void testCaseVariantsWithSpecialChars() {
        getDs().save(Arrays.asList(new TestQuery.Pic("making waves:  _.~\"~._.~\"~._.~\"~._.~\"~._"), new TestQuery.Pic(">++('>   fish bones"), new TestQuery.Pic("hacksaw [|^^^^^^^")));
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").contains("^").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").contains("aw [|^^").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").contains("AW [|^^").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").containsIgnoreCase("aw [|^^").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").containsIgnoreCase("AW [|^^").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").startsWith(">++('>   fish").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").startsWith(">++('>   FIsh").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").startsWithIgnoreCase(">++('>   FISH").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").startsWithIgnoreCase(">++('>   FISH").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").equal(">++('>   fish bones").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").equal(">++('>   FISH BONES").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").equalIgnoreCase(">++('>   fish bones").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").equalIgnoreCase(">++('>   FISH BONES").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").endsWith("'>   fish bones").count());
        Assert.assertEquals(0, getDs().find(TestQuery.Pic.class).field("name").endsWith("'>   FISH BONES").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").endsWithIgnoreCase("'>   fish bones").count());
        Assert.assertEquals(1, getDs().find(TestQuery.Pic.class).field("name").endsWithIgnoreCase("'>   FISH BONES").count());
    }

    @Test
    public void testCollations() {
        checkMinServerVersion(3.4);
        getMorphia().map(TestQuery.ContainsRenamedFields.class);
        getDs().save(Arrays.asList(new TestQuery.ContainsRenamedFields("first", "last"), new TestQuery.ContainsRenamedFields("First", "Last")));
        Query query = getDs().find(TestQuery.ContainsRenamedFields.class).field("last_name").equal("last");
        Assert.assertEquals(1, TestBase.toList(query.find()).size());
        Assert.assertEquals(2, TestBase.toList(query.find(new FindOptions().collation(Collation.builder().locale("en").collationStrength(SECONDARY).build()))).size());
        Assert.assertEquals(1, query.count());
        Assert.assertEquals(2, query.count(new CountOptions().collation(Collation.builder().locale("en").collationStrength(SECONDARY).build())));
    }

    @Test
    public void testCombinationQuery() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(4, 2), new Rectangle(6, 10), new Rectangle(8, 5), new Rectangle(10, 4)));
        Query<Rectangle> q = getDs().find(Rectangle.class);
        q.and(q.criteria("width").equal(10), q.criteria("height").equal(1));
        Assert.assertEquals(1, getDs().getCount(q));
        q = getDs().find(Rectangle.class);
        q.or(q.criteria("width").equal(10), q.criteria("height").equal(10));
        Assert.assertEquals(3, getDs().getCount(q));
        q = getDs().find(Rectangle.class);
        q.or(q.criteria("width").equal(10), q.and(q.criteria("width").equal(5), q.criteria("height").equal(8)));
        Assert.assertEquals(3, getDs().getCount(q));
    }

    @Test
    public void testCommentsShowUpInLogs() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        getDb().command(new BasicDBObject("profile", 2));
        String expectedComment = "test comment";
        TestBase.toList(getDs().find(TestQuery.Pic.class).find(new FindOptions().modifier("$comment", expectedComment)));
        DBCollection profileCollection = getDb().getCollection("system.profile");
        Assert.assertNotEquals(0, profileCollection.count());
        DBObject profileRecord = profileCollection.findOne(new BasicDBObject("op", "query").append("ns", getDs().getCollection(TestQuery.Pic.class).getFullName()));
        Assert.assertEquals(profileRecord.toString(), expectedComment, getCommentFromProfileRecord(profileRecord));
        turnOffProfilingAndDropProfileCollection();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCommentsShowUpInLogsOld() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        getDb().command(new BasicDBObject("profile", 2));
        String expectedComment = "test comment";
        TestBase.toList(getDs().find(TestQuery.Pic.class).comment(expectedComment).find());
        DBCollection profileCollection = getDb().getCollection("system.profile");
        Assert.assertNotEquals(0, profileCollection.count());
        DBObject profileRecord = profileCollection.findOne(new BasicDBObject("op", "query").append("ns", getDs().getCollection(TestQuery.Pic.class).getFullName()));
        Assert.assertEquals(profileRecord.toString(), expectedComment, getCommentFromProfileRecord(profileRecord));
        turnOffProfilingAndDropProfileCollection();
    }

    @Test
    public void testComplexElemMatchQuery() {
        TestQuery.Keyword oscar = new TestQuery.Keyword("Oscar", 42);
        getDs().save(new TestQuery.PhotoWithKeywords(oscar, new TestQuery.Keyword("Jim", 12)));
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword = ", "Oscar").filter("score = ", 12)).find(new FindOptions().limit(1)).tryNext());
        List<TestQuery.PhotoWithKeywords> keywords = TestBase.toList(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("score > ", 20).filter("score < ", 100)).find());
        Assert.assertEquals(1, keywords.size());
        Assert.assertEquals(oscar, keywords.get(0).keywords.get(0));
    }

    @Test
    public void testComplexIdQuery() {
        final TestMapper.CustomId cId = new TestMapper.CustomId();
        cId.setId(new ObjectId());
        cId.setType("banker");
        final TestMapper.UsesCustomIdObject object = new TestMapper.UsesCustomIdObject();
        object.setId(cId);
        object.setText("hllo");
        getDs().save(object);
        Assert.assertNotNull(getDs().find(TestMapper.UsesCustomIdObject.class).filter("_id.type", "banker").find(new FindOptions().limit(1)).tryNext());
        Assert.assertNotNull(getDs().find(TestMapper.UsesCustomIdObject.class).field("_id").hasAnyOf(Collections.singletonList(cId)).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testComplexIdQueryWithRenamedField() {
        final TestMapper.CustomId cId = new TestMapper.CustomId();
        cId.setId(new ObjectId());
        cId.setType("banker");
        final TestMapper.UsesCustomIdObject object = new TestMapper.UsesCustomIdObject();
        object.setId(cId);
        object.setText("hllo");
        getDs().save(object);
        Assert.assertNotNull(getDs().find(TestMapper.UsesCustomIdObject.class).filter("_id.t", "banker").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testComplexRangeQuery() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(4, 2), new Rectangle(6, 10), new Rectangle(8, 5), new Rectangle(10, 4)));
        Assert.assertEquals(2, getDs().getCount(getDs().find(Rectangle.class).filter("height >", 3).filter("height <", 8)));
        Assert.assertEquals(1, getDs().getCount(getDs().find(Rectangle.class).filter("height >", 3).filter("height <", 8).filter("width", 10)));
    }

    @Test
    public void testCompoundSort() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(3, 8), new Rectangle(6, 10), new Rectangle(10, 10), new Rectangle(10, 1)));
        Rectangle r1 = getDs().find(Rectangle.class).order("width,-height").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(1, r1.getWidth(), 0);
        Assert.assertEquals(10, r1.getHeight(), 0);
        r1 = getDs().find(Rectangle.class).order("-height,-width").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(r1);
        Assert.assertEquals(10, r1.getWidth(), 0);
        Assert.assertEquals(10, r1.getHeight(), 0);
    }

    @Test
    public void testCompoundSortWithSortBeans() {
        List<Rectangle> list = Arrays.asList(new Rectangle(1, 10), new Rectangle(3, 8), new Rectangle(6, 10), new Rectangle(10, 10), new Rectangle(10, 1));
        Collections.shuffle(list);
        getDs().save(list);
        compareLists(list, getDs().find(Rectangle.class).order("width,-height"), getDs().find(Rectangle.class).order(Sort.ascending("width"), Sort.descending("height")), new TestQuery.RectangleComparator());
        compareLists(list, getDs().find(Rectangle.class).order("-height,-width"), getDs().find(Rectangle.class).order(Sort.descending("height"), Sort.descending("width")), new TestQuery.RectangleComparator1());
        compareLists(list, getDs().find(Rectangle.class).order("width,height"), getDs().find(Rectangle.class).order(Sort.ascending("width"), Sort.ascending("height")), new TestQuery.RectangleComparator2());
        compareLists(list, getDs().find(Rectangle.class).order("width,height"), getDs().find(Rectangle.class).order("width, height"), new TestQuery.RectangleComparator3());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCorrectQueryForNotWithSizeEqIssue514() {
        Query<TestQuery.PhotoWithKeywords> query = getAds().find(TestQuery.PhotoWithKeywords.class).field("keywords").not().sizeEq(3);
        Assert.assertEquals(new BasicDBObject("keywords", new BasicDBObject("$not", new BasicDBObject("$size", 3))), query.getQueryObject());
    }

    @Test
    public void testDBObjectOrQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords("scott", "hernandez"));
        final List<DBObject> orList = new ArrayList<DBObject>();
        orList.add(new BasicDBObject("keywords.keyword", "scott"));
        orList.add(new BasicDBObject("keywords.keyword", "ralph"));
        final BasicDBObject orQuery = new BasicDBObject("$or", orList);
        Query<TestQuery.PhotoWithKeywords> q = getAds().createQuery(TestQuery.PhotoWithKeywords.class, orQuery);
        Assert.assertEquals(1, q.count());
        q = getAds().find(TestQuery.PhotoWithKeywords.class).disableValidation().filter("$or", orList);
        Assert.assertEquals(1, q.count());
    }

    @Test
    public void testDeepQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "california").find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "not").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testDeepQueryWithBadArgs() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", 1).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "california".getBytes()).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", null).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testDeepQueryWithRenamedFields() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "california").find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "not").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testDeleteQuery() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(10, 10), new Rectangle(10, 10)));
        Assert.assertEquals(5, getDs().getCount(Rectangle.class));
        getDs().delete(getDs().find(Rectangle.class).filter("height", 1.0));
        Assert.assertEquals(2, getDs().getCount(Rectangle.class));
    }

    @Test
    public void testElemMatchQuery() {
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords(), new TestQuery.PhotoWithKeywords("Scott", "Joe", "Sarah")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword", "Scott")).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword", "Randy")).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testElemMatchQueryOld() {
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords(), new TestQuery.PhotoWithKeywords("Scott", "Joe", "Sarah")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").hasThisElement(new TestQuery.Keyword("Scott")).find(new FindOptions().limit(1)).tryNext());
        // TODO add back when $and is done (> 1.5)  this needs multiple $elemMatch clauses
        // query = getDs().find(PhotoWithKeywords.class)
        // .field("keywords")
        // .hasThisElement(new Keyword[]{new Keyword("Scott"), new Keyword("Joe")});
        // System.out.println("************ query = " + query);
        // PhotoWithKeywords pwkScottSarah = query.get();
        // assertNotNull(pwkScottSarah);
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").hasThisElement(new TestQuery.Keyword("Randy")).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testElemMatchVariants() {
        final TestQuery.PhotoWithKeywords pwk1 = new TestQuery.PhotoWithKeywords();
        final TestQuery.PhotoWithKeywords pwk2 = new TestQuery.PhotoWithKeywords("Kevin");
        final TestQuery.PhotoWithKeywords pwk3 = new TestQuery.PhotoWithKeywords("Scott", "Joe", "Sarah");
        final TestQuery.PhotoWithKeywords pwk4 = new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("Scott", 14));
        Iterator<Key<TestQuery.PhotoWithKeywords>> iterator = getDs().save(Arrays.asList(pwk1, pwk2, pwk3, pwk4)).iterator();
        Key<TestQuery.PhotoWithKeywords> key1 = iterator.next();
        Key<TestQuery.PhotoWithKeywords> key2 = iterator.next();
        Key<TestQuery.PhotoWithKeywords> key3 = iterator.next();
        Key<TestQuery.PhotoWithKeywords> key4 = iterator.next();
        assertListEquals(Arrays.asList(key3, key4), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword = ", "Scott")).keys());
        assertListEquals(Arrays.asList(key3, key4), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).field("keyword").equal("Scott")).keys());
        assertListEquals(Collections.singletonList(key4), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("score = ", 14)).keys());
        assertListEquals(Collections.singletonList(key4), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).field("score").equal(14)).keys());
        assertListEquals(Arrays.asList(key1, key2), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").not().elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword = ", "Scott")).keys());
        assertListEquals(Arrays.asList(key1, key2), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").not().elemMatch(getDs().find(TestQuery.Keyword.class).field("keyword").equal("Scott")).keys());
    }

    @Test
    public void testExplainPlan() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        Map<String, Object> explainResult = getDs().find(TestQuery.Pic.class).explain();
        Assert.assertEquals(explainResult.toString(), 4, (serverIsAtMostVersion(2.7) ? explainResult.get("n") : ((Map) (explainResult.get("executionStats"))).get("nReturned")));
    }

    @Test
    public void testKeys() {
        TestQuery.PhotoWithKeywords pwk1 = new TestQuery.PhotoWithKeywords("california", "nevada", "arizona");
        TestQuery.PhotoWithKeywords pwk2 = new TestQuery.PhotoWithKeywords("Joe", "Sarah");
        TestQuery.PhotoWithKeywords pwk3 = new TestQuery.PhotoWithKeywords("MongoDB", "World");
        getDs().save(Arrays.asList(pwk1, pwk2, pwk3));
        MongoCursor<Key<TestQuery.PhotoWithKeywords>> keys = getDs().find(TestQuery.PhotoWithKeywords.class).keys();
        Assert.assertTrue(keys.hasNext());
        Assert.assertEquals(pwk1.id, keys.next().getId());
        Assert.assertEquals(pwk2.id, keys.next().getId());
        Assert.assertEquals(pwk3.id, keys.next().getId());
        List<ReferenceTest.Complex> list = Arrays.asList(new ReferenceTest.Complex(new ReferenceTest.ChildId("Turk", 27), "Turk"), new ReferenceTest.Complex(new ReferenceTest.ChildId("JD", 26), "Dorian"), new ReferenceTest.Complex(new ReferenceTest.ChildId("Carla", 29), "Espinosa"));
        getDs().save(list);
        Iterator<Key<ReferenceTest.Complex>> complexKeys = getDs().find(ReferenceTest.Complex.class).keys();
        Assert.assertTrue(complexKeys.hasNext());
        Assert.assertEquals(list.get(0).getId(), complexKeys.next().getId());
        Assert.assertEquals(list.get(1).getId(), complexKeys.next().getId());
        Assert.assertEquals(list.get(2).getId(), complexKeys.next().getId());
        Assert.assertFalse(complexKeys.hasNext());
    }

    @Test
    public void testFetchKeys() {
        TestQuery.PhotoWithKeywords pwk1 = new TestQuery.PhotoWithKeywords("california", "nevada", "arizona");
        TestQuery.PhotoWithKeywords pwk2 = new TestQuery.PhotoWithKeywords("Joe", "Sarah");
        TestQuery.PhotoWithKeywords pwk3 = new TestQuery.PhotoWithKeywords("MongoDB", "World");
        getDs().save(Arrays.asList(pwk1, pwk2, pwk3));
        MongoCursor<Key<TestQuery.PhotoWithKeywords>> keys = getDs().find(TestQuery.PhotoWithKeywords.class).keys();
        Assert.assertTrue(keys.hasNext());
        Assert.assertEquals(pwk1.id, keys.next().getId());
        Assert.assertEquals(pwk2.id, keys.next().getId());
        Assert.assertEquals(pwk3.id, keys.next().getId());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testFluentAndOrQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords("scott", "hernandez"));
        final Query<TestQuery.PhotoWithKeywords> q = getAds().find(TestQuery.PhotoWithKeywords.class);
        q.and(q.or(q.criteria("keywords.keyword").equal("scott")), q.or(q.criteria("keywords.keyword").equal("hernandez")));
        Assert.assertEquals(1, q.count());
        Assert.assertTrue(q.getQueryObject().containsField("$and"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testFluentAndQuery1() {
        getDs().save(new TestQuery.PhotoWithKeywords("scott", "hernandez"));
        final Query<TestQuery.PhotoWithKeywords> q = getAds().find(TestQuery.PhotoWithKeywords.class);
        q.and(q.criteria("keywords.keyword").hasThisOne("scott"), q.criteria("keywords.keyword").hasAnyOf(Arrays.asList("scott", "hernandez")));
        Assert.assertEquals(1, q.count());
        Assert.assertTrue(q.getQueryObject().containsField("$and"));
    }

    @Test
    public void testFluentNotQuery() {
        final TestQuery.PhotoWithKeywords pwk = new TestQuery.PhotoWithKeywords("scott", "hernandez");
        getDs().save(pwk);
        final Query<TestQuery.PhotoWithKeywords> query = getAds().find(TestQuery.PhotoWithKeywords.class);
        query.criteria("keywords.keyword").not().startsWith("ralph");
        Assert.assertEquals(1, query.count());
    }

    @Test
    public void testFluentOrQuery() {
        final TestQuery.PhotoWithKeywords pwk = new TestQuery.PhotoWithKeywords("scott", "hernandez");
        getDs().save(pwk);
        final Query<TestQuery.PhotoWithKeywords> q = getAds().find(TestQuery.PhotoWithKeywords.class);
        q.or(q.criteria("keywords.keyword").equal("scott"), q.criteria("keywords.keyword").equal("ralph"));
        Assert.assertEquals(1, q.count());
    }

    @Test
    public void testGetByKeysHetero() {
        final Iterable<Key<Object>> keys = getDs().save(Arrays.asList(new TestDatastore.FacebookUser(1, "scott"), new Rectangle(1, 1)));
        final List<Object> entities = getDs().getByKeys(keys);
        Assert.assertNotNull(entities);
        Assert.assertEquals(2, entities.size());
        int userCount = 0;
        int rectCount = 0;
        for (final Object o : entities) {
            if (o instanceof Rectangle) {
                rectCount++;
            } else
                if (o instanceof TestDatastore.FacebookUser) {
                    userCount++;
                }

        }
        Assert.assertEquals(1, rectCount);
        Assert.assertEquals(1, userCount);
    }

    @Test
    public void testIdFieldNameQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords("scott", "hernandez"));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("id !=", "scott").find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testIdRangeQuery() {
        getDs().save(Arrays.asList(new TestQuery.HasIntId(1), new TestQuery.HasIntId(11), new TestQuery.HasIntId(12)));
        Assert.assertEquals(2, getDs().find(TestQuery.HasIntId.class).filter("_id >", 5).filter("_id <", 20).count());
        Assert.assertEquals(1, getDs().find(TestQuery.HasIntId.class).field("_id").greaterThan(0).field("_id").lessThan(11).count());
    }

    @Test
    public void testInQuery() {
        getDs().save(new TestQuery.Photo(Arrays.asList("red", "green", "blue")));
        Assert.assertNotNull(getDs().find(TestQuery.Photo.class).field("keywords").in(Arrays.asList("red", "yellow")).find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testInQueryWithObjects() {
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords(), new TestQuery.PhotoWithKeywords("Scott", "Joe", "Sarah")));
        final Query<TestQuery.PhotoWithKeywords> query = getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").in(Arrays.asList(new TestQuery.Keyword("Scott"), new TestQuery.Keyword("Randy")));
        Assert.assertNotNull(query.find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testKeyList() {
        final Rectangle rect = new Rectangle(1000, 1);
        final Key<Rectangle> rectKey = getDs().save(rect);
        Assert.assertEquals(rectKey.getId(), rect.getId());
        final TestDatastore.FacebookUser fbUser1 = new TestDatastore.FacebookUser(1, "scott");
        final TestDatastore.FacebookUser fbUser2 = new TestDatastore.FacebookUser(2, "tom");
        final TestDatastore.FacebookUser fbUser3 = new TestDatastore.FacebookUser(3, "oli");
        final TestDatastore.FacebookUser fbUser4 = new TestDatastore.FacebookUser(4, "frank");
        final Iterable<Key<TestDatastore.FacebookUser>> fbKeys = getDs().save(Arrays.asList(fbUser1, fbUser2, fbUser3, fbUser4));
        Assert.assertEquals(1, fbUser1.getId());
        final List<Key<TestDatastore.FacebookUser>> fbUserKeys = new ArrayList<Key<TestDatastore.FacebookUser>>();
        for (final Key<TestDatastore.FacebookUser> key : fbKeys) {
            fbUserKeys.add(key);
        }
        Assert.assertEquals(fbUser1.getId(), fbUserKeys.get(0).getId());
        Assert.assertEquals(fbUser2.getId(), fbUserKeys.get(1).getId());
        Assert.assertEquals(fbUser3.getId(), fbUserKeys.get(2).getId());
        Assert.assertEquals(fbUser4.getId(), fbUserKeys.get(3).getId());
        final TestDatastore.KeysKeysKeys k1 = new TestDatastore.KeysKeysKeys(rectKey, fbUserKeys);
        final Key<TestDatastore.KeysKeysKeys> k1Key = getDs().save(k1);
        Assert.assertEquals(k1.getId(), k1Key.getId());
        final TestDatastore.KeysKeysKeys k1Loaded = getDs().get(k1);
        for (final Key<TestDatastore.FacebookUser> key : k1Loaded.getUsers()) {
            Assert.assertNotNull(key.getId());
        }
        Assert.assertNotNull(k1Loaded.getRect().getId());
    }

    @Test
    public void testKeyListLookups() {
        final TestDatastore.FacebookUser fbUser1 = new TestDatastore.FacebookUser(1, "scott");
        final TestDatastore.FacebookUser fbUser2 = new TestDatastore.FacebookUser(2, "tom");
        final TestDatastore.FacebookUser fbUser3 = new TestDatastore.FacebookUser(3, "oli");
        final TestDatastore.FacebookUser fbUser4 = new TestDatastore.FacebookUser(4, "frank");
        final Iterable<Key<TestDatastore.FacebookUser>> fbKeys = getDs().save(Arrays.asList(fbUser1, fbUser2, fbUser3, fbUser4));
        Assert.assertEquals(1, fbUser1.getId());
        final List<Key<TestDatastore.FacebookUser>> fbUserKeys = new ArrayList<Key<TestDatastore.FacebookUser>>();
        for (final Key<TestDatastore.FacebookUser> key : fbKeys) {
            fbUserKeys.add(key);
        }
        Assert.assertEquals(fbUser1.getId(), fbUserKeys.get(0).getId());
        Assert.assertEquals(fbUser2.getId(), fbUserKeys.get(1).getId());
        Assert.assertEquals(fbUser3.getId(), fbUserKeys.get(2).getId());
        Assert.assertEquals(fbUser4.getId(), fbUserKeys.get(3).getId());
        final TestDatastore.KeysKeysKeys k1 = new TestDatastore.KeysKeysKeys(null, fbUserKeys);
        final Key<TestDatastore.KeysKeysKeys> k1Key = getDs().save(k1);
        Assert.assertEquals(k1.getId(), k1Key.getId());
        final TestDatastore.KeysKeysKeys k1Reloaded = getDs().get(k1);
        final TestDatastore.KeysKeysKeys k1Loaded = getDs().getByKey(TestDatastore.KeysKeysKeys.class, k1Key);
        Assert.assertNotNull(k1Reloaded);
        Assert.assertNotNull(k1Loaded);
        for (final Key<TestDatastore.FacebookUser> key : k1Loaded.getUsers()) {
            Assert.assertNotNull(key.getId());
        }
        Assert.assertEquals(4, k1Loaded.getUsers().size());
        final List<TestDatastore.FacebookUser> fbUsers = getDs().getByKeys(TestDatastore.FacebookUser.class, k1Loaded.getUsers());
        Assert.assertEquals(4, fbUsers.size());
        for (final TestDatastore.FacebookUser fbUser : fbUsers) {
            Assert.assertNotNull(fbUser);
            Assert.assertNotNull(fbUser.getId());
            Assert.assertNotNull(fbUser.getUsername());
        }
    }

    @Test
    public void testMixedProjection() {
        getDs().save(new TestQuery.ContainsRenamedFields("Frank", "Zappa"));
        try {
            getDs().find(TestQuery.ContainsRenamedFields.class).project("first_name", true).project("last_name", false);
            Assert.fail("An exception should have been thrown indication a mixed projection");
        } catch (ValidationException e) {
            // all good
        }
        try {
            getDs().find(TestQuery.ContainsRenamedFields.class).project("first_name", true).project("last_name", true).project("_id", false);
        } catch (ValidationException e) {
            Assert.fail("An exception should not have been thrown indication a mixed projection because _id suppression is a special case");
        }
        try {
            getDs().find(TestQuery.ContainsRenamedFields.class).project("first_name", false).project("last_name", false).project("_id", true);
            Assert.fail("An exception should have been thrown indication a mixed projection");
        } catch (ValidationException e) {
            // all good
        }
        try {
            getDs().find(TestQuery.IntVector.class).project("name", false).project("scalars", new ArraySlice(5));
            Assert.fail("An exception should have been thrown indication a mixed projection");
        } catch (ValidationException e) {
            // all good
        }
    }

    @Test
    public void testMultipleConstraintsOnOneField() {
        checkMinServerVersion(3.0);
        getMorphia().map(TestQuery.ContainsPic.class);
        getDs().ensureIndexes();
        Query<TestQuery.ContainsPic> query = getDs().find(TestQuery.ContainsPic.class);
        query.field("size").greaterThanOrEq(10);
        query.field("size").lessThan(100);
        Map<String, Object> explain = query.explain();
        Map<String, Object> queryPlanner = ((Map<String, Object>) (explain.get("queryPlanner")));
        Map<String, Object> winningPlan = ((Map<String, Object>) (queryPlanner.get("winningPlan")));
        Map<String, Object> inputStage = ((Map<String, Object>) (winningPlan.get("inputStage")));
        Assert.assertEquals("IXSCAN", inputStage.get("stage"));
    }

    @Test
    public void testNaturalSortAscending() {
        getDs().save(Arrays.asList(new Rectangle(6, 10), new Rectangle(3, 8), new Rectangle(10, 10), new Rectangle(10, 1)));
        List<Rectangle> results = TestBase.toList(getDs().find(Rectangle.class).order(Sort.naturalAscending()).find());
        Assert.assertEquals(4, results.size());
        Rectangle r;
        r = results.get(0);
        Assert.assertNotNull(r);
        Assert.assertEquals(6, r.getHeight(), 0);
        Assert.assertEquals(10, r.getWidth(), 0);
        r = results.get(1);
        Assert.assertNotNull(r);
        Assert.assertEquals(3, r.getHeight(), 0);
        Assert.assertEquals(8, r.getWidth(), 0);
        r = results.get(2);
        Assert.assertNotNull(r);
        Assert.assertEquals(10, r.getHeight(), 0);
        Assert.assertEquals(10, r.getWidth(), 0);
    }

    @Test
    public void testNaturalSortDescending() {
        getDs().save(Arrays.asList(new Rectangle(6, 10), new Rectangle(3, 8), new Rectangle(10, 10), new Rectangle(10, 1)));
        List<Rectangle> results = TestBase.toList(getDs().find(Rectangle.class).order(Sort.naturalDescending()).find());
        Assert.assertEquals(4, results.size());
        Rectangle r;
        r = results.get(0);
        Assert.assertNotNull(r);
        Assert.assertEquals(10, r.getHeight(), 0);
        Assert.assertEquals(1, r.getWidth(), 0);
        r = results.get(1);
        Assert.assertNotNull(r);
        Assert.assertEquals(10, r.getHeight(), 0);
        Assert.assertEquals(10, r.getWidth(), 0);
        r = results.get(2);
        Assert.assertNotNull(r);
        Assert.assertEquals(3, r.getHeight(), 0);
        Assert.assertEquals(8, r.getWidth(), 0);
    }

    @Test
    public void testNegativeBatchSize() {
        getDs().delete(getDs().find(TestQuery.PhotoWithKeywords.class));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("1", "2"), new TestQuery.PhotoWithKeywords("3", "4"), new TestQuery.PhotoWithKeywords("5", "6")));
        Assert.assertEquals(2, TestBase.toList(getDs().find(TestQuery.PhotoWithKeywords.class).find(new FindOptions().batchSize((-2)))).size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNegativeBatchSizeOld() {
        getDs().delete(getDs().find(TestQuery.PhotoWithKeywords.class));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("1", "2"), new TestQuery.PhotoWithKeywords("3", "4"), new TestQuery.PhotoWithKeywords("5", "6")));
        Assert.assertEquals(2, TestBase.toList(getDs().find(TestQuery.PhotoWithKeywords.class).batchSize((-2)).find()).size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNoLifeCycleEventsOnParameters() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic("some pic");
        getDs().save(p);
        cpk.setPic(p);
        getDs().save(cpk);
        TestQuery.Pic queryPic = new TestQuery.Pic("some pic");
        queryPic.setId(p.getId());
        Query query = getDs().find(TestQuery.ContainsPic.class).field("pic").equal(queryPic);
        Assert.assertFalse(queryPic.isPrePersist());
        Assert.assertNotNull(query.find(new FindOptions().limit(1)).tryNext());
        getDs().find(TestQuery.ContainsPic.class).field("pic").hasThisElement(queryPic);
        Assert.assertFalse(queryPic.isPrePersist());
    }

    @Test
    public void testNonSnapshottedQuery() {
        Assume.assumeTrue(serverIsAtMostVersion(3.6));
        getDs().delete(getDs().find(TestQuery.PhotoWithKeywords.class));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez")));
        final Iterator<TestQuery.PhotoWithKeywords> it = getDs().find(TestQuery.PhotoWithKeywords.class).find(new FindOptions().modifier("$snapshot", true).batchSize(2));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("1", "2"), new TestQuery.PhotoWithKeywords("3", "4"), new TestQuery.PhotoWithKeywords("5", "6")));
        Assert.assertNotNull(it.next());
        Assert.assertNotNull(it.next());
        // okay, now we should getMore...
        Assert.assertTrue(it.hasNext());
        Assert.assertNotNull(it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertNotNull(it.next());
    }

    @Test
    public void testNonexistentFindGet() {
        Assert.assertNull(getDs().find(Hotel.class).filter("_id", (-1)).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testNonexistentGet() {
        Assert.assertNull(getDs().get(Hotel.class, (-1)));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNotGeneratesCorrectQueryForGreaterThan() {
        final Query<TestQuery.Keyword> query = getDs().find(TestQuery.Keyword.class);
        query.criteria("score").not().greaterThan(7);
        Assert.assertEquals(new BasicDBObject("score", new BasicDBObject("$not", new BasicDBObject("$gt", 7))), query.getQueryObject());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testProject() {
        getDs().save(new TestQuery.ContainsRenamedFields("Frank", "Zappa"));
        TestQuery.ContainsRenamedFields found = getDs().find(TestQuery.ContainsRenamedFields.class).project("first_name", true).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(found.firstName);
        Assert.assertNull(found.lastName);
        found = getDs().find(TestQuery.ContainsRenamedFields.class).project("firstName", true).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(found.firstName);
        Assert.assertNull(found.lastName);
        try {
            getDs().find(TestQuery.ContainsRenamedFields.class).project("bad field name", true).find(new FindOptions().limit(1)).tryNext();
            Assert.fail("Validation should have caught the bad field");
        } catch (ValidationException e) {
            // success!
        }
        DBObject fields = getDs().find(TestQuery.ContainsRenamedFields.class).project("_id", true).project("first_name", true).getFieldsObject();
        Assert.assertNull(fields.get(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    @Test
    public void testProjectArrayField() {
        int[] ints = new int[]{ 0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30 };
        TestQuery.IntVector vector = new TestQuery.IntVector(ints);
        getDs().save(vector);
        Assert.assertArrayEquals(copy(ints, 0, 4), getDs().find(TestQuery.IntVector.class).project("scalars", new ArraySlice(4)).find(new FindOptions().limit(1)).next().scalars);
        Assert.assertArrayEquals(copy(ints, 5, 4), getDs().find(TestQuery.IntVector.class).project("scalars", new ArraySlice(5, 4)).find(new FindOptions().limit(1)).next().scalars);
        Assert.assertArrayEquals(copy(ints, ((ints.length) - 10), 6), getDs().find(TestQuery.IntVector.class).project("scalars", new ArraySlice((-10), 6)).find(new FindOptions().limit(1)).next().scalars);
        Assert.assertArrayEquals(copy(ints, ((ints.length) - 12), 12), getDs().find(TestQuery.IntVector.class).project("scalars", new ArraySlice((-12))).find(new FindOptions().limit(1)).next().scalars);
    }

    @Test
    public void testQBE() {
        final TestMapper.CustomId cId = new TestMapper.CustomId();
        cId.setId(new ObjectId());
        cId.setType("banker");
        final TestMapper.UsesCustomIdObject object = new TestMapper.UsesCustomIdObject();
        object.setId(cId);
        object.setText("hllo");
        getDs().save(object);
        final TestMapper.UsesCustomIdObject loaded;
        // Add back if/when query by example for embedded fields is supported (require dotting each field).
        // CustomId exId = new CustomId();
        // exId.type = cId.type;
        // loaded = getDs().find(UsesCustomIdObject.class, "_id", exId).get();
        // assertNotNull(loaded);
        final TestMapper.UsesCustomIdObject ex = new TestMapper.UsesCustomIdObject();
        ex.setText(object.getText());
        loaded = getDs().queryByExample(ex).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(loaded);
    }

    @Test
    public void testQueryCount() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(1, 10), new Rectangle(10, 10), new Rectangle(10, 10)));
        Assert.assertEquals(3, getDs().getCount(getDs().find(Rectangle.class).filter("height", 1.0)));
        Assert.assertEquals(2, getDs().getCount(getDs().find(Rectangle.class).filter("height", 10.0)));
        Assert.assertEquals(5, getDs().getCount(getDs().find(Rectangle.class).filter("width", 10.0)));
    }

    @Test
    public void testQueryOverLazyReference() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        getDs().save(p);
        cpk.lazyPic = p;
        getDs().save(cpk);
        Assert.assertEquals(1, getDs().find(TestQuery.ContainsPic.class).field("lazyPic").equal(p).count());
    }

    @Test(expected = ValidationException.class)
    public void testQueryOverReference() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        getDs().save(p);
        cpk.pic = p;
        getDs().save(cpk);
        final Query<TestQuery.ContainsPic> query = getDs().find(TestQuery.ContainsPic.class);
        Assert.assertEquals(1, query.field("pic").equal(p).count());
        getDs().find(TestQuery.ContainsPic.class).filter("pic.name", "foo").find(new FindOptions().limit(1)).next();
    }

    @Test
    public void testRangeQuery() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(4, 2), new Rectangle(6, 10), new Rectangle(8, 5), new Rectangle(10, 4)));
        Assert.assertEquals(4, getDs().getCount(getDs().find(Rectangle.class).filter("height >", 3)));
        Assert.assertEquals(3, getDs().getCount(getDs().find(Rectangle.class).filter("height >", 3).filter("height <", 10)));
        Assert.assertEquals(1, getDs().getCount(getDs().find(Rectangle.class).filter("height >", 9).filter("width <", 5)));
        Assert.assertEquals(3, getDs().getCount(getDs().find(Rectangle.class).filter("height <", 7)));
    }

    @Test(expected = ValidationException.class)
    public void testReferenceQuery() {
        final TestQuery.Photo p = new TestQuery.Photo();
        final TestQuery.ContainsPhotoKey cpk = new TestQuery.ContainsPhotoKey();
        cpk.photo = getDs().save(p);
        getDs().save(cpk);
        Assert.assertNotNull(getDs().find(TestQuery.ContainsPhotoKey.class).filter("photo", p).find(new FindOptions().limit(1)).next());
        Assert.assertNotNull(getDs().find(TestQuery.ContainsPhotoKey.class).filter("photo", cpk.photo).find(new FindOptions().limit(1)).next());
        Assert.assertNull(getDs().find(TestQuery.ContainsPhotoKey.class).filter("photo", 1).find(new FindOptions().limit(1)).tryNext());
        getDs().find(TestQuery.ContainsPhotoKey.class).filter("photo.keywords", "foo").find(new FindOptions().limit(1)).next();
    }

    @Test
    public void testRegexInsensitiveQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        final Pattern p = Pattern.compile("(?i)caLifornia");
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).disableValidation().filter("keywords.keyword", p).find(new FindOptions().limit(1)).next());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", Pattern.compile("blah")).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testRegexQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).disableValidation().filter("keywords.keyword", Pattern.compile("california")).find(new FindOptions().limit(1)).next());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", Pattern.compile("blah")).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testRenamedFieldQuery() {
        getDs().save(new TestQuery.ContainsRenamedFields("Scott", "Bakula"));
        Assert.assertNotNull(getDs().find(TestQuery.ContainsRenamedFields.class).field("firstName").equal("Scott").find(new FindOptions().limit(1)).next());
        Assert.assertNotNull(getDs().find(TestQuery.ContainsRenamedFields.class).field("first_name").equal("Scott").find(new FindOptions().limit(1)).next());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testRetrievedFields() {
        getDs().save(new TestQuery.ContainsRenamedFields("Frank", "Zappa"));
        TestQuery.ContainsRenamedFields found = getDs().find(TestQuery.ContainsRenamedFields.class).retrievedFields(true, "first_name").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(found.firstName);
        Assert.assertNull(found.lastName);
        found = getDs().find(TestQuery.ContainsRenamedFields.class).retrievedFields(true, "firstName").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(found.firstName);
        Assert.assertNull(found.lastName);
        try {
            getDs().find(TestQuery.ContainsRenamedFields.class).retrievedFields(true, "bad field name").find(new FindOptions().limit(1)).tryNext();
            Assert.fail("Validation should have caught the bad field");
        } catch (ValidationException e) {
            // success!
        }
        DBObject fields = getDs().find(TestQuery.ContainsRenamedFields.class).retrievedFields(true, "_id", "first_name").getFieldsObject();
        Assert.assertNull(fields.get(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testReturnOnlyIndexedFields() {
        getDs().save(Arrays.asList(new TestQuery.Pic("pic1"), new TestQuery.Pic("pic2"), new TestQuery.Pic("pic3"), new TestQuery.Pic("pic4")));
        getDs().ensureIndex(TestQuery.Pic.class, "name");
        // When
        // find a document by using a search on the field in the index
        // Then
        TestQuery.Pic foundItem = getDs().find(TestQuery.Pic.class).returnKey().field("name").equal("pic2").find(new FindOptions().limit(1).modifier("$returnKey", true)).tryNext();
        Assert.assertNotNull(foundItem);
        Assert.assertThat("Name should be populated", foundItem.getName(), Is.is("pic2"));
        Assert.assertNull("ID should not be populated", foundItem.getId());
    }

    @Test
    public void testSimpleSort() {
        getDs().save(Arrays.asList(new Rectangle(1, 10), new Rectangle(3, 8), new Rectangle(6, 10), new Rectangle(10, 10), new Rectangle(10, 1)));
        Rectangle r1 = getDs().find(Rectangle.class).order("width").find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(r1);
        Assert.assertEquals(1, r1.getWidth(), 0);
        r1 = getDs().find(Rectangle.class).order("-width").find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(r1);
        Assert.assertEquals(10, r1.getWidth(), 0);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSizeEqQuery() {
        Assert.assertEquals(new BasicDBObject("keywords", new BasicDBObject("$size", 3)), getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").sizeEq(3).getQueryObject());
    }

    @Test
    public void testSnapshottedQuery() {
        Assume.assumeTrue(serverIsAtMostVersion(3.6));
        getDs().delete(getDs().find(TestQuery.PhotoWithKeywords.class));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez"), new TestQuery.PhotoWithKeywords("scott", "hernandez")));
        final Iterator<TestQuery.PhotoWithKeywords> it = getDs().find(TestQuery.PhotoWithKeywords.class).filter("keywords.keyword", "scott").find(new FindOptions().modifier("$snapshot", true).batchSize(2));
        getDs().save(Arrays.asList(new TestQuery.PhotoWithKeywords("1", "2"), new TestQuery.PhotoWithKeywords("3", "4"), new TestQuery.PhotoWithKeywords("5", "6")));
        Assert.assertNotNull(it.next());
        Assert.assertNotNull(it.next());
        // okay, now we should getMore...
        Assert.assertTrue(it.hasNext());
        Assert.assertNotNull(it.next());
        Assert.assertTrue((!(it.hasNext())));
    }

    @Test
    public void testStartsWithQuery() {
        getDs().save(new TestQuery.Photo());
        Assert.assertNotNull(getDs().find(TestQuery.Photo.class).field("keywords").startsWith("amaz").find(new FindOptions().limit(1)).next());
        Assert.assertNull(getDs().find(TestQuery.Photo.class).field("keywords").startsWith("notareal").find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testTailableCursors() {
        getMorphia().map(TestQuery.CappedPic.class);
        getDs().ensureCaps();
        final Query<TestQuery.CappedPic> query = getDs().find(TestQuery.CappedPic.class);
        final List<TestQuery.CappedPic> found = new ArrayList<TestQuery.CappedPic>();
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Assert.assertEquals(0, query.count());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getDs().save(new TestQuery.CappedPic(((System.currentTimeMillis()) + "")));
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        final Iterator<TestQuery.CappedPic> tail = query.find(new FindOptions().cursorType(Tailable));
        Awaitility.await().pollDelay(500, TimeUnit.MILLISECONDS).atMost(10, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (tail.hasNext()) {
                    found.add(tail.next());
                }
                return (found.size()) >= 10;
            }
        });
        executorService.shutdownNow();
        Assert.assertTrue(((query.count()) >= 10));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testThatElemMatchQueriesOnlyChecksRequiredFields() {
        final TestQuery.PhotoWithKeywords pwk1 = new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona"));
        final TestQuery.PhotoWithKeywords pwk2 = new TestQuery.PhotoWithKeywords("Joe", "Sarah");
        pwk2.keywords.add(new TestQuery.Keyword("Scott", 14));
        getDs().save(Arrays.asList(pwk1, pwk2));
        // In this case, we only want to match on the keyword field, not the
        // score field, which shouldn't be included in the elemMatch query.
        // As a result, the query in MongoDB should look like:
        // find({ keywords: { $elemMatch: { keyword: "Scott" } } })
        // NOT:
        // find({ keywords: { $elemMatch: { keyword: "Scott", score: 12 } } })
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").hasThisElement(new TestQuery.Keyword("Scott")).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword", "Scott")).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").hasThisElement(new TestQuery.Keyword("Randy")).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.PhotoWithKeywords.class).field("keywords").elemMatch(getDs().find(TestQuery.Keyword.class).filter("keyword", "Randy")).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testWhereCodeWScopeQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        // CodeWScope hasKeyword = new CodeWScope("for (kw in this.keywords) { if(kw.keyword == kwd) return true; } return false;
        // ", new BasicDBObject("kwd","california"));
        final CodeWScope hasKeyword = new CodeWScope("this.keywords != null", new BasicDBObject());
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).where(hasKeyword).find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testWhereStringQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords(new TestQuery.Keyword("california"), new TestQuery.Keyword("nevada"), new TestQuery.Keyword("arizona")));
        Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).where("this.keywords != null").find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testWhereWithInvalidStringQuery() {
        getDs().save(new TestQuery.PhotoWithKeywords());
        final CodeWScope hasKeyword = new CodeWScope("keywords != null", new BasicDBObject());
        try {
            // must fail
            Assert.assertNotNull(getDs().find(TestQuery.PhotoWithKeywords.class).where(hasKeyword.getCode()).find(new FindOptions().limit(1)).next());
            Assert.fail("Invalid javascript magically isn't invalid anymore?");
        } catch (MongoInternalException e) {
            // fine
        } catch (MongoException e) {
            // fine
        }
    }

    @Test
    public void testQueryUnmappedData() {
        getMorphia().map(TestQuery.Class1.class);
        getDs().ensureIndexes(true);
        getDs().getDB().getCollection("user").save(new BasicDBObject().append("@class", TestQuery.Class1.class.getName()).append("value1", "foo").append("someMap", new BasicDBObject("someKey", "value")));
        Query<TestQuery.Class1> query = getDs().createQuery(TestQuery.Class1.class);
        query.disableValidation().criteria("someMap.someKey").equal("value");
        TestQuery.Class1 retrievedValue = query.find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(retrievedValue);
        Assert.assertEquals("foo", retrievedValue.value1);
    }

    @Test
    public void testCriteriaContainers() {
        final Query<QueryForSubtypeTest.User> query = getDs().createQuery(QueryForSubtypeTest.User.class).disableValidation();
        final CriteriaContainer topAnd = query.and(query.or(query.criteria("fieldA").equal("a"), query.criteria("fieldB").equal("b")), query.and(query.criteria("fieldC").equal("c"), query.or(query.criteria("fieldD").equal("d"), query.criteria("fieldE").equal("e"))));
        DBObject expected = BasicDBObject.parse(("{\"$or\": [{\"fieldA\": \"a\"}, {\"fieldB\": \"b\"}]," + " \"fieldC\": \"c\", \"$or\": [{\"fieldD\": \"d\"}, {\"fieldE\": \"e\"}]}"));
        Assert.assertEquals(expected, query.getQueryObject());
    }

    @Entity(value = "user", noClassnameStored = true)
    private static class Class1 {
        @Id
        private ObjectId id;

        private String value1;
    }

    @Entity
    public static class Photo {
        @Id
        private ObjectId id;

        private List<String> keywords = Collections.singletonList("amazing");

        public Photo() {
        }

        Photo(final List<String> keywords) {
            this.keywords = keywords;
        }
    }

    public static class PhotoWithKeywords {
        @Id
        private ObjectId id;

        @Embedded
        private List<TestQuery.Keyword> keywords = new ArrayList<TestQuery.Keyword>();

        PhotoWithKeywords() {
        }

        PhotoWithKeywords(final String... words) {
            keywords = new ArrayList<TestQuery.Keyword>(words.length);
            for (final String word : words) {
                keywords.add(new TestQuery.Keyword(word));
            }
        }

        PhotoWithKeywords(final TestQuery.Keyword... keyword) {
            keywords.addAll(Arrays.asList(keyword));
        }
    }

    @Embedded(concreteClass = TestQuery.Keyword.class)
    public static class Keyword {
        private String keyword;

        private Integer score;

        protected Keyword() {
        }

        Keyword(final String k) {
            this.keyword = k;
        }

        Keyword(final String k, final Integer score) {
            this.keyword = k;
            this.score = score;
        }

        Keyword(final Integer score) {
            this.score = score;
        }

        @Override
        public int hashCode() {
            int result = ((keyword) != null) ? keyword.hashCode() : 0;
            result = (31 * result) + ((score) != null ? score.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof TestQuery.Keyword)) {
                return false;
            }
            final TestQuery.Keyword keyword1 = ((TestQuery.Keyword) (o));
            if ((keyword) != null ? !(keyword.equals(keyword1.keyword)) : (keyword1.keyword) != null) {
                return false;
            }
            return (score) != null ? score.equals(keyword1.score) : (keyword1.score) == null;
        }
    }

    private static class ContainsPhotoKey {
        @Id
        private ObjectId id;

        private Key<TestQuery.Photo> photo;
    }

    @Entity
    public static class HasIntId {
        @Id
        private int id;

        protected HasIntId() {
        }

        HasIntId(final int id) {
            this.id = id;
        }
    }

    @Entity
    public static class ContainsPic {
        @Id
        private ObjectId id;

        private String name = "test";

        @Reference
        private TestQuery.Pic pic;

        @Reference(lazy = true)
        private TestQuery.Pic lazyPic;

        @Reference(lazy = true)
        private TestQuery.PicWithObjectId lazyObjectIdPic;

        @Indexed
        private int size;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public TestQuery.PicWithObjectId getLazyObjectIdPic() {
            return lazyObjectIdPic;
        }

        public void setLazyObjectIdPic(final TestQuery.PicWithObjectId lazyObjectIdPic) {
            this.lazyObjectIdPic = lazyObjectIdPic;
        }

        public TestQuery.Pic getLazyPic() {
            return lazyPic;
        }

        public void setLazyPic(final TestQuery.Pic lazyPic) {
            this.lazyPic = lazyPic;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public TestQuery.Pic getPic() {
            return pic;
        }

        public void setPic(final TestQuery.Pic pic) {
            this.pic = pic;
        }

        public int getSize() {
            return size;
        }

        public void setSize(final int size) {
            this.size = size;
        }
    }

    @Entity
    public static class PicWithObjectId {
        @Id
        private ObjectId id;

        private String name;
    }

    @Entity
    public static class Pic {
        @Id
        private ObjectId id;

        private String name;

        private boolean prePersist;

        public Pic() {
        }

        Pic(final String name) {
            this.name = name;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        boolean isPrePersist() {
            return prePersist;
        }

        public void setPrePersist(final boolean prePersist) {
            this.prePersist = prePersist;
        }

        @PrePersist
        public void tweak() {
            prePersist = true;
        }
    }

    @Entity(value = "capped_pic", cap = @CappedAt(count = 1000))
    public static class CappedPic extends TestQuery.Pic {
        public CappedPic() {
        }

        CappedPic(final String name) {
            super(name);
        }
    }

    @Entity(noClassnameStored = true)
    public static class ContainsRenamedFields {
        @Id
        private ObjectId id;

        @Property("first_name")
        private String firstName;

        @Property("last_name")
        private String lastName;

        public ContainsRenamedFields() {
        }

        ContainsRenamedFields(final String firstName, final String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @Entity
    private static class KeyValue {
        @Id
        private ObjectId id;

        /**
         * The list of keys for this value.
         */
        @Indexed(unique = true)
        private List<Object> key;

        /**
         * The id of the value document
         */
        @Indexed
        private ObjectId value;
    }

    @Entity
    private static class GenericKeyValue<T> {
        @Id
        private ObjectId id;

        @Indexed(unique = true)
        private List<Object> key;

        @Embedded
        private T value;
    }

    @Entity
    private static class ReferenceKeyValue {
        @Id
        private TestQuery.ReferenceKey id;

        /**
         * The list of keys for this value.
         */
        @Indexed(unique = true)
        @Reference
        private List<TestQuery.Pic> key;

        /**
         * The id of the value document
         */
        @Indexed
        private ObjectId value;
    }

    static class ReferenceKey {
        @Id
        private ObjectId id;

        private String name;

        ReferenceKey() {
        }

        ReferenceKey(final String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final TestQuery.ReferenceKey that = ((TestQuery.ReferenceKey) (o));
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            if ((name) != null ? !(name.equals(that.name)) : (that.name) != null) {
                return false;
            }
            return true;
        }
    }

    static class IntVector {
        @Id
        private ObjectId id;

        private String name;

        private int[] scalars;

        IntVector() {
        }

        IntVector(final int... scalars) {
            this.scalars = scalars;
        }
    }

    private static class RectangleComparator implements Comparator<Rectangle> {
        @Override
        public int compare(final Rectangle o1, final Rectangle o2) {
            int compare = Double.compare(o1.getWidth(), o2.getWidth());
            return compare != 0 ? compare : Double.compare(o2.getHeight(), o1.getHeight());
        }
    }

    private static class RectangleComparator1 implements Comparator<Rectangle> {
        @Override
        public int compare(final Rectangle o1, final Rectangle o2) {
            int compare = Double.compare(o2.getHeight(), o1.getHeight());
            return compare != 0 ? compare : Double.compare(o2.getWidth(), o1.getWidth());
        }
    }

    private static class RectangleComparator2 implements Comparator<Rectangle> {
        @Override
        public int compare(final Rectangle o1, final Rectangle o2) {
            int compare = Double.compare(o1.getWidth(), o2.getWidth());
            return compare != 0 ? compare : Double.compare(o1.getHeight(), o2.getHeight());
        }
    }

    private static class RectangleComparator3 implements Comparator<Rectangle> {
        @Override
        public int compare(final Rectangle o1, final Rectangle o2) {
            int compare = Double.compare(o1.getWidth(), o2.getWidth());
            return compare != 0 ? compare : Double.compare(o1.getHeight(), o2.getHeight());
        }
    }
}

