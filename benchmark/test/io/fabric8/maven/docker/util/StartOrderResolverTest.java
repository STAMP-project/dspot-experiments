package io.fabric8.maven.docker.util;


import io.fabric8.maven.docker.service.QueryService;
import java.util.ArrayList;
import java.util.List;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 16.10.14
 */
public class StartOrderResolverTest {
    @Mocked
    private QueryService queryService;

    @Test
    public void simple() {
        checkData(new Object[][]{ new Object[]{ new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1") }, new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1") } }, new Object[]{ new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1", "2"), new StartOrderResolverTest.T("2") }, new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("2"), new StartOrderResolverTest.T("1", "2") } }, new Object[]{ new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1", "2", "3"), new StartOrderResolverTest.T("2", "3"), new StartOrderResolverTest.T("3") }, new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("3"), new StartOrderResolverTest.T("2", "3"), new StartOrderResolverTest.T("1", "2", "3") } } });
    }

    @Test(expected = IllegalStateException.class)
    public void circularDep() {
        checkData(new Object[][]{ new Object[]{ new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1", "2"), new StartOrderResolverTest.T("2", "1") }, new StartOrderResolverTest.T[]{ new StartOrderResolverTest.T("1", "2"), new StartOrderResolverTest.T("2", "1") } } });
        Assert.fail();
    }

    // ============================================================================
    private static class T implements StartOrderResolver.Resolvable {
        private String id;

        private List<String> deps;

        private T(String id, String... dep) {
            this.id = id;
            deps = new ArrayList<>();
            for (String d : dep) {
                deps.add(d);
            }
        }

        @Override
        public String getName() {
            return id;
        }

        @Override
        public String getAlias() {
            return null;
        }

        @Override
        public List<String> getDependencies() {
            return deps;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            StartOrderResolverTest.T t = ((StartOrderResolverTest.T) (o));
            if ((id) != null ? !(id.equals(t.id)) : (t.id) != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (id) != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return ((("T{" + "id='") + (id)) + '\'') + '}';
        }
    }
}

