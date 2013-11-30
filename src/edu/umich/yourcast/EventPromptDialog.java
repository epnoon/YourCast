package edu.umich.yourcast;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class EventPromptDialog extends DialogFragment {

	private EventPromptDialogListener mListener;

	public final static String EVENT_OPTIONS = "edu.umich.yourcast.event_options";
	public final static String EVENT_TITLE = "edu.umich.yourcast.event_title";
	public CharSequence[] c;
	public String title;

	public static EventPromptDialog create(ArrayList<String> options,
			String title) {
		EventPromptDialog f = new EventPromptDialog();
		Bundle args = new Bundle();
		args.putStringArrayList(EVENT_OPTIONS, options);
		args.putString(EVENT_TITLE, title);
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
		Bundle args = getArguments(); 
		// Log.d("MYMY", args.toString()); 
		String[] s = args.getStringArrayList(EVENT_OPTIONS)
				.toArray(new String[1]);
		c = (CharSequence[]) s;

		title = getArguments().getString(EVENT_TITLE);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title)
			.setItems(c,
				new DialogInterface.OnClickListener() {
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
