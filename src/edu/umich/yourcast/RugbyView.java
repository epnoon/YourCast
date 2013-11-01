package edu.umich.yourcast;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class RugbyView extends ImageView {
	@SuppressWarnings("deprecation")
	final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent e) {
		    Log.d("MYMY", "Longpress detected");
		    }
	});
	public RugbyView(Context context) {
		super(context); 
		// TODO Auto-generated constructor stub
	}
	
	public RugbyView(Context context, AttributeSet attrs) {
		super(context, attrs); 
	}
	
	public RugbyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
	}

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
}
