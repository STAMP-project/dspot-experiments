/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import AbstractGridFormat.BANDS;
import CoverageView.COVERAGE_VIEW;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.jai.ImageLayout;
import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.coverage.grid.io.GranuleStore;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.parameter.GeneralParameterValue;


/**
 * Base support class for CoverageViews based on multiple coverages from the same store.
 *
 * @author Daniele Romagnoli, GeoSolutions
 */
public class CoverageViewReaderTest extends GeoServerSystemTestSupport {
    public static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    protected static QName CURRENT = new QName(MockData.SF_URI, "regional_currents", MockData.SF_PREFIX);

    private CoverageView coverageView = null;

    private CoverageView multiBandCoverageView = null;

    /**
     *
     */
    @Test
    public void testCoverageView() throws Exception {
        createCoverageView();
        addViewToCatalog();
        final Catalog cat = getCatalog();
        final CoverageInfo coverageInfo = cat.getCoverageByName("regional_currents");
        final MetadataMap metadata = coverageInfo.getMetadata();
        final CoverageView metadataCoverageView = ((CoverageView) (metadata.get(COVERAGE_VIEW)));
        Assert.assertEquals(metadataCoverageView, coverageView);
        final ResourcePool resPool = cat.getResourcePool();
        final ReferencedEnvelope bbox = coverageInfo.getLatLonBoundingBox();
        final GridCoverage coverage = resPool.getGridCoverage(coverageInfo, "regional_currents", bbox, null);
        Assert.assertEquals(coverage.getNumSampleDimensions(), 2);
        dispose(true);
        final GridCoverageReader reader = resPool.getGridCoverageReader(coverageInfo, "regional_currents", null);
        final GranuleStore granules = ((GranuleStore) (getGranules("regional_currents", true)));
        SimpleFeatureCollection granulesCollection = granules.getGranules(null);
        // getting the actual phisical granules behind the view,
        Assert.assertEquals(2, granulesCollection.size());
        final Filter filter = CoverageViewReaderTest.FF.equal(CoverageViewReaderTest.FF.property("location"), CoverageViewReaderTest.FF.literal("sample.grb2"), true);
        final int removed = granules.removeGranules(filter);
        Assert.assertEquals(1, removed);
        granulesCollection = granules.getGranules(null);
        Assert.assertEquals(0, granulesCollection.size());
        GridCoverage2DReader myReader = ((GridCoverage2DReader) (reader));
        ImageLayout layout = myReader.getImageLayout();
        SampleModel sampleModel = layout.getSampleModel(null);
        Assert.assertEquals(2, sampleModel.getNumBands());
        ColorModel colorModel = layout.getColorModel(null);
        Assert.assertEquals(2, colorModel.getNumComponents());
        reader.dispose();
    }

