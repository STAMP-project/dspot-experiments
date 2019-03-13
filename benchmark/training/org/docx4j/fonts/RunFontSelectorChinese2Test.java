package org.docx4j.fonts;


import java.io.File;
import org.docx4j.XmlUtils;
import org.docx4j.dml.Theme;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.ThemePart;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.Document;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Text;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;


public class RunFontSelectorChinese2Test {
    protected static Logger log = LoggerFactory.getLogger(RunFontSelector.class);// same logger


    static String[] expectedFont = new String[]{ "SimSun", "SimSun", "SimSun", "SimSun", "SimSun", "SimSun", "Times New Roman", "SimSun", "Times New Roman" };

    @Test
    public void testFont() throws Exception {
        if (((System.getProperty("os.name")) != null) && (System.getProperty("os.name").toLowerCase().startsWith("Windows"))) {
            // OK, assume fonts present
        } else {
            RunFontSelectorChinese2Test.log.info("Skipping RunFontSelector test, since required fonts likely missing (non-Windows OS)");
            return;
        }
        boolean save = false;
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        // Styles part
        StyleDefinitionsPart sdp = mdp.getStyleDefinitionsPart();
        Styles styles = ((Styles) (XmlUtils.unmarshalString(RunFontSelectorChinese2Test.stylesXML)));
        sdp.setJaxbElement(styles);
        // Theme part
        ThemePart themePart = new ThemePart();
        Theme theme = ((Theme) (XmlUtils.unmarshalString(themeXML)));
        themePart.setJaxbElement(theme);
        mdp.addTargetPart(themePart);
        // Settings
        DocumentSettingsPart dsp = new DocumentSettingsPart();
        dsp.setJaxbElement(createSettings());
        mdp.addTargetPart(dsp);
        Document document = ((Document) (XmlUtils.unmarshalString(documentXML)));
        wordMLPackage.getMainDocumentPart().setJaxbElement(document);
        if (save) {
            wordMLPackage.save(new File(((System.getProperty("user.dir")) + "/OUT_RunFontSelectorTests.docx")));
        }
        RunFontSelector rfs = RunFontSelectorChinese2Test.createRunFontSelector(wordMLPackage);
        // For each w:p, test w:r/w:t
        for (int i = 0; i < (document.getContent().size()); i++) {
            // System.out.println(i);
            P p = ((P) (document.getContent().get(i)));
            PPr pPr = p.getPPr();
            RPr rPr = getRPr();
            Text wmlText = ((Text) (XmlUtils.unwrap(getContent().get(0))));
            RunFontSelectorChinese2Test.log.debug(wmlText.getValue());
            Object result = rfs.fontSelector(pPr, rPr, wmlText);
            // System.out.println(result.getClass().getName());
            DocumentFragment df = ((DocumentFragment) (result));
            RunFontSelectorChinese2Test.log.debug(XmlUtils.w3CDomNodeToString(df));
            Element foInline = ((Element) (df.getFirstChild()));
            // System.out.println(foInline.getAttribute("font-family"));
            Assert.assertEquals(("iteration " + i), foInline.getAttribute("font-family"), RunFontSelectorChinese2Test.expectedFont[i]);
        }
    }

