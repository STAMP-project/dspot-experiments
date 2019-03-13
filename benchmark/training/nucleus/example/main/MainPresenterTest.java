package nucleus.example.main;


import ServerAPI.Item;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import javax.inject.Singleton;
import nucleus.example.base.App;
import nucleus.example.base.MainThread;
import nucleus.example.base.ServerAPI;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import rx.Scheduler;
import rx.schedulers.TestScheduler;


public class MainPresenterTest {
    private static final String TEST_TEXT = "test text";

    public static final String FIRST_NAME = "Marilyn";

    public static final String LAST_NAME = "Manson";

    @Module(injects = MainPresenter.class)
    public class MainPresenterTestModule {
        @Singleton
        @Provides
        ServerAPI provideServerAPI() {
            return serverAPIMock;
        }

        @Singleton
        @Provides
        @MainThread
        Scheduler provideScheduler() {
            return testScheduler;
        }
    }

    ServerAPI serverAPIMock;

    TestScheduler testScheduler;

    @Test
    public void testRequest() throws Throwable {
        createServerApiMock();
        createTestScheduler();
        App.setObjectGraph(ObjectGraph.create(new MainPresenterTest.MainPresenterTestModule()));
        MainPresenter presenter = new MainPresenter();
        presenter.onCreate(null);
        presenter.request((((MainPresenterTest.FIRST_NAME) + " ") + (MainPresenterTest.LAST_NAME)));
        MainActivity mainActivity = Mockito.mock(MainActivity.class);
        presenter.takeView(mainActivity);
        testScheduler.triggerActions();
        Mockito.verify(serverAPIMock).getItems(MainPresenterTest.FIRST_NAME, MainPresenterTest.LAST_NAME);
        Mockito.verify(mainActivity).onItems(ArgumentMatchers.argThat(new ArgumentMatcher<ServerAPI[]>() {
            @Override
            public boolean matches(Object argument) {
                return ((ServerAPI[]) (argument))[0].text.equals(MainPresenterTest.TEST_TEXT);
            }
        }), ArgumentMatchers.anyString());
    }
}

