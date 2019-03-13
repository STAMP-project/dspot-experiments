package org.stagemonitor.core.elasticsearch;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;


public class ElasticsearchClientAvailabilityCheckTest {
    private ElasticsearchClient elasticsearchClient;

    @Test
    public void testNotAvailable() throws Exception {
        elasticsearchClient.checkEsAvailability();
        Assert.assertFalse(elasticsearchClient.isElasticsearchAvailable());
        Server server = new Server(41234);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getWriter().write(("{\n" + ((((((((((((((("  \"cluster_name\" : \"monitoring-cluster\",\n" + "  \"status\" : \"yellow\",\n") + "  \"timed_out\" : false,\n") + "  \"number_of_nodes\" : 1,\n") + "  \"number_of_data_nodes\" : 1,\n") + "  \"active_primary_shards\" : 8,\n") + "  \"active_shards\" : 8,\n") + "  \"relocating_shards\" : 0,\n") + "  \"initializing_shards\" : 0,\n") + "  \"unassigned_shards\" : 6,\n") + "  \"delayed_unassigned_shards\" : 0,\n") + "  \"number_of_pending_tasks\" : 0,\n") + "  \"number_of_in_flight_fetch\" : 0,\n") + "  \"task_max_waiting_in_queue_millis\" : 0,\n") + "  \"active_shards_percent_as_number\" : 57.14285714285714\n") + "}")));
                baseRequest.setHandled(true);
            }
        });
        server.start();
        elasticsearchClient.checkEsAvailability();
        Assert.assertTrue(elasticsearchClient.isElasticsearchAvailable());
        server.stop();
    }
}

