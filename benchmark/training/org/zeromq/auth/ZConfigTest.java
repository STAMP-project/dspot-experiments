package org.zeromq.auth;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZConfig;


public class ZConfigTest {
    private static final String TEST_FOLDER = "target/testCertFolder";

    private static ZConfig conf = new ZConfig("root", null);

    @Test
    public void testPutGet() {
        Assert.assertThat(ZConfigTest.conf.getValue("/curve/public-key"), CoreMatchers.is("abcdefg"));
        // intentionally checking without leading /
        Assert.assertThat(ZConfigTest.conf.getValue("curve/secret-key"), CoreMatchers.is("(w3lSF/5yv&j*c&0h{4JHe(CETJSksTr.QSjcZE}"));
        Assert.assertThat(ZConfigTest.conf.getValue("/metadata/name"), CoreMatchers.is("key-value tests"));
        // checking default value
        Assert.assertThat(ZConfigTest.conf.getValue("/metadata/nothinghere", "default"), CoreMatchers.is("default"));
    }

    @Test
    public void testLoadSave() throws IOException {
        ZConfigTest.conf.save(((ZConfigTest.TEST_FOLDER) + "/test.cert"));
        Assert.assertThat(isFileInPath(ZConfigTest.TEST_FOLDER, "test.cert"), CoreMatchers.is(true));
        ZConfig loadedConfig = ZConfig.load(((ZConfigTest.TEST_FOLDER) + "/test.cert"));
        // Object obj = loadedConfig.getValue("/curve/public-key");
        Assert.assertThat(loadedConfig.getValue("/curve/public-key"), CoreMatchers.is("abcdefg"));
        // intentionally checking without leading /
        Assert.assertThat(loadedConfig.getValue("curve/secret-key"), CoreMatchers.is("(w3lSF/5yv&j*c&0h{4JHe(CETJSksTr.QSjcZE}"));
        Assert.assertThat(loadedConfig.getValue("/metadata/name"), CoreMatchers.is("key-value tests"));
    }

    @Test
    public void testZPLSpecialCases() throws IOException {
        // this file was generated in the init-method and tests some cases that should be processed by the loader but are not
        // created with our writer.
        ZConfig zplSpecials = ZConfig.load(((ZConfigTest.TEST_FOLDER) + "/test.zpl"));
        // test leading quotes
        Assert.assertThat(zplSpecials.getValue("meta/leadingquote"), CoreMatchers.is("\"abcde"));
        // test ending quotes
        Assert.assertThat(zplSpecials.getValue("meta/endingquote"), CoreMatchers.is("abcde\""));
        // test full doublequoted. here the quotes should be removed
        Assert.assertThat(zplSpecials.getValue("meta/quoted"), CoreMatchers.is("abcde"));
        // test full singlequoted. here the quotes should be removed
        Assert.assertThat(zplSpecials.getValue("meta/singlequoted"), CoreMatchers.is("abcde"));
        // test no quotes tcp-pattern
        Assert.assertThat(zplSpecials.getValue("meta/bind"), CoreMatchers.is("tcp://eth0:5555"));
        // test comment after value
        Assert.assertThat(zplSpecials.getValue("meta/verbose"), CoreMatchers.is("1"));
        // test comment after container-name
        Assert.assertThat(zplSpecials.pathExists("meta/sub"), CoreMatchers.is(true));
    }

    @Test
    public void testReadReference() throws IOException {
        ZConfig ref = ZConfig.load(((ZConfigTest.TEST_FOLDER) + "/reference.zpl"));
        Assert.assertThat(ref.getValue("context/iothreads"), CoreMatchers.is("1"));
        Assert.assertThat(ref.getValue("context/verbose"), CoreMatchers.is("1"));
        Assert.assertThat(ref.getValue("main/type"), CoreMatchers.is("zqueue"));
        Assert.assertThat(ref.getValue("main/frontend/option/hwm"), CoreMatchers.is("1000"));
        Assert.assertThat(ref.getValue("main/frontend/option/swap"), CoreMatchers.is("25000000"));
        Assert.assertThat(ref.getValue("main/frontend/bind"), CoreMatchers.is("ipc://addr2"));
        Assert.assertThat(ref.getValue("main/backend/bind"), CoreMatchers.is("inproc://addr3"));
    }
}

