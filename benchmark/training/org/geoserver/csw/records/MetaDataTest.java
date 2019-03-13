/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.records;


import MetaDataDescriptor.METADATA_DESCRIPTOR;
import MetaDataDescriptor.METADATA_TYPE;
import org.geoserver.csw.records.iso.MetaDataDescriptor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;


public class MetaDataTest {
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    @Test
    public void testConfirmTypeBuilt() {
        Assert.assertNotNull(METADATA_TYPE);
        Assert.assertNotNull(METADATA_DESCRIPTOR);
    }

    @Test
    public void testBuildMDRecord() throws Exception, MismatchedDimensionException {
        GenericRecordBuilder rb = new GenericRecordBuilder(MetaDataDescriptor.getInstance());
        rb.addElement("fileIdentifier.CharacterString", "00180e67-b7cf-40a3-861d-b3a09337b195");
        rb.addElement("identificationInfo.AbstractMD_Identification.citation.CI_Citation.title.CharacterString", "Image2000 Product 1 (at1) Multispectral");
        rb.addElement("dateStamp.Date", "2004-10-04 00:00:00");
        rb.addElement("identificationInfo.AbstractMD_Identification.abstract.CharacterString", "IMAGE2000 product 1 individual orthorectified scenes. IMAGE2000 was  produced from ETM+ Landsat 7 satellite data and provides a consistent European coverage of individual orthorectified scenes in national map projection systems.");
        rb.addElement("hierarchyLevel.MD_ScopeCode.@codeListValue", "dataset");
        rb.addElement("identificationInfo.AbstractMD_Identification.descriptiveKeywords.MD_Keywords.keyword.CharacterString", "imagery", "baseMaps", "earthCover");
        rb.addElement("contact.CI_ResponsibleParty.individualName.CharacterString", "Niels Charlier");
        rb.addBoundingBox(new org.geotools.geometry.jts.ReferencedEnvelope(14.05, 17.24, 46.46, 28.42, DefaultGeographicCRS.WGS84));
        Feature f = rb.build(null);
        assertRecordElement(f, "gmd:fileIdentifier/gco:CharacterString", "00180e67-b7cf-40a3-861d-b3a09337b195");
        assertRecordElement(f, "gmd:identificationInfo/gmd:AbstractMD_Identification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString", "Image2000 Product 1 (at1) Multispectral");
        assertRecordElement(f, "gmd:dateStamp/gco:Date", "2004-10-04 00:00:00");
        assertRecordElement(f, "gmd:identificationInfo/gmd:AbstractMD_Identification/gmd:abstract/gco:CharacterString", "IMAGE2000 product 1 individual orthorectified scenes. IMAGE2000 was  produced from ETM+ Landsat 7 satellite data and provides a consistent European coverage of individual orthorectified scenes in national map projection systems.");
        assertRecordElement(f, "gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue", "dataset");
        assertRecordElement(f, "gmd:identificationInfo/gmd:AbstractMD_Identification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString", "imagery", "baseMaps", "earthCover");
        assertBBox(f, new org.geotools.geometry.jts.ReferencedEnvelope(14.05, 17.24, 46.46, 28.42, DefaultGeographicCRS.WGS84));
    }
}

