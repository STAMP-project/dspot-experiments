package dev.morphia.issue1175;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.testutil.IndexMatcher;
import dev.morphia.utils.IndexDirection;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class EnsureIndexesTest extends TestBase {
    @Test
    public final void ensureIndexesFromSuperclasses() {
        // given
        getMorphia().map(EnsureIndexesTest.Person.class, EnsureIndexesTest.Employee.class, EnsureIndexesTest.Company.class);
        // when
        getAds().ensureIndexes();
        // then
        List<DBObject> indexInfo = getDs().getCollection(EnsureIndexesTest.Company.class).getIndexInfo();
        Assert.assertEquals(4, indexInfo.size());
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("_id_"));
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("employees.birthday_-1"));
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("employees.fullName_1"));
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("employees.employeeId_1"));
    }

    @Test
    public final void ensureIndexesWithoutSubclasses() {
        // given
        getMorphia().map(EnsureIndexesTest.Person.class, EnsureIndexesTest.Employee.class, EnsureIndexesTest.Contract.class);
        // when
        getAds().ensureIndexes();
        // then
        List<DBObject> indexInfo = getDs().getCollection(EnsureIndexesTest.Contract.class).getIndexInfo();
        Assert.assertEquals(3, indexInfo.size());
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("_id_"));
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("person.birthday_-1"));
        Assert.assertThat(indexInfo, IndexMatcher.hasIndexNamed("person.fullName_1"));
        Assert.assertThat(indexInfo, IndexMatcher.doesNotHaveIndexNamed("person.employeeId_1"));
    }

    @Embedded
    public static class LivingBeing {
        @Indexed(IndexDirection.DESC)
        private Date birthday;
    }

    @Embedded
    public static class Person extends EnsureIndexesTest.LivingBeing {
        @Indexed
        private String fullName;
    }

    @Embedded
    public static class Employee extends EnsureIndexesTest.Person {
        @Indexed
        private Long employeeId;

        private String title;
    }

    @Entity
    public static class Contract {
        @Id
        private Long contractId;

        private Long companyId;

        @Embedded
        private EnsureIndexesTest.Person person;
    }

    @Entity
    public static class Company {
        @Id
        private Long companyId;

        @Embedded
        private List<EnsureIndexesTest.Employee> employees;
    }
}

