/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.integration;


import Keys.ENTER;
import java.net.URI;
import java.util.List;
import org.apache.zeppelin.AbstractZeppelinIT;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created for org.apache.zeppelin.integration on 13/06/16.
 */
public class AuthenticationIT extends AbstractZeppelinIT {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationIT.class);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static String shiroPath;

    static String authShiro = "[users]\n" + (((((((((((((((((("admin = password1, admin\n" + "finance1 = finance1, finance\n") + "finance2 = finance2, finance\n") + "hr1 = hr1, hr\n") + "hr2 = hr2, hr\n") + "[main]\n") + "sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager\n") + "securityManager.sessionManager = $sessionManager\n") + "securityManager.sessionManager.globalSessionTimeout = 86400000\n") + "shiro.loginUrl = /api/login\n") + "anyofrolesuser = org.apache.zeppelin.utils.AnyOfRolesUserAuthorizationFilter\n") + "[roles]\n") + "admin = *\n") + "hr = *\n") + "finance = *\n") + "[urls]\n") + "/api/version = anon\n") + "/api/interpreter/** = authc, anyofrolesuser[admin, finance]\n") + "/** = authc");

    static String originalShiro = "";

    @Test
    public void testAnyOfRolesUser() throws Exception {
        try {
            AuthenticationIT authenticationIT = new AuthenticationIT();
            authenticationIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            collector.checkThat("Check is user has permission to view this page", true, CoreMatchers.equalTo(pollingWait(By.xpath("//div[@id='main']/div/div[2]"), AbstractZeppelinIT.MIN_IMPLICIT_WAIT).isDisplayed()));
            authenticationIT.logoutUser("admin");
            authenticationIT.authenticationUser("finance1", "finance1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            collector.checkThat("Check is user has permission to view this page", true, CoreMatchers.equalTo(pollingWait(By.xpath("//div[@id='main']/div/div[2]"), AbstractZeppelinIT.MIN_IMPLICIT_WAIT).isDisplayed()));
            authenticationIT.logoutUser("finance1");
            authenticationIT.authenticationUser("hr1", "hr1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            try {
                collector.checkThat("Check is user has permission to view this page", true, CoreMatchers.equalTo(pollingWait(By.xpath("//li[contains(@class, 'ng-toast__message')]//span/span"), AbstractZeppelinIT.MIN_IMPLICIT_WAIT).isDisplayed()));
            } catch (TimeoutException e) {
                throw new Exception("Expected ngToast not found", e);
            }
            authenticationIT.logoutUser("hr1");
        } catch (Exception e) {
            handleException("Exception in AuthenticationIT while testAnyOfRolesUser ", e);
        }
    }

    @Test
    public void testGroupPermission() throws Exception {
        try {
            AuthenticationIT authenticationIT = new AuthenticationIT();
            authenticationIT.authenticationUser("finance1", "finance1");
            createNewNote();
            String noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            pollingWait(By.xpath("//span[@uib-tooltip='Note permissions']"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            pollingWait(By.xpath(".//*[@id='selectOwners']/following::span//input"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("finance ");
            pollingWait(By.xpath(".//*[@id='selectReaders']/following::span//input"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("finance ");
            pollingWait(By.xpath(".//*[@id='selectRunners']/following::span//input"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("finance ");
            pollingWait(By.xpath(".//*[@id='selectWriters']/following::span//input"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("finance ");
            pollingWait(By.xpath("//button[@ng-click='savePermissions()']"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys(ENTER);
            pollingWait(By.xpath(("//div[@class='modal-dialog'][contains(.,'Permissions Saved ')]" + "//div[@class='modal-footer']//button[contains(.,'OK')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            authenticationIT.logoutUser("finance1");
            authenticationIT.authenticationUser("hr1", "hr1");
            try {
                WebElement element = pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC);
                collector.checkThat("Check is user has permission to view this note link", false, CoreMatchers.equalTo(element.isDisplayed()));
            } catch (Exception e) {
                // This should have failed, nothing to worry.
            }
            AbstractZeppelinIT.driver.get(new URI(AbstractZeppelinIT.driver.getCurrentUrl()).resolve(("/#/notebook/" + noteId)).toString());
            List<WebElement> privilegesModal = AbstractZeppelinIT.driver.findElements(By.xpath(("//div[@class='modal-content']//div[@class='bootstrap-dialog-header']" + "//div[contains(.,'Insufficient privileges')]")));
            collector.checkThat("Check is user has permission to view this note", 1, CoreMatchers.equalTo(privilegesModal.size()));
            AbstractZeppelinIT.driver.findElement(By.xpath(("//div[@class='modal-content'][contains(.,'Insufficient privileges')]" + "//div[@class='modal-footer']//button[2]"))).click();
            authenticationIT.logoutUser("hr1");
            authenticationIT.authenticationUser("finance2", "finance2");
            try {
                WebElement element = pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC);
                collector.checkThat("Check is user has permission to view this note link", true, CoreMatchers.equalTo(element.isDisplayed()));
            } catch (Exception e) {
                // This should have failed, nothing to worry.
            }
            AbstractZeppelinIT.driver.get(new URI(AbstractZeppelinIT.driver.getCurrentUrl()).resolve(("/#/notebook/" + noteId)).toString());
            privilegesModal = AbstractZeppelinIT.driver.findElements(By.xpath(("//div[@class='modal-content']//div[@class='bootstrap-dialog-header']" + "//div[contains(.,'Insufficient privileges')]")));
            collector.checkThat("Check is user has permission to view this note", 0, CoreMatchers.equalTo(privilegesModal.size()));
            deleteTestNotebook(AbstractZeppelinIT.driver);
            authenticationIT.logoutUser("finance2");
        } catch (Exception e) {
            handleException("Exception in AuthenticationIT while testGroupPermission ", e);
        }
    }
}

