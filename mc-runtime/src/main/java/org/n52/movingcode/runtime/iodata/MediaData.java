package org.n52.movingcode.runtime.iodata;

import java.io.InputStream;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class MediaData {

    private InputStream data;
    private String mimeType;

    public MediaData() {
        super();
    }

    public MediaData(InputStream mediaStream, String mimeType) {
        super();
        this.mimeType = mimeType;
        this.data = mediaStream;
    }

    public InputStream getMediaStream() {
        return this.data;
    }

    public void setMediaStream(InputStream mediaStream) {
        this.data = mediaStream;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
