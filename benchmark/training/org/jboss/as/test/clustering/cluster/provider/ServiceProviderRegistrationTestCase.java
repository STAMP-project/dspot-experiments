package org.jboss.as.test.clustering.cluster.provider;


import java.util.Collection;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.cluster.provider.bean.ServiceProviderRetriever;
import org.jboss.as.test.clustering.cluster.provider.bean.ServiceProviderRetrieverBean;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ServiceProviderRegistrationTestCase extends AbstractClusteringTestCase {
    private static final String MODULE_NAME = ServiceProviderRegistrationTestCase.class.getSimpleName();

    @Test
    public void test() throws Exception {
        try (EJBDirectory directory = new RemoteEJBDirectory(ServiceProviderRegistrationTestCase.MODULE_NAME)) {
            ServiceProviderRetriever bean = directory.lookupStateless(ServiceProviderRetrieverBean.class, ServiceProviderRetriever.class);
            Collection<String> names = bean.getProviders();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.toString(), names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.toString(), names.contains(AbstractClusteringTestCase.NODE_2));
            undeploy(AbstractClusteringTestCase.DEPLOYMENT_1);
            names = bean.getProviders();
            Assert.assertEquals(1, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
            deploy(AbstractClusteringTestCase.DEPLOYMENT_1);
            names = bean.getProviders();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
            stop(AbstractClusteringTestCase.NODE_2);
            names = bean.getProviders();
            Assert.assertEquals(1, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            start(AbstractClusteringTestCase.NODE_2);
            names = bean.getProviders();
            Assert.assertEquals(2, names.size());
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(names.contains(AbstractClusteringTestCase.NODE_2));
        }
    }
}

