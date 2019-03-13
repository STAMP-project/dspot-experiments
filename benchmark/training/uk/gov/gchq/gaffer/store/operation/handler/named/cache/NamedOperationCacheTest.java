package uk.gov.gchq.gaffer.store.operation.handler.named.cache;


import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.named.operation.NamedOperationDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.user.User;


public class NamedOperationCacheTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static NamedOperationCache cache;

    private static final String GAFFER_USER = "gaffer user";

    private static final String ADVANCED_GAFFER_USER = "advanced gaffer user";

    private static final String ADMIN_AUTH = "admin auth";

    private static final String EMPTY_ADMIN_AUTH = "";

    private List<String> readers = Collections.singletonList(NamedOperationCacheTest.GAFFER_USER);

    private List<String> writers = Collections.singletonList(NamedOperationCacheTest.ADVANCED_GAFFER_USER);

    private User standardUser = new User.Builder().opAuths(NamedOperationCacheTest.GAFFER_USER).userId("123").build();

    private User userWithAdminAuth = new User.Builder().opAuths(NamedOperationCacheTest.ADMIN_AUTH).userId("adminUser").build();

    private User advancedUser = new User.Builder().opAuths(NamedOperationCacheTest.GAFFER_USER, NamedOperationCacheTest.ADVANCED_GAFFER_USER).userId("456").build();

    private OperationChain standardOpChain = new OperationChain.Builder().first(new AddElements()).build();

    private OperationChain alternativeOpChain = new OperationChain.Builder().first(new GetElements.Builder().build()).build();

    private static final String OPERATION_NAME = "New operation";

    private NamedOperationDetail standard = new NamedOperationDetail.Builder().operationName(NamedOperationCacheTest.OPERATION_NAME).description("standard operation").creatorId(standardUser.getUserId()).readers(readers).writers(writers).operationChain(standardOpChain).build();

    private NamedOperationDetail alternative = new NamedOperationDetail.Builder().operationName(NamedOperationCacheTest.OPERATION_NAME).description("alternative operation").creatorId(advancedUser.getUserId()).readers(readers).writers(writers).operationChain(alternativeOpChain).build();

    @Test
    public void shouldAddNamedOperation() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        NamedOperationDetail namedOperation = NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, standardUser);
        Assert.assertEquals(standard, namedOperation);
    }

    @Test
    public void shouldThrowExceptionIfNamedOperationAlreadyExists() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        exception.expect(OverwritingException.class);
        NamedOperationCacheTest.cache.addNamedOperation(alternative, false, advancedUser);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingIfKeyIsNull() throws CacheOperationFailedException {
        // needs work
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.deleteNamedOperation(null, advancedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingIfKeyIsNull() throws CacheOperationFailedException {
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.getNamedOperation(null, advancedUser);
    }

    @Test
    public void shouldThrowExceptionIfNamedOperationIsNull() throws CacheOperationFailedException {
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.addNamedOperation(null, false, standardUser);
    }

    @Test
    public void shouldThrowExceptionIfUnauthorisedUserTriesToReadOperation() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, new User());
    }

    @Test
    public void shouldAllowUsersWithCorrectOpAuthsReadAccessToTheOperationChain() throws CacheOperationFailedException {
        // see if this works with standard user - it should do
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        Assert.assertEquals(standard, NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, advancedUser));
    }

    @Test
    public void shouldAllowUsersReadAccessToTheirOwnNamedOperations() throws CacheOperationFailedException {
        NamedOperationDetail op = new NamedOperationDetail.Builder().operationName(NamedOperationCacheTest.OPERATION_NAME).creatorId(standardUser.getUserId()).operationChain(standardOpChain).readers(new java.util.ArrayList()).writers(writers).build();
        NamedOperationCacheTest.cache.addNamedOperation(op, false, standardUser);
        Assert.assertEquals(op, NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, standardUser));
    }

    @Test
    public void shouldAllowUsersWriteAccessToTheirOwnOperations() throws CacheOperationFailedException {
        NamedOperationDetail op = new NamedOperationDetail.Builder().operationName(NamedOperationCacheTest.OPERATION_NAME).creatorId(standardUser.getUserId()).operationChain(standardOpChain).readers(readers).writers(new java.util.ArrayList()).build();
        NamedOperationCacheTest.cache.addNamedOperation(op, false, standardUser);
        NamedOperationCacheTest.cache.addNamedOperation(standard, true, standardUser);
        Assert.assertEquals(standard, NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, standardUser));
    }

    @Test
    public void shouldThrowExceptionIfUnauthorisedUserTriesToOverwriteOperation() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(alternative, false, advancedUser);
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.addNamedOperation(standard, true, standardUser);
    }

    @Test
    public void shouldAllowOverWriteIfFlagIsSetAndUserIsAuthorised() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        NamedOperationCacheTest.cache.addNamedOperation(alternative, true, advancedUser);
        Assert.assertEquals(alternative, NamedOperationCacheTest.cache.getNamedOperation(NamedOperationCacheTest.OPERATION_NAME, standardUser));
    }

    @Test
    public void shouldThrowExceptionIfUnauthorisedUserTriesToDeleteOperation() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(alternative, false, advancedUser);
        exception.expect(CacheOperationFailedException.class);
        NamedOperationCacheTest.cache.deleteNamedOperation(NamedOperationCacheTest.OPERATION_NAME, standardUser);
    }

    @Test
    public void shouldReturnEmptySetIfThereAreNoOperationsInTheCache() {
        CloseableIterable<NamedOperationDetail> ops = NamedOperationCacheTest.cache.getAllNamedOperations(standardUser);
        assert (Iterables.size(ops)) == 0;
    }

    @Test
    public void shouldReturnSetOfNamedOperationsThatAUserCanExecute() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        NamedOperationDetail alt = new NamedOperationDetail.Builder().operationName("different operation").description("alt").creatorId(advancedUser.getUserId()).readers(readers).writers(writers).operationChain(alternativeOpChain).build();
        NamedOperationCacheTest.cache.addNamedOperation(alt, false, advancedUser);
        Set<NamedOperationDetail> actual = Sets.newHashSet(NamedOperationCacheTest.cache.getAllNamedOperations(standardUser));
        assert actual.contains(standard);
        assert actual.contains(alt);
        assert (actual.size()) == 2;
    }

    @Test
    public void shouldNotReturnANamedOperationThatAUserCannotExecute() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        NamedOperationDetail noReadAccess = new NamedOperationDetail.Builder().creatorId(advancedUser.getUserId()).description("an operation that a standard user cannot execute").operationName("test").readers(writers).writers(writers).operationChain(standardOpChain).build();
        NamedOperationCacheTest.cache.addNamedOperation(noReadAccess, false, advancedUser);
        Set<NamedOperationDetail> actual = Sets.newHashSet(NamedOperationCacheTest.cache.getAllNamedOperations(standardUser));
        assert actual.contains(standard);
        assert (actual.size()) == 1;
    }

    @Test
    public void shouldBeAbleToReturnFullExtendedOperationChain() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(standard, false, standardUser);
        NamedOperationDetail alt = new NamedOperationDetail.Builder().operationName("different").description("alt").creatorId(advancedUser.getUserId()).readers(readers).writers(writers).operationChain(alternativeOpChain).build();
        NamedOperationCacheTest.cache.addNamedOperation(alt, false, advancedUser);
        Set<NamedOperationDetail> actual = Sets.newHashSet(NamedOperationCacheTest.cache.getAllNamedOperations(standardUser));
        assert actual.contains(standard);
        assert actual.contains(alt);
        assert (actual.size()) == 2;
    }

    @Test
    public void shouldAllowAddingWhenUserHasAdminAuth() throws CacheOperationFailedException {
        NamedOperationCacheTest.cache.addNamedOperation(alternative, false, advancedUser, NamedOperationCacheTest.EMPTY_ADMIN_AUTH);
        NamedOperationDetail alt = new NamedOperationDetail.Builder().operationName(alternative.getOperationName()).description("alt").creatorId(standardUser.getUserId()).operationChain(alternativeOpChain).build();
        NamedOperationCacheTest.cache.addNamedOperation(alt, true, userWithAdminAuth, NamedOperationCacheTest.ADMIN_AUTH);
    }
}

