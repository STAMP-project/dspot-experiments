package azkaban.executor;


import org.junit.Ignore;
import org.junit.Test;


@Ignore("Requires azkaban server running -> run AzkabanSingleServer first")
public class ExecutorApiGatewaySystemTest {
    private ExecutorApiGateway apiGateway;

    @Test
    public void update100Executions() throws Exception {
        updateExecutions(100);
    }

    @Test
    public void update300Executions() throws Exception {
        // used to fail because the URL is too long
        // works after switching to HTTP POST
        updateExecutions(300);
    }

    @Test
    public void update100kExecutions() throws Exception {
        // used to fail because the URL is too long
        // works after switching to HTTP POST
        updateExecutions(100000);
    }
}

