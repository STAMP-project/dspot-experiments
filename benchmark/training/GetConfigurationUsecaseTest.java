

import com.hackvg.domain.ConfigurationUsecase;
import com.hackvg.model.MediaDataSource;
import com.squareup.otto.Bus;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Created by saulmm on 19/02/15.
 */
public class GetConfigurationUsecaseTest {
    // Class under test
    private ConfigurationUsecase configurationUsecase;

    @Mock
    private MediaDataSource mockDataSource;

    @Mock
    private Bus mockUiBus;

    @Test
    public void testConfigurationRequestExecution() {
        configurationUsecase.execute();
        Mockito.verify(mockDataSource, Mockito.times(1)).getConfiguration();
    }
}

