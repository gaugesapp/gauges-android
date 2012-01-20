package com.emorym.android_pusher;

/*	Copyright (C) 2011 Emory Myers 
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketConnection;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketMessage;

public class Pusher
{
	protected static final long WATCHDOG_SLEEP_TIME_MS = 5000;
	private final String VERSION = "1.8.3";
	private final String HOST = "ws.pusherapp.com";
	
	private final int WS_PORT = 80;
	private final int WSS_PORT = 443;
	
	private final String HTTP_PREFIX = "ws://";
	private final String HTTPS_PREFIX = "wss://";

	private WebSocket mWebSocket = null;
	private final Handler mHandler;
	private Thread mWatchdog; // handles reconnecting
	private String mSocketId;
	private String mApplicationkey;
	private boolean mEncrypted;
	
	public final HashMap<String, Channel> channels = new HashMap<String, Channel>();
	public final Channel globalChannel = new Channel("pusher_global_channel");

	public Pusher( String application_key, boolean encrypted )
	{
		mApplicationkey = application_key;
		mEncrypted = encrypted;
		mHandler = new PusherHandler(this);
		connect();
	}
	
	public Pusher( String application_key )
	{
		this(application_key, true );
	}

	@Deprecated
	public Pusher(Handler _mHandler, boolean encrypted)
	{
		// So we can get our messages back to whatever created this
		mHandler = _mHandler;
		mEncrypted = encrypted;
	}

	@Deprecated
	public Pusher(Handler _mHandler)
	{
		this(_mHandler, true);
	}

	public void disconnect()
	{
		try
		{
			mWatchdog.interrupt();
			mWatchdog = null;
			mWebSocket.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public Pusher bind(String event, PusherCallback callback)
	{
		globalChannel.bind( event, callback );
		
		return this;
	}
	
	public Pusher bindAll(PusherCallback callback)
	{
		globalChannel.bindAll( callback );
		
		return this;
	}

	public Pusher unbind(String event)
	{
		globalChannel.unbind( event );
		
		return this;
	}

	public Channel subscribe( String channelName )
	{
		Channel c = new Channel( channelName );

		if( mWebSocket != null && mWebSocket.isConnected() )
		{
			try
			{
				sendSubscribeMessage( c );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		channels.put( channelName, c );
		
		return c;
	}
	
	public void unsubscribe( String channelName )
	{
		if( channels.containsKey( channelName ) )
		{
			if( mWebSocket != null && mWebSocket.isConnected() )
			{
				try
				{
					sendUnsubscribeMessage( channels.get( channelName ) );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}

			channels.remove( channelName );
		}
	}

	private void subscribeToAllChannels()
	{
		try
		{
			for( String channelName : channels.keySet() )
			{
				sendSubscribeMessage( channels.get( channelName ) );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void sendSubscribeMessage( Channel c )
	{
		JSONObject data = new JSONObject();

		send( "pusher:subscribe", data, c.name );
	}

	private void sendUnsubscribeMessage( Channel c )
	{
		JSONObject data = new JSONObject();

		send( "pusher:unsubscribe", data, c.name );
	}

	public void send( String event_name, JSONObject data, String channel )
	{
		JSONObject message = new JSONObject();

		try
		{
			data.put( "channel", channel );
			message.put( "event", event_name );
			message.put( "data", data );
			Log.d( "Message", message.toString() );
			mWebSocket.send( message.toString() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void connect()
	{
		String prefix = mEncrypted ? HTTPS_PREFIX : HTTP_PREFIX;
		int port      = mEncrypted ? WSS_PORT : WS_PORT;
		
		String path = "/app/" + mApplicationkey + "?client=js&version=" + VERSION;

		try
		{
			URI url = new URI( prefix + HOST + ":" + port + path );
			Log.d( "Connecting", url.toString() );
			mWebSocket = new WebSocketConnection( url );
			mWebSocket.setEventHandler( new WebSocketEventHandler()
			{
				public void onOpen()
				{
					Log.d( "Open", "WebSocket Open" );
					subscribeToAllChannels();
				}

				public void onMessage( WebSocketMessage message )
				{
					try
					{
						Log.d( "Message", message.getText() );

						JSONObject jsonMessage = new JSONObject( message.getText() );

						String event = jsonMessage.optString( "event", null );

						if( event.equals( "pusher:connection_established" ) )
						{
							JSONObject data = new JSONObject( jsonMessage.getString( "data" ) );

							mSocketId = data.getString( "socket_id" );

							Log.d( "Connection Established", "Socket Id: " + mSocketId );
						}
						else
						{
							Bundle b = new Bundle();
							
							b.putString( "event", event );
							b.putString( "data", jsonMessage.getString( "data" ) );
							
							// backwards compatibility
							b.putString( "type", "pusher" );
							b.putString( "message", message.getText() );
							
							if ( jsonMessage.has( "channel" ) )
							{
								b.putString( "channel", jsonMessage.getString("channel") );
							}

							Message msg = new Message();
							msg.setData( b );
						
							mHandler.sendMessage( msg );
						}
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}

				public void onClose()
				{
					Log.d( "Close", "WebSocket Closed" );
				}
			} );

			mWatchdog = new Thread( new Runnable()
			{
				public void run()
				{
					boolean interrupted = false;
					while (!interrupted)
					{
						try
						{
							Thread.sleep( WATCHDOG_SLEEP_TIME_MS );
							if( !mWebSocket.isConnected() )
								mWebSocket.connect();
						}
						catch( InterruptedException e )
						{
							interrupted = true;
						}
						catch( Exception e )
						{
							e.printStackTrace();
						}
					}
				}
			} );

			mWatchdog.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Deprecated
	public void connect( String application_key, boolean encrypted )
	{
		mApplicationkey = application_key;
		mEncrypted = encrypted;
		connect();
	}

	@Deprecated
	public void connect( String application_key )
	{
		connect( application_key, true );
	}
	
	public class Channel
	{
		public final String name;
		
		public final HashMap<String, List<PusherCallback>> callbacks = new HashMap<String, List<PusherCallback>>();
		public final List<PusherCallback> globalCallbacks = new ArrayList<PusherCallback>();
		
		public Channel(String _name)
		{
			name = _name;
		}
		
		public Channel bind(String event, PusherCallback callback)
		{
			if ( ! callbacks.containsKey( event ) )
			{
				callbacks.put( event, new ArrayList<PusherCallback>() );
			}
			
			callbacks.get( event ).add( callback );
			
			return this;
		}
		
		public Channel bindAll(PusherCallback callback)
		{
			globalCallbacks.add( callback );
			
			return this;
		}

		/**
		 * TODO
		 * unbind should unbind callbacks from events or from the global callback list
		 * instead of whole events, in order to mirror js lib
		 **/
		public Channel unbind(String event)
		{
			if ( callbacks.containsKey( event ) )
			{
				callbacks.remove( event );
			}
			
			return this;
		}
		
		public void dispatch( String eventName, JSONObject eventData )
		{
			for( PusherCallback callback : globalCallbacks )
			{
				callback.onEvent( eventName, eventData );
			}

			if( callbacks.containsKey( eventName ))
			{
				for( PusherCallback callback : callbacks.get( eventName ) )
				{
					callback.onEvent( eventData );
				}
			}
		}
	}

	private class PusherHandler extends Handler
	{
		Pusher pusher;

		public PusherHandler(Pusher pusher)
		{
			this.pusher = pusher;
		}
		
		public void handleMessage(Message msg)
		{
			Log.d("handle", msg.getData().toString());
			
			try
			{
				String eventName = msg.getData().getString("event");
				
				JSONObject eventData = new JSONObject( msg.getData().getString("data") );
				
				pusher.globalChannel.dispatch( eventName, eventData );
				
				if (msg.getData().containsKey("channel"))
				{
					String channelName = msg.getData().getString("channel");
					
					if (pusher.channels.containsKey( channelName ))
					{
						Channel channel = pusher.channels.get( channelName );
						
						channel.dispatch( eventName, eventData );
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
