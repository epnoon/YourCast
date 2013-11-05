package edu.umich.yourcast;

import java.io.*;
import java.net.*;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.os.AsyncTask;

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
	public static int PORT = 30303;
	private static InetAddress address;

	public int Connect (InetAddress addr) {
		new ConnectTask().execute(addr);
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
	
	public class ConnectTask extends AsyncTask<InetAddress, Boolean, Integer> {
		protected Integer doInBackground(InetAddress... addrs) {
			address = addrs[0];
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
			
			Packet request = Packet.newBuilder()
					.setType("hello")
					.setUserId(0)
					.build();
			byte[] serialized_request = request.toByteArray();
			int request_len = serialized_request.length;
			DatagramPacket packet = new DatagramPacket(serialized_request, request_len, address, PORT);
			
			byte[] buffer = new byte[1024];
			int buflen = 1024;
			DatagramPacket recvpacket = new DatagramPacket(buffer, buflen);
			
			for (int attempts = 0;attempts < 3;attempts++) {
				try {
					socket.send(packet);
					socket.receive(recvpacket);
					Log.d("MYMY", "got packet");
					break;
				}
				catch (SocketTimeoutException e) {
					// Let it timeout 
				} catch (IOException e) {
					Log.e("MYMY" ,Log.getStackTraceString(e));
					return 1;
				}
			}
			
			Packet response;
			try {
				String packet_data = new String(recvpacket.getData(), 0, recvpacket.getLength());
				response = Packet.parseFrom(packet_data.getBytes());
			}
			catch (Exception e){
			
				Log.e("MYMY" ,Log.getStackTraceString(e));
				return 1;
			}

			if (response.getType().equals(PTYPE_NEWUSER)) {
				if (!response.hasUserId()){
					Log.d("MYMY", "No userid in packet");
					return 1;
				}
				uid = response.getUserId();
				Log.d("MYMY", "connection established!");
				return 0;
			}
			else {
				Log.d("MYMY", "type is "+response.getType());
				return 1;
			}
			
		}
		protected void onPostExecute(int result){
			Log.d("MYMY", "Post Ex");
		}
			
	}
}


