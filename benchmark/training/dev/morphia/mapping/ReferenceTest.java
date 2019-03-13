package dev.morphia.mapping;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import dev.morphia.Datastore;
import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.lazy.LazyFeatureDependencies;
import dev.morphia.mapping.lazy.ProxyTestBase;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gene Trog, (eternal0@github.com)
 */
public class ReferenceTest extends ProxyTestBase {
    @Test
    public void testComplexIds() {
        ReferenceTest.Complex complex = new ReferenceTest.Complex(new ReferenceTest.ChildId("Bob", 67), "Kelso");
        List<ReferenceTest.Complex> list = Arrays.asList(new ReferenceTest.Complex(new ReferenceTest.ChildId("Turk", 27), "Turk"), new ReferenceTest.Complex(new ReferenceTest.ChildId("JD", 26), "Dorian"), new ReferenceTest.Complex(new ReferenceTest.ChildId("Carla", 29), "Espinosa"));
        List<ReferenceTest.Complex> lazyList = Arrays.asList(new ReferenceTest.Complex(new ReferenceTest.ChildId("Bippity", 67), "Boppity"), new ReferenceTest.Complex(new ReferenceTest.ChildId("Cinder", 22), "Ella"), new ReferenceTest.Complex(new ReferenceTest.ChildId("Prince", 29), "Charming"));
        ReferenceTest.ComplexParent parent = new ReferenceTest.ComplexParent();
        parent.complex = complex;
        parent.list = list;
        parent.lazyList = lazyList;
        getDs().save(complex);
        getDs().save(list);
        getDs().save(lazyList);
        getDs().save(parent);
        ReferenceTest.ComplexParent complexParent = getDs().get(ReferenceTest.ComplexParent.class, parent.id);
        Assert.assertEquals(parent, complexParent);
    }

