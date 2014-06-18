import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import config.Config;
import config.Settings;


public class ConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFtpFolder() {
		assertEquals("/public_html", Config.getInstance().getString(Settings.FTP_FOLDER));
	}

	@Test
	public void testIsFtpFilesPool() {
		assertEquals(true, Config.getInstance().getBoolean(Settings.IS_FTP_FILES_POOL));
	}
}
