package org.tacet.nodeagentapi.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class NetworkHelperTest {

    @Test
    public void get_host_name_does_not_report_an_ip_address() {
        assertTrue(NetworkHelper.getHostIp().matches("(\\d+\\.){3}\\d+"));
    }

}
