package com.luke.volley.data;

/**
 * Created by cplu on 2016/6/24.
 */
public class FileResponse {
	private long size;
	private long byteStart;
	private long bytesReceived;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getByteStart() {
		return byteStart;
	}

	public void setByteStart(long byteStart) {
		this.byteStart = byteStart;
	}

	public long getBytesReceived() {
		return bytesReceived;
	}

	public void setBytesReceived(long bytesReceived) {
		this.bytesReceived = bytesReceived;
	}
}
