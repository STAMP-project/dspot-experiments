package com.brucetoo.pickview.provincepick.utils;


import com.brucetoo.pickview.provincepick.PickViewTestSupport;
import com.brucetoo.pickview.provincepick.ProvinceModel;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


public class ProvinceInfoHandlerTest extends PickViewTestSupport {
    private ProvinceInfoHandler provinceInfoHandler;

    private List<ProvinceModel> provinces;

    @Test
    public void testConstructor() {
        Assert.assertTrue(provinceInfoHandler.getProvinceList().isEmpty());
    }

    @Test
    public void testConstructorWithNullArgument() {
        provinceInfoHandler = new ProvinceInfoHandler(null);
        Assert.assertNull(provinceInfoHandler.getProvinceList());
    }

    @Test
    public void testDocumentParsing() throws IOException, ParserConfigurationException, SAXException {
        // when
        parseDocument("valid-data.xml");
        // then
        assertProvinceList(provinces, provinceInfoHandler.getProvinceList());
    }

    @Test
    public void testDocumentParsingWithInitialNullProvincesList() throws IOException, ParserConfigurationException, SAXException {
        // given
        provinceInfoHandler = new ProvinceInfoHandler(null);
        // when
        parseDocument("valid-data.xml");
        // then
        assertProvinceList(provinces, provinceInfoHandler.getProvinceList());
    }

    @Test
    public void testEmptyDocumentParsing() throws IOException, ParserConfigurationException, SAXException {
        // when
        parseDocument("empty-data.xml");
        // then
        Assert.assertTrue((((provinceInfoHandler.getProvinceList()) == null) || (provinceInfoHandler.getProvinceList().isEmpty())));
    }

    @Test
    public void testUnknownTagsDocumentParsing() throws IOException, ParserConfigurationException, SAXException {
        // given
        provinceInfoHandler = new ProvinceInfoHandler(null);
        // when
        parseDocument("unknown-data.xml");
        // then
        assertProvinceList(provinces, provinceInfoHandler.getProvinceList());
    }
}

