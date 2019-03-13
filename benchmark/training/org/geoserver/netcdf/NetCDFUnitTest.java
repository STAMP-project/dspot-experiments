package org.geoserver.netcdf;


import NetCDFUnitFormat.NETCDF_UNIT_ALIASES;
import NetCDFUnitFormat.NETCDF_UNIT_REPLACEMENTS;
import java.io.OutputStreamWriter;
import java.util.Properties;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geoserver.platform.resource.Resource;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class NetCDFUnitTest extends GeoServerSystemTestSupport {
    @Test
    public void testUnit() throws Exception {
        CoverageDimensionInfo dimension = getSSTCoverageDimensionInfo();
        Assert.assertEquals("\u2103", dimension.getUnit());
    }

    @Test
    public void testUnitAliasesOnReload() throws Exception {
        // reconfigure units with something funny
        Resource aliasResource = getDataDirectory().get(NETCDF_UNIT_ALIASES);
        Properties p = new Properties();
        p.put("celsius", "g*(m/s)^2");
        try (OutputStreamWriter osw = new OutputStreamWriter(aliasResource.out(), "UTF8")) {
            p.store(osw, null);
        }
        try {
            // force unit definitions to be reloaded
            getGeoServer().reload();
            // try again
            CoverageDimensionInfo dimension = getSSTCoverageDimensionInfo();
            Assert.assertEquals("g*m^2*s^-2", dimension.getUnit());
        } finally {
            aliasResource.delete();
            getGeoServer().reload();
        }
    }

    @Test
    public void testUnitAliasesOnReset() throws Exception {
        // reconfigure units with something funny
        Resource aliasResource = getDataDirectory().get(NETCDF_UNIT_ALIASES);
        Properties p = new Properties();
        p.put("celsius", "g*(m/s)^2");
        try (OutputStreamWriter osw = new OutputStreamWriter(aliasResource.out(), "UTF8")) {
            p.store(osw, null);
        }
        try {
            // force unit definitions to be reloaded
            getGeoServer().reset();
            // try again
            CoverageDimensionInfo dimension = getSSTCoverageDimensionInfo();
            Assert.assertEquals("g*m^2*s^-2", dimension.getUnit());
        } finally {
            aliasResource.delete();
            getGeoServer().reset();
        }
    }

    @Test
    public void testUnitReplacementsOnReset() throws Exception {
        // reconfigure units with something funny
        Resource replacementsResource = getDataDirectory().get(NETCDF_UNIT_REPLACEMENTS);
        Properties p = new Properties();
        p.put("celsius", "g*(m/s)^2");
        try (OutputStreamWriter osw = new OutputStreamWriter(replacementsResource.out(), "UTF8")) {
            p.store(osw, null);
        }
        try {
            // force unit definitions to be reloaded
            getGeoServer().reset();
            // try again
            CoverageDimensionInfo dimension = getSSTCoverageDimensionInfo();
            Assert.assertEquals("g*m^2*s^-2", dimension.getUnit());
        } finally {
            replacementsResource.delete();
            getGeoServer().reset();
        }
    }
}

