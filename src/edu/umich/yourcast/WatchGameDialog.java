package edu.umich.yourcast;

import edu.umich.yourcast.NewGameDialog.NewGameDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class WatchGameDialog extends DialogFragment {
    // private String gamesList;
    private CharSequence[] items = { "123", "234", "345" };
    private WatchGameDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
	// Verify that the host activity implements the callback interface
	try {
	    // Instantiate the NoticeDialogListener so we can send events to the
	    // host
	    mListener = (WatchGameDialogListener) activity;
	} catch (ClassCastException e) {
	    // The activity doesn't implement the interface, throw exception
	    throw new ClassCastException(activity.toString()
		    + " must implement WatchGameDialogListener");
	}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	builder.setTitle(R.string.live_games)
		.setItems(items, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			/*Toast.makeText(getActivity(), items[id],
				Toast.LENGTH_SHORT).show();*/
			mListener.onSelectedGameClick(WatchGameDialog.this);
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

    public interface WatchGameDialogListener {
	public void onSelectedGameClick(WatchGameDialog dialog);
    }

}
