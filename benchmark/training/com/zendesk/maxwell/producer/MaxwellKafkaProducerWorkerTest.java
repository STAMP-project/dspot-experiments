package com.zendesk.maxwell.producer;


import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.monitoring.NoOpMetrics;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.mockito.Mockito;


public class MaxwellKafkaProducerWorkerTest {
    @Test
    public void constructNewWorkerWithNullTopic() throws TimeoutException {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        Mockito.when(context.getConfig()).thenReturn(config);
        Mockito.when(context.getMetrics()).thenReturn(new NoOpMetrics());
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", "localhost:9092");
        String kafkaTopic = null;
        // shouldn't throw NPE
        MaxwellKafkaProducerWorker worker = new MaxwellKafkaProducerWorker(context, kafkaProperties, kafkaTopic, null);
        worker.close();
    }
}

