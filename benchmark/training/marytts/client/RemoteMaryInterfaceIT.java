package marytts.client;


import MaryDataType.RAWMARYXML;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;
import marytts.MaryInterface;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class RemoteMaryInterfaceIT {
    private static final int testPort = 59111;

    MaryInterface mary;

    @Test
    public void canGetMaryInterface() throws Exception {
        Assert.assertNotNull(mary);
        Assert.assertEquals("TEXT", mary.getInputType());
        Assert.assertEquals("AUDIO", mary.getOutputType());
        Assert.assertEquals(Locale.US, mary.getLocale());
    }

    @Test
    public void canSetInputType() throws Exception {
        String in = "RAWMARYXML";
        Assert.assertTrue((!(in.equals(mary.getInputType()))));
        mary.setInputType(in);
        Assert.assertEquals(in, mary.getInputType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownInputType() throws Exception {
        mary.setInputType("something strange");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInputType() throws Exception {
        mary.setInputType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notAnInputType() throws Exception {
        mary.setInputType("AUDIO");
    }

    @Test
    public void canSetOutputType() throws Exception {
        String out = "TOKENS";
        Assert.assertTrue((!(out.equals(mary.getOutputType()))));
        mary.setOutputType(out);
        Assert.assertEquals(out, mary.getOutputType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownOutputType() throws Exception {
        mary.setOutputType("something strange");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOutputType() throws Exception {
        mary.setOutputType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notAnOutputType() throws Exception {
        mary.setOutputType("TEXT");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetUnsupportedLocale() throws Exception {
        Locale loc = new Locale("abcde");
        mary.setLocale(loc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNullLocale() throws Exception {
        mary.setLocale(null);
    }

    @Test
    public void canProcessToTokens() throws Exception {
        // setup
        mary.setOutputType("TOKENS");
        // exercise
        Document tokens = mary.generateXML("Hello world");
        // verify
        Assert.assertNotNull(tokens);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuseWrongInput1() throws Exception {
        // setup
        mary.setInputType(RAWMARYXML.name());
        // method with string arg does not match declared input type:
        mary.generateXML("some text");
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuseWrongOutput1() throws Exception {
        // requesting xml output but set to default output type AUDIO:
        mary.generateXML("some text");
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuseWrongOutput2() throws Exception {
        // setup
        mary.setOutputType("TOKENS");
        // requesting audio putput but set to XML output type:
        mary.generateAudio("some text");
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuseWrongOutput3() throws Exception {
        // setup
        mary.setOutputType("TOKENS");
        // requesting text putput but set to XML output type:
        mary.generateText("some text");
    }

    @Test
    public void canProcessTokensToAllophones() throws Exception {
        // setup
        Document tokens = getExampleTokens();
        Assert.assertNotNull(tokens);
        mary.setInputType("TOKENS");
        mary.setOutputType("ALLOPHONES");
        // exercise
        Document allos = mary.generateXML(tokens);
        // verify
        Assert.assertNotNull(allos);
    }

    @Test
    public void convertTextToAcoustparams() throws Exception {
        mary.setOutputType("ACOUSTPARAMS");
        Document doc = mary.generateXML("Hello world");
        Assert.assertNotNull(doc);
    }

    @Test
    public void convertTextToTargetfeatures() throws Exception {
        mary.setOutputType("TARGETFEATURES");
        String tf = mary.generateText("Hello world");
        Assert.assertNotNull(tf);
    }

    @Test
    public void convertTokensToTargetfeatures() throws Exception {
        Document tokens = getExampleTokens();
        mary.setInputType("TOKENS");
        mary.setOutputType("TARGETFEATURES");
        String tf = mary.generateText(tokens);
        Assert.assertNotNull(tf);
    }

    @Test
    public void canSelectTargetfeatures() throws Exception {
        String input = "Hello world";
        mary.setOutputType("TARGETFEATURES");
        String allFeatures = mary.generateText(input);
        String featureNames = "phone stressed";
        mary.setOutputTypeParams(featureNames);
        String selectedFeatures = mary.generateText(input);
        Assert.assertTrue((!(allFeatures.equals(selectedFeatures))));
        Assert.assertTrue(((allFeatures.length()) > (selectedFeatures.length())));
    }

    @Test
    public void canProcessTextToSpeech() throws Exception {
        mary.setVoice("cmu-slt-hsmm");
        AudioInputStream audio = mary.generateAudio("Hello world");
        Assert.assertNotNull(audio);
    }

    @Test
    public void canProcessTokensToSpeech() throws Exception {
        mary.setInputType("TOKENS");
        Document doc = getExampleTokens();
        AudioInputStream audio = mary.generateAudio(doc);
        Assert.assertNotNull(audio);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetInvalidVoiceName() throws Exception {
        mary.setVoice("abcde");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNullVoiceName() throws Exception {
        mary.setVoice(null);
    }
}

