package net.chiappone.elastic.plugin.util;

import java.io.File;
import java.util.TimerTask;

/**
 * @author Kurtis Chiappone
 */
public abstract class FileModifiedMonitor extends TimerTask {

    private File file = null;
    private long timestamp;

    public FileModifiedMonitor( File file ) {

        this.file = file;
        this.timestamp = file.lastModified();
    }

    public File getFile() {

        return file;
    }

    public long getTimestamp() {

        return timestamp;
    }

    private void setTimestamp( long timestamp ) {

        this.timestamp = timestamp;
    }

    /**
     * Action to take when the file changes.
     */
    protected abstract void onFileChange();

    @Override
    public final void run() {

        long lastModified = getFile().lastModified();

        if ( getTimestamp() != lastModified ) {

            setTimestamp( lastModified );
            onFileChange();

        }

    }

}