package com.pushtorefresh.storio3.sqlite.impl;


import com.pushtorefresh.storio3.sqlite.Changes;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import java.util.HashSet;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ChangesFilterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void applyForTables_throwsIfTablesNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Set of tables can not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        ChangesFilter.applyForTables(Flowable.<Changes>empty(), null);
    }

    @Test
    public void applyForTables_shouldFilterRequiredTable() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTables(Flowable.just(Changes.newInstance("table1"), Changes.newInstance("table2"), Changes.newInstance("table3")), Collections.singleton("table2")).subscribe(testSubscriber);
        // All other tables should be filtered
        testSubscriber.assertValue(Changes.newInstance("table2"));
        testSubscriber.dispose();
    }

    @Test
    public void applyForTables_shouldFilterRequiredTableWhichIsPartOfSomeChanges() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTables(Flowable.just(Changes.newInstance("table1"), Changes.newInstance(new HashSet<String>() {
            {
                add("table1");
                // Notice, that required table
                // Is just a part of one Changes object
                add("table2");
                add("table3");
            }
        })), Collections.singleton("table3")).subscribe(testSubscriber);
        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance(new HashSet<String>() {
            {
                add("table1");
                add("table2");
                add("table3");
            }
        }));
        testSubscriber.dispose();
    }

    @Test
    public void applyForTables_throwsIfTagsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Set of tags can not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        ChangesFilter.applyForTags(Flowable.<Changes>empty(), null);
    }

    @Test
    public void applyForTags_shouldFilterRequiredTag() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTags(Flowable.just(Changes.newInstance("table1", "tag1"), Changes.newInstance("table2", "tag2"), Changes.newInstance("table3")), Collections.singleton("tag1")).subscribe(testSubscriber);
        // All other tags should be filtered
        testSubscriber.assertValue(Changes.newInstance("table1", "tag1"));
        testSubscriber.dispose();
    }

    @Test
    public void applyForTags_shouldFilterRequiredTagWhichIsPartOfSomeChanges() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        Changes changes = Changes.newInstance(new HashSet<String>() {
            {
                add("table1");
            }
        }, new HashSet<String>() {
            {
                add("tag1");
                add("tag2");
            }
        });
        ChangesFilter.applyForTags(Flowable.just(changes, Changes.newInstance("table3", "tag3"), Changes.newInstance("table4")), Collections.singleton("tag1")).subscribe(testSubscriber);
        // All other tags should be filtered
        testSubscriber.assertValue(changes);
        testSubscriber.dispose();
    }

    @Test
    public void applyForTablesAndTags_throwsIfTablesNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Set of tables can not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        ChangesFilter.applyForTablesAndTags(Flowable.<Changes>empty(), null, Collections.<String>emptySet());
    }

    @Test
    public void applyForTablesAndTags_throwsIfTagsNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Set of tags can not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        ChangesFilter.applyForTablesAndTags(Flowable.<Changes>empty(), Collections.<String>emptySet(), null);
    }

    @Test
    public void applyForTablesAndTags_shouldNotifyByTable() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTablesAndTags(Flowable.just(Changes.newInstance("table1", "another_tag"), Changes.newInstance("table2", "tag2"), Changes.newInstance("table3")), Collections.singleton("table1"), Collections.singleton("tag1")).subscribe(testSubscriber);
        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance("table1", "another_tag"));
        testSubscriber.dispose();
    }

    @Test
    public void applyForTablesAndTags_shouldNotifyByTag() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTablesAndTags(Flowable.just(Changes.newInstance("another_table", "tag1"), Changes.newInstance("table2", "tag2"), Changes.newInstance("table3")), Collections.singleton("table1"), Collections.singleton("tag1")).subscribe(testSubscriber);
        // All other Changes should be filtered
        testSubscriber.assertValue(Changes.newInstance("another_table", "tag1"));
        testSubscriber.dispose();
    }

    @Test
    public void applyForTablesAndTags_shouldSendJustOnceNotificationIfBothTableAndTagAreSatisfy() {
        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        ChangesFilter.applyForTablesAndTags(Flowable.just(Changes.newInstance("target_table", "target_tag")), Collections.singleton("target_table"), Collections.singleton("target_tag")).subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(Changes.newInstance("target_table", "target_tag"));
        testSubscriber.dispose();
    }
}

