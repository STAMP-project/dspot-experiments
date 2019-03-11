package liquibase.integration.cdi;


import LogType.LOG;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import liquibase.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Nikita Lipatov (https://github.com/islonik),
 * @since 27/5/17.
 */
public class SchemesCDIConfigBuilderTest {
    private static final Long FILE_LOCK_TIMEOUT = 5L;

    private static final String BEFORE_KEY = "Before";

    private static final String AFTER_KEY = "After";

    private static AtomicLong COUNTER;

    private static Logger log;

    private SchemesCDIConfigBuilder schemesCDIConfigBuilder;

    private BeanManager bm;

    private SchemesTreeBuilder treeBuilder;

    /**
     * General execution.
     */
    @Test
    public void testCreateCDILiquibaseConfig() throws Exception {
        Set<Bean<?>> beans = new LinkedHashSet<Bean<?>>();
        beans.add(mockBean(new A1()));
        beans.add(mockBean(new B2()));
        Mockito.when(bm.getBeans(ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(new SchemesCDIConfigBuilder.AnnotationLiteralDefault()))).thenReturn(beans);
        CDILiquibaseConfig config = schemesCDIConfigBuilder.createCDILiquibaseConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals("liquibase.cdi.schema.xml", config.getChangeLog());
    }

    /**
     * Emulating concurrency migrations inside one JVM
     * <p>
     * We use only 1 monitor here, synchronized block inside jvmLocked should prevent multiple access, wait() isn't fired
     */
    @Test
    public void testJvmLocked() throws Exception {
        final long jobTimeout = 10L;
        final int n = 100;
        final ExecutorService executors = Executors.newFixedThreadPool(n);
        try {
            List<Future<CDILiquibaseConfig>> futures = new ArrayList<Future<CDILiquibaseConfig>>();
            for (int i = 1; i <= n; i++) {
                final Random random = new Random();
                final String tempId = String.format("id-%s", i);
                Callable<CDILiquibaseConfig> callable = new Callable<CDILiquibaseConfig>() {
                    @Override
                    public CDILiquibaseConfig call() throws Exception {
                        Thread.sleep(((random.nextInt(2)) + 1));
                        return schemesCDIConfigBuilder.jvmLocked(tempId, getAction(jobTimeout));
                    }
                };
                futures.add(executors.submit(callable));
            }
            validateFutures(futures);
        } catch (Exception e) {
            SchemesCDIConfigBuilderTest.log.warning(LOG, e.getMessage(), e);
        } finally {
            executors.shutdown();
        }
    }

    @Test
    public void testFileLocked() throws Exception {
        final long jobTimeout = 10L;
        final int n = 100;
        final ExecutorService executors = Executors.newFixedThreadPool(n);
        try {
            List<Future<CDILiquibaseConfig>> futures = new ArrayList<Future<CDILiquibaseConfig>>();
            for (int i = 1; i <= n; i++) {
                final Random random = new Random();
                final String tempId = String.format("id-%s", i);
                Callable<CDILiquibaseConfig> callable = new Callable<CDILiquibaseConfig>() {
                    @Override
                    public CDILiquibaseConfig call() throws Exception {
                        Thread.sleep(((random.nextInt(2)) + 1));
                        return schemesCDIConfigBuilder.fileLocked(tempId, getAction(jobTimeout));
                    }
                };
                futures.add(executors.submit(callable));
            }
            validateFutures(futures);
        } catch (Exception e) {
            SchemesCDIConfigBuilderTest.log.warning(LOG, e.getMessage(), e);
        } finally {
            executors.shutdown();
        }
    }
}

