package org.kairosdb.eventbus;


import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class PipelineRegistryTest {
    private PipelineRegistry registry;

    @Test
    public void testRegister_noReturnType() {
        registry.register(new PipelineRegistryTest.NoReturnSubscriber());
    }

    @Test
    public void testRegister() {
        Assert.assertEquals(0, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.StringSubscriber());
        Assert.assertEquals(1, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.StringSubscriber());
        Assert.assertEquals(2, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.ObjectSubscriber());
        Assert.assertEquals(2, registry.getPipeline(String.class).size());
        Assert.assertEquals(1, registry.getPipeline(Object.class).size());
    }

    /* @Test
    public void testUnregister()
    {
    StringSubscriber s1 = new StringSubscriber();
    StringSubscriber s2 = new StringSubscriber();

    registry.register(s1);
    registry.register(s2);

    registry.unregister(s1);
    assertEquals(1, registry.getPipeline(String.class).size());

    registry.unregister(s2);
    assertTrue(registry.getPipeline(String.class).isEmpty());
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testUnregister_notRegistered()
    {
    try
    {
    registry.unregister(new StringSubscriber());
    fail();
    }
    catch (IllegalArgumentException expected)
    {
    }

    StringSubscriber s1 = new StringSubscriber();
    registry.register(s1);
    try
    {
    registry.unregister(new StringSubscriber());
    fail();
    }
    catch (IllegalArgumentException expected)
    {
    // a StringSubscriber was registered, but not the same one we tried to unregister
    }

    registry.unregister(s1);

    try
    {
    registry.unregister(s1);
    fail();
    }
    catch (IllegalArgumentException expected)
    {
    }
    }
     */
    @Test
    public void testGetSubscribers() {
        Assert.assertEquals(0, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.StringSubscriber());
        Assert.assertEquals(1, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.StringSubscriber());
        Assert.assertEquals(2, registry.getPipeline(String.class).size());
        registry.register(new PipelineRegistryTest.ObjectSubscriber());
        Assert.assertEquals(2, registry.getPipeline(String.class).size());
        Assert.assertEquals(1, registry.getPipeline(Object.class).size());
        Assert.assertEquals(0, registry.getPipeline(Integer.class).size());
        registry.register(new PipelineRegistryTest.IntegerSubscriber());
        Assert.assertEquals(2, registry.getPipeline(String.class).size());
        Assert.assertEquals(1, registry.getPipeline(Object.class).size());
        Assert.assertEquals(1, registry.getPipeline(Integer.class).size());
    }

    @Test
    public void testGetSubscribers_returnsImmutableSnapshot() {
        PipelineRegistryTest.StringSubscriber s1 = new PipelineRegistryTest.StringSubscriber();
        PipelineRegistryTest.StringSubscriber s2 = new PipelineRegistryTest.StringSubscriber();
        PipelineRegistryTest.StringSubscriber o1 = new PipelineRegistryTest.StringSubscriber();
        Iterator<FilterSubscriber> empty = registry.getPipeline(String.class).iterator();
        TestCase.assertFalse(empty.hasNext());
        empty = registry.getPipeline(String.class).iterator();
        registry.register(s1, 1);
        TestCase.assertFalse(empty.hasNext());
        Iterator<FilterSubscriber> one = registry.getPipeline(String.class).iterator();
        Assert.assertEquals(s1, one.next().target);
        TestCase.assertFalse(one.hasNext());
        one = registry.getPipeline(String.class).iterator();
        registry.register(s2, 2);
        registry.register(o1, 3);
        Iterator<FilterSubscriber> three = registry.getPipeline(String.class).iterator();
        Assert.assertEquals(s1, one.next().target);
        TestCase.assertFalse(one.hasNext());
        Assert.assertEquals(s1, three.next().target);
        Assert.assertEquals(s2, three.next().target);
        Assert.assertEquals(o1, three.next().target);
        TestCase.assertFalse(three.hasNext());
        three = registry.getPipeline(String.class).iterator();
        /* registry.unregister(s2);

        assertEquals(s1, three.next().target);
        assertEquals(s2, three.next().target);
        assertEquals(o1, three.next().target);
        assertFalse(three.hasNext());

        Iterator<FilterSubscriber> two = registry.getPipeline(String.class).iterator();
        assertEquals(s1, two.next().target);
        assertEquals(o1, two.next().target);
        assertFalse(two.hasNext());
         */
    }

    @Test
    public void test_register_priority() {
        PipelineRegistryTest.StringSubscriber s1 = new PipelineRegistryTest.StringSubscriber();
        PipelineRegistryTest.StringSubscriber s2 = new PipelineRegistryTest.StringSubscriber();
        PipelineRegistryTest.StringSubscriber s3 = new PipelineRegistryTest.StringSubscriber();
        Assert.assertEquals(0, registry.getPipeline(String.class).size());
        registry.register(s1, 80);
        registry.register(s2, 30);
        registry.register(s3, 10);
        Pipeline subscribers = registry.getPipeline(String.class);
        Assert.assertEquals(3, subscribers.size());
        Iterator<FilterSubscriber> iterator = subscribers.iterator();
        MatcherAssert.assertThat(iterator.next().target, CoreMatchers.equalTo(s3));
        MatcherAssert.assertThat(iterator.next().target, CoreMatchers.equalTo(s2));
        MatcherAssert.assertThat(iterator.next().target, CoreMatchers.equalTo(s1));
    }

    public static class NoReturnSubscriber {
        @SuppressWarnings("unused")
        @Subscribe
        public void handle(String s) {
        }
    }

    public static class StringSubscriber {
        @Subscribe
        @SuppressWarnings("unused")
        public String handle(String s) {
            return s;
        }
    }

    public static class IntegerSubscriber {
        @Subscribe
        @SuppressWarnings("unused")
        public Integer handle(Integer i) {
            return i;
        }
    }

    public static class ObjectSubscriber {
        @Subscribe
        @SuppressWarnings("unused")
        public Object handle(Object o) {
            return o;
        }
    }

    @Test
    public void testFlattenHierarchy() {
        Assert.assertEquals(ImmutableSet.of(Object.class, PipelineRegistryTest.HierarchyFixtureInterface.class, PipelineRegistryTest.HierarchyFixtureSubinterface.class, PipelineRegistryTest.HierarchyFixtureParent.class, PipelineRegistryTest.HierarchyFixture.class), PipelineRegistry.flattenHierarchy(PipelineRegistryTest.HierarchyFixture.class));
    }

    // Exists only for hierarchy mapping; no members.
    private interface HierarchyFixtureInterface {}

    // Exists only for hierarchy mapping; no members.
    private interface HierarchyFixtureSubinterface extends PipelineRegistryTest.HierarchyFixtureInterface {}

    // Exists only for hierarchy mapping; no members.
    private static class HierarchyFixtureParent implements PipelineRegistryTest.HierarchyFixtureSubinterface {}

    // Exists only for hierarchy mapping; no members.
    private static class HierarchyFixture extends PipelineRegistryTest.HierarchyFixtureParent {}
}

