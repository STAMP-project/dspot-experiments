package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.query.FindOptions;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author scotthernandez
 */
public class ReferencesWIgnoreMissingTest extends TestBase {
    @Test
    public void testMissingReference() {
        final ReferencesWIgnoreMissingTest.Container c = new ReferencesWIgnoreMissingTest.Container();
        c.refs = new ReferencesWIgnoreMissingTest.StringHolder[]{ new ReferencesWIgnoreMissingTest.StringHolder(), new ReferencesWIgnoreMissingTest.StringHolder() };
        getDs().save(c);
        getDs().save(c.refs[0]);
        ReferencesWIgnoreMissingTest.Container reloadedContainer = getDs().find(ReferencesWIgnoreMissingTest.Container.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(reloadedContainer);
        Assert.assertNotNull(reloadedContainer.refs);
        Assert.assertEquals(1, reloadedContainer.refs.length);
        reloadedContainer = getDs().get(c);
        Assert.assertNotNull(reloadedContainer);
        Assert.assertNotNull(reloadedContainer.refs);
        Assert.assertEquals(1, reloadedContainer.refs.length);
        final List<ReferencesWIgnoreMissingTest.Container> cs = TestBase.toList(getDs().find(ReferencesWIgnoreMissingTest.Container.class).find());
        Assert.assertNotNull(cs);
        Assert.assertEquals(1, cs.size());
    }

    @Entity
    static class Container {
        @Id
        private ObjectId id;

        @Reference(ignoreMissing = true)
        private ReferencesWIgnoreMissingTest.StringHolder[] refs;
    }

    @Entity
    static class StringHolder {
        @Id
        private ObjectId id = new ObjectId();
    }
}

