package com.example.romania;

import java.io.Serializable;
import android.graphics.PointF;

public class Location implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	//private int type;
	private Point coords;
	
	static final float MAX_DIST = 20.0f;
	
	private static Location singleton = null;
	
	public Location(String name) {
		this.setName(name);
		//this.setType(type);
	}
		
	public static Location getInstance(String name) {
		if(singleton == null) {
			singleton = new Location(name);
			return singleton;
		}
		return singleton;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Point getCoords() {
		return coords;
	}


	public void setCoords(Point coords) {
		this.coords = coords;
	}
}
