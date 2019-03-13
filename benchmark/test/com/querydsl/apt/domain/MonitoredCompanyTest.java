package com.querydsl.apt.domain;


import org.junit.Assert;
import org.junit.Test;

import static QMonitoredCompany.monitoredCompany;


public class MonitoredCompanyTest {
    @Test
    public void test() {
        QMonitoredCompany monitoredCompany = monitoredCompany;
        Assert.assertNotNull(monitoredCompany.companyGroup);
        Assert.assertNotNull(monitoredCompany.companyGroup.mainCompany);
    }
}

