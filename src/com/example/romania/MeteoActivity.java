package com.example.romania;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MeteoActivity extends Activity {

	String location;
	TextView title, t1, t2, t3, t4, t5, t6, t7;
	String xml = null;
	Document doc = null;
	
	StringBuilder url;
	
	String tempNodeValue;
	String humidityNodeValue;
	String windNodeName;
	String windNodeDirection;
	String cloudsNodeName;
	String precNodeMode;
	String pressureNodeValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meteo);
		
		Intent meteoIntent = getIntent();
		location = meteoIntent.getStringExtra("Location");
		
		System.out.println("Meteo: " + location);
		
		title = (TextView) findViewById(R.id.textView1);
		t1 = (TextView) findViewById(R.id.textView2);
		t2 = (TextView) findViewById(R.id.textView3);
		t3 = (TextView) findViewById(R.id.textView4);
		t4 = (TextView) findViewById(R.id.textView5);
		t5 = (TextView) findViewById(R.id.textView6);
		t6 = (TextView) findViewById(R.id.textView7);
		t7 = (TextView) findViewById(R.id.textView8);
		
		title.setText("Weather in " + location.toString());
		
		url = new StringBuilder();
		url.append("http://api.openweathermap.org/data/2.5/weather?q=");
		url.append(location + "&mode=xml");
		
		new Thread (new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("run");
				xml = getXml(url.toString());
				System.out.println(xml);
				
				doc = getDocument(xml);
				
				NodeList tempNode = doc.getElementsByTagName("temperature");
				tempNodeValue = tempNode.item(0).getAttributes().getNamedItem("value").getNodeValue();
				
				NodeList humidityNode = doc.getElementsByTagName("humidity");
				humidityNodeValue = humidityNode.item(0).getAttributes().getNamedItem("value").getNodeValue();
				
				NodeList windSpeedNode = doc.getElementsByTagName("speed");
				windNodeName = windSpeedNode.item(0).getAttributes().getNamedItem("name").getNodeValue();
				
				NodeList windDirectionNode = doc.getElementsByTagName("direction");
				windNodeDirection = windDirectionNode.item(0).getAttributes().getNamedItem("name").getNodeValue();
				
				NodeList cloudsNode = doc.getElementsByTagName("clouds");
				cloudsNodeName = cloudsNode.item(0).getAttributes().getNamedItem("name").getNodeValue();
				
				NodeList precNode = doc.getElementsByTagName("precipitation");
				precNodeMode = precNode.item(0).getAttributes().getNamedItem("mode").getNodeValue();
				
				NodeList pressureNode = doc.getElementsByTagName("pressure");
				pressureNodeValue = pressureNode.item(0).getAttributes().getNamedItem("value").getNodeValue();
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Double celsius = Double.parseDouble(tempNodeValue) - 272.15;
						t1.setText("Temperature: " + new DecimalFormat("#.##").format(celsius) + " degrees");
						
						t2.setText("Humidity: " + humidityNodeValue + " %");
						
						t3.setText("Wind state: " + windNodeName);
						
						t4.setText("Wind Direction: " + windNodeDirection);
						
						t5.setText("Clouds: " + cloudsNodeName);
						
						t6.setText("Precipitation: " + precNodeMode);
						
						t7.setText("Pressure: " + pressureNodeValue + " hPa");
					}
				});
				
			//NodeList nodes = doc.getChildNodes().item(0).getChildNodes();
			
			/*for(int i=0;i<nodes.getLength();i++)
				
			System.out.println("child"+i+" - "+ nodes.item(i).getNodeName());
			System.out.println(nodes.item(3).getAttributes().getNamedItem("value").getNodeValue());*/
			}
		}).start();
	}
	
	public String getXml(String url) {
		
		System.out.println("getXml");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        System.out.println("httpClient: " + httpClient);
        HttpPost httpPost = new HttpPost(url);
        System.out.println("httpPost: " + httpPost);

        try {
        	HttpResponse httpResponse = httpClient.execute(httpPost);
        	System.out.println("httpResponse: " + httpResponse);
        	
            HttpEntity httpEntity = httpResponse.getEntity();
            System.out.println("httpEntity: " + httpEntity);
            
			xml = EntityUtils.toString(httpEntity);			
			//System.out.println("Xml: " + xml);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("sfarsit");
        return xml;
	}
	
	public Document getDocument(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
        
            return doc;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meteo, menu);
		return true;
	}
	
	
}
