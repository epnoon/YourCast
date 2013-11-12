package edu.umich.yourcast;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.content.ContextWrapper;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.umich.yourcast.Ycpacket.Event;
import edu.umich.yourcast.Ycpacket.*;

public class EventListener {
	public static final String PTYPE_CREATE = "create_session";
	public static final String PTYPE_BROADCAST = "broadcast";
	public static final String PTYPE_POLL = "poll";
	public static final String PTYPE_UPTODATE = "up_to_date";
	public static final String PTYPE_EVENT = "event";
	public static final String PTYPE_NEWUSER = "new_user";
	public static final String PTYPE_EXCEPTION = "exception";
	public static final String PTYPE_CONFIRM = "confirm_event";
	public static final String PTYPE_GET_SESSIONS = "get_sessions";
	
	private static DatagramSocket socket = null;
	private static int uid;
	private static int session;
	public static int PORT = 30303;
	private static InetAddress address = null;
	private Context ctx;
	private ListView listview;
	public int eventid=0;
	private FragmentManager currentFragment = null;
	
	public EventListener(Context c) {
		this.ctx = c;
	}
	
	public EventListener() {
		this.ctx = null;
	}

	public int Connect (String... params) {
		try {
			new ConnectTask().execute(params);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	

	
	public int broadcast (String message, int x, int y) {
		try {
			new BroadcastTask(message, x, y).execute(message);
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public int get_sessions(WatchGameDialog dialog) {
		try {
			new GetSessionsTask().execute(dialog);
			return 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
	
	public int poll(int session_num, int event_id) {
		try {
			new PollTask().execute(session_num, event_id);
			return 1;
		}
		catch (Exception e){
			e.printStackTrace();
			return 1;
		}
	}
		
	public class PollTask extends AsyncTask<Integer, Boolean, Integer>{
		ArrayAdapter<String> new_array;
		protected Integer doInBackground(Integer... params) {
			int session_num = params[0];
			int event_id = params[1];
			Log.d("MYMY", "polling event "+Integer.toString(event_id)+" session "+Integer.toString(session_num));
			Packet request = Packet.newBuilder()
				.setType(PTYPE_POLL)
				.setUserId(uid)
				.setEventId(event_id)
				.setSessionNum(session_num)
				.build();
			byte[] serialized_request = request.toByteArray();
			int request_len = serialized_request.length;
			DatagramPacket request_packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			try {
				socket.send(request_packet);
			}
			catch (IOException e){
				return 34;
			}
		
			Event[] responses = getEvents();
			if (responses.length == 0) {
				Log.d("MYMY", "No new events");
				return 0;
			}
			String[] event_strings;
			event_strings = getStrings(responses);
			if (event_strings.length == 0){
				return 1;
			}
				
			ListAdapter adapter = listview.getAdapter();
			new_array = new ArrayAdapter<String>(ctx, R.layout.list_update, event_strings);
			//new_array.addAll(event_strings);
			return 0;
		}
		protected void onProgessUpdate(Boolean...prog){
			if (new_array != null) {
				Log.d("MYMY", "Updating new array!");
				listview.setAdapter(new_array);
			}
		}
		protected void onPostExecute(Integer res){
			if (res == 0){
				Log.d("MYMY", "Updating new array!");
				listview.setAdapter(new_array);
			}
		}
	}
	
	public void setList(ListView view){
		listview = view;
	}
	
	private Event[] getEvents() {
		List<Event> events = new ArrayList<Event>();
		while (true) {
			byte[] buffer = new byte[1024];
			int buflen = 1024;
			DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
			try {
				socket.receive(recvpacket);
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return events.toArray(new Event[events.size()]);
			}
			// Parse the buffered data
			Packet response;
			try {
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return events.toArray(new Event[events.size()]);
			}
			
			if (response.getType().equals(PTYPE_EVENT)) {
				Event event = response.getEvent();
				if (!response.hasEvent()) {
					Log.d("MYMY", "no event!");
				}
				events.add(event);
				Log.d("MYMY", "Got eventid "+Integer.toString(event.getId()));
				Log.d("MYMY", "Got event "+event.getMsg());
			}
			else {
				Log.d("MYMY", "got packet type "+response.getType());
				return events.toArray(new Event[events.size()]);
			}
		}
	}
	
	public String[] getStrings(Event[] responses) {
		List<String> strings= new ArrayList<String>();
		for (int x=0;x<responses.length;x++) {
			strings.add(responses[x].getMsg());
		}
		return strings.toArray(new String[strings.size()]);
	}

	private int sendForResponse(DatagramPacket outgoing, DatagramPacket incoming, int maxAttempts){
		for (int attempts = 0;attempts < maxAttempts;attempts++) {
			try {
				socket.send(outgoing);
				socket.receive(incoming);
				break;
			}
			catch (SocketTimeoutException e) {
				if (attempts == 2) {
					Log.d("MYMY", "Connection timed out");
					return 1;
				}
			} catch (IOException e) {
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
		}
		return 0;
	}
	
	public int setAddr(String addr) {
		try {
			address = InetAddress.getByName(addr);
			return 0;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public void setFragment(FragmentManager fm){
		currentFragment = fm;
	}
	
	public class GetSessionsTask extends AsyncTask<WatchGameDialog, Boolean, Integer> {
		protected Integer doInBackground(WatchGameDialog... dialogs) {
			// Check to see if addr is set
			if (address == null) {
				return 1;
			}
			
			// Open up a socket
			if (socket == null) {
				try {
					socket = new DatagramSocket(30303);
					socket.setSoTimeout(5000);
				}
				catch (Exception e) {
					Log.e("MYMY", Log.getStackTraceString(e));
					return 1;
				}
			}
			
			// Create packet
			Packet request = Packet.newBuilder()
				.setType(PTYPE_GET_SESSIONS)
				.setUserId(1)
				.build();
			byte[] serialized_request = request.toByteArray();
			int request_len = serialized_request.length;
			DatagramPacket packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			// Construct recv buffer
			byte[] buffer = new byte[1024];
			int buflen = 1024;
			DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
			
			// Send packet
			int result = sendForResponse(packet, recvpacket, 3);
			if (result == 1) {
				return 1;
			}
			
			// Parse the buffered data
			Packet response;
			try {
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
			
			// Confirm server received event
			if (response.getType().equals(PTYPE_GET_SESSIONS)) {
				String json_str = response.getMsg();
				Log.d("MYMY", "Got sessions"+json_str);
				dialogs[0].setGames(json_str);
				dialogs[0].show(currentFragment, "WatchGameDialog");
				return 0;
			}
			return 1;

		}
		protected void onPostExecute(Integer result){
			if (result == 0) {
				
			}
		}
	}
	
	public class BroadcastTask extends AsyncTask<String, Boolean, Integer> {
		private int x_coord;
		private int y_coord;
		String event_msg;
		public BroadcastTask(String msg, int x, int y) {
			x_coord = x;
			y_coord = y;
			event_msg = msg;
		}
		protected Integer doInBackground(String... params) {
			// Make sure we are connected
			if (address == null || socket == null) {
				Log.d("MYMY", "No connection");
				return 1;
			}
			
			// Create event
			Event event = Event.newBuilder()
				.setType(1)
				.setId(eventid++)
				.setMsg(params[0])
				//.addData(x_coord)
				//.addData(y_coord)
				.build();
			
			// Create packet
			Packet request = Packet.newBuilder()
				.setType(PTYPE_BROADCAST)
				.setUserId(uid)
				.setSessionNum(session)
				.setEvent(event)
				.build();
			byte[] serialized_request = request.toByteArray();
			int request_len = serialized_request.length;
			DatagramPacket packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			// Construct recv buffer
			byte[] buffer = new byte[1024];
			int buflen = 1024;
			DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
			
			// Send packet
			int result = sendForResponse(packet, recvpacket, 3);
			if (result == 1) {
				return 1;
			}
			
			// Parse the buffered data
			Packet response;
			try {
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
			
			// Confirm server received event
			if (response.getType().equals(PTYPE_CONFIRM)) {
				Log.d("MYMY", "Sent event for session"+Integer.toString(session)+"uid "+Integer.toString(uid));
				return 0;
			}
			
			return 1;
		}
		protected void onPostExecute(Integer result){
			if (result == 0) {
				Toast.makeText(ctx, "Event broadcast", Toast.LENGTH_SHORT);
				Log.d("MYMY", "event broadcasted");
			}
			else {
				Toast.makeText(ctx, "Event broadcast failed", Toast.LENGTH_SHORT);
				Log.d("MYMY", "event broadcast failed");
			}
		}
	}
	
	public class ConnectTask extends AsyncTask<String, Boolean, Integer> {
		protected Integer doInBackground(String... params) {
			// Convert addr to InetAddress
			try {
				address = InetAddress.getByName(params[0]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			// Open up a socket
			if (socket == null) {
				try {
					socket = new DatagramSocket(30303);
					socket.setSoTimeout(5000);
				}
				catch (Exception e) {
					Log.e("MYMY", Log.getStackTraceString(e));
					return 1;
				}
			}
			
			// Create hello packet
			Packet request = Packet.newBuilder()
					.setType("hello")
					.setUserId(0)
					.build();
			byte[] serialized_request = request.toByteArray();
			int request_len = serialized_request.length;
			DatagramPacket packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			// Construct recv buffer
			byte[] buffer = new byte[1024];
			int buflen = 1024;
			DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
			
			// Send packet
			int result = sendForResponse(packet, recvpacket, 3);
			if (result == 1) {
				return 1;
			}
			
			// Parse the buffered data
			Packet response;
			try {
				ByteString packet_data = ByteString.copyFrom(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data);
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
			
			// Verify the server sent us a UserId
			if (response.getType().equals(PTYPE_NEWUSER)) {
				if (!response.hasUserId()){
					Log.d("MYMY", "No userid in packet");
					return 1;
				}
				uid = response.getUserId();
				Log.d("MYMY", "User ID is "+Integer.toString(uid));
			}
			else {
				Log.d("MYMY", "type is "+response.getType());
				return 1;
			}
			
			if (params.length == 1) {
				// Dont create a session
				return 0;
			}
			
			// Create new session packet
			request = Packet.newBuilder()
					.setType(PTYPE_CREATE)
					.setUserId(uid)
					.setMsg(params[1])
					.build();
			serialized_request = request.toByteArray();
			request_len = serialized_request.length;
			packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			// Construct recv buffer
			//buffer = new byte[1024];
			recvpacket = new DatagramPacket(buffer, buflen);
			
			// Send packet
			result = sendForResponse(packet, recvpacket, 3);
			if (result == 1) {
				return 1;
			}
			
			// Parse the buffered data
			try {
				ByteString packet_data = ByteString.copyFrom(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data);
			}
			catch (Exception e){
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}
			
			// Verify we have a new session
			if (response.getType().equals(PTYPE_CREATE)) {
				if (!response.hasSessionNum()){
					Log.d("MYMY", "No session num in packet");
					return 1;
				}
				
				Log.d("MYMY", "Session num "+Integer.toString(response.getSessionNum()));
				session = response.getSessionNum();
				return 0;
			}
			else {
				Log.d("MYMY", "type is "+response.getType());
				return 1;
			}
		}
		protected void onPostExecute(Integer result){
			if (result == 0) {
				Toast.makeText(ctx, "Session created", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(ctx, "Connection failed", Toast.LENGTH_SHORT).show();
			}
		}
			
	}
}


