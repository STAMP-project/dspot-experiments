package uk.gov.gchq.gaffer.integration.operation.named.cache;


import java.util.Properties;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.cache.impl.HashMapCacheService;
import uk.gov.gchq.gaffer.named.operation.AddNamedOperation;
import uk.gov.gchq.gaffer.named.operation.GetAllNamedOperations;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.operation.handler.named.AddNamedOperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.named.DeleteNamedOperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.named.GetAllNamedOperationsHandler;
import uk.gov.gchq.gaffer.user.User;


public class NamedOperationCacheIT {
    private static final String CACHE_NAME = "NamedOperation";

    private final Properties cacheProps = new Properties();

    private final Store store = Mockito.mock(Store.class);

    private final String adminAuth = "admin auth";

    private final StoreProperties properties = new StoreProperties();

    private AddNamedOperation add = new AddNamedOperation.Builder().name("op").description("test operation").operationChain(new OperationChain.Builder().first(new GetAllElements.Builder().build()).build()).overwrite().score(0).build();

    private User user = new User();

    private User authorisedUser = new User.Builder().userId("authorisedUser").opAuth("authorised").build();

    private User adminAuthUser = new User.Builder().userId("adminAuthUser").opAuth(adminAuth).build();

    private Context context = new Context(user);

    private GetAllNamedOperationsHandler getAllNamedOperationsHandler = new GetAllNamedOperationsHandler();

    private AddNamedOperationHandler addNamedOperationHandler = new AddNamedOperationHandler();

    private GetAllNamedOperationsHandler getAllNamedOperationsHandler1 = new GetAllNamedOperationsHandler();

    private DeleteNamedOperationHandler deleteNamedOperationHandler = new DeleteNamedOperationHandler();

    private GetAllNamedOperations get = new GetAllNamedOperations();

    @Test
    public void shouldWorkUsingHashMapServiceClass() throws CacheOperationException, OperationException {
        reInitialiseCacheService(HashMapCacheService.class);
        runTests();
    }
}

