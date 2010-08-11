package org.tacet.nodeagentapi.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class NetworkHelper {

    public static String getHostName() {
        List<String> addresses = new ArrayList<String>();
        try {
            for (NetworkInterface networkInterface : EnumerationWrapper.from(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isLoopback() && !networkInterface.isVirtual() && networkInterface.isUp()) {
                    for (InetAddress inetAddress : EnumerationWrapper.from(networkInterface.getInetAddresses())) {
                        if (inetAddress instanceof Inet4Address) {
                            addresses.add(inetAddress.getHostAddress());
                        }
                    }
                }
            }
            if (addresses.size() != 0) {
                Collections.sort(addresses);
                return addresses.get(0);
            }
        } catch (SocketException e) {
            throw new RuntimeException("Unable to resolve host.name", e);
        }
        throw new RuntimeException("No trustworthy host.name found");
    }


}
