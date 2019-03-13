package dev.morphia.query;


import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import java.util.ArrayList;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * For issue #615.
 *
 * @author jbyler
 */
public class QueryForSubtypeTest extends TestBase {
    private MappedClass jobMappedClass;

    @Test
    public void testImplementingClassIsCompatibleWithInterface() {
        MappedField user = jobMappedClass.getMappedField("owner");
        boolean compatible = QueryValidator.isCompatibleForOperator(jobMappedClass, user, QueryForSubtypeTest.User.class, FilterOperator.EQUAL, new QueryForSubtypeTest.UserImpl(), new ArrayList<dev.morphia.query.validation.ValidationFailure>());
        Assert.assertThat(compatible, CoreMatchers.is(true));
    }

    @Test
    public void testIntSizeShouldBeCompatibleWithArrayList() {
        MappedField attributes = jobMappedClass.getMappedField("attributes");
        boolean compatible = QueryValidator.isCompatibleForOperator(jobMappedClass, attributes, ArrayList.class, FilterOperator.SIZE, 2, new ArrayList<dev.morphia.query.validation.ValidationFailure>());
        Assert.assertThat(compatible, CoreMatchers.is(true));
    }

    @Test
    public void testSubclassOfKeyShouldBeCompatibleWithFieldUser() {
        MappedField user = jobMappedClass.getMappedField("owner");
        Key<QueryForSubtypeTest.User> anonymousKeySubclass = new Key<QueryForSubtypeTest.User>(QueryForSubtypeTest.User.class, "User", 212L) {};
        boolean compatible = QueryValidator.isCompatibleForOperator(jobMappedClass, user, QueryForSubtypeTest.User.class, FilterOperator.EQUAL, anonymousKeySubclass, new ArrayList<dev.morphia.query.validation.ValidationFailure>());
        Assert.assertThat(compatible, CoreMatchers.is(true));
    }

    interface User {}

    @Entity
    static class UserImpl implements QueryForSubtypeTest.User {
        @Id
        @SuppressWarnings("unused")
        private ObjectId id;
    }

    @Entity
    static class Job {
        @Id
        @SuppressWarnings("unused")
        private ObjectId id;

        @Reference
        @SuppressWarnings("unused")
        private QueryForSubtypeTest.User owner;

        @SuppressWarnings("unused")
        private ArrayList<String> attributes;
    }
}

