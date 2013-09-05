package com.example.romania;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

public class LocationList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Location> locations  = new ArrayList<Location>();
	static private FileOutputStream fos;
	static private LocationList singleton = null;
	
	private LocationList() {}
	
	static public LocationList getInstance (Context c){
		
		//System.out.println("Entering getInstance");
		ObjectInputStream in = null;
		
		if (singleton == null) {
			try {
				
				in = new ObjectInputStream(c.openFileInput("Data"));
				
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (in == null) {
				
				System.out.println("in");
				singleton = new LocationList();
				return singleton;
				
			}
			
			System.out.println("readObject");
			try {
				
				singleton = (LocationList) in.readObject();
				
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return singleton;
		}
		System.out.println("exista deja");
		return singleton;
	}
	
	
	public void addLocation(Location l) {
		locations.add(l);
	}
	
	public ArrayList<Location> getLocations() {
		return this.locations;
	}
	
	public Location getLocationByCoords(Point p) {
			for(Location l : locations) {
				if (l.getCoords().distanceTo(p) <= Location.MAX_DIST) {
					return l;
				}
			}
			return null;
	}
	
	public static void save(Context c, LocationList ls) {
		try {
			
			fos = c.openFileOutput("Data", Context.MODE_PRIVATE);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found in save");
			e.printStackTrace();
		}
		try {
			System.out.println("intra");
			ObjectOutputStream out = new ObjectOutputStream(fos);
			System.out.println("Opened out");
			out.writeObject(ls);
			System.out.println("Write obj");
			out.close();
			System.out.println("saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String buf = ""; 
		for(Location l : locations) {
			buf += l.getName() + ": " + l.getCoords().toString() + "  ";
		}
		return buf;
	}
}
