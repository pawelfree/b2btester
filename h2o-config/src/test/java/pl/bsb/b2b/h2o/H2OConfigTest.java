package pl.bsb.b2b.h2o;

import java.util.Properties;
import org.junit.Test;
import pl.bsb.b2b.h2o.util.H2OConfig;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class H2OConfigTest {

    @Test
    public void getMessageParametersTest() {
        Properties params = H2OConfig.getMessageParameters();
        assertNotNull("Parameters empty",params);
        assertTrue(params.getProperty("cpaId").equals("cpaId"));
    }
}
