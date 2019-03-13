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
import Keys.RETURN;
import StringUtils.EMPTY;
import org.apache.zeppelin.AbstractZeppelinIT;
import org.apache.zeppelin.ZeppelinITUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParagraphActionsIT extends AbstractZeppelinIT {
    private static final Logger LOG = LoggerFactory.getLogger(ParagraphActionsIT.class);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void testCreateNewButton() throws Exception {
        try {
            createNewNote();
            Actions action = new Actions(AbstractZeppelinIT.driver);
            waitForParagraph(1, "READY");
            Integer oldNosOfParas = AbstractZeppelinIT.driver.findElements(By.xpath("//div[@ng-controller=\"ParagraphCtrl\"]")).size();
            collector.checkThat("Before Insert New : the number of  paragraph ", oldNosOfParas, CoreMatchers.equalTo(1));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click=\"insertNew(\'below\')\"]"))).click();
            waitForParagraph(2, "READY");
            Integer newNosOfParas = AbstractZeppelinIT.driver.findElements(By.xpath("//div[@ng-controller=\"ParagraphCtrl\"]")).size();
            collector.checkThat("After Insert New (using Insert New button) :  number of  paragraph", (oldNosOfParas + 1), CoreMatchers.equalTo(newNosOfParas));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click='removeParagraph(paragraph)']"))).click();
            ZeppelinITUtils.sleep(1000, false);
            AbstractZeppelinIT.driver.findElement(By.xpath(("//div[@class='modal-dialog'][contains(.,'delete this paragraph')]" + "//div[@class='modal-footer']//button[contains(.,'OK')]"))).click();
            ZeppelinITUtils.sleep(1000, false);
            setTextOfParagraph(1, " original paragraph ");
            WebElement newPara = AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class,'new-paragraph')][1]")));
            action.moveToElement(newPara).click().build().perform();
            ZeppelinITUtils.sleep(1000, false);
            waitForParagraph(1, "READY");
            collector.checkThat("Paragraph is created above", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo(EMPTY));
            setTextOfParagraph(1, " this is above ");
            newPara = AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class,'new-paragraph')][2]")));
            action.moveToElement(newPara).click().build().perform();
            waitForParagraph(3, "READY");
            collector.checkThat("Paragraph is created below", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(3)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo(EMPTY));
            setTextOfParagraph(3, " this is below ");
            collector.checkThat("The output field of paragraph1 contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo(" this is above "));
            collector.checkThat("The output field paragraph2 contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo(" original paragraph "));
            collector.checkThat("The output field paragraph3 contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(3)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo(" this is below "));
            collector.checkThat("The current number of paragraphs after creating  paragraph above and below", AbstractZeppelinIT.driver.findElements(By.xpath("//div[@ng-controller=\"ParagraphCtrl\"]")).size(), CoreMatchers.equalTo(3));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testCreateNewButton ", e);
        }
    }

    @Test
    public void testRemoveButton() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click=\"insertNew(\'below\')\"]"))).click();
            waitForParagraph(2, "READY");
            Integer oldNosOfParas = AbstractZeppelinIT.driver.findElements(By.xpath("//div[@ng-controller=\"ParagraphCtrl\"]")).size();
            collector.checkThat("Before Remove : Number of paragraphs are ", oldNosOfParas, CoreMatchers.equalTo(2));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click='removeParagraph(paragraph)']")));
            clickAndWait(By.xpath(("//div[@class='modal-dialog'][contains(.,'delete this paragraph')" + "]//div[@class='modal-footer']//button[contains(.,'OK')]")));
            Integer newNosOfParas = AbstractZeppelinIT.driver.findElements(By.xpath("//div[@ng-controller=\"ParagraphCtrl\"]")).size();
            collector.checkThat("After Remove : Number of paragraphs are", newNosOfParas, CoreMatchers.equalTo((oldNosOfParas - 1)));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testRemoveButton ", e);
        }
    }

    @Test
    public void testMoveUpAndDown() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            setTextOfParagraph(1, "1");
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click=\"insertNew(\'below\')\"]"))).click();
            waitForParagraph(2, "READY");
            setTextOfParagraph(2, "2");
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("1"));
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("2"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click='moveDown(paragraph)']")));
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("2"));
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("1"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(2)) + "//ul/li/a[@ng-click='moveUp(paragraph)']")));
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("1"));
            collector.checkThat("The paragraph1 value contains", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'editor')]"))).getText(), CoreMatchers.equalTo("2"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testMoveUpAndDown ", e);
        }
    }

    @Test
    public void testDisableParagraphRunButton() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            setTextOfParagraph(1, "println (\"abcd\")");
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click='toggleEnableDisable(paragraph)']")));
            collector.checkThat("The play button class was ", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-control-play shortcut-icon']"))).isDisplayed(), CoreMatchers.equalTo(false));
            AbstractZeppelinIT.driver.findElement(By.xpath(".//*[@id='main']//button[contains(@ng-click, 'runAllParagraphs')]")).sendKeys(ENTER);
            ZeppelinITUtils.sleep(1000, false);
            AbstractZeppelinIT.driver.findElement(By.xpath(("//div[@class='modal-dialog'][contains(.,'Run all paragraphs?')]" + "//div[@class='modal-footer']//button[contains(.,'OK')]"))).click();
            ZeppelinITUtils.sleep(2000, false);
            collector.checkThat("Paragraph status is ", getParagraphStatus(1), CoreMatchers.equalTo("READY"));
            AbstractZeppelinIT.driver.navigate().refresh();
            ZeppelinITUtils.sleep(3000, false);
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testDisableParagraphRunButton ", e);
        }
    }

    @Test
    public void testClearOutputButton() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            String xpathToOutputField = (getParagraphXPath(1)) + "//div[contains(@id,\"_text\")]";
            setTextOfParagraph(1, "println (\"abcd\")");
            collector.checkThat("Before Run Output field contains ", AbstractZeppelinIT.driver.findElements(By.xpath(xpathToOutputField)).size(), CoreMatchers.equalTo(0));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("After Run Output field contains  ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToOutputField)).getText(), CoreMatchers.equalTo("abcd"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//ul/li/a[@ng-click='clearParagraphOutput(paragraph)']")));
            collector.checkThat("After Clear  Output field contains ", AbstractZeppelinIT.driver.findElements(By.xpath(xpathToOutputField)).size(), CoreMatchers.equalTo(0));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testClearOutputButton ", e);
        }
    }

    @Test
    public void testWidth() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            collector.checkThat("Default Width is 12 ", AbstractZeppelinIT.driver.findElement(By.xpath("//div[contains(@class,'col-md-12')]")).isDisplayed(), CoreMatchers.equalTo(true));
            for (Integer newWidth = 1; newWidth <= 11; newWidth++) {
                clickAndWait(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']")));
                String visibleText = newWidth.toString();
                selectByVisibleText(visibleText);
                collector.checkThat(("New Width is : " + newWidth), AbstractZeppelinIT.driver.findElement(By.xpath((("//div[contains(@class,'col-md-" + newWidth) + "')]"))).isDisplayed(), CoreMatchers.equalTo(true));
            }
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testWidth ", e);
        }
    }

    @Test
    public void testFontSize() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            Float height = Float.valueOf(AbstractZeppelinIT.driver.findElement(By.xpath("//div[contains(@class,'ace_content')]")).getCssValue("height").replace("px", ""));
            for (Integer newFontSize = 10; newFontSize <= 20; newFontSize++) {
                clickAndWait(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']")));
                String visibleText = newFontSize.toString();
                selectByVisibleText(visibleText);
                Float newHeight = Float.valueOf(AbstractZeppelinIT.driver.findElement(By.xpath("//div[contains(@class,'ace_content')]")).getCssValue("height").replace("px", ""));
                collector.checkThat(("New Font size is : " + newFontSize), (newHeight > height), CoreMatchers.equalTo(true));
                height = newHeight;
            }
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testFontSize ", e);
        }
    }

    @Test
    public void testTitleButton() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            String xpathToTitle = (getParagraphXPath(1)) + "//div[contains(@class, 'title')]/div";
            String xpathToSettingIcon = (getParagraphXPath(1)) + "//span[@class='icon-settings']";
            String xpathToShowTitle = (getParagraphXPath(1)) + "//ul/li/a[@ng-show='!paragraph.config.title']";
            String xpathToHideTitle = (getParagraphXPath(1)) + "//ul/li/a[@ng-show='paragraph.config.title']";
            ZeppelinITUtils.turnOffImplicitWaits(AbstractZeppelinIT.driver);
            Integer titleElems = AbstractZeppelinIT.driver.findElements(By.xpath(xpathToTitle)).size();
            collector.checkThat("Before Show Title : The title doesn't exist", titleElems, CoreMatchers.equalTo(0));
            ZeppelinITUtils.turnOnImplicitWaits(AbstractZeppelinIT.driver);
            clickAndWait(By.xpath(xpathToSettingIcon));
            collector.checkThat("Before Show Title : The title option in option panel of paragraph is labeled as", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToShowTitle)).getText(), CoreMatchers.allOf(CoreMatchers.endsWith("Show title"), CoreMatchers.containsString("Ctrl+"), CoreMatchers.anyOf(CoreMatchers.containsString("Option"), CoreMatchers.containsString("Alt")), CoreMatchers.containsString("+T")));
            clickAndWait(By.xpath(xpathToShowTitle));
            collector.checkThat("After Show Title : The title field contains", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToTitle)).getText(), CoreMatchers.equalTo("Untitled"));
            clickAndWait(By.xpath(xpathToSettingIcon));
            collector.checkThat("After Show Title : The title option in option panel of paragraph is labeled as", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToHideTitle)).getText(), CoreMatchers.allOf(CoreMatchers.endsWith("Hide title"), CoreMatchers.containsString("Ctrl+"), CoreMatchers.anyOf(CoreMatchers.containsString("Option"), CoreMatchers.containsString("Alt")), CoreMatchers.containsString("+T")));
            clickAndWait(By.xpath(xpathToHideTitle));
            ZeppelinITUtils.turnOffImplicitWaits(AbstractZeppelinIT.driver);
            titleElems = AbstractZeppelinIT.driver.findElements(By.xpath(xpathToTitle)).size();
            collector.checkThat("After Hide Title : The title field is hidden", titleElems, CoreMatchers.equalTo(0));
            ZeppelinITUtils.turnOnImplicitWaits(AbstractZeppelinIT.driver);
            AbstractZeppelinIT.driver.findElement(By.xpath(xpathToSettingIcon)).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(xpathToShowTitle)).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'title')]"))).click();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//input"))).sendKeys(("NEW TITLE" + (Keys.ENTER)));
            ZeppelinITUtils.sleep(500, false);
            collector.checkThat("After Editing the Title : The title field contains ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToTitle)).getText(), CoreMatchers.equalTo("NEW TITLE"));
            AbstractZeppelinIT.driver.navigate().refresh();
            ZeppelinITUtils.sleep(1000, false);
            collector.checkThat("After Page Refresh : The title field contains ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToTitle)).getText(), CoreMatchers.equalTo("NEW TITLE"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testTitleButton  ", e);
        }
    }

    @Test
    public void testShowAndHideLineNumbers() throws Exception {
        try {
            createNewNote();
            waitForParagraph(1, "READY");
            String xpathToLineNumberField = (getParagraphXPath(1)) + "//div[contains(@class, 'ace_gutter-layer')]";
            String xpathToShowLineNumberButton = (getParagraphXPath(1)) + "//ul/li/a[@ng-click='showLineNumbers(paragraph)']";
            String xpathToHideLineNumberButton = (getParagraphXPath(1)) + "//ul/li/a[@ng-click='hideLineNumbers(paragraph)']";
            collector.checkThat("Before \"Show line number\" the Line Number is Enabled ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToLineNumberField)).isDisplayed(), CoreMatchers.equalTo(false));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            collector.checkThat("Before \"Show line number\" The option panel in paragraph has button labeled ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToShowLineNumberButton)).getText(), CoreMatchers.allOf(CoreMatchers.endsWith("Show line numbers"), CoreMatchers.containsString("Ctrl+"), CoreMatchers.anyOf(CoreMatchers.containsString("Option"), CoreMatchers.containsString("Alt")), CoreMatchers.containsString("+M")));
            clickAndWait(By.xpath(xpathToShowLineNumberButton));
            collector.checkThat("After \"Show line number\" the Line Number is Enabled ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToLineNumberField)).isDisplayed(), CoreMatchers.equalTo(true));
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']")));
            collector.checkThat("After \"Show line number\" The option panel in paragraph has button labeled ", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToHideLineNumberButton)).getText(), CoreMatchers.allOf(CoreMatchers.endsWith("Hide line numbers"), CoreMatchers.containsString("Ctrl+"), CoreMatchers.anyOf(CoreMatchers.containsString("Option"), CoreMatchers.containsString("Alt")), CoreMatchers.containsString("+M")));
            clickAndWait(By.xpath(xpathToHideLineNumberButton));
            collector.checkThat("After \"Hide line number\" the Line Number is Enabled", AbstractZeppelinIT.driver.findElement(By.xpath(xpathToLineNumberField)).isDisplayed(), CoreMatchers.equalTo(false));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testShowAndHideLineNumbers ", e);
        }
    }

    @Test
    public void testSingleDynamicFormTextInput() throws Exception {
        try {
            createNewNote();
            setTextOfParagraph(1, "%spark println(\"Hello \"+z.textbox(\"name\", \"world\")) ");
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Output text is equal to value specified initially", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello world"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//input"))).clear();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//input"))).sendKeys("Zeppelin");
            collector.checkThat("After new data in text input form, output should not be changed", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello world"));
            runParagraph(1);
            ZeppelinITUtils.sleep(1000, false);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Only after running the paragraph, we can see the newly updated output", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello Zeppelin"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testSingleDynamicFormTextInput  ", e);
        }
    }

    @Test
    public void testSingleDynamicFormCheckboxForm() throws Exception {
        try {
            createNewNote();
            setTextOfParagraph(1, ("%spark val options = Seq((\"han\",\"Han\"), (\"leia\",\"Leia\"), " + "(\"luke\",\"Luke\")); println(\"Greetings \"+z.checkbox(\"skywalkers\",options).mkString(\" and \"))"));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Output text should display all of the options included in check boxes", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings han and leia and luke"));
            WebElement firstCheckbox = AbstractZeppelinIT.driver.findElement(By.xpath((("(" + (getParagraphXPath(1))) + "//input[@type='checkbox'])[1]")));
            firstCheckbox.click();
            collector.checkThat("After unchecking one of the boxes, we can see the newly updated output without the option we unchecked", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings leia and luke"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//span[@class='icon-settings']"))).click();
            clickAndWait(By.xpath(((getParagraphXPath(1)) + "//ul/li/form/input[contains(@ng-checked, 'true')]")));
            WebElement secondCheckbox = AbstractZeppelinIT.driver.findElement(By.xpath((("(" + (getParagraphXPath(1))) + "//input[@type='checkbox'])[2]")));
            secondCheckbox.click();
            collector.checkThat("After 'Run on selection change' checkbox is unchecked, the paragraph should not run if check box state is modified", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings leia and luke"));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testSingleDynamicFormCheckboxForm  ", e);
        }
    }

    @Test
    public void testNoteDynamicFormTextInput() throws Exception {
        try {
            createNewNote();
            setTextOfParagraph(1, "%spark println(\"Hello \"+z.noteTextbox(\"name\", \"world\")) ");
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Output text is equal to value specified initially", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello world"));
            AbstractZeppelinIT.driver.findElement(By.xpath(((getNoteFormsXPath()) + "//input"))).clear();
            AbstractZeppelinIT.driver.findElement(By.xpath(((getNoteFormsXPath()) + "//input"))).sendKeys("Zeppelin");
            AbstractZeppelinIT.driver.findElement(By.xpath(((getNoteFormsXPath()) + "//input"))).sendKeys(RETURN);
            collector.checkThat("After new data in text input form, output should not be changed", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello world"));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Only after running the paragraph, we can see the newly updated output", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello Zeppelin"));
            setTextOfParagraph(2, "%spark println(\"Hello \"+z.noteTextbox(\"name\", \"world\")) ");
            runParagraph(2);
            waitForParagraph(2, "FINISHED");
            collector.checkThat("Running the another paragraph with same form, we can see value from note form", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("Hello Zeppelin"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testNoteDynamicFormTextInput  ", e);
        }
    }

    @Test
    public void testDynamicNoteFormCheckbox() throws Exception {
        try {
            createNewNote();
            setTextOfParagraph(1, ("%spark val options = Seq((\"han\",\"Han\"), (\"leia\",\"Leia\"), " + "(\"luke\",\"Luke\")); println(\"Greetings \"+z.noteCheckbox(\"skywalkers\",options).mkString(\" and \"))"));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("Output text should display all of the options included in check boxes", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings han and leia and luke"));
            WebElement firstCheckbox = AbstractZeppelinIT.driver.findElement(By.xpath((("(" + (getNoteFormsXPath())) + "//input[@type='checkbox'])[1]")));
            firstCheckbox.click();
            collector.checkThat("After unchecking one of the boxes, output should not be changed", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings han and leia and luke"));
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("After run paragraph again, we can see the newly updated output", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings leia and luke"));
            setTextOfParagraph(2, ("%spark val options = Seq((\"han\",\"Han\"), (\"leia\",\"Leia\"), " + "(\"luke\",\"Luke\")); println(\"Greetings \"+z.noteCheckbox(\"skywalkers\",options).mkString(\" and \"))"));
            runParagraph(2);
            waitForParagraph(2, "FINISHED");
            collector.checkThat("Running the another paragraph with same form, we can see value from note form", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(2)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.containsString("Greetings leia and luke"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testDynamicNoteFormCheckbox  ", e);
        }
    }

    @Test
    public void testWithNoteAndParagraphDynamicFormTextInput() throws Exception {
        try {
            createNewNote();
            setTextOfParagraph(1, "%spark println(z.noteTextbox(\"name\", \"note\") + \" \" + z.textbox(\"name\", \"paragraph\")) ");
            runParagraph(1);
            waitForParagraph(1, "FINISHED");
            collector.checkThat("After run paragraph, we can see computed output from two forms", AbstractZeppelinIT.driver.findElement(By.xpath(((getParagraphXPath(1)) + "//div[contains(@class, 'text plainTextContent')]"))).getText(), CoreMatchers.equalTo("note paragraph"));
            deleteTestNotebook(AbstractZeppelinIT.driver);
        } catch (Exception e) {
            handleException("Exception in ParagraphActionsIT while testWithNoteAndParagraphDynamicFormTextInput  ", e);
        }
    }
}

