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


import org.apache.zeppelin.AbstractZeppelinIT;
import org.apache.zeppelin.CommandExecutor;
import org.apache.zeppelin.ProcessData;
import org.apache.zeppelin.ZeppelinITUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.zeppelin.ProcessData.Types_Of_Data.OUTPUT;


public class InterpreterModeActionsIT extends AbstractZeppelinIT {
    private static final Logger LOG = LoggerFactory.getLogger(InterpreterModeActionsIT.class);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static String shiroPath;

    static String authShiro = "[users]\n" + (((((((((((("admin = password1, admin\n" + "user1 = password2, admin\n") + "user2 = password3, admin\n") + "[main]\n") + "sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager\n") + "securityManager.sessionManager = $sessionManager\n") + "securityManager.sessionManager.globalSessionTimeout = 86400000\n") + "shiro.loginUrl = /api/login\n") + "[roles]\n") + "admin = *\n") + "[urls]\n") + "/api/version = anon\n") + "/** = authc");

    static String originalShiro = "";

    static String interpreterOptionPath = "";

    static String originalInterpreterOption = "";

    static String cmdPsPython = "ps aux | grep 'zeppelin_ipython' | grep -v 'grep' | wc -l";

    static String cmdPsInterpreter = "ps aux | grep 'zeppelin/interpreter/python/*' |" + " sed -E '/grep|local-repo/d' | wc -l";

