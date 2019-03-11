package com.zendesk.maxwell.monitoring;


import MaxwellDiagnosticContext.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.zendesk.maxwell.Maxwell;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.MaxwellTestWithIsolatedServer;
import com.zendesk.maxwell.MaxwellWithContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DiagnosticMaxwellTest extends MaxwellTestWithIsolatedServer {
    private ByteArrayOutputStream outputStream;

    private PrintWriter writer;

    @Test
    public void testNormalBinlogReplicationDiagnostic() throws Exception {
        // Given
        MaxwellDiagnosticContext.Config config = new MaxwellDiagnosticContext.Config();
        config.timeout = 5000;
        MaxwellContext maxwellContext = buildContext();
        DiagnosticHealthCheck healthCheck = getDiagnosticHealthCheck(config, maxwellContext);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
        final CountDownLatch latch = new CountDownLatch(1);
        Maxwell maxwell = new MaxwellWithContext(maxwellContext) {
            @Override
            protected void onReplicatorStart() {
                latch.countDown();
            }
        };
        new Thread(maxwell).start();
        latch.await();
        // When
        healthCheck.doGet(request, response);
        writer.flush();
        // Then
        JsonNode binlogNode = getBinlogNode();
        Assert.assertThat(binlogNode.get("name").asText(), Is.is("binlog-connector"));
        Assert.assertThat(binlogNode.get("success").asBoolean(), Is.is(true));
        Assert.assertTrue(binlogNode.get("mandatory").asBoolean());
        Assert.assertTrue(binlogNode.get("message").asText().contains("Binlog replication lag"));
        maxwell.terminate();
    }

    @Test
    public void testBinlogReplicationDiagnosticTimeout() throws Exception {
        // Given
        MaxwellDiagnosticContext.Config config = new MaxwellDiagnosticContext.Config();
        config.timeout = 100;
        MaxwellContext maxwellContext = buildContext();
        DiagnosticHealthCheck healthCheck = getDiagnosticHealthCheck(config, maxwellContext);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
        // When
        healthCheck.doGet(request, response);
        writer.flush();
        // Then
        JsonNode binlogNode = getBinlogNode();
        Assert.assertThat(binlogNode.get("name").asText(), Is.is("binlog-connector"));
        Assert.assertThat(binlogNode.get("success").asBoolean(), Is.is(false));
        Assert.assertTrue(binlogNode.get("mandatory").asBoolean());
        Assert.assertTrue(binlogNode.get("message").asText().contains("check did not return after 100 ms"));
    }
}

