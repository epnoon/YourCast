package edu.umich.yourcast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class GameInfoDialog extends DialogFragment {
    private View v;
    
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    v = inflater.inflate(R.layout.dialog_game_info, null);
	    
	    TextView tv = (TextView) v.findViewById(R.id.game_time);
	    String time=getArguments().getString("Time");
	    tv.setText(time);
	    
	    builder.setView(v)
	    // Add action buttons
	           /*.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   mListener.onDialogPositiveClick(NewGameDialog.this);
	               }
	           })*/
	           .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	        	   GameInfoDialog.this.getDialog().cancel();
	               }
	           });      
	    return builder.create();
	}
}
