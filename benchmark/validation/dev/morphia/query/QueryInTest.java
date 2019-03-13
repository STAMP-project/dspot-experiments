package dev.morphia.query;


import com.mongodb.MongoException;
import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.testutil.TestEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author scotthernandez
 */
public class QueryInTest extends TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(QueryInTest.class);

    @Test
    public void testAddEmpty() {
        Query<QueryInTest.Data> query = getDs().find(QueryInTest.Data.class);
        List<ObjectId> memberships = new ArrayList<ObjectId>();
        query.or(query.criteria("id").hasAnyOf(memberships), query.criteria("otherIds").hasAnyOf(memberships));
        Assert.assertFalse(query.find().hasNext());
    }

    @Test
    public void testIdOnly() {
        QueryInTest.ReferencedEntity b = new QueryInTest.ReferencedEntity();
        b.setId(new ObjectId("111111111111111111111111"));
        getDs().save(b);
        QueryInTest.HasIdOnly has = new QueryInTest.HasIdOnly();
        has.list = new ArrayList<QueryInTest.ReferencedEntity>();
        has.list.add(b);
        has.entity = b;
        getDs().save(has);
        Query<QueryInTest.HasIdOnly> q = getDs().find(QueryInTest.HasIdOnly.class);
        q.criteria("list").in(Collections.singletonList(b));
        Assert.assertEquals(1, q.count());
        q = getDs().find(QueryInTest.HasIdOnly.class);
        q.criteria("entity").equal(b.getId());
        Assert.assertEquals(1, q.count());
    }

    @Test
    public void testInIdList() {
        final QueryInTest.Doc doc = new QueryInTest.Doc();
        doc.id = 1;
        getDs().save(doc);
        // this works
        getDs().find(QueryInTest.Doc.class).field("_id").equal(1).find();
        final List<Long> idList = new ArrayList<Long>();
        idList.add(1L);
        // this causes an NPE
        getDs().find(QueryInTest.Doc.class).field("_id").in(idList).find();
    }

    @Test
    public void testInQuery() {
        checkMinServerVersion(2.5);
        final QueryInTest.HasRefs hr = new QueryInTest.HasRefs();
        for (int x = 0; x < 10; x++) {
            final QueryInTest.ReferencedEntity re = new QueryInTest.ReferencedEntity(("" + x));
            hr.refs.add(re);
        }
        getDs().save(hr.refs);
        getDs().save(hr);
        Query<QueryInTest.HasRefs> query = getDs().find(QueryInTest.HasRefs.class).field("refs").in(hr.refs.subList(1, 3));
        Assert.assertEquals(1, query.count());
    }

    @Test
    public void testInQueryByKey() {
        checkMinServerVersion(2.5);
        final QueryInTest.HasRef hr = new QueryInTest.HasRef();
        List<Key<QueryInTest.ReferencedEntity>> refs = new ArrayList<Key<QueryInTest.ReferencedEntity>>();
        for (int x = 0; x < 10; x++) {
            final QueryInTest.ReferencedEntity re = new QueryInTest.ReferencedEntity(("" + x));
            getDs().save(re);
            refs.add(new Key<QueryInTest.ReferencedEntity>(QueryInTest.ReferencedEntity.class, getMorphia().getMapper().getCollectionName(QueryInTest.ReferencedEntity.class), re.getId()));
        }
        hr.ref = refs.get(0);
        getDs().save(hr);
        Query<QueryInTest.HasRef> query = getDs().find(QueryInTest.HasRef.class).field("ref").in(refs);
        try {
            Assert.assertEquals(1, query.count());
        } catch (MongoException e) {
            QueryInTest.LOG.debug(("query = " + query));
            throw e;
        }
    }

    @Test
    public void testMapping() {
        getMorphia().map(QueryInTest.HasRefs.class);
        getMorphia().map(QueryInTest.ReferencedEntity.class);
    }

    @Test
    public void testReferenceDoesNotExist() {
        final QueryInTest.HasRefs hr = new QueryInTest.HasRefs();
        getDs().save(hr);
        final Query<QueryInTest.HasRefs> q = getDs().find(QueryInTest.HasRefs.class);
        q.field("refs").doesNotExist();
        Assert.assertEquals(1, q.count());
    }

    @Entity("data")
    private static final class Data {
        private ObjectId id;

        private Set<ObjectId> otherIds;

        private Data() {
            otherIds = new HashSet<ObjectId>();
        }
    }

    @Entity
    private static class HasRef implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        @Reference
        private Key<QueryInTest.ReferencedEntity> ref;
    }

    @Entity
    private static class HasRefs implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        @Reference
        private List<QueryInTest.ReferencedEntity> refs = new ArrayList<QueryInTest.ReferencedEntity>();
    }

    @Entity
    private static class ReferencedEntity extends TestEntity {
        private String foo;

        ReferencedEntity() {
        }

        ReferencedEntity(final String s) {
            foo = s;
        }
    }

    @Entity(value = "as", noClassnameStored = true)
    private static class HasIdOnly {
        @Id
        private ObjectId id;

        private String name;

        @Reference(idOnly = true)
        private List<QueryInTest.ReferencedEntity> list;

        @Reference(idOnly = true)
        private QueryInTest.ReferencedEntity entity;
    }

    @Entity("docs")
    private static class Doc {
        @Id
        private long id = 4;
    }
}

