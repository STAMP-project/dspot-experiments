package MyPerf4J;


import cn.myperf4j.base.Scheduler;
import cn.myperf4j.base.constant.PropertyValues;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/19
 */
public class JvmMetricsSchedulerTest {
    @Test
    public void test() {
        init();
        int processorType = PropertyValues.METRICS_PROCESS_TYPE_STDOUT;
        JvmClassMetricsProcessor classProcessor = MetricsProcessorFactory.getClassMetricsProcessor(processorType);
        JvmGCMetricsProcessor gcProcessor = MetricsProcessorFactory.getGCMetricsProcessor(processorType);
        JvmMemoryMetricsProcessor memoryProcessor = MetricsProcessorFactory.getMemoryMetricsProcessor(processorType);
        JvmThreadMetricsProcessor threadProcessor = MetricsProcessorFactory.getThreadMetricsProcessor(processorType);
        JvmBufferPoolMetricsProcessor bufferPoolProcessor = MetricsProcessorFactory.getBufferPoolMetricsProcessor(processorType);
        Scheduler scheduler = new cn.myperf4j.core.scheduler.JvmMetricsScheduler(classProcessor, gcProcessor, memoryProcessor, bufferPoolProcessor, threadProcessor);
        long startMills = System.currentTimeMillis();
        scheduler.run(startMills, (startMills + (60 * 1000)));
    }
}

