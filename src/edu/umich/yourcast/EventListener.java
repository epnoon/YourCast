package edu.umich.yourcast;

import java.io.*;
import java.net.*;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.os.AsyncTask;
import android.content.ContextWrapper;

import com.google.protobuf.InvalidProtocolBufferException;
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
	
	private static DatagramSocket socket = null;
	private static int uid;
	private static int session;
	public static int PORT = 30303;
	private static InetAddress address;
	private Context ctx;
	
	public EventListener(Context c) {
		this.ctx = c;
	}

	public int Connect (String addr, String name) {
		try {
			new ConnectTask().execute(addr, name);
		}
		catch (Exception e){
			return 1;
		}
		return 0;
	}
		
	public int poll(int session_num, int event_id) {
		Packet request = Packet.newBuilder()
			.setType(PTYPE_POLL)
			.setUserId(uid)
			.setEventId(event_id)
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
		
		byte[] buffer = new byte[1024];
		int buflen = 1024;
		DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
		try {
			socket.receive(recvpacket);
		}
		catch (SocketTimeoutException e){
			return 0;
		}
		catch (IOException e){
			return 0;
		}
		return 1;
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
					Log.e("MYMY" ,Log.getStackTraceString(e));
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
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
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
			}
			else {
				Log.d("MYMY", "type is "+response.getType());
				return 1;
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
			
			// Send packet
			result = sendForResponse(packet, recvpacket, 3);
			if (result == 1) {
				return 1;
			}
			
			// Parse the buffered data
			try {
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
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
				session = response.getSessionNum();
				return 0;
			}
			else {
				Log.d("MYMY", "type is "+response.getType());
				return 1;
			}
		}
		protected void onPostExecute(Integer result){
			Log.d("MYMY", "Post Ex");
			if (result == 0) {
				Toast.makeText(ctx, "Session created", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(ctx, "Connection failed", Toast.LENGTH_SHORT).show();
			}
		}
			
	}
}


