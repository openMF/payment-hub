package hu;

import hu.dpc.rt.psp.config.HubSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HubSettingsTest.TestConfiguration.class })
public class HubSettingsTest {

    @Autowired
    private HubSettings hubSettings;

    @Test
    public void getInstance() {
        assertEquals("in01",hubSettings.getInstance());
    }

    @Test
    public void getExpiration() {
        assertEquals("30000",hubSettings.getExpiration().toString());
    }

    @Test
    public void getTenants_0() {
        assertEquals("tn03",hubSettings.getTenants().get(0));
    }

    @Test
    public void getTenants_1() {
        assertEquals("tn04",hubSettings.getTenants().get(1));
    }

    @EnableConfigurationProperties(HubSettings.class)
    public static class TestConfiguration {
        // nothing
    }
}