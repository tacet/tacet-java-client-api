package org.tacet.nodeagentapi;

import java.io.File;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class FreeDiskSpaceMeter implements Meter<Long> {
    private File directory;

    public FreeDiskSpaceMeter(File directory) {
        this.directory = directory;
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("File does not exist or is not a directory");
        }
    }

    @Override
    public ValueMeasurement<Long> measure() {
        return ValueMeasurement.newInstance("disk-space", directory.toString(), directory.getUsableSpace());
    }
    
}
