package org.docx4j.convert.out.html;


import javax.xml.bind.JAXBElement;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtContentBlock;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.org.docx4j.wml.Tbl;
import org.docx4j.wml.org.docx4j.wml.Tc;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListsToContentControlsTest {
    protected static Logger log = LoggerFactory.getLogger(ListsToContentControls.class);// same logger


    private static ObjectFactory wmlObjectFactory = Context.getWmlObjectFactory();

    @Test
    public void singleList() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void singleListTwice() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void singleList2Levels() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 1));// nested

        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void singleList2LevelsPop() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 1));// nested

        mdp.getContent().add(createNumberedP(1, 0));// then back

        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void singleListStartLvl2() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 1));
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(1, 2));
        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void twoLists() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(2, 0));
        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void existingControl() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        SdtBlock sdtBlock = new SdtBlock();
        SdtContentBlock sdtContentBlock = new SdtContentBlock();
        sdtBlock.setSdtContent(sdtContentBlock);
        mdp.getContent().add(sdtBlock);
        sdtBlock.getSdtContent().getContent().add(createUnnumberedP());
        sdtBlock.getSdtContent().getContent().add(createNumberedP(1, 0));
        sdtBlock.getSdtContent().getContent().add(createNumberedP(1, 0));
        sdtBlock.getSdtContent().getContent().add(createUnnumberedP());
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void tableCell() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();
        Tbl tbl = wmlObjectFactory.createTbl();
        JAXBElement<org.docx4j.wml.Tbl> tblWrapped = wmlObjectFactory.createCTFtnEdnTbl(tbl);
        // Create object for tr
        Tr tr = wmlObjectFactory.createTr();
        tbl.getContent().add(tr);
        // Create object for tc (wrapped in JAXBElement)
        Tc tc = wmlObjectFactory.createTc();
        JAXBElement<org.docx4j.wml.Tc> tcWrapped = wmlObjectFactory.createTrTc(tc);
        tr.getContent().add(tcWrapped);
        mdp.getContent().add(tbl);
        tc.getContent().add(createUnnumberedP());
        tc.getContent().add(createNumberedP(1, 0));
        tc.getContent().add(createNumberedP(1, 0));
        tc.getContent().add(createUnnumberedP());
        ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
    }

    @Test
    public void EndToEnd() throws Exception {
        WordprocessingMLPackage wordMLPackage = createPkg();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        mdp.getContent().add(createUnnumberedP());
        mdp.getContent().add(createNumberedP(1, 0));
        mdp.getContent().add(createNumberedP(2, 0));
        mdp.getContent().add(createUnnumberedP());
        // wordMLPackage.save(new File(System.getProperty("user.dir") + "/OUT_simpleTest.docx"));
        // ListsToContentControls.process(wordMLPackage);
        System.out.println(mdp.getXML());
        toHTML(wordMLPackage);
    }

    private static final String numbering = "<w:numbering mc:Ignorable=\"w14 wp14\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">" + // Ordered list
    ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<w:abstractNum w:abstractNumId=\"0\">" + "<w:nsid w:val=\"069653C8\"/>") + "<w:multiLevelType w:val=\"multilevel\"/>") + "<w:tmpl w:val=\"0C09001D\"/>") + "<w:lvl w:ilvl=\"0\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"decimal\"/>") + "<w:lvlText w:val=\"%1)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"360\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"1\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerLetter\"/>") + "<w:lvlText w:val=\"%2)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"720\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"2\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerRoman\"/>") + "<w:lvlText w:val=\"%3)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1080\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"3\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"decimal\"/>") + "<w:lvlText w:val=\"(%4)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1440\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"4\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerLetter\"/>") + "<w:lvlText w:val=\"(%5)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1800\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"5\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerRoman\"/>") + "<w:lvlText w:val=\"(%6)\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2160\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"6\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"decimal\"/>") + "<w:lvlText w:val=\"%7.\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2520\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"7\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerLetter\"/>") + "<w:lvlText w:val=\"%8.\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2880\"/>") + "</w:pPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"8\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"lowerRoman\"/>") + "<w:lvlText w:val=\"%9.\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"3240\"/>") + "</w:pPr>") + "</w:lvl>") + "</w:abstractNum>") + // Unordered (bulleted) list
    "<w:abstractNum w:abstractNumId=\"1\">") + "<w:nsid w:val=\"38572E21\"/>") + "<w:multiLevelType w:val=\"multilevel\"/>") + "<w:tmpl w:val=\"0C090021\"/>") + "<w:lvl w:ilvl=\"0\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf076\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"360\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"1\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0d8\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"720\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"2\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0a7\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1080\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"3\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0b7\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1440\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Symbol\" w:hAnsi=\"Symbol\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"4\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0a8\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"1800\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Symbol\" w:hAnsi=\"Symbol\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"5\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0d8\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2160\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"6\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0a7\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2520\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"7\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0b7\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"2880\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Symbol\" w:hAnsi=\"Symbol\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "<w:lvl w:ilvl=\"8\">") + "<w:start w:val=\"1\"/>") + "<w:numFmt w:val=\"bullet\"/>") + "<w:lvlText w:val=\"\uf0a8\"/>") + "<w:lvlJc w:val=\"left\"/>") + "<w:pPr>") + "<w:ind w:hanging=\"360\" w:left=\"3240\"/>") + "</w:pPr>") + "<w:rPr>") + "<w:rFonts w:ascii=\"Symbol\" w:hAnsi=\"Symbol\" w:hint=\"default\"/>") + "</w:rPr>") + "</w:lvl>") + "</w:abstractNum>") + "<w:num w:numId=\"1\">") + "<w:abstractNumId w:val=\"0\"/>") + "</w:num>") + "<w:num w:numId=\"2\">") + "<w:abstractNumId w:val=\"1\"/>") + "</w:num>") + "</w:numbering>");
}

