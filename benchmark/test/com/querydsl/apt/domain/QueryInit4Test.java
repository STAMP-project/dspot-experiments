package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import java.sql.Date;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static QQueryInit4Test_Tenant.tenant;


public class QueryInit4Test {
    @QueryEntity
    public static class Organization {}

    @QueryEntity
    public static class Application {}

    @QueryEntity
    public static class Tenant {
        Long id;

        String tenantBusinessKey;

        String sourceSystemKey;

        String tenantName;

        @QueryInit({ "user.primaryTenant", "tenant" })
        Set<QueryInit4Test.UserTenantApplication> userTenantApplications;

        Set<QueryInit4Test.Organization> organizations;

        Date lastModifiedDate;

        Long lastModifiedUserId;
    }

    @QueryEntity
    public static class UserTenantApplication {
        QueryInit4Test.User user;

        QueryInit4Test.Tenant tenant;

        QueryInit4Test.Application application;

        Date lastModifiedDate;

        Long lastModifiedUserId;
    }

    @QueryEntity
    public static class User {
        Long id;

        QueryInit4Test.Tenant primaryTenant;

        Set<QueryInit4Test.UserTenantApplication> userTenantApplications;
    }

    @Test
    public void test() {
        QQueryInit4Test_Tenant tenant = tenant;
        Assert.assertNotNull(tenant.userTenantApplications.any().user.id);
        Assert.assertNotNull(tenant.userTenantApplications.any().tenant.id);
        Assert.assertNotNull(tenant.userTenantApplications.any().user.primaryTenant.id);
    }
}

