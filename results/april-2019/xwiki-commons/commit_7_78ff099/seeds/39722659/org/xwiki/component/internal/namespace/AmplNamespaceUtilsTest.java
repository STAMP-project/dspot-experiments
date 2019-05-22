package org.xwiki.component.internal.namespace;


import org.junit.Assert;
import org.junit.Test;
import org.xwiki.component.namespace.Namespace;
import org.xwiki.component.namespace.NamespaceUtils;


public class AmplNamespaceUtilsTest {
    @Test(timeout = 10000)
    public void toNamespace_literalMutationString139() throws Exception {
        Namespace o_toNamespace_literalMutationString139__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString139__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString139__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString139__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString139__5 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString139__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString139__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString139__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString139__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString139__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString139__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString139__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString139__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString139__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString139__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__3)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString139__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString139__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString139__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString145() throws Exception {
        Namespace o_toNamespace_literalMutationString145__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString145__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString145__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString145__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString145__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString145__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString145__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString145__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString145__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString145__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString145__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString145__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString145__7 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString145__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString145__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString145__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString145__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString145__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString145__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString145__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString145__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString145__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString145__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString145__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString145__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString145__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString145__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString145__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString145__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString145__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString145__5)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString145__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString145__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString145__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString145__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString133() throws Exception {
        Namespace o_toNamespace_literalMutationString133__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString133__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString133__3 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString133__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString133__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString133__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString133__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString133__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString133__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString133__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString133__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString133__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString133__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString133__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString133__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString133__1);
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString133__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString133__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString133__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString133__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString133__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString133__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString133__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString152() throws Exception {
        Namespace o_toNamespace_literalMutationString152__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString152__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString152__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString152__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString152__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString152__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString152__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString152__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString152__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString152__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString152__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString152__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString152__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString152__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString152__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString152__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString152__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString152__9 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString152__9)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString152__9)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString152__9)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString152__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString152__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString152__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString152__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString152__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString152__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString152__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString152__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString152__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString152__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString152__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString152__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString152__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString152__7)).getType());
    }
}