    @Test
    public void testFindByEntityReference() {
        final ReferenceTest.Ref ref = new ReferenceTest.Ref("refId");
        getDs().save(ref);
        final ReferenceTest.Container container = new ReferenceTest.Container();
        container.singleRef = ref;
        getDs().save(container);
        Assert.assertNotNull(getDs().find(ReferenceTest.Container.class).filter("singleRef", ref).find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testIdOnlyReferences() {
        final List<ReferenceTest.Ref> refs = Arrays.asList(new ReferenceTest.Ref("foo"), new ReferenceTest.Ref("bar"), new ReferenceTest.Ref("baz"));
        final ReferenceTest.Container c = new ReferenceTest.Container(refs);
        // test that we can save it
        final Key<ReferenceTest.Container> key = getDs().save(c);
        getDs().save(refs);
        // ensure that we're not using DBRef
        final DBCollection collection = getDs().getCollection(ReferenceTest.Container.class);
        final DBObject persisted = collection.findOne(key.getId());
        Assert.assertNotNull(persisted);
        Assert.assertEquals("foo", persisted.get("singleRef"));
        Assert.assertEquals("foo", persisted.get("lazySingleRef"));
        final BasicDBList expectedList = new BasicDBList();
        expectedList.add("foo");
        expectedList.add("bar");
        expectedList.add("baz");
        Assert.assertEquals(expectedList, persisted.get("collectionRef"));
        Assert.assertEquals(expectedList, persisted.get("lazyCollectionRef"));
        final DBObject expectedMap = new BasicDBObject();
        expectedMap.put("0", "foo");
        expectedMap.put("1", "bar");
        expectedMap.put("2", "baz");
        Assert.assertEquals(expectedMap, persisted.get("mapRef"));
        Assert.assertEquals(expectedMap, persisted.get("lazyMapRef"));
        // ensure that we can retrieve it
        final ReferenceTest.Container retrieved = getDs().getByKey(ReferenceTest.Container.class, key);
        Assert.assertEquals(refs.get(0), retrieved.getSingleRef());
        if (LazyFeatureDependencies.testDependencyFullFilled()) {
            assertIsProxy(retrieved.getLazySingleRef());
        }
        Assert.assertEquals(refs.get(0), unwrap(retrieved.getLazySingleRef()));
        final List<ReferenceTest.Ref> expectedRefList = new ArrayList<ReferenceTest.Ref>();
        final Map<Integer, ReferenceTest.Ref> expectedRefMap = new LinkedHashMap<Integer, ReferenceTest.Ref>();
        for (int i = 0; i < (refs.size()); i++) {
            expectedRefList.add(refs.get(i));
            expectedRefMap.put(i, refs.get(i));
        }
        Assert.assertEquals(expectedRefList, retrieved.getCollectionRef());
        Assert.assertEquals(expectedRefList, unwrapList(retrieved.getLazyCollectionRef()));
        Assert.assertEquals(expectedRefMap, retrieved.getMapRef());
        Assert.assertEquals(expectedRefMap, unwrapMap(retrieved.getLazyMapRef()));
    }

    @Test
    public void testNullReferences() {
        ReferenceTest.Container container = new ReferenceTest.Container();
        container.lazyMapRef = null;
        container.singleRef = null;
        container.lazySingleRef = null;
        container.collectionRef = null;
        container.lazyCollectionRef = null;
        container.mapRef = null;
        container.lazyMapRef = null;
        getMorphia().getMapper().getOptions().setStoreNulls(true);
        getDs().save(container);
        allNull(container);
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        getDs().save(container);
        allNull(container);
    }

    @Test
    public void testReferenceQueryWithoutValidation() {
        ReferenceTest.Ref ref = new ReferenceTest.Ref("no validation");
        getDs().save(ref);
        final ReferenceTest.Container container = new ReferenceTest.Container(Collections.singletonList(ref));
        getDs().save(container);
        final Query<ReferenceTest.Container> query = getDs().find(ReferenceTest.Container.class).disableValidation().field("singleRef").equal(ref);
        Assert.assertNotNull(query.find(new FindOptions().limit(1)).next());
    }

    @Test
    public void testReferencesWithoutMapping() {
        ReferenceTest.Child child1 = new ReferenceTest.Child();
        getDs().save(child1);
        ReferenceTest.Parent parent1 = new ReferenceTest.Parent();
        parent1.children.add(child1);
        getDs().save(parent1);
        List<ReferenceTest.Parent> parentList = TestBase.toList(getDs().find(ReferenceTest.Parent.class).find());
        Assert.assertEquals(1, parentList.size());
        // reset Datastore to reset internal Mapper cache, so Child class
        // already cached by previous save is cleared
        Datastore localDs = getMorphia().createDatastore(getMongoClient(), new Mapper(), getDb().getName());
        parentList = TestBase.toList(localDs.find(ReferenceTest.Parent.class).find());
        Assert.assertEquals(1, parentList.size());
    }

    @Test
    public void testFetchKeys() {
        List<ReferenceTest.Complex> list = Arrays.asList(new ReferenceTest.Complex(new ReferenceTest.ChildId("Turk", 27), "Turk"), new ReferenceTest.Complex(new ReferenceTest.ChildId("JD", 26), "Dorian"), new ReferenceTest.Complex(new ReferenceTest.ChildId("Carla", 29), "Espinosa"));
        getDs().save(list);
        MongoCursor<Key<ReferenceTest.Complex>> keys = getDs().find(ReferenceTest.Complex.class).keys();
        Assert.assertTrue(keys.hasNext());
        Assert.assertEquals(list.get(0).getId(), keys.next().getId());
        Assert.assertEquals(list.get(1).getId(), keys.next().getId());
        Assert.assertEquals(list.get(2).getId(), keys.next().getId());
        Assert.assertFalse(keys.hasNext());
    }

    public static class Container {
        @Id
        private ObjectId id;

        @Reference(idOnly = true)
        private ReferenceTest.Ref singleRef;

        @Reference(idOnly = true, lazy = true)
        private ReferenceTest.Ref lazySingleRef;

        @Reference(idOnly = true)
        private List<ReferenceTest.Ref> collectionRef;

        @Reference(idOnly = true, lazy = true)
        private List<ReferenceTest.Ref> lazyCollectionRef;

        @Reference(idOnly = true)
        private LinkedHashMap<Integer, ReferenceTest.Ref> mapRef;

        @Reference(idOnly = true, lazy = true)
        private LinkedHashMap<Integer, ReferenceTest.Ref> lazyMapRef;

        /* required by morphia */
        Container() {
        }

        Container(final List<ReferenceTest.Ref> refs) {
            singleRef = refs.get(0);
            lazySingleRef = refs.get(0);
            collectionRef = refs;
            lazyCollectionRef = refs;
            mapRef = new LinkedHashMap<Integer, ReferenceTest.Ref>();
            lazyMapRef = new LinkedHashMap<Integer, ReferenceTest.Ref>();
            for (int i = 0; i < (refs.size()); i++) {
                mapRef.put(i, refs.get(i));
                lazyMapRef.put(i, refs.get(i));
            }
        }

        ObjectId getId() {
            return id;
        }

        ReferenceTest.Ref getSingleRef() {
            return singleRef;
        }

        ReferenceTest.Ref getLazySingleRef() {
            return lazySingleRef;
        }

        List<ReferenceTest.Ref> getCollectionRef() {
            return collectionRef;
        }

        List<ReferenceTest.Ref> getLazyCollectionRef() {
            return lazyCollectionRef;
        }

        LinkedHashMap<Integer, ReferenceTest.Ref> getMapRef() {
            return mapRef;
        }

        LinkedHashMap<Integer, ReferenceTest.Ref> getLazyMapRef() {
            return lazyMapRef;
        }
    }

    @Entity
    public static class Ref {
        @Id
        private String id;

        public Ref() {
        }

        Ref(final String id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ReferenceTest.Ref)) {
                return false;
            }
            final ReferenceTest.Ref ref = ((ReferenceTest.Ref) (o));
            if ((id) != null ? !(id.equals(ref.id)) : (ref.id) != null) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return String.format("Ref{id='%s'}", id);
        }
    }

