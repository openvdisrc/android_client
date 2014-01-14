package com.openvdi.remoteclient.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

class MultipartEntity implements HttpEntity {

	private String boundary = "ZiNgMeEmAiL";

	private boolean isCancel = false;

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	File file;

	private boolean isChunk = false;

	byte [] postDataBytes;
	byte [] headerDataBytes;
	byte [] endDataBytes;

	private long byteToWrite;
	private long skip_offset = 0;

	private UploadListener listener;

	public UploadListener getListener() {
		return listener;
	}

	public void setListener(UploadListener listener) {
		this.listener = listener;
	}

	public MultipartEntity(File file,
			byte[] headerDataBytes,
			byte[] postDataBytes,
			byte[] endDataBytes) {

		this.isChunk = false;
		this.file = file;

		this.headerDataBytes = headerDataBytes;
		this.postDataBytes = postDataBytes;
		this.endDataBytes = endDataBytes;
	}

	public MultipartEntity(File file,
			byte[] headerDataBytes,
			byte[] postDataBytes,
			byte[] endDataBytes,
			long skip_offset,
			long byteToWrite) {
		this.isChunk = true;
		this.file = file;

		this.headerDataBytes = headerDataBytes;
		this.postDataBytes = postDataBytes;
		this.endDataBytes = endDataBytes;

		this.skip_offset = skip_offset;
		this.byteToWrite = byteToWrite;
	}

	@Override
	public long getContentLength() {
		return postDataBytes.length + headerDataBytes.length + file.length() + endDataBytes.length;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {

		if(isChunk)//Upload chunk
		{
			FileInputStream fileInputStream = null;
			BufferedOutputStream os = null;
			try
			{
				fileInputStream = new FileInputStream(file);
				os = new BufferedOutputStream(outstream);
				os.write(postDataBytes);
				os.write(headerDataBytes);

				long byteWrite = byteToWrite;
				fileInputStream.skip(skip_offset);
				while (byteWrite > 0)
				{
					if(isCancel)
					{
						break;
					}

					int bufferSize = (int)Math.min(byteWrite, 4096);
					byte[] buffer = new byte[bufferSize];
					int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					os.write(buffer);

					byteWrite -= bufferSize;
				}
				os.write(endDataBytes);
				os.flush();
				os.close();
			}
			catch(Exception e){
				if(listener != null)
					listener.onErrorData(e.toString());
				e.printStackTrace();
			}
			finally{
				if(fileInputStream != null)
				{
					fileInputStream.close();
					fileInputStream = null;
				}

				if(os != null)
				{
					os.close();
					os = null;
				}

			}
		}
		else
		{
			FileInputStream fileInputStream = null;
			BufferedOutputStream os = null;
			try
			{
				fileInputStream = new FileInputStream(file);
				os = new BufferedOutputStream(outstream);
				os.write(postDataBytes);
				os.write(headerDataBytes);

				long length = file.length();
				int offset = 0;
				int bytesAvailable;
				while((bytesAvailable = fileInputStream.available()) > 0)
				{
					if(isCancel)
					{
						break;
					}

					int bufferSize = Math.min(bytesAvailable, 4096);
					byte[] buffer = new byte[bufferSize];
					int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					os.write(buffer);
					offset += bufferSize;
					long percent = (offset*100)/length;
					if (listener != null) {
						listener.progress(percent, percent + "%");
					}
				}
				os.write(endDataBytes);
				os.flush();
				os.close();
			}
			catch(Exception e){
				if(listener != null)
					listener.onErrorData(e.toString());
				e.printStackTrace();
			}
			finally{
				if(fileInputStream != null)
				{
					fileInputStream.close();
					fileInputStream = null;
				}

				if(os != null)
				{
					os.close();
					os = null;
				}
			}

		}

	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public void consumeContent() throws IOException,
	UnsupportedOperationException {
		if (isStreaming()) {
			throw new UnsupportedOperationException(
			"Streaming entity does not implement #consumeContent()");
		}
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	//	@Override
	//	public InputStream getContent() throws IOException,
	//	UnsupportedOperationException {
	//		return new ByteArrayInputStream(out.toByteArray());
	//	}
}