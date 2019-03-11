package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StatsCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(StatsCmdIT.class);

    private static int NUM_STATS = 5;

    @Test
    public void testStatsStreaming() throws IOException, InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        CountDownLatch countDownLatch = new CountDownLatch(StatsCmdIT.NUM_STATS);
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("top").withName(containerName).exec();
        StatsCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        StatsCmdIT.StatsCallbackTest statsCallback = dockerRule.getClient().statsCmd(container.getId()).exec(new StatsCmdIT.StatsCallbackTest(countDownLatch));
        countDownLatch.await(3, TimeUnit.SECONDS);
        Boolean gotStats = statsCallback.gotStats();
        StatsCmdIT.LOG.info("Stop stats collection");
        close();
        StatsCmdIT.LOG.info("Stopping container");
        dockerRule.getClient().stopContainerCmd(container.getId()).exec();
        dockerRule.getClient().removeContainerCmd(container.getId()).exec();
        StatsCmdIT.LOG.info("Completed test");
        Assert.assertTrue("Expected true", gotStats);
    }

    private class StatsCallbackTest extends ResultCallbackTemplate<StatsCmdIT.StatsCallbackTest, Statistics> {
        private final CountDownLatch countDownLatch;

        private Boolean gotStats = false;

        public StatsCallbackTest(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onNext(Statistics stats) {
            StatsCmdIT.LOG.info("Received stats #{}: {}", countDownLatch.getCount(), stats);
            if (stats != null) {
                gotStats = true;
            }
            countDownLatch.countDown();
        }

        public Boolean gotStats() {
            return gotStats;
        }
    }
}

