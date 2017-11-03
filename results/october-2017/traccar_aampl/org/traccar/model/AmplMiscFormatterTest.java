package org.traccar.model;


public class AmplMiscFormatterTest {
    @org.junit.Test
    public void testToString() throws java.lang.Exception {
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        org.junit.Assert.assertEquals("<info><a>3</a><b>2</b></info>", org.traccar.model.MiscFormatter.toXmlString(position.getAttributes()));
    }
}

