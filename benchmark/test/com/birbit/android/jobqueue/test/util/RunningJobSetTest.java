package com.birbit.android.jobqueue.test.util;


import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.RunningJobSet;
import com.birbit.android.jobqueue.test.timer.MockTimer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RunningJobSetTest {
    RunningJobSet set;

    @Test
    public void testEmpty() {
        MatcherAssert.assertThat(set.getSafe().size(), CoreMatchers.is(0));
    }

    @Test
    public void testAdd() {
        set.add("g1");
        MatcherAssert.assertThat(set.getSafe().iterator().next(), CoreMatchers.is("g1"));
    }

    @Test
    public void testAddTheSame() {
        set.add("g1");
        set.add("g1");
        MatcherAssert.assertThat(set.getSafe().iterator().next(), CoreMatchers.is("g1"));
        MatcherAssert.assertThat(set.getSafe().size(), CoreMatchers.is(1));
    }

    @Test
    public void testRemove() {
        set.add("g1");
        set.remove("g1");
        MatcherAssert.assertThat(set.getSafe().iterator().hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testOrder() {
        set.add("a");
        set.add("z");
        set.add("b");
        assertList("a", "b", "z");
    }

    @Test
    public void testAddWithTimeout() {
        MockTimer timer = new MockTimer();
        set = new RunningJobSet(timer);
        set.addGroupUntil("g1", 10L);
        timer.setNow(5);
        assertList("g1");
        timer.setNow(11);
        assertList();
        timer.setNow(3);
        assertList();// should've pruned the list

    }

    @Test
    public void testAddSameGroupTwiceWithTimeout() {
        MockTimer timer = new MockTimer();
        set = new RunningJobSet(timer);
        set.addGroupUntil("g1", 10L);
        set.addGroupUntil("g1", 12L);
        timer.setNow(5);
        assertList("g1");
        timer.setNow(11);
        assertList("g1");
        timer.setNow(13);
        assertList();
    }

    @Test
    public void testAddMultipleGroupTimeouts() {
        MockTimer timer = new MockTimer();
        set = new RunningJobSet(timer);
        set.addGroupUntil("g1", 10L);
        set.addGroupUntil("g2", 20L);
        timer.setNow(5);
        assertList("g1", "g2");
        timer.setNow(11);
        assertList("g2");
        timer.setNow(21);
        assertList();
    }
}