    String documentXML = "<w:document mc:Ignorable=\"w14 wp14\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<w:body>" + "<w:p>") + "<w:pPr>") + "<w:ind w:firstLineChars=\"0\"/>") + "</w:pPr>") + "<w:r>") + "<w:rPr>") + "<w:rFonts w:hint=\"eastAsia\"/>") + "</w:rPr>") + "<w:t>??</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:pPr>") + "<w:ind w:firstLineChars=\"0\"/>") + "</w:pPr>") + "<w:r>") + "<w:rPr>") + "<w:rFonts w:hint=\"eastAsia\"/>") + "</w:rPr>") + "<w:t>??</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:pPr>") + "<w:rPr>") + "<w:shd w:color=\"auto\" w:fill=\"FFFFFF\" w:val=\"clear\"/>") + "</w:rPr>") + "</w:pPr>") + "<w:r>") + "<w:rPr>") + "<w:shd w:color=\"auto\" w:fill=\"FFFFFF\" w:val=\"clear\"/>") + "</w:rPr>") + "<w:t>???</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:pPr>") + "<w:rPr>") + "<w:shd w:color=\"auto\" w:fill=\"FFFFFF\" w:val=\"clear\"/>") + "</w:rPr>") + "</w:pPr>") + "<w:r>") + "<w:t>???</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:r>") + "<w:rPr>") + "<w:shd w:color=\"auto\" w:fill=\"FFFFFF\" w:val=\"clear\"/>") + "</w:rPr>") + "<w:t>???</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:r>") + "<w:t>???</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:r>") + "<w:t>?</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:r>") + "<w:rPr>") + "<w:rFonts w:ascii=\"SimSun\" w:hAnsi=\"SimSun\" w:hint=\"eastAsia\"/>") + "</w:rPr>") + "<w:t>?</w:t>") + "</w:r>") + "</w:p>") + "<w:p>") + "<w:r>") + "<w:rPr>") + "<w:rFonts w:hint=\"eastAsia\"/>") + "</w:rPr>") + "<w:t>-</w:t>") + "</w:r>") + "</w:p>") + "</w:body>") + "</w:document>");

    static String stylesXML = "<w:styles mc:Ignorable=\"w14\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">" + (((((((((((((((((((((((((((("<w:docDefaults>" + "<w:rPrDefault>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Calibri\" w:cs=\"Times New Roman\" w:eastAsia=\"SimSun\" w:hAnsi=\"Calibri\"/>") + "<w:lang w:bidi=\"ar-SA\" w:eastAsia=\"zh-CN\" w:val=\"en-US\"/>") + "</w:rPr>") + "</w:rPrDefault>") + "<w:pPrDefault/>") + "</w:docDefaults>") + "<w:style w:default=\"1\" w:styleId=\"Normal\" w:type=\"paragraph\">") + "<w:name w:val=\"Normal\"/>") + "<w:qFormat/>") + "<w:pPr>") + "<w:jc w:val=\"both\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\"/>") + "<w:kern w:val=\"2\"/>") + "<w:sz w:val=\"24\"/>") + "<w:szCs w:val=\"22\"/>") + "</w:rPr>") + "</w:style>") + "<w:style w:default=\"1\" w:styleId=\"DefaultParagraphFont\" w:type=\"character\">") + "<w:name w:val=\"Default Paragraph Font\"/>") + "<w:uiPriority w:val=\"1\"/>") + "<w:semiHidden/>") + "<w:unhideWhenUsed/>") + "</w:style>") + "</w:styles>");

    String themeXML = "<a:theme name=\"Office \u4e3b\u9898\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" xmlns:thm15=\"http://schemas.microsoft.com/office/thememl/2012/main\">" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<a:themeElements>" + "<a:clrScheme name=\"Office\">") + "<a:dk1>") + "<a:sysClr lastClr=\"000000\" val=\"windowText\"/>") + "</a:dk1>") + "<a:lt1>") + "<a:sysClr lastClr=\"FFFFFF\" val=\"window\"/>") + "</a:lt1>") + "<a:dk2>") + "<a:srgbClr val=\"44546A\"/>") + "</a:dk2>") + "<a:lt2>") + "<a:srgbClr val=\"E7E6E6\"/>") + "</a:lt2>") + "<a:accent1>") + "<a:srgbClr val=\"5B9BD5\"/>") + "</a:accent1>") + "<a:accent2>") + "<a:srgbClr val=\"ED7D31\"/>") + "</a:accent2>") + "<a:accent3>") + "<a:srgbClr val=\"A5A5A5\"/>") + "</a:accent3>") + "<a:accent4>") + "<a:srgbClr val=\"FFC000\"/>") + "</a:accent4>") + "<a:accent5>") + "<a:srgbClr val=\"4472C4\"/>") + "</a:accent5>") + "<a:accent6>") + "<a:srgbClr val=\"70AD47\"/>") + "</a:accent6>") + "<a:hlink>") + "<a:srgbClr val=\"0563C1\"/>") + "</a:hlink>") + "<a:folHlink>") + "<a:srgbClr val=\"954F72\"/>") + "</a:folHlink>") + "</a:clrScheme>") + "<a:fontScheme name=\"Office\">") + "<a:majorFont>") + "<a:latin panose=\"020F0302020204030204\" typeface=\"Calibri Light\"/>") + "<a:ea typeface=\"\"/>") + "<a:cs typeface=\"\"/>") + "<a:font script=\"Jpan\" typeface=\"\uff2d\uff33 \u30b4\u30b7\u30c3\u30af\"/>") + "<a:font script=\"Hang\" typeface=\"\ub9d1\uc740 \uace0\ub515\"/>") + "<a:font script=\"Hans\" typeface=\"\u5b8b\u4f53\"/>") + "<a:font script=\"Hant\" typeface=\"\u65b0\u7d30\u660e\u9ad4\"/>") + "<a:font script=\"Arab\" typeface=\"Times New Roman\"/>") + "<a:font script=\"Hebr\" typeface=\"Times New Roman\"/>") + "<a:font script=\"Thai\" typeface=\"Angsana New\"/>") + "<a:font script=\"Ethi\" typeface=\"Nyala\"/>") + "<a:font script=\"Beng\" typeface=\"Vrinda\"/>") + "<a:font script=\"Gujr\" typeface=\"Shruti\"/>") + "<a:font script=\"Khmr\" typeface=\"MoolBoran\"/>") + "<a:font script=\"Knda\" typeface=\"Tunga\"/>") + "<a:font script=\"Guru\" typeface=\"Raavi\"/>") + "<a:font script=\"Cans\" typeface=\"Euphemia\"/>") + "<a:font script=\"Cher\" typeface=\"Plantagenet Cherokee\"/>") + "<a:font script=\"Yiii\" typeface=\"Microsoft Yi Baiti\"/>") + "<a:font script=\"Tibt\" typeface=\"Microsoft Himalaya\"/>") + "<a:font script=\"Thaa\" typeface=\"MV Boli\"/>") + "<a:font script=\"Deva\" typeface=\"Mangal\"/>") + "<a:font script=\"Telu\" typeface=\"Gautami\"/>") + "<a:font script=\"Taml\" typeface=\"Latha\"/>") + "<a:font script=\"Syrc\" typeface=\"Estrangelo Edessa\"/>") + "<a:font script=\"Orya\" typeface=\"Kalinga\"/>") + "<a:font script=\"Mlym\" typeface=\"Kartika\"/>") + "<a:font script=\"Laoo\" typeface=\"DokChampa\"/>") + "<a:font script=\"Sinh\" typeface=\"Iskoola Pota\"/>") + "<a:font script=\"Mong\" typeface=\"Mongolian Baiti\"/>") + "<a:font script=\"Viet\" typeface=\"Times New Roman\"/>") + "<a:font script=\"Uigh\" typeface=\"Microsoft Uighur\"/>") + "<a:font script=\"Geor\" typeface=\"Sylfaen\"/>") + "</a:majorFont>") + "<a:minorFont>") + "<a:latin panose=\"020F0502020204030204\" typeface=\"Calibri\"/>") + "<a:ea typeface=\"\"/>") + "<a:cs typeface=\"\"/>") + "<a:font script=\"Jpan\" typeface=\"\uff2d\uff33 \u660e\u671d\"/>") + "<a:font script=\"Hang\" typeface=\"\ub9d1\uc740 \uace0\ub515\"/>") + "<a:font script=\"Hans\" typeface=\"\u5b8b\u4f53\"/>") + "<a:font script=\"Hant\" typeface=\"\u65b0\u7d30\u660e\u9ad4\"/>") + "<a:font script=\"Arab\" typeface=\"Arial\"/>") + "<a:font script=\"Hebr\" typeface=\"Arial\"/>") + "<a:font script=\"Thai\" typeface=\"Cordia New\"/>") + "<a:font script=\"Ethi\" typeface=\"Nyala\"/>") + "<a:font script=\"Beng\" typeface=\"Vrinda\"/>") + "<a:font script=\"Gujr\" typeface=\"Shruti\"/>") + "<a:font script=\"Khmr\" typeface=\"DaunPenh\"/>") + "<a:font script=\"Knda\" typeface=\"Tunga\"/>") + "<a:font script=\"Guru\" typeface=\"Raavi\"/>") + "<a:font script=\"Cans\" typeface=\"Euphemia\"/>") + "<a:font script=\"Cher\" typeface=\"Plantagenet Cherokee\"/>") + "<a:font script=\"Yiii\" typeface=\"Microsoft Yi Baiti\"/>") + "<a:font script=\"Tibt\" typeface=\"Microsoft Himalaya\"/>") + "<a:font script=\"Thaa\" typeface=\"MV Boli\"/>") + "<a:font script=\"Deva\" typeface=\"Mangal\"/>") + "<a:font script=\"Telu\" typeface=\"Gautami\"/>") + "<a:font script=\"Taml\" typeface=\"Latha\"/>") + "<a:font script=\"Syrc\" typeface=\"Estrangelo Edessa\"/>") + "<a:font script=\"Orya\" typeface=\"Kalinga\"/>") + "<a:font script=\"Mlym\" typeface=\"Kartika\"/>") + "<a:font script=\"Laoo\" typeface=\"DokChampa\"/>") + "<a:font script=\"Sinh\" typeface=\"Iskoola Pota\"/>") + "<a:font script=\"Mong\" typeface=\"Mongolian Baiti\"/>") + "<a:font script=\"Viet\" typeface=\"Arial\"/>") + "<a:font script=\"Uigh\" typeface=\"Microsoft Uighur\"/>") + "<a:font script=\"Geor\" typeface=\"Sylfaen\"/>") + "</a:minorFont>") + "</a:fontScheme>") + "<a:fmtScheme name=\"Office\">") + "<a:fillStyleLst>") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\"/>") + "</a:solidFill>") + "<a:gradFill rotWithShape=\"1\">") + "<a:gsLst>") + "<a:gs pos=\"0\">") + "<a:schemeClr val=\"phClr\">") + "<a:lumMod val=\"110000\"/>") + "<a:satMod val=\"105000\"/>") + "<a:tint val=\"67000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"50000\">") + "<a:schemeClr val=\"phClr\">") + "<a:lumMod val=\"105000\"/>") + "<a:satMod val=\"103000\"/>") + "<a:tint val=\"73000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"100000\">") + "<a:schemeClr val=\"phClr\">") + "<a:lumMod val=\"105000\"/>") + "<a:satMod val=\"109000\"/>") + "<a:tint val=\"81000\"/>") + "</a:schemeClr>") + "</a:gs>") + "</a:gsLst>") + "<a:lin ang=\"5400000\" scaled=\"0\"/>") + "</a:gradFill>") + "<a:gradFill rotWithShape=\"1\">") + "<a:gsLst>") + "<a:gs pos=\"0\">") + "<a:schemeClr val=\"phClr\">") + "<a:satMod val=\"103000\"/>") + "<a:lumMod val=\"102000\"/>") + "<a:tint val=\"94000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"50000\">") + "<a:schemeClr val=\"phClr\">") + "<a:satMod val=\"110000\"/>") + "<a:lumMod val=\"100000\"/>") + "<a:shade val=\"100000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"100000\">") + "<a:schemeClr val=\"phClr\">") + "<a:lumMod val=\"99000\"/>") + "<a:satMod val=\"120000\"/>") + "<a:shade val=\"78000\"/>") + "</a:schemeClr>") + "</a:gs>") + "</a:gsLst>") + "<a:lin ang=\"5400000\" scaled=\"0\"/>") + "</a:gradFill>") + "</a:fillStyleLst>") + "<a:lnStyleLst>") + "<a:ln algn=\"ctr\" cap=\"flat\" cmpd=\"sng\" w=\"6350\">") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\"/>") + "</a:solidFill>") + "<a:prstDash val=\"solid\"/>") + "<a:miter lim=\"800000\"/>") + "</a:ln>") + "<a:ln algn=\"ctr\" cap=\"flat\" cmpd=\"sng\" w=\"12700\">") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\"/>") + "</a:solidFill>") + "<a:prstDash val=\"solid\"/>") + "<a:miter lim=\"800000\"/>") + "</a:ln>") + "<a:ln algn=\"ctr\" cap=\"flat\" cmpd=\"sng\" w=\"19050\">") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\"/>") + "</a:solidFill>") + "<a:prstDash val=\"solid\"/>") + "<a:miter lim=\"800000\"/>") + "</a:ln>") + "</a:lnStyleLst>") + "<a:effectStyleLst>") + "<a:effectStyle>") + "<a:effectLst/>") + "</a:effectStyle>") + "<a:effectStyle>") + "<a:effectLst/>") + "</a:effectStyle>") + "<a:effectStyle>") + "<a:effectLst>") + "<a:outerShdw algn=\"ctr\" blurRad=\"57150\" dir=\"5400000\" dist=\"19050\" rotWithShape=\"0\">") + "<a:srgbClr val=\"000000\">") + "<a:alpha val=\"63000\"/>") + "</a:srgbClr>") + "</a:outerShdw>") + "</a:effectLst>") + "</a:effectStyle>") + "</a:effectStyleLst>") + "<a:bgFillStyleLst>") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\"/>") + "</a:solidFill>") + "<a:solidFill>") + "<a:schemeClr val=\"phClr\">") + "<a:tint val=\"95000\"/>") + "<a:satMod val=\"170000\"/>") + "</a:schemeClr>") + "</a:solidFill>") + "<a:gradFill rotWithShape=\"1\">") + "<a:gsLst>") + "<a:gs pos=\"0\">") + "<a:schemeClr val=\"phClr\">") + "<a:tint val=\"93000\"/>") + "<a:satMod val=\"150000\"/>") + "<a:shade val=\"98000\"/>") + "<a:lumMod val=\"102000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"50000\">") + "<a:schemeClr val=\"phClr\">") + "<a:tint val=\"98000\"/>") + "<a:satMod val=\"130000\"/>") + "<a:shade val=\"90000\"/>") + "<a:lumMod val=\"103000\"/>") + "</a:schemeClr>") + "</a:gs>") + "<a:gs pos=\"100000\">") + "<a:schemeClr val=\"phClr\">") + "<a:shade val=\"63000\"/>") + "<a:satMod val=\"120000\"/>") + "</a:schemeClr>") + "</a:gs>") + "</a:gsLst>") + "<a:lin ang=\"5400000\" scaled=\"0\"/>") + "</a:gradFill>") + "</a:bgFillStyleLst>") + "</a:fmtScheme>") + "</a:themeElements>") + "<a:objectDefaults/>") + "<a:extraClrSchemeLst/>") + "<a:extLst>") + "<a:ext uri=\"{05A4C25C-085E-4340-85A3-A5531E510DB2}\">") + "<thm15:themeFamily id=\"{62F939B6-93AF-4DB8-9C6B-D6C7DFDC589F}\" name=\"Office Theme\" vid=\"{4A3C46E8-61CC-4603-A589-7422A47A8E4A}\"/>") + "</a:ext>") + "</a:extLst>") + "</a:theme>");
}

