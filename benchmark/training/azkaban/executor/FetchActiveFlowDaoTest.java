package azkaban.executor;


import azkaban.executor.FetchActiveFlowDao.FetchActiveExecutableFlows;
import azkaban.utils.Pair;
import java.sql.ResultSet;
import java.util.Map;
import org.junit.Test;


/**
 * Also @see ExecutionFlowDaoTest - DB operations of FetchActiveFlowDao are tested there.
 */
public class FetchActiveFlowDaoTest {
    private ResultSet rs;

    @Test
    public void handleResultMissingExecutor() throws Exception {
        final FetchActiveExecutableFlows resultHandler = new FetchActiveExecutableFlows();
        mockResultWithData();
        final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> result = resultHandler.handle(this.rs);
        assertThat(result.containsKey(1)).isTrue();
        assertThat(result.get(1).getFirst().getExecutor().isPresent()).isFalse();
    }

    @Test
    public void handleResultNullData() throws Exception {
        final FetchActiveExecutableFlows resultHandler = new FetchActiveExecutableFlows();
        mockResultWithNullData();
        final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> result = resultHandler.handle(this.rs);
        assertThat(result).isEmpty();
    }
}

