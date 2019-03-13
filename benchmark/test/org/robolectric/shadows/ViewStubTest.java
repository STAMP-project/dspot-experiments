package org.robolectric.shadows;


import android.R.attr.inflatedId;
import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.id.include_id;
import static org.robolectric.R.layout.media;


@RunWith(AndroidJUnit4.class)
public class ViewStubTest {
    private Context ctxt;

    @Test
    public void inflate_shouldReplaceOriginalWithLayout() throws Exception {
        ViewStub viewStub = new ViewStub(ctxt);
        int stubId = 12345;
        int inflatedId = 12346;
        viewStub.setId(stubId);
        viewStub.setInflatedId(inflatedId);
        viewStub.setLayoutResource(media);
        LinearLayout root = new LinearLayout(ctxt);
        root.addView(new View(ctxt));
        root.addView(viewStub);
        root.addView(new View(ctxt));
        View inflatedView = viewStub.inflate();
        Assert.assertNotNull(inflatedView);
        Assert.assertSame(inflatedView, root.findViewById(inflatedId));
        Assert.assertNull(root.findViewById(stubId));
        Assert.assertEquals(1, root.indexOfChild(inflatedView));
        Assert.assertEquals(3, root.getChildCount());
    }

    @Test
    public void shouldApplyAttributes() throws Exception {
        ViewStub viewStub = new ViewStub(ctxt, Robolectric.buildAttributeSet().addAttribute(inflatedId, "@+id/include_id").addAttribute(android.R.attr.layout, "@layout/media").build());
        assertThat(viewStub.getInflatedId()).isEqualTo(include_id);
        assertThat(viewStub.getLayoutResource()).isEqualTo(media);
    }
}