    /**
     * Test creation of a Coverage from a multi band CoverageView using an {@link org.geotools.coverage.grid.io.AbstractGridFormat#BANDS} reading parameter
     */
    @Test
    public void testBandSelectionOnCoverageView() throws Exception {
        final Catalog cat = getCatalog();
        final CoverageInfo coverageInfo = cat.getCoverageByName("multiband_select");
        final MetadataMap metadata = coverageInfo.getMetadata();
        final ResourcePool resPool = cat.getResourcePool();
        final ReferencedEnvelope bbox = coverageInfo.getLatLonBoundingBox();
        final GridCoverage coverage = resPool.getGridCoverage(coverageInfo, "multiband_select", bbox, null);
        RenderedImage srcImage = coverage.getRenderedImage();
        Assert.assertEquals(coverage.getNumSampleDimensions(), 5);
        dispose(true);
        final GridCoverageReader reader = resPool.getGridCoverageReader(coverageInfo, "multiband_select", null);
        int[] bandIndices = new int[]{ 2, 0, 1, 0, 2, 2, 2, 3 };
        Parameter<int[]> bandIndicesParam = null;
        if (bandIndices != null) {
            bandIndicesParam = ((Parameter<int[]>) (BANDS.createValue()));
            bandIndicesParam.setValue(bandIndices);
        }
        GridCoverage2DReader myReader = ((GridCoverage2DReader) (reader));
        ImageLayout layout = myReader.getImageLayout();
        SampleModel sampleModel = layout.getSampleModel(null);
        Assert.assertEquals(5, sampleModel.getNumBands());
        reader.dispose();
        List<GeneralParameterValue> paramList = new ArrayList<GeneralParameterValue>();
        paramList.addAll(Arrays.asList(bandIndicesParam));
        GeneralParameterValue[] readParams = paramList.toArray(new GeneralParameterValue[paramList.size()]);
        GridCoverage result = reader.read(readParams);
        Assert.assertEquals(8, result.getNumSampleDimensions());
        RenderedImage destImage = result.getRenderedImage();
        int dWidth = destImage.getWidth();
        int dHeight = destImage.getHeight();
        int[] destImageRowBand0 = new int[dWidth * dHeight];
        int[] destImageRowBand1 = new int[destImageRowBand0.length];
        int[] destImageRowBand2 = new int[destImageRowBand0.length];
        int[] destImageRowBand3 = new int[destImageRowBand0.length];
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 0, destImageRowBand0);
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 1, destImageRowBand1);
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 2, destImageRowBand2);
        destImage.getData().getSamples(0, 0, dWidth, dHeight, 3, destImageRowBand3);
        int sWidth = srcImage.getWidth();
        int sHeight = srcImage.getHeight();
        int[] srcImageRowBand0 = new int[sWidth * sHeight];
        int[] srcImageRowBand1 = new int[srcImageRowBand0.length];
        int[] srcImageRowBand2 = new int[srcImageRowBand0.length];
        int[] srcImageRowBand3 = new int[srcImageRowBand0.length];
        srcImage.getData().getSamples(0, 0, sWidth, sHeight, 0, srcImageRowBand0);
        srcImage.getData().getSamples(0, 0, sWidth, sHeight, 1, srcImageRowBand1);
        srcImage.getData().getSamples(0, 0, sWidth, sHeight, 2, srcImageRowBand2);
        Assert.assertTrue(Arrays.equals(destImageRowBand0, srcImageRowBand2));
        Assert.assertTrue(Arrays.equals(destImageRowBand1, srcImageRowBand0));
        Assert.assertTrue(Arrays.equals(destImageRowBand2, srcImageRowBand1));
        Assert.assertTrue(Arrays.equals(destImageRowBand3, srcImageRowBand0));
        Assert.assertFalse(Arrays.equals(destImageRowBand0, srcImageRowBand0));
    }

    /**
     * Test creation of a Coverage from a multi band CoverageView which has more bands compared to
     * the input CoverageView
     */
    @Test
    public void testOutputWithMoreBandsThanInputCoverageView() throws Exception {
        final Catalog cat = getCatalog();
        final CoverageInfo coverageInfo = cat.getCoverageByName("multiband_select");
        final MetadataMap metadata = coverageInfo.getMetadata();
        final ResourcePool resPool = cat.getResourcePool();
        final ReferencedEnvelope bbox = coverageInfo.getLatLonBoundingBox();
        final GridCoverage coverage = resPool.getGridCoverage(coverageInfo, "multiband_select", bbox, null);
        RenderedImage srcImage = coverage.getRenderedImage();
        Assert.assertEquals(coverage.getNumSampleDimensions(), 5);
        dispose(true);
        final GridCoverageReader reader = resPool.getGridCoverageReader(coverageInfo, "multiband_select", null);
        int[] bandIndices = new int[]{ 2, 0, 1, 0, 2, 2, 2, 3, 4, 0, 1, 0, 4, 2, 3 };
        Parameter<int[]> bandIndicesParam = null;
        if (bandIndices != null) {
            bandIndicesParam = ((Parameter<int[]>) (BANDS.createValue()));
            bandIndicesParam.setValue(bandIndices);
        }
        GridCoverage2DReader myReader = ((GridCoverage2DReader) (reader));
        ImageLayout layout = myReader.getImageLayout();
        SampleModel sampleModel = layout.getSampleModel(null);
        Assert.assertEquals(5, sampleModel.getNumBands());
        reader.dispose();
        List<GeneralParameterValue> paramList = new ArrayList<GeneralParameterValue>();
        paramList.addAll(Arrays.asList(bandIndicesParam));
        GeneralParameterValue[] readParams = paramList.toArray(new GeneralParameterValue[paramList.size()]);
        GridCoverage result = myReader.read(readParams);
        Assert.assertEquals(15, result.getNumSampleDimensions());
    }
}

