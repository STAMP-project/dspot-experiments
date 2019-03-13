package org.drools.core.common;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.drools.core.ObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ClassAwareObjectStoreTest {
    private final ClassAwareObjectStore underTest;

    @Test
    public void iterateObjectsReturnsObjectsOfAllTypes() throws Exception {
        String aStringValue = "a string";
        BigDecimal bigDecimalValue = new BigDecimal("1");
        insertObjectWithFactHandle(aStringValue);
        insertObjectWithFactHandle(bigDecimalValue);
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects());
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test
    public void iterateByClassFiltersByClass() {
        ClassAwareObjectStoreTest.SimpleClass object = new ClassAwareObjectStoreTest.SimpleClass();
        insertObjectWithFactHandle("some string");
        insertObjectWithFactHandle(object);
        Collection<Object> results = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SimpleClass.class));
        Assert.assertThat(results.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(results, CoreMatchers.hasItem(object));
    }

    @Test
    public void queryBySuperTypeFindsSubType() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SubClass.class)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SuperClass.class)));
    }

    @Test
    public void queryBySubtypeDoesNotReturnSuperType() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SubClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SubClass.class)));
    }

    /**
     * Should have identical results to {@link #queryBySuperTypeFindsSubType()}
     */
    @Test
    public void queryBySubTypeDoesNotPreventInsertionsBeingPropogatedToSuperTypeQueries() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SubClass.class));
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SubClass.class)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SuperClass.class)));
    }

    @Test
    public void queryBySuperTypeCanFindSubTypeWhenNoSuperTypeInstancesAreInStore() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SubClass.class)));
    }

    @Test
    public void isOkayToReinsertSameTypeThenQuery() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        // Check there's no duplication of results
        Assert.assertThat(new HashSet<Object>(result).size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test
    public void onceSuperClassIsSetUpForReadingItCanBecomeSetUpForWritingWithoutGettingDuplicateQueryReturns() throws Exception {
        Assert.assertTrue(ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class)).isEmpty());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(ClassAwareObjectStoreTest.SuperClass.class));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        // Check there's no duplication of results
        Assert.assertThat(new HashSet<Object>(result).size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test
    public void clearRemovesInsertedObjects() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SimpleClass());
        Assert.assertThat(ClassAwareObjectStoreTest.collect(underTest.iterateObjects()).size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        underTest.clear();
        Assert.assertThat(ClassAwareObjectStoreTest.collect(underTest.iterateObjects()).size(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test
    public void canIterateOverObjectsUsingCustomFilter() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        Collection<Object> result = ClassAwareObjectStoreTest.collect(underTest.iterateObjects(new ObjectFilter() {
            @Override
            public boolean accept(Object o) {
                return ClassAwareObjectStoreTest.SubClass.class.isInstance(o);
            }
        }));
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(result, CoreMatchers.hasItem(CoreMatchers.isA(ClassAwareObjectStoreTest.SubClass.class)));
    }

    @Test
    public void iteratingOverFactHandlesHasSameNumberOfResultsAsIteratingOverObjects() throws Exception {
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SuperClass());
        insertObjectWithFactHandle(new ClassAwareObjectStoreTest.SubClass());
        Assert.assertThat(ClassAwareObjectStoreTest.collect(underTest.iterateFactHandles(ClassAwareObjectStoreTest.SubClass.class)).size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(ClassAwareObjectStoreTest.collect(underTest.iterateFactHandles(ClassAwareObjectStoreTest.SuperClass.class)).size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    public ClassAwareObjectStoreTest(RuleBaseConfiguration ruleBaseConfiguration) {
        underTest = new ClassAwareObjectStore(ruleBaseConfiguration, new ReentrantLock());
    }

    private static final AtomicInteger factCounter = new AtomicInteger(0);

    private static class SimpleClass {}

    private static class SuperClass {}

    private static class SubClass extends ClassAwareObjectStoreTest.SuperClass {}
}

