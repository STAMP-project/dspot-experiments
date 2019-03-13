/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.params.extractor;


import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class EchoParametersDaoTest extends TestSupport {
    @Test
    public void testParsingEmptyFile() throws Exception {
        TestSupport.doWork("data/echoParameters1.xml", (InputStream inputStream) -> {
            List<EchoParameter> echoParameters = EchoParametersDao.getEchoParameters(inputStream);
            Assert.assertThat(echoParameters.size(), CoreMatchers.is(0));
        });
    }

    @Test
    public void testParsingEmptyEchoParameters() throws Exception {
        TestSupport.doWork("data/echoParameters2.xml", (InputStream inputStream) -> {
            List<EchoParameter> echoParameters = EchoParametersDao.getEchoParameters(inputStream);
            Assert.assertThat(echoParameters.size(), CoreMatchers.is(0));
        });
    }

    @Test
    public void testParsingEchoParameter() throws Exception {
        TestSupport.doWork("data/echoParameters3.xml", (InputStream inputStream) -> {
            List<EchoParameter> echoParameters = EchoParametersDao.getEchoParameters(inputStream);
            Assert.assertThat(echoParameters.size(), CoreMatchers.is(1));
            checkEchoParameter(echoParameters.get(0), new EchoParameterBuilder().withId("0").withParameter("CQL_FILTER").withActivated(true).build());
        });
    }

    @Test
    public void testParsingMultipleEchoParameters() throws Exception {
        TestSupport.doWork("data/echoParameters4.xml", (InputStream inputStream) -> {
            List<EchoParameter> echoParameters = EchoParametersDao.getEchoParameters(inputStream);
            Assert.assertThat(echoParameters.size(), CoreMatchers.is(2));
            checkEchoParameter(findEchoParameter("0", echoParameters), new EchoParameterBuilder().withId("0").withParameter("CQL_FILTER").withActivated(true).build());
            checkEchoParameter(findEchoParameter("1", echoParameters), new EchoParameterBuilder().withId("1").withParameter("BBOX").withActivated(false).build());
        });
    }

    @Test
    public void testEchoParameterCrud() {
        // create the echo parameters to be used, echo parameter C is an update of echo parameter B
        // (the id is the same)
        EchoParameter echoParameterA = new EchoParameterBuilder().withId("0").withActivated(true).withParameter("cql_filter").build();
        EchoParameter echoParameterB = new EchoParameterBuilder().withId("1").withActivated(true).withParameter("bbox").build();
        EchoParameter echoParameterC = new EchoParameterBuilder().withId("1").withActivated(false).withParameter("bbox").build();
        // get the existing echo parameters, this should return an empty list
        List<EchoParameter> echoParameters = EchoParametersDao.getEchoParameters();
        Assert.assertThat(echoParameters.size(), CoreMatchers.is(0));
        // we save echo parameters A and B
        EchoParametersDao.saveOrUpdateEchoParameter(echoParameterA);
        EchoParametersDao.saveOrUpdateEchoParameter(echoParameterB);
        echoParameters = EchoParametersDao.getEchoParameters();
        Assert.assertThat(echoParameters.size(), CoreMatchers.is(2));
        checkEchoParameter(echoParameterA, findEchoParameter("0", echoParameters));
        checkEchoParameter(echoParameterB, findEchoParameter("1", echoParameters));
        // we update echo parameter B using rule C
        EchoParametersDao.saveOrUpdateEchoParameter(echoParameterC);
        echoParameters = EchoParametersDao.getEchoParameters();
        Assert.assertThat(echoParameters.size(), CoreMatchers.is(2));
        checkEchoParameter(echoParameterA, findEchoParameter("0", echoParameters));
        checkEchoParameter(echoParameterC, findEchoParameter("1", echoParameters));
        // we delete echo parameter A
        EchoParametersDao.deleteEchoParameters("0");
        echoParameters = EchoParametersDao.getEchoParameters();
        Assert.assertThat(echoParameters.size(), CoreMatchers.is(1));
    }
}

