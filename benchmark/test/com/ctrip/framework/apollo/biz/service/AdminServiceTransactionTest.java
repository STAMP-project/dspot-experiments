package com.ctrip.framework.apollo.biz.service;


import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.biz.repository.AppNamespaceRepository;
import com.ctrip.framework.apollo.biz.repository.AppRepository;
import com.ctrip.framework.apollo.biz.repository.ClusterRepository;
import com.ctrip.framework.apollo.biz.repository.NamespaceRepository;
import com.ctrip.framework.apollo.common.entity.App;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;


public class AdminServiceTransactionTest extends AbstractIntegrationTest {
    @Autowired
    AdminService adminService;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Test
    @Rollback
    public void modifyDatabaseWithinTransaction() {
        String appId = "someAppId";
        App app = new App();
        app.setAppId(appId);
        app.setName("someAppName");
        String owner = "someOwnerName";
        app.setOwnerName(owner);
        app.setOwnerEmail("someOwnerName@ctrip.com");
        app.setDataChangeCreatedBy(owner);
        app.setDataChangeLastModifiedBy(owner);
        app.setDataChangeCreatedTime(new Date());
        adminService.createNewApp(app);
    }
}

