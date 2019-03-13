package org.jboss.as.test.clustering.cluster.registry;


import java.util.Collection;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.cluster.registry.bean.RegistryRetriever;
import org.jboss.as.test.clustering.cluster.registry.bean.RegistryRetrieverBean;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class RegistryTestCase extends AbstractClusteringTestCase {
    private static final String MODULE_NAME = RegistryTestCase.class.getSimpleName();

    @Test
    public void test() throws Exception {
        try (EJBDirectory context = new RemoteEJBDirectory(RegistryTestCase.MODULE_NAME)) {
            RegistryRetriever bean = context.lookupStateless(RegistryRetrieverBean.class, RegistryRetriever.class);
            Collection<String> names = bean.getNodes();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.toString(), names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.toString(), names.contains(AbstractClusteringTestCase.NODE_2));
            undeploy(AbstractClusteringTestCase.DEPLOYMENT_1);
            names = bean.getNodes();
            Assert.assertEquals(1, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
            deploy(AbstractClusteringTestCase.DEPLOYMENT_1);
            names = bean.getNodes();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
            stop(AbstractClusteringTestCase.NODE_2);
            names = bean.getNodes();
            Assert.assertEquals(1, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            start(AbstractClusteringTestCase.NODE_2);
            names = bean.getNodes();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
        }
    }
}

