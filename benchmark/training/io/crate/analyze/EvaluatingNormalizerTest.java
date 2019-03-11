package io.crate.analyze;


import io.crate.action.sql.SessionContext;
import io.crate.expression.eval.EvaluatingNormalizer;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ClusterReferenceResolver;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Functions;
import io.crate.metadata.Reference;
import io.crate.metadata.RowGranularity;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SymbolMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class EvaluatingNormalizerTest extends CrateUnitTest {
    private ClusterReferenceResolver referenceResolver;

    private Functions functions;

    private Reference dummyLoadInfo;

    private final CoordinatorTxnCtx coordinatorTxnCtx = new CoordinatorTxnCtx(SessionContext.systemSessionContext());

    @Test
    public void testEvaluation() {
        EvaluatingNormalizer visitor = new EvaluatingNormalizer(functions, RowGranularity.NODE, referenceResolver, null);
        Function op_or = prepareFunctionTree();
        // the dummy reference load == 0.08 evaluates to true,
        // so the whole query can be normalized to a single boolean literal
        Symbol query = visitor.normalize(op_or, coordinatorTxnCtx);
        assertThat(query, SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testEvaluationClusterGranularity() {
        EvaluatingNormalizer visitor = new EvaluatingNormalizer(functions, RowGranularity.CLUSTER, referenceResolver, null);
        Function op_or = prepareFunctionTree();
        Symbol query = visitor.normalize(op_or, coordinatorTxnCtx);
        assertThat(query, CoreMatchers.instanceOf(Function.class));
    }
}

