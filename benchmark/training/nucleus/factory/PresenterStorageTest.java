package nucleus.factory;


import PresenterStorage.INSTANCE;
import nucleus.presenter.Presenter;
import org.junit.Assert;
import org.junit.Test;


public class PresenterStorageTest {
    @Test
    public void testSavePresenter() throws Exception {
        Presenter presenter = new Presenter();
        INSTANCE.add(presenter);
        Presenter presenter2 = new Presenter();
        INSTANCE.add(presenter2);
        String id = INSTANCE.getId(presenter);
        Assert.assertNotEquals(id, INSTANCE.getId(presenter2));
        Assert.assertEquals(presenter, INSTANCE.getPresenter(id));
        presenter.destroy();
        Assert.assertNull(INSTANCE.getPresenter(id));
    }
}

