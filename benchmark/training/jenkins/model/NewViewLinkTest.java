package jenkins.model;


import NewViewLink.ICON_FILE_NAME;
import hudson.model.Action;
import hudson.model.View;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static NewViewLink.URL_NAME;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ NewViewLink.class, Jenkins.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class NewViewLinkTest {
    @Mock
    private Jenkins jenkins;

    @Mock
    private final String rootUrl = "https://127.0.0.1:8080/";

    private NewViewLink newViewLink;

    private View view = Mockito.mock(View.class);

    @Test
    public void getActionsHasPermission() throws Exception {
        Mockito.when(view.hasPermission(ArgumentMatchers.any())).thenReturn(true);
        final List<Action> actions = newViewLink.createFor(view);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.get(0);
        Assert.assertEquals(Messages.NewViewLink_NewView(), action.getDisplayName());
        Assert.assertEquals(ICON_FILE_NAME, action.getIconFileName());
        Assert.assertEquals(((rootUrl) + (URL_NAME)), action.getUrlName());
    }

    @Test
    public void getActionsNoPermission() throws Exception {
        Mockito.when(view.hasPermission(ArgumentMatchers.any())).thenReturn(false);
        final List<Action> actions = newViewLink.createFor(view);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.get(0);
        Assert.assertNull(action.getDisplayName());
        Assert.assertNull(action.getIconFileName());
        Assert.assertEquals(((rootUrl) + (URL_NAME)), action.getUrlName());
    }
}

