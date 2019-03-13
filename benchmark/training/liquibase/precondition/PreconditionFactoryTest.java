package liquibase.precondition;


import java.util.Map;
import liquibase.precondition.core.AndPrecondition;
import liquibase.precondition.core.OrPrecondition;
import org.junit.Assert;
import org.junit.Test;


public class PreconditionFactoryTest {
    @Test
    public void getInstance() {
        Assert.assertNotNull(PreconditionFactory.getInstance());
        Assert.assertTrue(((PreconditionFactory.getInstance()) == (PreconditionFactory.getInstance())));
    }

    @Test
    public void register() {
        PreconditionFactory.getInstance().getPreconditions().clear();
        Assert.assertEquals(0, PreconditionFactory.getInstance().getPreconditions().size());
        PreconditionFactory.getInstance().register(new MockPrecondition());
        Assert.assertEquals(1, PreconditionFactory.getInstance().getPreconditions().size());
    }

    @Test
    public void unregister_instance() {
        PreconditionFactory factory = PreconditionFactory.getInstance();
        factory.getPreconditions().clear();
        Assert.assertEquals(0, factory.getPreconditions().size());
        factory.register(new OrPrecondition());
        factory.register(new AndPrecondition());
        Assert.assertEquals(2, factory.getPreconditions().size());
        factory.unregister("and");
        Assert.assertEquals(1, factory.getPreconditions().size());
    }

    @Test
    public void reset() {
        PreconditionFactory instance1 = PreconditionFactory.getInstance();
        PreconditionFactory.reset();
        Assert.assertFalse((instance1 == (PreconditionFactory.getInstance())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void builtInGeneratorsAreFound() {
        Map<String, Class<? extends Precondition>> generators = PreconditionFactory.getInstance().getPreconditions();
        Assert.assertTrue(((generators.size()) > 5));
    }

    @Test
    public void createPreconditions() {
        Precondition precondtion = PreconditionFactory.getInstance().create("and");
        Assert.assertNotNull(precondtion);
        Assert.assertTrue((precondtion instanceof AndPrecondition));
    }
}

