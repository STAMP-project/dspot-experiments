package dev.morphia;


import dev.morphia.mapping.MappingException;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.TestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestQueriesOnReferences extends TestBase {
    @Test
    public void testKeyExists() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        cpk.setPic(p);
        getDs().save(p);
        getDs().save(cpk);
        Assert.assertNotNull(getDs().find(TestQuery.ContainsPic.class).field("pic").exists().project("pic", true).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull(getDs().find(TestQuery.ContainsPic.class).field("pic").doesNotExist().project("pic", true).find(new FindOptions().limit(1)).tryNext());
    }

    @Test(expected = MappingException.class)
    public void testMissingReferences() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        cpk.setPic(p);
        getDs().save(p);
        getDs().save(cpk);
        getDs().delete(p);
        TestBase.toList(getDs().find(TestQuery.ContainsPic.class).find());
    }

    @Test
    public void testQueryOverLazyReference() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        getDs().save(p);
        final TestQuery.PicWithObjectId withObjectId = new TestQuery.PicWithObjectId();
        getDs().save(withObjectId);
        cpk.setLazyPic(p);
        cpk.setLazyObjectIdPic(withObjectId);
        getDs().save(cpk);
        Query<TestQuery.ContainsPic> query = getDs().find(TestQuery.ContainsPic.class);
        Assert.assertNotNull(query.field("lazyPic").equal(p).find(new FindOptions().limit(1)).tryNext());
        query = getDs().find(TestQuery.ContainsPic.class);
        Assert.assertNotNull(query.field("lazyObjectIdPic").equal(withObjectId).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testQueryOverReference() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        getDs().save(p);
        cpk.setPic(p);
        getDs().save(cpk);
        final Query<TestQuery.ContainsPic> query = getDs().find(TestQuery.ContainsPic.class);
        final TestQuery.ContainsPic object = query.field("pic").equal(p).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(object);
    }

    @Test
    public void testWithKeyQuery() {
        final TestQuery.ContainsPic cpk = new TestQuery.ContainsPic();
        final TestQuery.Pic p = new TestQuery.Pic();
        cpk.setPic(p);
        getDs().save(p);
        getDs().save(cpk);
        TestQuery.ContainsPic containsPic = getDs().find(TestQuery.ContainsPic.class).field("pic").equal(new Key<TestQuery.Pic>(TestQuery.Pic.class, "Pic", p.getId())).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals(cpk.getId(), containsPic.getId());
        containsPic = getDs().find(TestQuery.ContainsPic.class).field("pic").equal(new Key<TestQuery.Pic>(TestQuery.Pic.class, "Pic", p.getId())).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals(cpk.getId(), containsPic.getId());
    }
}

