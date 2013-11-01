package edu.umich.yourcast;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umich.yourcast.NewGameDialog.NewGameDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

public class EventPromptDialog extends DialogFragment {
	
	private EventPromptDialogListener mListener; 
	
	public final static String EVENT_INFO = "edu.umich.yourcast.event_info";
	public CharSequence[] c; 
	
	public static EventPromptDialog create(ArrayList<String> s) {
		EventPromptDialog f = new EventPromptDialog();
		Bundle args = new Bundle();
		args.putStringArrayList(EVENT_INFO, s);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (EventPromptDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement EventPromptDialogListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retrieve the arguments that were supplied by create()
		String[] s = getArguments().getStringArrayList(EVENT_INFO).toArray(new String[1]);
		c = (CharSequence[]) s; 

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Event")
	           .setItems(c, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   mListener.onClick(c[which].toString()); 
	               }
	    });
	    return builder.create();
	}

	public interface EventPromptDialogListener {
		public void onClick(String s);
	}
	
}