    @Entity(value = "children", noClassnameStored = true)
    static class Child {
        @Id
        private ObjectId id;
    }

    @Entity(value = "parents", noClassnameStored = true)
    private static class Parent {
        @Id
        private ObjectId id;

        @Reference(lazy = true)
        private List<ReferenceTest.Child> children = new ArrayList<ReferenceTest.Child>();
    }

    private static class ComplexParent {
        @Id
        private ObjectId id;

        @Reference
        private ReferenceTest.Complex complex;

        @Reference
        private List<ReferenceTest.Complex> list = new ArrayList<ReferenceTest.Complex>();

        @Reference(lazy = true)
        private List<ReferenceTest.Complex> lazyList = new ArrayList<ReferenceTest.Complex>();

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        ReferenceTest.Complex getComplex() {
            return complex;
        }

        public void setComplex(final ReferenceTest.Complex complex) {
            this.complex = complex;
        }

        public List<ReferenceTest.Complex> getList() {
            return list;
        }

        public void setList(final List<ReferenceTest.Complex> list) {
            this.list = list;
        }

        List<ReferenceTest.Complex> getLazyList() {
            return lazyList;
        }

        public void setLazyList(final List<ReferenceTest.Complex> lazyList) {
            this.lazyList = lazyList;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ReferenceTest.ComplexParent)) {
                return false;
            }
            final ReferenceTest.ComplexParent that = ((ReferenceTest.ComplexParent) (o));
            if ((getId()) != null ? !(getId().equals(that.getId())) : (that.getId()) != null) {
                return false;
            }
            if ((getComplex()) != null ? !(getComplex().equals(that.getComplex())) : (that.getComplex()) != null) {
                return false;
            }
            if ((getList()) != null ? !(getList().equals(that.getList())) : (that.getList()) != null) {
                return false;
            }
            return (getLazyList()) != null ? getLazyList().equals(that.getLazyList()) : (that.getLazyList()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((getComplex()) != null ? getComplex().hashCode() : 0);
            result = (31 * result) + ((getList()) != null ? getList().hashCode() : 0);
            result = (31 * result) + ((getLazyList()) != null ? getLazyList().hashCode() : 0);
            return result;
        }
    }

    @Entity("complex")
    public static class Complex {
        @Id
        @Embedded
        private ReferenceTest.ChildId id;

        private String value;

        Complex() {
        }

        public Complex(final ReferenceTest.ChildId id, final String value) {
            this.id = id;
            this.value = value;
        }

        public ReferenceTest.ChildId getId() {
            return id;
        }

        public void setId(final ReferenceTest.ChildId id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ReferenceTest.Complex)) {
                return false;
            }
            final ReferenceTest.Complex complex = ((ReferenceTest.Complex) (o));
            if ((getId()) != null ? !(getId().equals(complex.getId())) : (complex.getId()) != null) {
                return false;
            }
            return (getValue()) != null ? getValue().equals(complex.getValue()) : (complex.getValue()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((getValue()) != null ? getValue().hashCode() : 0);
            return result;
        }
    }

    @Embedded
    public static class ChildId {
        private String name;

        private int age;

        ChildId() {
        }

        public ChildId(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ReferenceTest.ChildId)) {
                return false;
            }
            final ReferenceTest.ChildId childId = ((ReferenceTest.ChildId) (o));
            if ((getAge()) != (childId.getAge())) {
                return false;
            }
            return (getName()) != null ? getName().equals(childId.getName()) : (childId.getName()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getName()) != null) ? getName().hashCode() : 0;
            result = (31 * result) + (getAge());
            return result;
        }
    }
}

