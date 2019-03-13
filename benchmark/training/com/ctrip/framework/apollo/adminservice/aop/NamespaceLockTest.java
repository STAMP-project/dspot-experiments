package com.ctrip.framework.apollo.adminservice.aop;


import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.service.ItemService;
import com.ctrip.framework.apollo.biz.service.NamespaceLockService;
import com.ctrip.framework.apollo.biz.service.NamespaceService;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.common.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;


@RunWith(MockitoJUnitRunner.class)
public class NamespaceLockTest {
    private static final String APP = "app-test";

    private static final String CLUSTER = "cluster-test";

    private static final String NAMESPACE = "namespace-test";

    private static final String CURRENT_USER = "user-test";

    private static final String ANOTHER_USER = "user-test2";

    private static final long NAMESPACE_ID = 100;

    @Mock
    private NamespaceLockService namespaceLockService;

    @Mock
    private NamespaceService namespaceService;

    @Mock
    private ItemService itemService;

    @Mock
    private BizConfig bizConfig;

    @InjectMocks
    NamespaceAcquireLockAspect namespaceLockAspect;

    @Test
    public void acquireLockWithNotLockedAndSwitchON() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(true);
        namespaceLockAspect.acquireLock(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(namespaceService, Mockito.times(0)).findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE);
    }

    @Test
    public void acquireLockWithNotLockedAndSwitchOFF() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);
        Mockito.when(namespaceService.findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE)).thenReturn(mockNamespace());
        Mockito.when(namespaceLockService.findLock(ArgumentMatchers.anyLong())).thenReturn(null);
        namespaceLockAspect.acquireLock(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(bizConfig).isNamespaceLockSwitchOff();
        Mockito.verify(namespaceService).findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE);
        Mockito.verify(namespaceLockService).findLock(ArgumentMatchers.anyLong());
        Mockito.verify(namespaceLockService).tryLock(ArgumentMatchers.any());
    }

    @Test(expected = BadRequestException.class)
    public void acquireLockWithAlreadyLockedByOtherGuy() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);
        Mockito.when(namespaceService.findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE)).thenReturn(mockNamespace());
        Mockito.when(namespaceLockService.findLock(NamespaceLockTest.NAMESPACE_ID)).thenReturn(mockNamespaceLock(NamespaceLockTest.ANOTHER_USER));
        namespaceLockAspect.acquireLock(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(bizConfig).isNamespaceLockSwitchOff();
        Mockito.verify(namespaceService).findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE);
        Mockito.verify(namespaceLockService).findLock(NamespaceLockTest.NAMESPACE_ID);
    }

    @Test
    public void acquireLockWithAlreadyLockedBySelf() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);
        Mockito.when(namespaceService.findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE)).thenReturn(mockNamespace());
        Mockito.when(namespaceLockService.findLock(NamespaceLockTest.NAMESPACE_ID)).thenReturn(mockNamespaceLock(NamespaceLockTest.CURRENT_USER));
        namespaceLockAspect.acquireLock(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(bizConfig).isNamespaceLockSwitchOff();
        Mockito.verify(namespaceService).findOne(NamespaceLockTest.APP, NamespaceLockTest.CLUSTER, NamespaceLockTest.NAMESPACE);
        Mockito.verify(namespaceLockService).findLock(NamespaceLockTest.NAMESPACE_ID);
    }

    @Test
    public void acquireLockWithNamespaceIdSwitchOn() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);
        Mockito.when(namespaceService.findOne(NamespaceLockTest.NAMESPACE_ID)).thenReturn(mockNamespace());
        Mockito.when(namespaceLockService.findLock(NamespaceLockTest.NAMESPACE_ID)).thenReturn(null);
        namespaceLockAspect.acquireLock(NamespaceLockTest.NAMESPACE_ID, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(bizConfig).isNamespaceLockSwitchOff();
        Mockito.verify(namespaceService).findOne(NamespaceLockTest.NAMESPACE_ID);
        Mockito.verify(namespaceLockService).findLock(NamespaceLockTest.NAMESPACE_ID);
        Mockito.verify(namespaceLockService).tryLock(ArgumentMatchers.any());
    }

    @Test(expected = ServiceException.class)
    public void testDuplicateLock() {
        Mockito.when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);
        Mockito.when(namespaceService.findOne(NamespaceLockTest.NAMESPACE_ID)).thenReturn(mockNamespace());
        Mockito.when(namespaceLockService.findLock(NamespaceLockTest.NAMESPACE_ID)).thenReturn(null);
        Mockito.when(namespaceLockService.tryLock(ArgumentMatchers.any())).thenThrow(DataIntegrityViolationException.class);
        namespaceLockAspect.acquireLock(NamespaceLockTest.NAMESPACE_ID, NamespaceLockTest.CURRENT_USER);
        Mockito.verify(bizConfig).isNamespaceLockSwitchOff();
        Mockito.verify(namespaceService).findOne(NamespaceLockTest.NAMESPACE_ID);
        Mockito.verify(namespaceLockService, Mockito.times(2)).findLock(NamespaceLockTest.NAMESPACE_ID);
        Mockito.verify(namespaceLockService).tryLock(ArgumentMatchers.any());
    }
}

