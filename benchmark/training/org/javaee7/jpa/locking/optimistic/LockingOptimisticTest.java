package org.javaee7.jpa.locking.optimistic;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class LockingOptimisticTest {
    @Inject
    private MovieBean movieBean;

    @Resource
    private ManagedExecutorService executor;

    @Test
    public void testLockingOptimisticUpdateAndRead() throws Exception {
        System.out.println("Enter testLockingOptimisticUpdateAndRead");
        resetCountDownLatches();
        List<Movie> movies = movieBean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        final CountDownLatch testCountDownLatch = new CountDownLatch(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                movieBean.updateMovie(3, "INCEPTION UR");
                testCountDownLatch.countDown();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                movieBean.findMovie(3);
                MovieBeanAlternative.lockCountDownLatch.countDown();
            }
        });
        Assert.assertTrue(testCountDownLatch.await(10, TimeUnit.SECONDS));
        Assert.assertEquals("INCEPTION UR", movieBean.findMovie(3).getName());
    }

    @Test
    public void testLockingOptimisticReadAndUpdate() throws Exception {
        System.out.println("Enter testLockingOptimisticReadAndUpdate");
        resetCountDownLatches();
        List<Movie> movies = movieBean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        final CountDownLatch testCountDownLatch = new CountDownLatch(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    movieBean.readMovie(3);
                } catch (RuntimeException e) {
                    // Should throw an javax.persistence.OptimisticLockException? Hibernate is throwing org.hibernate.OptimisticLockException. Investigate!
                    testCountDownLatch.countDown();
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MovieBeanAlternative.lockCountDownLatch.countDown();
                movieBean.updateMovie(3, "INCEPTION RU");
                MovieBeanAlternative.readCountDownLatch.countDown();
            }
        });
        Assert.assertTrue(testCountDownLatch.await(10, TimeUnit.SECONDS));
        Assert.assertEquals("INCEPTION RU", movieBean.findMovie(3).getName());
    }

    @Test
    public void testLockingOptimisticDelete() throws Exception {
        System.out.println("Enter testLockingOptimisticDelete");
        resetCountDownLatches();
        List<Movie> movies = movieBean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        final CountDownLatch testCountDownLatch1 = new CountDownLatch(1);
        final CountDownLatch testCountDownLatch2 = new CountDownLatch(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(((("Update thread " + (Thread.currentThread().getId())) + " at ") + (System.nanoTime())));
                try {
                    testCountDownLatch1.countDown();
                    movieBean.updateMovie2(3, "INCEPTION");
                } catch (RuntimeException e) {
                    // Should throw an javax.persistence.OptimisticLockException? The Exception is wrapped around an javax.ejb.EJBException
                    testCountDownLatch2.countDown();
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(((("Delete thread " + (Thread.currentThread().getId())) + " at ") + (System.nanoTime())));
                try {
                    testCountDownLatch1.await(10, TimeUnit.SECONDS);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                movieBean.deleteMovie(3);
                MovieBeanAlternative.lockCountDownLatch.countDown();
            }
        });
        Assert.assertTrue(testCountDownLatch2.await(20, TimeUnit.SECONDS));
    }
}

