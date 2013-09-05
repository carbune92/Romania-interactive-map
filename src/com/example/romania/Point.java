package com.example.romania;

import java.io.Serializable;

public class Point implements Serializable{
	float x;
	float y;
	
	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	Point() {}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float distanceTo(Point p) {
		float res = (float) Math.sqrt ((x-p.x) * (x-p.x) + (y-p.y) * (y-p.y));
		System.out.println(res);
		
		return res;
	}
	
	public String toString() {
		return "X: " + this.x + " Y: " + this.y;
	}
}