    @Test
    public void testGloballyAction() throws Exception {
        try {
            // step 1: (admin) login, set 'globally in shared' mode of python interpreter, logout
            InterpreterModeActionsIT interpreterModeActionsIT = new InterpreterModeActionsIT();
            interpreterModeActionsIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            pollingWait(By.xpath("//input[contains(@ng-model, 'searchInterpreter')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("python");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath(("//div[contains(@id, \'python\')]//button[contains(@ng-click, \'valueform.$show();\n" + "                  copyOriginInterpreterSettingProperties(setting.id)')]")));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]/div[2]/div/div/div[1]/span[1]/button"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//li/a[contains(.,'Globally')]"));
            JavascriptExecutor jse = ((JavascriptExecutor) (AbstractZeppelinIT.driver));
            jse.executeScript("window.scrollBy(0,250)", "");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//div/form/button[contains(@type, 'submit')]"));
            clickAndWait(By.xpath("//div[@class='modal-dialog']//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"));
            clickAndWait(By.xpath("//a[@class='navbar-brand navbar-title'][contains(@href, '#/')]"));
            interpreterModeActionsIT.logoutUser("admin");
            // step 2: (user1) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user1' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            By locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            WebElement element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            String user1noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user1\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user1"));
            String resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user1");
            // step 3: (user2) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user2' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user2\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 4: (user1) login, come back note user1 made, run second paragraph, check result, check process,
            // restart python interpreter, check process again, logout
            // paragraph: Check if the result is 'user2' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            waitForParagraph(2, "FINISHED");
            runParagraph(2);
            try {
                waitForParagraph(2, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(2, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            clickAndWait(By.xpath("//div[@data-ng-repeat='item in interpreterBindings' and contains(., 'python')]//a"));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart python interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not testGloballyAction");
            boolean invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            locator = By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]");
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("0"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("0"));
            interpreterModeActionsIT.logoutUser("user1");
        } catch (Exception e) {
            handleException("Exception in InterpreterModeActionsIT while testGloballyAction ", e);
        }
    }

    @Test
    public void testPerUserScopedAction() throws Exception {
        try {
            // step 1: (admin) login, set 'Per user in scoped' mode of python interpreter, logout
            InterpreterModeActionsIT interpreterModeActionsIT = new InterpreterModeActionsIT();
            interpreterModeActionsIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            pollingWait(By.xpath("//input[contains(@ng-model, 'searchInterpreter')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("python");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath(("//div[contains(@id, \'python\')]//button[contains(@ng-click, \'valueform.$show();\n" + "                  copyOriginInterpreterSettingProperties(setting.id)')]")));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]/div[2]/div/div/div[1]/span[1]/button"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//li/a[contains(.,'Per User')]"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]/div[2]/div/div/div[1]/span[2]/button"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//li/a[contains(.,'scoped per user')]"));
            JavascriptExecutor jse = ((JavascriptExecutor) (AbstractZeppelinIT.driver));
            jse.executeScript("window.scrollBy(0,250)", "");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//div/form/button[contains(@type, 'submit')]"));
            clickAndWait(By.xpath("//div[@class='modal-dialog']//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"));
            clickAndWait(By.xpath("//a[@class='navbar-brand navbar-title'][contains(@href, '#/')]"));
            interpreterModeActionsIT.logoutUser("admin");
            // step 2: (user1) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user1' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            By locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            WebElement element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            String user1noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user1\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user1"));
            String resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user1");
            // step 3: (user2) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user2' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '2'
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            String user2noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user2\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 4: (user1) login, come back note user1 made, run second paragraph, check result,
            // restart python interpreter in note, check process again, logout
            // paragraph: Check if the result is 'user1' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            runParagraph(2);
            try {
                waitForParagraph(2, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(2, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user1"));
            clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            clickAndWait(By.xpath("//div[@data-ng-repeat='item in interpreterBindings' and contains(., 'python')]//a"));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart python interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserScopedAction");
            boolean invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            locator = By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]");
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user1");
            // step 5: (user2) login, come back note user2 made, restart python interpreter in note, check process, logout
            // System: Check if the number of python interpreter process is '0'
            // System: Check if the number of python process is '0'
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            clickAndWait(By.xpath("//div[@data-ng-repeat='item in interpreterBindings' and contains(., 'python')]//a"));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart python interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserScopedAction");
            invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            locator = By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]");
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("0"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("0"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 6: (user1) login, come back note user1 made, run first paragraph,logout
            // (user2) login, come back note user2 made, run first paragraph, check process, logout
            // System: Check if the number of python process is '2'
            // System: Check if the number of python interpreter process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            waitForParagraph(1, "FINISHED");
            runParagraph(1);
            try {
                waitForParagraph(1, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(1, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            interpreterModeActionsIT.logoutUser("user1");
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            runParagraph(1);
            try {
                waitForParagraph(1, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(1, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 7: (admin) login, restart python interpreter in interpreter tab, check process, logout
            // System: Check if the number of python interpreter process is 0
            // System: Check if the number of python process is 0
            interpreterModeActionsIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            pollingWait(By.xpath("//input[contains(@ng-model, 'searchInterpreter')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("python");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath(("//div[contains(@id, 'python')]" + "//button[contains(@ng-click, 'restartInterpreterSetting(setting.id)')]")));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart this interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserScopedAction");
            invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("0"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("0"));
            interpreterModeActionsIT.logoutUser("admin");
        } catch (Exception e) {
            handleException("Exception in InterpreterModeActionsIT while testPerUserScopedAction ", e);
        }
    }

    @Test
    public void testPerUserIsolatedAction() throws Exception {
        try {
            // step 1: (admin) login, set 'Per user in isolated' mode of python interpreter, logout
            InterpreterModeActionsIT interpreterModeActionsIT = new InterpreterModeActionsIT();
            interpreterModeActionsIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            pollingWait(By.xpath("//input[contains(@ng-model, 'searchInterpreter')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("python");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath(("//div[contains(@id, \'python\')]//button[contains(@ng-click, \'valueform.$show();\n" + "                  copyOriginInterpreterSettingProperties(setting.id)')]")));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]/div[2]/div/div/div[1]/span[1]/button"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//li/a[contains(.,'Per User')]"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]/div[2]/div/div/div[1]/span[2]/button"));
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//li/a[contains(.,'isolated per user')]"));
            JavascriptExecutor jse = ((JavascriptExecutor) (AbstractZeppelinIT.driver));
            jse.executeScript("window.scrollBy(0,250)", "");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath("//div[contains(@id, 'python')]//div/form/button[contains(@type, 'submit')]"));
            clickAndWait(By.xpath("//div[@class='modal-dialog']//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"));
            clickAndWait(By.xpath("//a[@class='navbar-brand navbar-title'][contains(@href, '#/')]"));
            interpreterModeActionsIT.logoutUser("admin");
            // step 2: (user1) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user1' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            By locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            WebElement element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            String user1noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user1\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user1"));
            String resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user1");
            // step 3: (user2) login, create a new note, run two paragraph with 'python', check result, check process, logout
            // paragraph: Check if the result is 'user2' in the second paragraph
            // System: Check if the number of python interpreter process is '2'
            // System: Check if the number of python process is '2'
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath(("//div[contains(@class, 'col-md-4')]/div/h5/a[contains(.,'Create new" + " note')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                createNewNote();
            }
            String user2noteId = AbstractZeppelinIT.driver.getCurrentUrl().substring(((AbstractZeppelinIT.driver.getCurrentUrl().lastIndexOf("/")) + 1));
            waitForParagraph(1, "READY");
            interpreterModeActionsIT.setPythonParagraph(1, "user=\"user2\"");
            waitForParagraph(2, "READY");
            interpreterModeActionsIT.setPythonParagraph(2, "print user");
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("2"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 4: (user1) login, come back note user1 made, run second paragraph, check result,
            // restart python interpreter in note, check process again, logout
            // paragraph: Check if the result is 'user1' in the second paragraph
            // System: Check if the number of python interpreter process is '1'
            // System: Check if the number of python process is '1'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            runParagraph(2);
            try {
                waitForParagraph(2, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(2, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            collector.checkThat("The output field paragraph contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("user1"));
            clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            clickAndWait(By.xpath("//div[@data-ng-repeat='item in interpreterBindings' and contains(., 'python')]//a"));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart python interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserIsolatedAction");
            boolean invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            locator = By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]");
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("1"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("1"));
            interpreterModeActionsIT.logoutUser("user1");
            // step 5: (user2) login, come back note user2 made, restart python interpreter in note, check process, logout
            // System: Check if the number of python interpreter process is '0'
            // System: Check if the number of python process is '0'
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            clickAndWait(By.xpath("//div[@data-ng-repeat='item in interpreterBindings' and contains(., 'python')]//a"));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart python interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserIsolatedAction");
            invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            locator = By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]");
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                clickAndWait(By.xpath("//*[@id='actionbar']//span[contains(@uib-tooltip, 'Interpreter binding')]"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("0"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("0"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 6: (user1) login, come back note user1 made, run first paragraph,logout
            // (user2) login, come back note user2 made, run first paragraph, check process, logout
            // System: Check if the number of python process is '2'
            // System: Check if the number of python interpreter process is '2'
            interpreterModeActionsIT.authenticationUser("user1", "password2");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user1noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            waitForParagraph(1, "FINISHED");
            runParagraph(1);
            try {
                waitForParagraph(1, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(1, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            interpreterModeActionsIT.logoutUser("user1");
            interpreterModeActionsIT.authenticationUser("user2", "password3");
            locator = By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]"));
            element = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.visibilityOfElementLocated(locator));
            if (element.isDisplayed()) {
                pollingWait(By.xpath((("//*[@id='notebook-names']//a[contains(@href, '" + user2noteId) + "')]")), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            }
            runParagraph(1);
            try {
                waitForParagraph(1, "FINISHED");
            } catch (TimeoutException e) {
                waitForParagraph(1, "ERROR");
                collector.checkThat("Exception in InterpreterModeActionsIT while running Python Paragraph", "ERROR", CoreMatchers.equalTo("FINISHED"));
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("2"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("2"));
            interpreterModeActionsIT.logoutUser("user2");
            // step 7: (admin) login, restart python interpreter in interpreter tab, check process, logout
            // System: Check if the number of python interpreter process is 0
            // System: Check if the number of python process is 0
            interpreterModeActionsIT.authenticationUser("admin", "password1");
            pollingWait(By.xpath("//div/button[contains(@class, 'nav-btn dropdown-toggle ng-scope')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).click();
            clickAndWait(By.xpath("//li/a[contains(@href, '#/interpreter')]"));
            pollingWait(By.xpath("//input[contains(@ng-model, 'searchInterpreter')]"), AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).sendKeys("python");
            ZeppelinITUtils.sleep(500, false);
            clickAndWait(By.xpath(("//div[contains(@id, 'python')]" + "//button[contains(@ng-click, 'restartInterpreterSetting(setting.id)')]")));
            clickAndWait(By.xpath(("//div[@class='modal-dialog']" + ("[contains(.,'Do you want to restart this interpreter?')]" + "//div[@class='bootstrap-dialog-footer-buttons']//button[contains(., 'OK')]"))));
            locator = By.xpath("//div[@class='modal-dialog'][contains(.,'Do you want to restart python interpreter?')]");
            InterpreterModeActionsIT.LOG.info("Holding on until if interpreter restart dialog is disappeared or not in testPerUserIsolatedAction");
            invisibilityStatus = new org.openqa.selenium.support.ui.WebDriverWait(AbstractZeppelinIT.driver, AbstractZeppelinIT.MAX_BROWSER_TIMEOUT_SEC).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (invisibilityStatus == false) {
                Assert.assertTrue("interpreter setting dialog visibility status", invisibilityStatus);
            }
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsPython, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python process is", resultProcessNum, CoreMatchers.equalTo("0"));
            resultProcessNum = ((String) (CommandExecutor.executeCommandLocalHost(InterpreterModeActionsIT.cmdPsInterpreter, false, OUTPUT)));
            resultProcessNum = resultProcessNum.trim().replaceAll("\n", "");
            collector.checkThat("The number of python interpreter process is", resultProcessNum, CoreMatchers.equalTo("0"));
            interpreterModeActionsIT.logoutUser("admin");
        } catch (Exception e) {
            handleException("Exception in InterpreterModeActionsIT while testPerUserIsolatedAction ", e);
        }
    }
}

