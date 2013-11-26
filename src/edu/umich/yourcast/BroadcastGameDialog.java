package edu.umich.yourcast;

import edu.umich.yourcast.WatchGameDialog.WatchGameDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BroadcastGameDialog extends DialogFragment {

    private BroadcastGameDialogListener mListener;

    private CharSequence[] options = { "New Game", "Existing Games" };

    // Override the Fragment.onAttach() method to instantiate this
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
	// Verify that the host activity implements the callback interface
	try {
	    // Instantiate the NoticeDialogListener so we can send events to the
	    // host
	    mListener = (BroadcastGameDialogListener) activity;
	} catch (ClassCastException e) {
	    // The activity doesn't implement the interface, throw exception
	    throw new ClassCastException(activity.toString()
		    + " must implement BroadcastGameDialogListener");
	}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	builder.setTitle(R.string.what_do_you_want_to_broadcast)
		.setItems(options, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			/*
			 * Toast.makeText(getActivity(), items[id],
			 * Toast.LENGTH_SHORT).show();
			 */
			Log.d("MYMY", "id" + Integer.toString(id));
			mListener.onOptionClick(BroadcastGameDialog.this, id);
		    }
		})
		.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			    }
			});
	return builder.create();
    }

    public interface BroadcastGameDialogListener {
	public void onOptionClick(BroadcastGameDialog dialog, int id);
    }

}
