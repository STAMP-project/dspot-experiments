package org.docx4j;


import java.util.List;
import javax.xml.bind.JAXBElement;
import junit.framework.Assert;
import org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.GraphicData;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.dml.wordprocessingDrawing.ObjectFactory;
import org.junit.Test;


// public static void main(String[] args) throws Docx4JException, SAXException, IOException {
// 
// P p = Context.getWmlObjectFactory().createP();
// R r = Context.getWmlObjectFactory().createR();
// p.getContent().add(r);
// 
// Drawing d = Context.getWmlObjectFactory().createDrawing();
// r.getContent().add(d);
// 
// org.docx4j.dml.wordprocessingDrawing.Anchor anchor = (new org.docx4j.dml.wordprocessingDrawing.ObjectFactory()).createAnchor();
// d.getAnchorOrInline().add(anchor);
// 
// org.docx4j.dml.ObjectFactory dmlObjectFactory = new org.docx4j.dml.ObjectFactory();
// Graphic g = dmlObjectFactory.createGraphic();
// 
// anchor.setGraphic(g);
// 
// GraphicData gd = dmlObjectFactory.createGraphicData();
// g.setGraphicData(gd);
// 
// org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape wShape =
// new org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape();
// 
// JAXBElement<CTWordprocessingShape> jx = (new org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.ObjectFactory()).createWsp(wShape);
// 
// gd.getAny().add(jx);
// 
// System.out.println(XmlUtils.marshaltoString(p));
// 
// 
// List<Object> list = TraversalUtil.getChildrenImpl(anchor);
// 
// }
public class TraversalUtilTest {
    @Test
    public void testIssue344Anchor() {
        /* Create
        <wp:anchor distT="45720" distB="45720" distL="114300" distR="114300" simplePos="0" 
        relativeHeight="251659264" behindDoc="0" locked="0" layoutInCell="1" allowOverlap="1" wp14:anchorId="4B7F61D6" wp14:editId="70D91E50">
        <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
        <a:graphicData uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
        <wps:wsp>
         */
        Anchor anchor = new ObjectFactory().createAnchor();
        org.docx4j.dml.ObjectFactory dmlObjectFactory = new org.docx4j.dml.ObjectFactory();
        Graphic g = dmlObjectFactory.createGraphic();
        anchor.setGraphic(g);
        GraphicData gd = dmlObjectFactory.createGraphicData();
        g.setGraphicData(gd);
        CTWordprocessingShape wShape = new CTWordprocessingShape();
        JAXBElement<CTWordprocessingShape> jx = new org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.ObjectFactory().createWsp(wShape);
        gd.getAny().add(jx);
        List<Object> list = TraversalUtil.getChildrenImpl(anchor);
        Assert.assertNull(list);
    }

    @Test
    public void testIssue344Inline() {
        /* Create
        <wp:inline distT="45720" distB="45720" distL="114300" distR="114300" simplePos="0" 
        relativeHeight="251659264" behindDoc="0" locked="0" layoutInCell="1" allowOverlap="1" wp14:anchorId="4B7F61D6" wp14:editId="70D91E50">
        <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
        <a:graphicData uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
        <wps:wsp>
         */
        Inline inline = new ObjectFactory().createInline();
        org.docx4j.dml.ObjectFactory dmlObjectFactory = new org.docx4j.dml.ObjectFactory();
        Graphic g = dmlObjectFactory.createGraphic();
        inline.setGraphic(g);
        GraphicData gd = dmlObjectFactory.createGraphicData();
        g.setGraphicData(gd);
        CTWordprocessingShape wShape = new CTWordprocessingShape();
        JAXBElement<CTWordprocessingShape> jx = new org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.ObjectFactory().createWsp(wShape);
        gd.getAny().add(jx);
        List<Object> list = TraversalUtil.getChildrenImpl(inline);
        Assert.assertNull(list);
    }
}

/**
 * <w:p >
 * <w:r>
 * <w:drawing>
 * <wp:anchor distT="45720" distB="45720" distL="114300" distR="114300" simplePos="0"
 * relativeHeight="251659264" behindDoc="0" locked="0" layoutInCell="1" allowOverlap="1" wp14:anchorId="4B7F61D6" wp14:editId="70D91E50">
 * <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
 * <a:graphicData uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
 * <wps:wsp>
 * <wps:txbx> *
 */
