package com.example.romania;

import java.io.Serializable;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;


public class MainActivity extends Activity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Map ImageView
	ImageView img;
	
	//Pictures
	static final int SPLASH_SCREEN = R.drawable.splash;
	static final int START_MAP = R.drawable.harta;
	static final int ZOOMED_MAP = R.drawable.hartaf;
	
	// Matrices used for scaling and translating the map
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
    float[] matrixInfo = new float[9];
	
	// Dimensions of the map
	int imgWidth;
	int imgHeight;

	// Variables used for checking the 'click on location' event
	static boolean oneFinger = false;
	static final int CLICK_DURATION = 180;
	
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	
	// Min/Max scales
	static final float MAX_SCALE = 3f;
	static final float MIN_SCALE = 0.4f;
	static final float CHANGE_SCALE = 1.5f;
	
	
	// App modes : ADD for adding locations via touchscreen; NORMAL for user mode
	static final int ADD = 0;
	static final int NORMAL = 1;
	// Modify this to add location in ADD mode
	final String LOCATION_NAME = "";
	
	int appMode = NORMAL;
	
	// Remember some things for zooming
	Point start = new Point(); // start is of class Point because we need to serialize it, PointF does not support serializing
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		img = (ImageView) findViewById(R.id.imageEnhance);
				
		// Initialize scale
		Drawable d = getResources().getDrawable(START_MAP);
		imgHeight = d.getIntrinsicHeight();
		imgWidth = d.getIntrinsicWidth();
		
		
			img.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
				@Override
				public void onGlobalLayout() {
					// TODO Auto-generated method stub
					RectF rect1 = new RectF(0, 0, imgWidth, imgHeight);
					RectF rect2 = new RectF(0, 0, img.getWidth(), img.getHeight());
					matrix.setRectToRect(rect1, rect2, Matrix.ScaleToFit.CENTER);
					img.setImageMatrix(matrix);
				
					//matrix.setScale(INIT_SCALE, INIT_SCALE, INIT_X, INIT_Y);
					//Log.println(Log.DEBUG, "Scale", "")
					//img.setImageMatrix(matrix);
				}
			});
		
		img.setOnTouchListener(new OnTouchListener() {
			
			long clickStart;
			long clickEnd;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				ImageView view = (ImageView) v;
                
			    dumpEvent(event);
			    
			    if(event.getPointerCount() > 1)
			    	oneFinger = false;
			    		    
			    // Handle touch events here...
			    switch (event.getAction() & MotionEvent.ACTION_MASK) {
			    case MotionEvent.ACTION_DOWN:
			    	
			    	clickStart = System.currentTimeMillis();
			    	
			    	oneFinger = true;
			    	
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        
			        Log.println(Log.DEBUG ,"Zoom", "mode=DRAG");
			        
			        mode = DRAG;
			        break;
			    case MotionEvent.ACTION_POINTER_DOWN:
			        oldDist = spacing(event);
			        Log.println(Log.DEBUG, "Zoom", "oldDist=" + oldDist);
			        if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            midPoint(mid, event);
			            mode = ZOOM;
			            Log.println(Log.DEBUG, "Zoom", "mode=ZOOM");
			        }
			        break;
			    case MotionEvent.ACTION_UP:
			    	
			    	clickEnd = System.currentTimeMillis() - clickStart;
			       
			    	switch(appMode) {
			    	
			    	case ADD:
			    		if (clickEnd <= CLICK_DURATION  && oneFinger) {
			    			
				    		Location l = new Location(LOCATION_NAME);
				    		
				    		Point absCoords = getAbsoluteCoords(new Point(start.x, start.y));
				    		l.setCoords(absCoords);
				    		
				    		LocationList ls = LocationList.getInstance(MainActivity.this);
				    		
				    		ls.addLocation(l);
				    		LocationList.save(MainActivity.this, ls); 
				    		System.out.println(ls);
				    		Toast.makeText (MainActivity.this, absCoords.x + " " + absCoords.y, Toast.LENGTH_SHORT).show();	
				    		
			    		}
			    		break;
			    		
			    	case NORMAL:
			    		if (clickEnd <= CLICK_DURATION && oneFinger) {
			    			
			    			LocationList ls = LocationList.getInstance(MainActivity.this);
			    			
			    			System.out.println(ls);
			    			
			    			Point absCoords = getAbsoluteCoords(start);			    			
			    			Location l = ls.getLocationByCoords(absCoords);
			    			
			    			System.out.println("Finger on: " + absCoords.x + " " + absCoords.y);
			    			
			    			if(l != null) {
			    				System.out.println("You pressed on " + l.getName());
			    				
			    				receiveLocationInfo(l.getName());
			    				
			    				
			    			}else{
			    				System.out.println("Error");
			    				Toast.makeText (MainActivity.this, "Not in database", Toast.LENGTH_SHORT).show();
			    			}
			    		}
			    		break;
			    	}
			    	
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        Log.println(Log.DEBUG, "Zoom", "mode=NONE");
			        break;
			    case MotionEvent.ACTION_MOVE:
			    	
			        if (mode == DRAG) {
			            // ...
			            matrix.set(savedMatrix);
			            matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			            
			        } else if (mode == ZOOM) {
			        	
			            float newDist = spacing(event);
			            Log.println(Log.DEBUG, "Zoom", "newDist=" + newDist);
			            if (newDist > 10f) {
			                matrix.set(savedMatrix);
			                float scale = newDist / oldDist;
			                
			                
			                Matrix temp = new Matrix();
			                //Matrix savedMatrixZoom = new Matrix();
			                
			                temp.set(matrix);
			                temp.postScale(scale, scale, mid.x, mid.y);
			                
			                temp.getValues(matrixInfo);
			                float zoomLevel = matrixInfo[Matrix.MSCALE_X];
			                
			                if(zoomLevel <= MAX_SCALE && zoomLevel >= MIN_SCALE) {
			                	matrix.postScale(scale, scale, mid.x, mid.y);
			                	
			                    if(zoomLevel > CHANGE_SCALE) {
				                	img.setImageResource(ZOOMED_MAP);
				                	img.setImageMatrix(matrix);
				                }else{
				                	img.setImageResource(START_MAP);
				                	img.setImageMatrix(matrix);
				                }
			                	
			                	//savedMatrixZoom.set(matrix);			                	
			                	Log.println(Log.DEBUG, "zoom_info", "Scale: " + matrixInfo[Matrix.MSCALE_X] + " midX: " + mid.x + " midY: " + mid.y);
			                }
			                else 
			                	matrix.set(savedMatrix);
			                
			               // matrix.postScale(scale, scale, mid.x, mid.y);
			            }
			        }
			        break;
			    }

			    view.setImageMatrix(matrix);
			    return true;
			}
			private void dumpEvent(MotionEvent event) {
			    String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
			            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			    StringBuilder sb = new StringBuilder();
			    int action = event.getAction();
			    int actionCode = action & MotionEvent.ACTION_MASK;
			    sb.append("event ACTION_").append(names[actionCode]);
			    if (actionCode == MotionEvent.ACTION_POINTER_DOWN
			            || actionCode == MotionEvent.ACTION_POINTER_UP) {
			        sb.append("(pid ").append(
			                action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			        sb.append(")");
			    }
			    sb.append("[");
			    for (int i = 0; i < event.getPointerCount(); i++) {
			        sb.append("#").append(i);
			        sb.append("(pid ").append(event.getPointerId(i));
			        sb.append(")=").append((int) event.getX(i));
			        sb.append(",").append((int) event.getY(i));
			        if (i + 1 < event.getPointerCount())
			            sb.append(";");
			    }
			    sb.append("]");
			    Log.println(Log.DEBUG, "event_info", sb.toString());
			}
			
			
			private float spacing(MotionEvent event) {
			    float x = event.getX(0) - event.getX(1);
			    float y = event.getY(0) - event.getY(1);
			    return FloatMath.sqrt(x * x + y * y);
			}
			
			private void midPoint(PointF point, MotionEvent event) {
			    float x = event.getX(0) + event.getX(1);
			    float y = event.getY(0) + event.getY(1);
			    point.set(x / 2, y / 2);
			}
			
			private Point getAbsoluteCoords(Point p) {
				Point absPoint = new Point();
				matrix.getValues(matrixInfo);
				
				absPoint.x = (p.x - matrixInfo[2]) / matrixInfo[0];
				absPoint.y = (p.y - matrixInfo[5]) / matrixInfo[4];
			
				return absPoint;
			}
			
			
			private void receiveLocationInfo(String locationName) {
				Intent i = new Intent(MainActivity.this, MyTabbedActivity.class);
				
				i.putExtra("Location", locationName);
				
				startActivity(i);
			}
		});
	}
	
	public void onSaveInstanceState(Bundle onState) {
		matrix.getValues(matrixInfo);
		onState.putFloatArray("Matrix", matrixInfo);
		onState.putFloat("MidX", mid.x);
		onState.putFloat("MIdY", mid.y);
		super.onSaveInstanceState(onState);
	}
}
