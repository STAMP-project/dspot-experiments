package dev.morphia;


import dev.morphia.annotations.CappedAt;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestCapped extends TestBase {
    @Test
    public void testCappedEntity() {
        // given
        getMorphia().map(TestCapped.CurrentStatus.class);
        getDs().ensureCaps();
        // when-then
        final TestCapped.CurrentStatus cs = new TestCapped.CurrentStatus("All Good");
        getDs().save(cs);
        Assert.assertEquals(1, getDs().getCount(TestCapped.CurrentStatus.class));
        getDs().save(new TestCapped.CurrentStatus("Kinda Bad"));
        Assert.assertEquals(1, getDs().getCount(TestCapped.CurrentStatus.class));
        Assert.assertTrue(getDs().find(TestCapped.CurrentStatus.class).find(new FindOptions().limit(1)).next().message.contains("Bad"));
        getDs().save(new TestCapped.CurrentStatus("Kinda Bad2"));
        Assert.assertEquals(1, getDs().getCount(TestCapped.CurrentStatus.class));
        getDs().save(new TestCapped.CurrentStatus("Kinda Bad3"));
        Assert.assertEquals(1, getDs().getCount(TestCapped.CurrentStatus.class));
        getDs().save(new TestCapped.CurrentStatus("Kinda Bad4"));
        Assert.assertEquals(1, getDs().getCount(TestCapped.CurrentStatus.class));
    }

    @Entity(cap = @CappedAt(count = 1))
    private static class CurrentStatus {
        @Id
        private ObjectId id;

        private String message;

        private CurrentStatus() {
        }

        CurrentStatus(final String msg) {
            message = msg;
        }
    }
}

