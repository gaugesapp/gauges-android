/*
 *  Copyright (C) 2011 Roderick Baier
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

/*
 * 09/02/2011 - Emory Myers - 	printing stacktraces for debugging purposes
 * 								added some logging
 * 								implemented isConnected method
 * 								modified send method
 */

package de.roderick.weberknecht;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import android.util.Log;

public class WebSocketConnection implements WebSocket {
	private URI url = null;
	private WebSocketEventHandler eventHandler = null;

	private volatile boolean connected = false;

	private Socket socket = null;
	private InputStream input = null;
	private PrintStream output = null;

	private WebSocketReceiver receiver = null;
	private WebSocketHandshake handshake = null;

	public WebSocketConnection(URI url) throws WebSocketException {
		this(url, null);
	}

	public WebSocketConnection(URI url, String protocol)
			throws WebSocketException {
		this.url = url;
		handshake = new WebSocketHandshake(url, protocol);
	}

	public void setEventHandler(WebSocketEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public WebSocketEventHandler getEventHandler() {
		return this.eventHandler;
	}

	public void connect() throws WebSocketException {
		try {
			if (connected) {
				throw new WebSocketException("already connected");
			}

			socket = createSocket();
			input = socket.getInputStream();
			output = new PrintStream(socket.getOutputStream());

			output.write(handshake.getHandshake());

			boolean handshakeComplete = false;
			boolean header = true;
			int len = 1000;
			byte[] buffer = new byte[len];
			int pos = 0;
			ArrayList<String> handshakeLines = new ArrayList<String>();

			byte[] serverResponse = new byte[16];

			while (!handshakeComplete) {
				int b = input.read();
				buffer[pos] = (byte) b;
				pos += 1;

				if (!header) {
					serverResponse[pos - 1] = (byte) b;
					if (pos == 16) {
						handshakeComplete = true;
					}
				} else if (buffer[pos - 1] == 0x0A && buffer[pos - 2] == 0x0D) {
					String line = new String(buffer, "UTF-8");
					if (line.trim().equals("")) {
						header = false;
					} else {
						handshakeLines.add(line.trim());
					}

					buffer = new byte[len];
					pos = 0;
				}
			}

			handshake.verifyServerStatusLine(handshakeLines.get(0));
			handshake.verifyServerResponse(serverResponse);

			handshakeLines.remove(0);

			HashMap<String, String> headers = new HashMap<String, String>();
			for (String line : handshakeLines) {
				String[] keyValue = line.split(": ", 2);
				headers.put(keyValue[0], keyValue[1]);
			}
			handshake.verifyServerHandshakeHeaders(headers);

			receiver = new WebSocketReceiver(input, this);
			receiver.start();
			connected = true;
			eventHandler.onOpen();
		} catch (WebSocketException wse) {
			wse.printStackTrace();
			throw wse;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new WebSocketException("error while connecting: "
					+ ioe.getMessage(), ioe);
		}
	}

	public synchronized void send(String data) throws WebSocketException {
		if (!connected) {
			throw new WebSocketException(
					"error while sending text data: not connected");
		}

		try {
			output.write(0x00);
			output.write(data.getBytes(("UTF-8")));
			output.write(0xff);
			// output.write("\r\n".getBytes());
			Log.d("Output", "Wrote to Output");
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
			throw new WebSocketException(
					"error while sending text data: unsupported encoding", uee);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new WebSocketException("error while sending text data", ioe);
		}
	}

	// public synchronized void send(byte[] data)
	// throws WebSocketException
	// {
	// if (!connected) {
	// throw new
	// WebSocketException("error while sending binary data: not connected");
	// }
	//
	// try {
	// output.write(0x80);
	// output.write(data.length);
	// output.write(data);
	// output.write("\r\n".getBytes());
	// }
	// catch (IOException ioe) {
	// throw new WebSocketException("error while sending binary data: ", ioe);
	// }
	// }

	public void handleReceiverError() {
		try {
			if (connected) {
				close();
			}
		} catch (WebSocketException wse) {
			wse.printStackTrace();
		}
	}

	public synchronized void close() throws WebSocketException {
		Log.d("Close", "WebSocket closed");

		if (!connected) {
			return;
		}

		sendCloseHandshake();

		if (receiver.isRunning()) {
			receiver.stopit();
		}

		closeStreams();

		eventHandler.onClose();
	}

	private synchronized void sendCloseHandshake() throws WebSocketException {
		if (!connected) {
			throw new WebSocketException(
					"error while sending close handshake: not connected");
		}

		try {
			output.write(0xff00);
			output.write("\r\n".getBytes());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new WebSocketException("error while sending close handshake",
					ioe);
		}

		connected = false;
	}

	private Socket createSocket() throws WebSocketException {
		String scheme = url.getScheme();
		String host = url.getHost();
		int port = url.getPort();

		Socket socket = null;

		if (scheme != null && scheme.equals("ws")) {
			if (port == -1) {
				port = 80;
			}
			try {
				socket = new Socket(host, port);
				socket.setKeepAlive(true);
				socket.setSoTimeout(0);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				throw new WebSocketException("unknown host: " + host, uhe);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw new WebSocketException("error while creating socket to "
						+ url, ioe);
			}
		} else if (scheme != null && scheme.equals("wss")) {
			if (port == -1) {
				port = 443;
			}
			try {
				SocketFactory factory = SSLSocketFactory.getDefault();
				socket = factory.createSocket(host, port);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				throw new WebSocketException("unknown host: " + host, uhe);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw new WebSocketException(
						"error while creating secure socket to " + url, ioe);
			}
		} else {
			throw new WebSocketException("unsupported protocol: " + scheme);
		}

		return socket;
	}

	private void closeStreams() throws WebSocketException {
		Log.d("ClosedStreams", "closeStreams");

		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new WebSocketException(
					"error while closing websocket connection: ", ioe);
		}
	}

	public boolean isConnected() {
		return connected;
	}
}
