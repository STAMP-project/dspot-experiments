package org.jabref.gui.util;


import TooltipTextUtil.TextType;
import TooltipTextUtil.TextType.BOLD;
import TooltipTextUtil.TextType.ITALIC;
import TooltipTextUtil.TextType.MONOSPACED;
import TooltipTextUtil.TextType.NORMAL;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.Text;
import org.jabref.gui.search.TextFlowEqualityHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TooltipTextUtilTest {
    @Test
    public void testCreateText() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, NORMAL);
        Assertions.assertEquals("Regular", text.getFont().getStyle());
        Assertions.assertEquals(testText, text.getText());
    }

    @Test
    public void testCreateTextBold() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, BOLD);
        Assertions.assertEquals("tooltip-text-bold", text.getStyleClass().toString());
        Assertions.assertEquals(testText, text.getText());
    }

    @Test
    public void testCreateTextItalic() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, ITALIC);
        Assertions.assertEquals("tooltip-text-italic", text.getStyleClass().toString());
        Assertions.assertEquals(testText, text.getText());
    }

    @Test
    public void testCreateTextMonospaced() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, MONOSPACED);
        Assertions.assertEquals("tooltip-text-monospaced", text.getStyleClass().toString());
        Assertions.assertEquals(testText, text.getText());
    }

    @Test
    public void testTextToHTMLStringBold() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, BOLD);
        String htmlString = TooltipTextUtil.textToHTMLString(text);
        Assertions.assertEquals((("<b>" + testText) + "</b>"), htmlString);
    }

    @Test
    public void testTextToHTMLStringItalic() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, ITALIC);
        String htmlString = TooltipTextUtil.textToHTMLString(text);
        Assertions.assertEquals((("<i>" + testText) + "</i>"), htmlString);
    }

    @Test
    public void testTextToHTMLStringMonospaced() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, MONOSPACED);
        String htmlString = TooltipTextUtil.textToHTMLString(text);
        Assertions.assertEquals((("<kbd>" + testText) + "</kbd>"), htmlString);
    }

    @Test
    public void testTextToHTMLStringMonospacedBold() {
        String testText = "this is a test text";
        Text text = TooltipTextUtil.createText(testText, MONOSPACED);
        text.getStyleClass().add("tooltip-text-bold");
        String htmlString = TooltipTextUtil.textToHTMLString(text);
        Assertions.assertEquals((("<b><kbd>" + testText) + "</kbd></b>"), htmlString);
    }

    @Test
    public void testTextToHTMLStringWithLinebreaks() {
        String testText = "this\nis a\ntest text";
        Text text = TooltipTextUtil.createText(testText, NORMAL);
        String htmlString = TooltipTextUtil.textToHTMLString(text);
        Assertions.assertEquals("this<br>is a<br>test text", htmlString);
    }

    @Test
    public void testFormatToTextsNoReplacements() {
        List<Text> expectedTextList = new ArrayList<>();
        expectedTextList.add(TooltipTextUtil.createText("This search contains entries in which any field contains the regular expression "));
        String test = "This search contains entries in which any field contains the regular expression ";
        List<Text> textList = TooltipTextUtil.formatToTexts(test);
        Assertions.assertTrue(TextFlowEqualityHelper.checkIfTextsEqualsExpectedTexts(expectedTextList, textList));
    }

    @Test
    public void testFormatToTextsEnd() {
        List<Text> expectedTextList = new ArrayList<>();
        expectedTextList.add(TooltipTextUtil.createText("This search contains entries in which any field contains the regular expression "));
        expectedTextList.add(TooltipTextUtil.createText("replacing text", BOLD));
        String test = "This search contains entries in which any field contains the regular expression <b>%0</b>";
        List<Text> textList = TooltipTextUtil.formatToTexts(test, new TooltipTextUtil.TextReplacement("<b>%0</b>", "replacing text", TextType.BOLD));
        Assertions.assertTrue(TextFlowEqualityHelper.checkIfTextsEqualsExpectedTexts(expectedTextList, textList));
    }

    @Test
    public void testFormatToTextsBegin() {
        List<Text> expectedTextList = new ArrayList<>();
        expectedTextList.add(TooltipTextUtil.createText("replacing text", BOLD));
        expectedTextList.add(TooltipTextUtil.createText(" This search contains entries in which any field contains the regular expression"));
        String test = "<b>%0</b> This search contains entries in which any field contains the regular expression";
        List<Text> textList = TooltipTextUtil.formatToTexts(test, new TooltipTextUtil.TextReplacement("<b>%0</b>", "replacing text", TextType.BOLD));
        Assertions.assertTrue(TextFlowEqualityHelper.checkIfTextsEqualsExpectedTexts(expectedTextList, textList));
    }

    @Test
    public void testFormatToTextsMiddle() {
        List<Text> expectedTextList = new ArrayList<>();
        expectedTextList.add(TooltipTextUtil.createText("This search contains entries "));
        expectedTextList.add(TooltipTextUtil.createText("replacing text", BOLD));
        expectedTextList.add(TooltipTextUtil.createText(" in which any field contains the regular expression"));
        String test = "This search contains entries <b>%0</b> in which any field contains the regular expression";
        List<Text> textList = TooltipTextUtil.formatToTexts(test, new TooltipTextUtil.TextReplacement("<b>%0</b>", "replacing text", TextType.BOLD));
        Assertions.assertTrue(TextFlowEqualityHelper.checkIfTextsEqualsExpectedTexts(expectedTextList, textList));
    }
}

