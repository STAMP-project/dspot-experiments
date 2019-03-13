package liquibase.integration.spring;


import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Tests for {@link SpringLiquibase}
 */
public class SpringLiquibaseTest {
    public static final String TEST_CONTEXT = "test_context";

    public static final String TEST_LABELS = "test_labels";

    public static final String TEST_TAG = "test_tag";

    private SpringLiquibase springLiquibase = new SpringLiquibase();

    private Liquibase liquibase;

    private ArgumentCaptor<Contexts> contextCaptor;

    private ArgumentCaptor<LabelExpression> labelCaptor;

    private ArgumentCaptor<String> stringCaptor;

    @Test
    public void testRollbackOnUpdateToFalse() throws Exception {
        springLiquibase.setTestRollbackOnUpdate(false);
        springLiquibase.performUpdate(liquibase);
        Mockito.verify(liquibase).update(contextCaptor.capture(), labelCaptor.capture());
        Assert.assertSame(contextCaptor.getValue().getContexts().size(), 1);
        Assert.assertTrue(contextCaptor.getValue().getContexts().contains(SpringLiquibaseTest.TEST_CONTEXT));
        Assert.assertSame(labelCaptor.getValue().getLabels().size(), 1);
        Assert.assertTrue(labelCaptor.getValue().getLabels().contains(SpringLiquibaseTest.TEST_LABELS));
    }

    @Test
    public void testRollbackOnUpdateToFalseWithTag() throws Exception {
        springLiquibase.setTag(SpringLiquibaseTest.TEST_TAG);
        springLiquibase.setTestRollbackOnUpdate(false);
        springLiquibase.performUpdate(liquibase);
        Mockito.verify(liquibase).update(stringCaptor.capture(), contextCaptor.capture(), labelCaptor.capture());
        Assert.assertSame(contextCaptor.getValue().getContexts().size(), 1);
        Assert.assertTrue(contextCaptor.getValue().getContexts().contains(SpringLiquibaseTest.TEST_CONTEXT));
        Assert.assertSame(labelCaptor.getValue().getLabels().size(), 1);
        Assert.assertTrue(labelCaptor.getValue().getLabels().contains(SpringLiquibaseTest.TEST_LABELS));
        Assert.assertSame(stringCaptor.getValue(), SpringLiquibaseTest.TEST_TAG);
    }

    @Test
    public void testRollbackOnUpdateToTrue() throws Exception {
        springLiquibase.setTestRollbackOnUpdate(true);
        springLiquibase.performUpdate(liquibase);
        Mockito.verify(liquibase).updateTestingRollback(contextCaptor.capture(), labelCaptor.capture());
        Assert.assertSame(contextCaptor.getValue().getContexts().size(), 1);
        Assert.assertTrue(contextCaptor.getValue().getContexts().contains(SpringLiquibaseTest.TEST_CONTEXT));
        Assert.assertSame(labelCaptor.getValue().getLabels().size(), 1);
        Assert.assertTrue(labelCaptor.getValue().getLabels().contains(SpringLiquibaseTest.TEST_LABELS));
    }

    @Test
    public void testRollbackOnUpdateToTrueWithTag() throws Exception {
        springLiquibase.setTag(SpringLiquibaseTest.TEST_TAG);
        springLiquibase.setTestRollbackOnUpdate(true);
        springLiquibase.performUpdate(liquibase);
        Mockito.verify(liquibase).updateTestingRollback(stringCaptor.capture(), contextCaptor.capture(), labelCaptor.capture());
        Assert.assertSame(contextCaptor.getValue().getContexts().size(), 1);
        Assert.assertTrue(contextCaptor.getValue().getContexts().contains(SpringLiquibaseTest.TEST_CONTEXT));
        Assert.assertSame(labelCaptor.getValue().getLabels().size(), 1);
        Assert.assertTrue(labelCaptor.getValue().getLabels().contains(SpringLiquibaseTest.TEST_LABELS));
        Assert.assertSame(stringCaptor.getValue(), SpringLiquibaseTest.TEST_TAG);
    }
}

