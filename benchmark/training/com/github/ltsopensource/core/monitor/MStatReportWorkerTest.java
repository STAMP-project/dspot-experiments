package com.github.ltsopensource.core.monitor;


import com.github.ltsopensource.core.domain.monitor.MData;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import org.junit.Test;


/**
 *
 *
 * @author Robert HG (254963746@qq.com) on 7/13/16.
 */
public class MStatReportWorkerTest {
    protected final Logger LOGGER = LoggerFactory.getLogger(MStatReportWorker.class);

    private PriorityBlockingQueue<MData> queue = new PriorityBlockingQueue<MData>(16, new Comparator<MData>() {
        @Override
        public int compare(MData o1, MData o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    });

    private static final int MAX_RETRY_RETAIN = 30;

    private static final int BATCH_REPORT_SIZE = 10;

    private volatile boolean running = false;

    @Test
    public void testReport() {
        for (int i = 0; i < 100; i++) {
            MData mData = new MData();
            mData.setTimestamp(Long.valueOf(i));
            report(mData);
        }
    }
}

