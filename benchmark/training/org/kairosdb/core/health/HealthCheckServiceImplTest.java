package org.kairosdb.core.health;


import HealthCheck.Result;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HealthCheckServiceImplTest {
    @Test(expected = NullPointerException.class)
    public void testConstructorNullInjectorInvalid() {
        new HealthCheckServiceImpl(null);
    }

    @Test
    public void testGetChecks() {
        Injector injector = Guice.createInjector(new HealthCheckServiceImplTest.TestModule());
        HealthCheckServiceImpl checkService = new HealthCheckServiceImpl(injector);
        List<HealthStatus> checks = checkService.getChecks();
        Assert.assertThat(checks.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(checks, CoreMatchers.hasItems(new HealthCheckServiceImplTest.HealthStatus1(), new HealthCheckServiceImplTest.HealthStatus2()));
    }

    private class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(HealthCheckServiceImplTest.HealthStatus1.class);
            bind(HealthCheckServiceImplTest.HealthStatus2.class);
            bind(HealthCheckServiceImplTest.NotHealthStatus.class);
        }
    }

    private static class HealthStatus1 implements HealthStatus {
        private String name = getClass().getSimpleName();

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Result execute() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            HealthCheckServiceImplTest.HealthStatus1 that = ((HealthCheckServiceImplTest.HealthStatus1) (o));
            return !((name) != null ? !(name.equals(that.name)) : (that.name) != null);
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }
    }

    private static class HealthStatus2 implements HealthStatus {
        private String name = getClass().getSimpleName();

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Result execute() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            HealthCheckServiceImplTest.HealthStatus2 that = ((HealthCheckServiceImplTest.HealthStatus2) (o));
            return !((name) != null ? !(name.equals(that.name)) : (that.name) != null);
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }
    }

    private static class NotHealthStatus {}
}

