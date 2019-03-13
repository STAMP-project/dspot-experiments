package org.docx4j.model.fields;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.docx4j.XmlUtils;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.junit.Assert;
import org.junit.Test;


public class CanonicalisationTests {
    @Test
    public void testBookmarked() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_bookmarked.xml"), fieldRefs);
        String xml = XmlUtils.marshaltoString(resultP, true, true);
        // bookmarks still present
        Assert.assertTrue(xml.contains("<w:bookmarkStart"));
        Assert.assertTrue(xml.contains("<w:bookmarkEnd"));
        // result slot contents
        FieldRef fieldRef1 = fieldRefs.get(0);
        R r = fieldRef1.getResultsSlot();
        Text text = ((Text) (XmlUtils.unwrap(r.getContent().get(0))));
        Assert.assertTrue(text.getValue().equals("(Customer)"));
    }

    @Test
    public void testFORMTEXTwithDefault() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_FORMTEXT_default.xml"), fieldRefs);
        // Should contain 3 runs
        Assert.assertTrue(((resultP.getContent().size()) == 3));
        // 2nd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(1)) == (fieldRef1.getResultsSlot())));
    }

    @Test
    public void testFORMTEXTwithSpaces() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_FORMTEXT-spaces.xml"), fieldRefs);
        // Should contain 3 runs
        Assert.assertTrue(((resultP.getContent().size()) == 3));
        // 2nd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(1)) == (fieldRef1.getResultsSlot())));
        // System.out.println(XmlUtils.marshaltoString(fieldRef1.getResultsSlot(), true, true));
    }

    @Test
    public void testFORMTEXTdefault_trail() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_FORMTEXTdefault_trail.xml"), fieldRefs);
        // Should contain 3 + 1 runs
        Assert.assertTrue(((resultP.getContent().size()) == 4));
        // 2nd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(1)) == (fieldRef1.getResultsSlot())));
    }

    @Test
    public void testLeadFORMTEXTdefault() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_lead_FORMTEXTdefault.xml"), fieldRefs);
        // Should contain 1 + 3 runs
        Assert.assertTrue(((resultP.getContent().size()) == 4));
        // 3rd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(2)) == (fieldRef1.getResultsSlot())));
    }

    @Test
    public void testLeadMERGEFIELDMergeFormat() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_lead_MERGEFIELD-MERGEFORMAT.xml"), fieldRefs);
        // Should contain 1 + 3 + 1 runs
        Assert.assertTrue(((resultP.getContent().size()) == 5));
        // 3rd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(2)) == (fieldRef1.getResultsSlot())));
    }

    @Test
    public void testLeadMERGEFIELDUpper() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_lead_MERGEFIELDUpper_tail.xml"), fieldRefs);
        // Should contain 1 + 3 + 1 runs
        Assert.assertTrue(((resultP.getContent().size()) == 5));
        // 3rd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(2)) == (fieldRef1.getResultsSlot())));
    }

    @Test
    public void testLeadMERGEFIELD() throws IOException, JAXBException {
        List<FieldRef> fieldRefs = new ArrayList<FieldRef>();
        P resultP = FieldsPreprocessor.canonicalise(getP("Canon_MERGEFIELD.xml"), fieldRefs);
        // Should contain 3 runs
        Assert.assertTrue(((resultP.getContent().size()) == 3));
        // 2nd run is the result slot
        FieldRef fieldRef1 = fieldRefs.get(0);
        Assert.assertTrue(((resultP.getContent().get(1)) == (fieldRef1.getResultsSlot())));
    }
}

