package com.github.ltsopensource.tasktracker.runner;


import Environment.UNIT_TEST;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.cluster.LTSConfig;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.JobMeta;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.ec.injvm.InjvmEventCenter;
import com.github.ltsopensource.tasktracker.domain.Response;
import com.github.ltsopensource.tasktracker.domain.TaskTrackerAppContext;
import com.github.ltsopensource.tasktracker.expcetion.NoAvailableJobRunnerException;
import com.github.ltsopensource.tasktracker.monitor.TaskTrackerMStatReporter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 *
 *
 * @author Robert HG (254963746@qq.com) on 2/21/16.
 */
public class RunnerPoolTest {
    @Test
    public void testInterruptor() throws NoAvailableJobRunnerException {
        LTSConfig.setEnvironment(UNIT_TEST);
        Config config = new Config();
        config.setWorkThreads(10);
        config.setIdentity("fjdaslfjlasj");
        TaskTrackerAppContext appContext = new TaskTrackerAppContext();
        appContext.setConfig(config);
        appContext.setEventCenter(new InjvmEventCenter());
        appContext.setJobRunnerClass(TestInterruptorJobRunner.class);
        // appContext.setJobRunnerClass(NormalJobRunner.class);
        RunnerPool runnerPool = new RunnerPool(appContext);
        appContext.setRunnerPool(runnerPool);
        TaskTrackerMStatReporter monitor = new TaskTrackerMStatReporter(appContext);
        appContext.setMStatReporter(monitor);
        RunnerCallback callback = new RunnerCallback() {
            @Override
            public JobMeta runComplete(Response response) {
                System.out.println(("complete:" + (JSON.toJSONString(response))));
                return null;
            }
        };
        Job job = new Job();
        job.setTaskId("fdsafas");
        JobMeta jobMeta = new JobMeta();
        jobMeta.setJobId("111111");
        jobMeta.setJob(job);
        runnerPool.execute(jobMeta, callback);
        System.out.println(runnerPool.getAvailablePoolSize());
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 5s????
        runnerPool.stopWorking();
        while (true) {
            try {
                // ??????????,????????,???? NormalJobRunner ??????  TestInterruptorJobRunner ???
                System.out.println(NormalJobRunner.l);
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(runnerPool.getAvailablePoolSize());
        } 
    }

    @Test
    public void test() throws NoAvailableJobRunnerException {
        int workThreads = 5;
        ThreadPoolExecutor threadPoolExecutor = // ?????????????
        new ThreadPoolExecutor(workThreads, workThreads, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory("test"), new ThreadPoolExecutor.AbortPolicy());
        final List<Thread> list = new CopyOnWriteArrayList<Thread>();
        for (int i = 0; i < 12; i++) {
            submitJob(threadPoolExecutor, list);
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.get(0).interrupt();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        submitJob(threadPoolExecutor, list);
        submitJob(threadPoolExecutor, list);
        try {
            Thread.sleep(5000000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

