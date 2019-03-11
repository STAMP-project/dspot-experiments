package cucumber.runtime.java.spring.hooks;


import cucumber.api.spring.SpringTransactionHooks;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;


public class SpringTransactionHooksTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SpringTransactionHooks target;

    @Mock
    private BeanFactory mockedBeanFactory;

    @Mock
    private PlatformTransactionManager mockedPlatformTransactionManager;

    @Test
    public void shouldObtainPlatformTransactionManagerByTypeWhenTxnManagerBeanNameNotSet() {
        SpringTransactionHooks localTarget = new SpringTransactionHooks();
        localTarget.setBeanFactory(mockedBeanFactory);
        Mockito.when(mockedBeanFactory.getBean(PlatformTransactionManager.class)).thenReturn(mockedPlatformTransactionManager);
        Assert.assertSame(localTarget.obtainPlatformTransactionManager(), mockedPlatformTransactionManager);
        Mockito.verify(mockedBeanFactory).getBean(PlatformTransactionManager.class);
    }

    @Test
    public void shouldObtainPlatformTransactionManagerByNameWhenTxnManagerBeanNameIsSet() {
        SpringTransactionHooks localTarget = new SpringTransactionHooks();
        localTarget.setBeanFactory(mockedBeanFactory);
        final String txnManagerBeanName = "myTxnManagerBeanName";
        localTarget.setTxnManagerBeanName(txnManagerBeanName);
        Mockito.when(mockedBeanFactory.getBean(txnManagerBeanName, PlatformTransactionManager.class)).thenReturn(mockedPlatformTransactionManager);
        Assert.assertSame(localTarget.obtainPlatformTransactionManager(), mockedPlatformTransactionManager);
        Mockito.verify(mockedBeanFactory).getBean(txnManagerBeanName, PlatformTransactionManager.class);
    }

    @Test
    public void shouldObtainOrStartTransactionInBeforeHook() {
        final SimpleTransactionStatus dummyTxStatus = new SimpleTransactionStatus();
        Mockito.when(mockedPlatformTransactionManager.getTransaction(ArgumentMatchers.isA(TransactionDefinition.class))).thenReturn(dummyTxStatus);
        target.startTransaction();
        Assert.assertSame(target.getTransactionStatus(), dummyTxStatus);
    }

    @Test
    public void shouldTriggerTransactionRollbackInAfterHook() {
        final SimpleTransactionStatus dummyTxStatus = new SimpleTransactionStatus();
        target.setTransactionStatus(dummyTxStatus);
        target.rollBackTransaction();
        Mockito.verify(mockedPlatformTransactionManager).rollback(dummyTxStatus);
    }
}

