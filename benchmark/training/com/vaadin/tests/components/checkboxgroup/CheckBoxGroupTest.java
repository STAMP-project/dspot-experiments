package com.vaadin.tests.components.checkboxgroup;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Test for CheckBoxGroup
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroupTest extends MultiBrowserTest {
    @Test
    public void initialLoad_containsCorrectItems() {
        assertItems(20);
    }

    @Test
    public void initialItems_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void disabled_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void disabled_increaseItemCountWithinPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "20");
        assertItems(20);
    }

    @Test
    public void disabled_increaseItemCountBeyondPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        getSelect().selectByText("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));
        getSelect().selectByText("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals("2. Selected: [Item 4, Item 2]", getLogRow(0));
        getSelect().selectByText("Item 4");
        Assert.assertEquals("3. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");
        Assert.assertTrue(getSelect().findElements(By.tagName("input")).stream().allMatch(( element) -> (element.getAttribute("disabled")) != null));
        selectMenuPath("Component", "Listeners", "Selection listener");
        String lastLogRow = getLogRow(0);
        getSelect().selectByText("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        getSelect().selectByText("Item 2");
        // Selection order (most recently selected is last)
        Assert.assertEquals(lastLogRow, getLogRow(0));
        getSelect().selectByText("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");
        getSelect().selectByText("Item 4");
        selectMenuPath("Component", "State", "Enabled");
        getSelect().selectByText("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));
        getSelect().selectByText("Item 2");
        Assert.assertEquals("4. Selected: [Item 5, Item 2]", getLogRow(0));
        getSelect().selectByText("Item 5");
        Assert.assertEquals("5. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void itemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator", "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void nullItemCaptionGenerator() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator", "Null Caption Generator");
        for (String text : getSelect().getOptions()) {
            Assert.assertEquals("", text);
        }
    }

    @Test
    public void itemIconGenerator() {
        selectMenuPath("Component", "Item Generator", "Use Item Icon Generator");
        assertItemSuffices(20);
        List<WebElement> icons = getSelect().findElements(By.cssSelector(".v-select-optiongroup .v-icon"));
        Assert.assertFalse(icons.isEmpty());
        for (int i = 0; i < (icons.size()); i++) {
            Assert.assertEquals(VaadinIcons.values()[(i + 1)].getCodepoint(), icons.get(i).getText().charAt(0));
        }
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("2. Selected: [Item 5]", getLogRow(0));
        assertSelected("Item 5");
        selectMenuPath("Component", "Selection", "Toggle Item 1");
        // Selection order (most recently selected is last)
        Assert.assertEquals("4. Selected: [Item 5, Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1", "Item 5");
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("6. Selected: [Item 1]", getLogRow(0));
        assertSelected("Item 1");
    }

    @Test
    public void testItemDescriptionGenerators() {
        TestBenchElement label;
        selectMenuPath("Component", "Item Description Generator", "Item Description Generator", "Default Description Generator");
        label = ((TestBenchElement) (findElements(By.tagName("label")).get(5)));
        label.showTooltip();
        Assert.assertEquals("Tooltip should contain the same text as caption", label.getText(), getTooltipElement().getText());
        selectMenuPath("Component", "Item Description Generator", "Item Description Generator", "Custom Description Generator");
        label = ((TestBenchElement) (findElements(By.tagName("label")).get(5)));
        label.showTooltip();
        Assert.assertEquals("Tooltip should contain caption + ' Description'", ((label.getText()) + " Description"), getTooltipElement().getText());
    }

    @Test
    public void testDisabled() {
        List<String> optionsCssClasses = getSelect().getOptionElements().stream().map(( element) -> element.getAttribute("class")).collect(Collectors.toList());
        for (int i = 0; i < (optionsCssClasses.size()); i++) {
            String cssClassList = optionsCssClasses.get(i);
            if (i == 10) {
                Assert.assertTrue("10th item should be disabled", cssClassList.toLowerCase(Locale.ROOT).contains("disabled"));
            } else {
                Assert.assertFalse("Only 10th item should be disabled", cssClassList.toLowerCase(Locale.ROOT).contains("disabled"));
            }
        }
    }

    @Test
    public void testIconUrl() {
        List<String> optionsIcons = new ArrayList<>();
        for (WebElement option : getSelect().getOptionElements()) {
            List<WebElement> images = option.findElements(By.tagName("img"));
            if (!(images.isEmpty())) {
                optionsIcons.add(images.get(0).getAttribute("src"));
            } else {
                optionsIcons.add(null);
            }
        }
        for (int i = 0; i < (optionsIcons.size()); i++) {
            String icon = optionsIcons.get(i);
            if (i == 2) {
                Assert.assertNotNull("2nd item should have icon", icon);
            } else {
                Assert.assertNull("Only 2nd item should have icon", icon);
            }
        }
    }

    // #9258
    @Test
    public void disabled_correctClassNamesApplied() {
        openTestURL("theme=valo");
        selectMenuPath("Component", "State", "Enabled");
        List<WebElement> options = getSelect().findElements(By.tagName("span"));
        Assert.assertTrue(((options.size()) > 0));
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( className) -> verifyCheckboxDisabledClassNames(className, true));
        selectMenuPath("Component", "State", "Enabled");
        options = getSelect().findElements(By.tagName("span"));
        Assert.assertTrue(((options.size()) > 0));
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(options.remove(10).getAttribute("className"), true);
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( className) -> verifyCheckboxDisabledClassNames(className, false));
    }

    // #9258
    @Test
    public void itemDisabledWithEnabledProvider_correctClassNamesApplied() {
        openTestURL("theme=valo");
        List<WebElement> options = getSelect().findElements(By.tagName("span"));
        Assert.assertTrue(((options.size()) > 0));
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(options.remove(10).getAttribute("className"), true);
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( cs) -> verifyCheckboxDisabledClassNames(cs, false));
        selectMenuPath("Component", "Item Enabled Provider", "Item Enabled Provider", "Disable Item 0");
        options = getSelect().findElements(By.tagName("span"));
        String className = options.get(0).getAttribute("className");
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(className, true);
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(options.remove(10).getAttribute("className"), false);
        selectMenuPath("Component", "Item Enabled Provider", "Item Enabled Provider", "Disable Item 3");
        className = getSelect().findElements(By.tagName("span")).get(0).getAttribute("className");
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(className, false);
        className = getSelect().findElements(By.tagName("span")).get(3).getAttribute("className");
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(className, true);
        selectMenuPath("Component", "State", "Enabled");
        options = getSelect().findElements(By.tagName("span"));
        Assert.assertTrue(((options.size()) > 0));
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( cs) -> verifyCheckboxDisabledClassNames(cs, true));
        selectMenuPath("Component", "Item Enabled Provider", "Item Enabled Provider", "Disable Item 5");
        options = getSelect().findElements(By.tagName("span"));
        Assert.assertTrue(((options.size()) > 0));
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( cs) -> verifyCheckboxDisabledClassNames(cs, true));
        selectMenuPath("Component", "State", "Enabled");
        options = getSelect().findElements(By.tagName("span"));
        className = options.remove(5).getAttribute("className");
        Assert.assertTrue(((options.size()) > 0));
        options.stream().map(( element) -> element.getAttribute("className")).forEach(( cs) -> verifyCheckboxDisabledClassNames(cs, false));
        CheckBoxGroupTest.verifyCheckboxDisabledClassNames(className, true);
    }

    // #3387
    @Test
    public void shouldApplySelectedClassToSelectedItems() {
        openTestURL("theme=valo");
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        String className = getSelect().getOptionElements().get(5).getAttribute("className");
        Assert.assertTrue(("No v-select-option-selected class, was " + className), className.contains("v-select-option-selected"));
        selectMenuPath("Component", "Selection", "Toggle Item 5");
        className = getSelect().getOptionElements().get(5).getAttribute("className");
        Assert.assertFalse(("Extra v-select-option-selected class, was " + className), className.contains("v-select-option-selected"));
    }
}

