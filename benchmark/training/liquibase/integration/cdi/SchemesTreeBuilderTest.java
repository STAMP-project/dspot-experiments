package liquibase.integration.cdi;


import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import liquibase.integration.cdi.annotations.LiquibaseSchema;
import liquibase.integration.cdi.exceptions.CyclicDependencyException;
import liquibase.integration.cdi.exceptions.DependencyNotFoundException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nikita Lipatov (https://github.com/islonik), antoermo (https://github.com/dikeert)
 * @since 27/5/17.
 */
public class SchemesTreeBuilderTest {
    @Test(expected = DependencyNotFoundException.class)
    public void testMissingDependencies() throws Exception {
        Collection<LiquibaseSchema> schemes = getWithAbsentDependency();
        new SchemesTreeBuilder().build(UUID.randomUUID().toString(), schemes);
    }

    @Test(expected = CyclicDependencyException.class)
    public void testShortCyclic() throws Exception {
        Collection<LiquibaseSchema> liquibaseSchemas = getShortCyclic();
        new SchemesTreeBuilder().build(UUID.randomUUID().toString(), liquibaseSchemas);
    }

    @Test(expected = CyclicDependencyException.class)
    public void testMediumCyclic() throws Exception {
        Collection<LiquibaseSchema> liquibaseSchemas = getMediumCyclic();
        new SchemesTreeBuilder().build(UUID.randomUUID().toString(), liquibaseSchemas);
    }

    @Test(expected = CyclicDependencyException.class)
    public void testLongCyclic() throws Exception {
        Collection<LiquibaseSchema> liquibaseSchemas = getLongCyclic();
        new SchemesTreeBuilder().build(UUID.randomUUID().toString(), liquibaseSchemas);
    }

    @Test
    public void testWithDependencies() throws Exception {
        Collection<LiquibaseSchema> liquibaseSchemas = getDependent();
        Collection<LiquibaseSchema> resolved = new SchemesTreeBuilder().build(UUID.randomUUID().toString(), liquibaseSchemas);
        Assert.assertEquals(liquibaseSchemas.size(), resolved.size());
        Collection<LiquibaseSchema> previous = new ArrayList<LiquibaseSchema>(resolved.size());
        for (LiquibaseSchema liquibaseSchema : resolved) {
            if (!(Strings.isNullOrEmpty(liquibaseSchema.depends()))) {
                Assert.assertFalse(isDependencyMissed(liquibaseSchema, previous));
            }
            previous.add(liquibaseSchema);
        }
        Assert.assertEquals(9, previous.size());
    }

    @Test
    public void testMixed() throws Exception {
        Collection<LiquibaseSchema> schemes = new ArrayList<LiquibaseSchema>() {
            {
                addAll(getNonDependent());
                addAll(getDependent());
            }
        };
        Collection<LiquibaseSchema> resolved = new SchemesTreeBuilder().build(UUID.randomUUID().toString(), schemes);
        Assert.assertEquals(schemes.size(), resolved.size());
        for (LiquibaseSchema liquibaseSchema : schemes) {
            Assert.assertTrue(resolved.contains(liquibaseSchema));
        }
        Collection<LiquibaseSchema> previous = new ArrayList<LiquibaseSchema>(resolved.size());
        for (LiquibaseSchema liquibaseSchema : resolved) {
            if (!(Strings.isNullOrEmpty(liquibaseSchema.depends()))) {
                Assert.assertFalse(isDependencyMissed(liquibaseSchema, previous));
            }
            previous.add(liquibaseSchema);
        }
        Assert.assertEquals(16, previous.size());
    }

    @Test
    public void testNoDependencies() throws Exception {
        Collection<LiquibaseSchema> liquibaseSchemas = getNonDependent();
        Collection<LiquibaseSchema> resolved = new SchemesTreeBuilder().build(UUID.randomUUID().toString(), liquibaseSchemas);
        Assert.assertEquals(liquibaseSchemas.size(), resolved.size());
        for (LiquibaseSchema liquibaseSchema : liquibaseSchemas) {
            Assert.assertTrue(resolved.contains(liquibaseSchema));
        }
        Assert.assertEquals(7, resolved.size());
    }
}

