package com.emorym.android_pusher;

import org.json.JSONObject;

public class PusherCallback
{
	public void onEvent(String eventName, JSONObject eventData)
	{
		// implement me for your bindAll(callback) callbacks
	}
	
	public void onEvent(JSONObject eventData)
	{
		// implement me for your bind(eventName, callback) callbacks
	}
}
