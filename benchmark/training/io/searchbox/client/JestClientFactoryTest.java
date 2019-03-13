package io.searchbox.client;


import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.http.JestHttpClient;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestClientFactoryTest {
    @Test
    public void clientCreationWithTimeout() {
        JestClientFactory factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("someUri").connTimeout(150).readTimeout(300).build();
        factory.setHttpClientConfig(httpClientConfig);
        final RequestConfig defaultRequestConfig = factory.getRequestConfig();
        Assert.assertNotNull(defaultRequestConfig);
        Assert.assertEquals(150, defaultRequestConfig.getConnectTimeout());
        Assert.assertEquals(300, defaultRequestConfig.getSocketTimeout());
    }

    @Test
    public void clientCreationWithDiscovery() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).build());
        JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()));
        Assert.assertTrue((jestClient != null));
        Assert.assertNotNull(jestClient.getAsyncClient());
        Assert.assertTrue(((factory.getConnectionManager()) instanceof BasicHttpClientConnectionManager));
        Assert.assertEquals(jestClient.getServerPoolSize(), 1);
    }

    @Test
    public void clientCreationWithNullClientConfig() {
        JestClientFactory factory = new JestClientFactory();
        JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()));
        Assert.assertTrue((jestClient != null));
        Assert.assertNotNull(jestClient.getAsyncClient());
        Assert.assertEquals(jestClient.getServerPoolSize(), 1);
        Assert.assertEquals("server list should contain localhost:9200", "http://localhost:9200", jestClient.getNextServer());
    }

    @Test
    public void multiThreadedClientCreation() {
        JestClientFactory factory = new JestClientFactory();
        HttpRoute routeOne = new HttpRoute(new HttpHost("http://test.localhost"));
        HttpRoute routeTwo = new HttpRoute(new HttpHost("http://localhost"));
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).maxTotalConnection(20).defaultMaxTotalConnectionPerRoute(10).maxTotalConnectionPerRoute(routeOne, 5).maxTotalConnectionPerRoute(routeTwo, 6).build();
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()));
        Assert.assertTrue((jestClient != null));
        Assert.assertEquals(jestClient.getServerPoolSize(), 1);
        Assert.assertEquals("server list should contain localhost:9200", "http://localhost:9200", jestClient.getNextServer());
        final HttpClientConnectionManager connectionManager = factory.getConnectionManager();
        Assert.assertTrue((connectionManager instanceof PoolingHttpClientConnectionManager));
        Assert.assertEquals(10, getDefaultMaxPerRoute());
        Assert.assertEquals(20, getMaxTotal());
        Assert.assertEquals(5, getMaxPerRoute(routeOne));
        Assert.assertEquals(6, getMaxPerRoute(routeTwo));
        final NHttpClientConnectionManager nConnectionManager = factory.getAsyncConnectionManager();
        Assert.assertTrue((nConnectionManager instanceof PoolingNHttpClientConnectionManager));
        Assert.assertEquals(10, getDefaultMaxPerRoute());
        Assert.assertEquals(20, getMaxTotal());
        Assert.assertEquals(5, getMaxPerRoute(routeOne));
        Assert.assertEquals(6, getMaxPerRoute(routeTwo));
    }

    @Test
    public void clientCreationWithDiscoveryAndOverriddenNodeChecker() {
        JestClientFactory factory = Mockito.spy(new JestClientFactoryTest.ExtendedJestClientFactory());
        HttpClientConfig httpClientConfig = Mockito.spy(new HttpClientConfig.Builder("http://somehost:9200").discoveryEnabled(true).build());
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestClient = ((JestHttpClient) (factory.getObject()));
        Assert.assertTrue((jestClient != null));
        Assert.assertNotNull(jestClient.getAsyncClient());
        Assert.assertEquals(jestClient.getServerPoolSize(), 1);
        Assert.assertEquals("server list should contain localhost:9200", "http://somehost:9200", jestClient.getNextServer());
        Mockito.verify(factory, Mockito.times(1)).createNodeChecker(Mockito.any(JestHttpClient.class), Mockito.same(httpClientConfig));
    }

    @Test
    public void clientCreationWithPreemptiveAuth() {
        JestClientFactory factory = new JestClientFactory();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("someUser", "somePassword"));
        HttpHost targetHost1 = new HttpHost("targetHostName1", 80, "http");
        HttpHost targetHost2 = new HttpHost("targetHostName2", 80, "http");
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("someUri").credentialsProvider(credentialsProvider).preemptiveAuthTargetHosts(new HashSet<HttpHost>(Arrays.asList(targetHost1, targetHost2))).build();
        factory.setHttpClientConfig(httpClientConfig);
        JestHttpClient jestHttpClient = ((JestHttpClient) (factory.getObject()));
        HttpClientContext httpClientContext = jestHttpClient.getHttpClientContextTemplate();
        Assert.assertNotNull(httpClientContext.getAuthCache().get(targetHost1));
        Assert.assertNotNull(httpClientContext.getAuthCache().get(targetHost2));
        Assert.assertEquals(credentialsProvider, httpClientContext.getCredentialsProvider());
    }

    class ExtendedJestClientFactory extends JestClientFactory {
        @Override
        protected NodeChecker createNodeChecker(JestHttpClient client, HttpClientConfig httpClientConfig) {
            return new JestClientFactoryTest.OtherNodeChecker(client, httpClientConfig);
        }
    }

    class OtherNodeChecker extends NodeChecker {
        public OtherNodeChecker(JestClient jestClient, ClientConfig clientConfig) {
            super(jestClient, clientConfig);
        }
    }
}

