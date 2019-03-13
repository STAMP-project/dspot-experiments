/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.ui.repository.pur.repositoryexplorer.model;


import java.io.Serializable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryTestBase;
import org.pentaho.platform.api.engine.IAuthorizationPolicy;
import org.pentaho.platform.api.engine.security.userroledao.IUserRoleDao;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.mt.ITenantManager;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.platform.api.repository2.unified.IBackingRepositoryLifecycleManager;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.repository2.unified.IRepositoryFileDao;
import org.pentaho.platform.security.policy.rolebased.IRoleAuthorizationPolicyRoleBindingDao;
import org.pentaho.platform.security.userroledao.DefaultTenantedPrincipleNameResolver;
import org.pentaho.test.platform.engine.core.MicroPlatform;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.jcr.JcrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.transaction.support.TransactionTemplate;


@ContextConfiguration(locations = { "classpath:/repository.spring.xml", "classpath:/repository-test-override.spring.xml" })
public class UIEERepositoryDirectoryIT extends RepositoryTestBase implements Serializable , ApplicationContextAware {
    static final long serialVersionUID = 2064159405078106703L;/* EESOURCE: UPDATE SERIALVERUID */


    private IUnifiedRepository repo;

    private ITenantedPrincipleNameResolver userNameUtils = new DefaultTenantedPrincipleNameResolver();

    private ITenantedPrincipleNameResolver roleNameUtils = new DefaultTenantedPrincipleNameResolver(DefaultTenantedPrincipleNameResolver.ALTERNATE_DELIMETER);

    private ITenantManager tenantManager;

    private ITenant systemTenant;

    private IRoleAuthorizationPolicyRoleBindingDao roleBindingDaoTarget;

    private String repositoryAdminUsername;

    private JcrTemplate testJcrTemplate;

    private MicroPlatform mp;

    IUserRoleDao testUserRoleDao;

    IUserRoleDao userRoleDao;

    private String singleTenantAdminRoleName;

    private String tenantAuthenticatedRoleName;

    private String sysAdminUserName;

    private String superAdminRoleName;

    private TransactionTemplate txnTemplate;

    private IRepositoryFileDao repositoryFileDao;

    private final String TENANT_ID_ACME = "acme";

    private IBackingRepositoryLifecycleManager repositoryLifecyleManager;

    private final String TENANT_ID_DUFF = "duff";

    private static IAuthorizationPolicy authorizationPolicy;

    private TestContextManager testContextManager;

    public UIEERepositoryDirectoryIT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    /**
     * Allow PentahoSystem to create this class but it in turn delegates to the authorizationPolicy fetched from Spring's
     * ApplicationContext.
     */
    public static class DelegatingAuthorizationPolicy implements IAuthorizationPolicy {
        public List<String> getAllowedActions(final String actionNamespace) {
            return UIEERepositoryDirectoryIT.authorizationPolicy.getAllowedActions(actionNamespace);
        }

        public boolean isAllowed(final String actionName) {
            return UIEERepositoryDirectoryIT.authorizationPolicy.isAllowed(actionName);
        }
    }

    @Test
    public void testUiDelete() throws Exception {
        RepositoryDirectoryInterface rootDir = repository.loadRepositoryDirectoryTree();
        final String startDirName = "home";
        final String testDirName = "testdir";
        final String startDirPath = "/" + startDirName;
        final String testDirPath = (startDirPath + "/") + testDirName;
        RepositoryDirectoryInterface startDir = rootDir.findDirectory(startDirName);
        final RepositoryDirectoryInterface testDirCreated = repository.createRepositoryDirectory(startDir, testDirName);
        Assert.assertNotNull(testDirCreated);
        Assert.assertNotNull(testDirCreated.getObjectId());
        rootDir = repository.loadRepositoryDirectoryTree();
        final RepositoryDirectoryInterface startDirFound = repository.findDirectory(startDirPath);
        final RepositoryDirectoryInterface testDirFound = repository.findDirectory(testDirPath);
        Assert.assertNotNull(testDirFound);
        final UIEERepositoryDirectory startDirUi = new UIEERepositoryDirectory(startDirFound, null, repository);
        final UIEERepositoryDirectory testDirUi = new UIEERepositoryDirectory(testDirFound, startDirUi, repository);
        testDirUi.delete(true);
        RepositoryDirectoryInterface testDirFound2 = repository.findDirectory(testDirPath);
        Assert.assertNull(testDirFound2);
    }

    @Test
    public void testUiDeleteNotEmpty() throws Exception {
        RepositoryDirectoryInterface rootDir = repository.loadRepositoryDirectoryTree();
        final String startDirName = "home";
        final String testDirName = "testdir";
        final String testDir2Name = "testdir2";
        final String startDirPath = "/" + startDirName;
        final String testDirPath = (startDirPath + "/") + testDirName;
        final String testDir2Path = (testDirPath + "/") + testDir2Name;
        RepositoryDirectoryInterface startDir = rootDir.findDirectory(startDirName);
        final RepositoryDirectoryInterface testDirCreated = repository.createRepositoryDirectory(startDir, testDirName);
        final RepositoryDirectoryInterface testDir2Created = repository.createRepositoryDirectory(testDirCreated, testDir2Name);
        Assert.assertNotNull(testDirCreated);
        Assert.assertNotNull(testDirCreated.getObjectId());
        Assert.assertNotNull(testDir2Created);
        rootDir = repository.loadRepositoryDirectoryTree();
        startDir = rootDir.findDirectory(startDirName);
        final RepositoryDirectoryInterface startDirFound = repository.findDirectory(startDirPath);
        final RepositoryDirectoryInterface testDirFound = repository.findDirectory(testDirPath);
        Assert.assertNotNull(testDirFound);
        final RepositoryDirectoryInterface testDir2Found = repository.findDirectory(testDir2Path);
        Assert.assertNotNull(testDir2Found);
        final UIEERepositoryDirectory startDirUi = new UIEERepositoryDirectory(startDirFound, null, repository);
        final UIEERepositoryDirectory testDirUi = new UIEERepositoryDirectory(testDirFound, startDirUi, repository);
        testDirUi.delete(true);
        RepositoryDirectoryInterface testDirFound2 = repository.findDirectory(testDirPath);
        Assert.assertNull(testDirFound2);
    }
}

