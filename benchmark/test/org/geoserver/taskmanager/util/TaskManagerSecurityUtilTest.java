/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.util;


import java.util.Arrays;
import org.geoserver.catalog.Catalog;
import org.geoserver.security.impl.DataAccessRuleDAO;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class TaskManagerSecurityUtilTest extends AbstractTaskManagerTest {
    @Autowired
    private DataAccessRuleDAO ruleDao;

    @Autowired
    private TaskManagerSecurityUtil secUtil;

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private BatchJobService bjService;

    @Autowired
    private Catalog catalog;

    private Configuration config;

    private Batch batch;

    @Test
    public void testReadable() {
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken("jan", "jan", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("readcdf"), new SimpleGrantedAuthority("readcite") }));
        Assert.assertTrue(secUtil.isReadable(user, config));
        Assert.assertTrue(secUtil.isReadable(user, batch));
        user = new UsernamePasswordAuthenticationToken("piet", "piet", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("readcdf") }));
        Assert.assertTrue(secUtil.isReadable(user, config));
        Assert.assertFalse(secUtil.isReadable(user, batch));
        user = new UsernamePasswordAuthenticationToken("pol", "pol", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("readcite") }));
        Assert.assertFalse(secUtil.isReadable(user, config));
        Assert.assertFalse(secUtil.isReadable(user, batch));
    }

    @Test
    public void testWritable() {
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken("jan", "jan", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("writecdf"), new SimpleGrantedAuthority("writecite") }));
        Assert.assertTrue(secUtil.isWritable(user, batch));
        user = new UsernamePasswordAuthenticationToken("piet", "piet", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("writecdf") }));
        Assert.assertFalse(secUtil.isWritable(user, batch));
        user = new UsernamePasswordAuthenticationToken("pol", "pol", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("writecite") }));
        Assert.assertFalse(secUtil.isWritable(user, batch));
    }

    @Test
    public void testAdminable() {
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken("jan", "jan", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("admincdf"), new SimpleGrantedAuthority("admincite") }));
        Assert.assertTrue(secUtil.isAdminable(user, config));
        Assert.assertTrue(secUtil.isAdminable(user, batch));
        user = new UsernamePasswordAuthenticationToken("piet", "piet", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("admincdf") }));
        Assert.assertTrue(secUtil.isAdminable(user, config));
        Assert.assertFalse(secUtil.isAdminable(user, batch));
        user = new UsernamePasswordAuthenticationToken("pol", "pol", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("admincite") }));
        Assert.assertFalse(secUtil.isAdminable(user, config));
        Assert.assertFalse(secUtil.isAdminable(user, batch));
    }
}

