/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import SampleDimensionType.REAL_64BITS;
import SampleDimensionType.SIGNED_16BITS;
import VocabularyKeys.NODATA;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geoserver.catalog.CoverageDimensionCustomizerReader.GridCoverageWrapper;
import org.geoserver.catalog.CoverageDimensionCustomizerReader.WrappedSampleDimension;
import org.geoserver.catalog.impl.CoverageDimensionImpl;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.coverage.Category;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.metadata.i18n.Vocabulary;
import org.geotools.util.NumberRange;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.ColorInterpretation;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.SampleDimensionType;


public class CoverageDimensionCustomizerReaderTest extends GeoServerSystemTestSupport {
    private static final double DELTA = 1.0E-4;

    /**
     * Test that the null values and range of a wrapped sampleDimension are the same configured on
     * the {@link CoverageDimensionInfo} object used to customize them
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDimensionsWrapping() throws IOException {
        final GridSampleDimension sampleDim = new GridSampleDimension("original", SampleDimensionType.REAL_64BITS, ColorInterpretation.GRAY_INDEX, null, null, new double[]{ -9999.0 }, (-1000.0), 1000.0, 1.0, 0.0, null);
        // Setting coverage dimension
        final CoverageDimensionImpl coverageDim = new CoverageDimensionImpl();
        final String wrappedName = "wrapped";
        coverageDim.setName(wrappedName);
        coverageDim.setDimensionType(REAL_64BITS);
        final double newMinimum = -2000.0;
        final double newMaximum = 2000.0;
        final NumberRange<Double> range = new NumberRange<Double>(Double.class, newMinimum, newMaximum);
        coverageDim.setRange(range);
        final List<Double> nullValues = new ArrayList<Double>();
        final double noData1 = -32768.0;
        final double noData2 = -32767.0;
        nullValues.add(noData1);
        nullValues.add(noData2);
        coverageDim.setNullValues(nullValues);
        final SampleDimension wrappedDim = WrappedSampleDimension.build(sampleDim, coverageDim);
        double[] noData = wrappedDim.getNoDataValues();
        Assert.assertEquals(2, noData.length);
        Assert.assertEquals(noData1, noData[0], CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(noData2, noData[1], CoverageDimensionCustomizerReaderTest.DELTA);
        NumberRange wrappedRange = getRange();
        Assert.assertEquals(newMinimum, wrappedRange.getMinimum(), CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(newMaximum, wrappedRange.getMaximum(), CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(wrappedName, wrappedDim.getDescription().toString());
    }

    @Test
    public void testWrapCustomizationSurviveCopyConstructor() throws Exception {
        final GridSampleDimension sampleDim = new GridSampleDimension("original", SampleDimensionType.REAL_64BITS, ColorInterpretation.GRAY_INDEX, null, null, new double[]{ -9999.0 }, (-1000.0), 1000.0, 1.0, 0.0, null);
        // Setting coverage dimension
        final CoverageDimensionImpl coverageDim = new CoverageDimensionImpl();
        final String wrappedName = "wrapped";
        coverageDim.setName(wrappedName);
        coverageDim.setDimensionType(REAL_64BITS);
        final double newMinimum = -2000.0;
        final double newMaximum = 2000.0;
        final NumberRange<Double> range = new NumberRange<Double>(Double.class, newMinimum, newMaximum);
        coverageDim.setRange(range);
        final List<Double> nullValues = new ArrayList<Double>();
        final double noData1 = -32768.0;
        final double noData2 = -32767.0;
        nullValues.add(noData1);
        nullValues.add(noData2);
        coverageDim.setNullValues(nullValues);
        final SampleDimension wrappedDim = WrappedSampleDimension.build(sampleDim, coverageDim);
        double[] noData = wrappedDim.getNoDataValues();
        Assert.assertEquals(2, noData.length);
        Assert.assertEquals(noData1, noData[0], CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(noData2, noData[1], CoverageDimensionCustomizerReaderTest.DELTA);
        NumberRange wrappedRange = getRange();
        Assert.assertEquals(newMinimum, wrappedRange.getMinimum(), CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(newMaximum, wrappedRange.getMaximum(), CoverageDimensionCustomizerReaderTest.DELTA);
    }

    /**
     * Test that the wrapped nodata categories contains the defined nodata as an int
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testIntegerNoDataCategoryWrapping() throws IOException {
        // Setting coverage dimension
        final CoverageDimensionImpl coverageDim = new CoverageDimensionImpl();
        final String wrappedName = "wrapped";
        coverageDim.setName(wrappedName);
        coverageDim.setDimensionType(SIGNED_16BITS);
        coverageDim.setRange(NumberRange.create(0.0, 10000.0));
        // Definition of the nodata
        final List<Double> nullValues = new ArrayList<Double>();
        final double noData1 = -32768.0;
        nullValues.add(noData1);
        coverageDim.setNullValues(nullValues);
        // Quantitative nodata category
        GridSampleDimension sampleDim = new GridSampleDimension("original", new Category[]{ new Category(Vocabulary.formatInternational(NODATA), new Color[]{ new Color(0, 0, 0, 0) }, NumberRange.create((-9999), (-9999))) }, null);
        // Wrap the dimension
        GridSampleDimension copy = WrappedSampleDimension.build(sampleDim, coverageDim);
        // Extract categories
        List<Category> categories = copy.getCategories();
        // Ensure NoData Category is present
        Category category = categories.get(0);
        Assert.assertTrue(category.getName().equals(Category.NODATA.getName()));
        // Check if it contains sampleToGeophisics and the Range contains the first nodata defined
        Assert.assertEquals(category.getRange().getMinimum(), noData1, CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(category.getRange().getMaximum(), noData1, CoverageDimensionCustomizerReaderTest.DELTA);
    }

    /**
     * Test that the wrapped nodata categories contains the defined nodata
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testNoDataCategoryWrapping() throws IOException {
        // Setting coverage dimension
        final CoverageDimensionImpl coverageDim = new CoverageDimensionImpl();
        final String wrappedName = "wrapped";
        coverageDim.setName(wrappedName);
        coverageDim.setDimensionType(REAL_64BITS);
        // Definition of the nodata
        final List<Double> nullValues = new ArrayList<Double>();
        final double noData1 = -32768.0;
        final double noData2 = -32767.0;
        nullValues.add(noData1);
        nullValues.add(noData2);
        coverageDim.setNullValues(nullValues);
        // Qualitative nodata category
        GridSampleDimension sampleDim = new GridSampleDimension("original", new Category[]{ new Category(Vocabulary.formatInternational(NODATA), new Color(0, 0, 0, 0), Double.NaN) }, null);
        // Wrap the dimension
        GridSampleDimension wrappedDim = WrappedSampleDimension.build(sampleDim, coverageDim);
        // run the copy constructor
        GridSampleDimension copy = new GridSampleDimension(wrappedDim) {};
        // Extract categories
        List<Category> categories = copy.getCategories();
        // Ensure NoData Category is present
        Category category = categories.get(0);
        Assert.assertTrue(category.getName().equals(Category.NODATA.getName()));
        // Check that it does not contain sampleToGeophisics and that the Range contains only NaN
        Assert.assertEquals(category.getRange().getMinimum(), Double.NaN, CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(category.getRange().getMaximum(), Double.NaN, CoverageDimensionCustomizerReaderTest.DELTA);
        // Quantitative nodata category
        sampleDim = new GridSampleDimension("original", new Category[]{ new Category(Vocabulary.formatInternational(NODATA), new Color[]{ new Color(0, 0, 0, 0) }, NumberRange.create((-9999), (-9999))) }, null);
        // Wrap the dimension
        copy = WrappedSampleDimension.build(sampleDim, coverageDim);
        // Extract categories
        categories = copy.getCategories();
        // Ensure NoData Category is present
        category = categories.get(0);
        Assert.assertTrue(category.getName().equals(Category.NODATA.getName()));
        // Check if it contains sampleToGeophisics and the Range contains the first nodata defined
        Assert.assertEquals(category.getRange().getMinimum(), noData1, CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(category.getRange().getMaximum(), noData1, CoverageDimensionCustomizerReaderTest.DELTA);
    }

    /**
     * Test that if no range is defined, Category values or Default values are used
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testNoRange() throws IOException {
        GridSampleDimension sampleDim = new GridSampleDimension("original", SampleDimensionType.REAL_64BITS, ColorInterpretation.GRAY_INDEX, null, null, new double[]{ -9999.0 }, (-1000.0), 1000.0, 1.0, 0.0, null);
        // Setting coverage dimension
        final CoverageDimensionImpl coverageDim = new CoverageDimensionImpl();
        final String wrappedName = "wrapped";
        coverageDim.setName(wrappedName);
        coverageDim.setDimensionType(REAL_64BITS);
        // Creation of the WrappedSampleDimension
        SampleDimension wrappedDim = WrappedSampleDimension.build(sampleDim, coverageDim);
        // Get the range
        NumberRange<? extends Number> wrappedRange = getRange();
        // Ensure the range is not present
        Assert.assertNull(wrappedRange);
        // Check if min and max are taken from the categories
        Assert.assertEquals((-9999), wrappedDim.getMinimumValue(), CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(1000, wrappedDim.getMaximumValue(), CoverageDimensionCustomizerReaderTest.DELTA);
        // Check that the description is equal to the sample dimension name
        Assert.assertEquals(wrappedName, wrappedDim.getDescription().toString());
        // Configure a new GridSampleDimension without categories
        sampleDim = new GridSampleDimension("original", null, new tec.uom.se.unit.BaseUnit<javax.measure.quantity.Dimensionless>("test"));
        // New wrapped sample dimension
        wrappedDim = WrappedSampleDimension.build(sampleDim, coverageDim);
        // Get the range
        wrappedRange = ((WrappedSampleDimension) (wrappedDim)).getRange();
        // Ensure the range is not present
        Assert.assertNull(wrappedRange);
        // Check if min and max are taken from the categories
        Assert.assertEquals(Double.NEGATIVE_INFINITY, wrappedDim.getMinimumValue(), CoverageDimensionCustomizerReaderTest.DELTA);
        Assert.assertEquals(Double.POSITIVE_INFINITY, wrappedDim.getMaximumValue(), CoverageDimensionCustomizerReaderTest.DELTA);
    }

    /**
     * Test GridCoverage unwrapping
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGridCoverageUnwrapping() throws IOException {
        GridCoverageFactory gcFactory = new GridCoverageFactory();
        RenderedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        GridCoverage2D original = gcFactory.create("original", image, new GeneralEnvelope(new Rectangle2D.Double(0, 0, 64, 64)));
        GridSampleDimension[] gsd = new GridSampleDimension[]{ new GridSampleDimension("wrappedSampleDimension") };
        GridCoverageWrapper wrapper = new GridCoverageWrapper("wrapped", original, gsd, null);
        Assert.assertNotSame(original.getSampleDimensions(), wrapper.getSampleDimensions());
        Assert.assertNotSame(wrapper, original);
        Assert.assertSame(original, wrapper.unwrap(original.getClass()));
    }
}

