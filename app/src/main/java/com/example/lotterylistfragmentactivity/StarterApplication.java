package com.example.lotterylistfragmentactivity;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StarterApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		final String filename = "mysignonstuff.txt";
		final int myMagicNumber = 5;

		try {
			//Steps to extract data from a file
			//1)create an Inputstream object to read data from a file
			InputStream is = getApplicationContext().getAssets().open(filename);

			//2)Creats a bufferedreader object( read the text from a character-based input stream)
			// and pass inputStream to a new  inputStreamReader object
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int count = 0;
			String line, lineToBeParsed = null;
			while ((line = br.readLine()) != null) {

				if(count == myMagicNumber) {
					lineToBeParsed = line;
					break;
				}
				count++;
			}
			//String dataString = sb.toString();
			final String DELIMITER = "#";
			String[] getMeIn = lineToBeParsed.split(DELIMITER);

			// Enable Local Datastore.
			Parse.enableLocalDatastore(this);
			Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
					.applicationId(getMeIn[0])
					.clientKey(getMeIn[1])
					.server(getMeIn[2])
					.build()
			);
			ParseACL defaultACL = new ParseACL();
			defaultACL.setPublicReadAccess(true);
			defaultACL.setPublicWriteAccess(true);
			ParseACL.setDefaultACL(defaultACL, true);
			br.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
