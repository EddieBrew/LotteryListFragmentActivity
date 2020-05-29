package com.example.lotterylistfragmentactivity;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;
import com.example.lotterylistfragmentactivity.myFragments.FrequencyFragment;
import com.example.lotterylistfragmentactivity.myFragments.GeneratorFragment;
import com.example.lotterylistfragmentactivity.myFragments.PastLottoNumbersFragment;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.lotterylistfragmentactivity.MainActivity.lottoList;
import static com.example.lotterylistfragmentactivity.MainActivity.myDatabase;

public class CustomMenuActivity extends AppCompatActivity {
	private Intent mIntent;
	private static final String TAG = "CustomMenu";
	private FragmentManager fm;
	ActionBar actionBar;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getMenuInflater();
		mMenuInflater.inflate(R.menu.menu_activity, menu);
		changeActionBarTitle("LOTTO NUMBER GENERATOR");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int fragmentSelector;

		switch (item.getItemId()) {
			case (R.id.menu_generateLottoNumbers)://When the Delete Database menu item is clicked, the event below is performed:
				changeActionBarTitle("LOTTO NUMBER GENERATOR");
				fm = getSupportFragmentManager();
				GeneratorFragment generatorFragment = new GeneratorFragment();
				fm.beginTransaction()
						.replace(R.id.myContainer, generatorFragment)
						.addToBackStack(null)
						.commit();
				Toast.makeText(this, "LOTTO NUMBER GENERATOR", Toast.LENGTH_LONG).show();
				return true;

			case (R.id.menu_pastLottoNumbers)://
				changeActionBarTitle("PAST LOTTO NUMBERS");
				//FragmentManager fm;
				fm = getSupportFragmentManager();
				PastLottoNumbersFragment pastLottoNumbersFragment = new PastLottoNumbersFragment();
				fm.beginTransaction()
						.replace(R.id.myContainer, pastLottoNumbersFragment)
						.addToBackStack(null)
						.commit();


				Toast.makeText(this, "PAST LOTTERY NUMBERS", Toast.LENGTH_LONG).show();
				return true;




			case (R.id.menu_numberQuery)://

				//FragmentManager fm3;;
				fm = getSupportFragmentManager();
				FrequencyFragment frequencyFragment = FrequencyFragment.newInstance(lottoList, null);
				fm.beginTransaction()
						.replace(R.id.myContainer, frequencyFragment)
						.addToBackStack(null)
						.commit();
				changeActionBarTitle("LOTTO NUMBER FREQUENCY");
				Toast.makeText(this, "Lotto Number Frequencies", Toast.LENGTH_LONG).show();
				return true;


			case (R.id.menu_uploadToServer):
				try {
					uploadLottoNumbersToServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(this, "Lotto Number Uploaded to Database", Toast.LENGTH_LONG).show();

			default:
		}//end switch


		return true;
	}



	private void uploadLottoNumbersToServer() throws IOException {

		final List<LotteryNumbersHolder> lottoList = MainActivity.getDatabaseContent();

        //reads the parse server configuration details from a file into an inputstream
		InputStream is = getApplicationContext().getAssets().open("mysignonstuff.txt");
		int ch;
		StringBuilder sb = new StringBuilder();
		while((ch = is.read()) != -1) {//reads inputstream chars into a stringbuilder object
			sb.append((char) ch);
		}
		String dataString = sb.toString(); // converts strinbuilderobject to a string
		final String DELIMITER = "#";
		String[] getMeIn = dataString.split(DELIMITER); // splits the files details into

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

		ParseUser user = new ParseUser();
		user.setUsername(getMeIn[3]);
		user.setPassword(getMeIn[2]);

		ParseUser currentUser = ParseUser.getCurrentUser();

		if(currentUser!=null){
			Toast.makeText(getApplicationContext(), "Login Good", Toast.LENGTH_SHORT).show();
		}
		else{

			Toast.makeText(getApplicationContext(), "Login Bad", Toast.LENGTH_SHORT).show();
		}

		ParseUser.logInInBackground("rbrewer", "luistam1959", new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					Log.i("Signup", "Login successful");

					for(int i =0; i < lottoList.size(); i++){
						Log.d(TAG, lottoList.get(i).getDate());
					}

					for(int i = 0; i < lottoList.size(); i++){
						//entry writtent to Parse Server
						String callNumber = "H36";
						ParseObject object = new ParseObject("LotteryNumbers");
						object.put("DATE", lottoList.get(i).getDate() );
						//Log.d(TAG, lottoList.get(i).getDate());
						object.put("NUM1", lottoList.get(i).getLottoNumbers(0));
						object.put("NUM2", lottoList.get(i).getLottoNumbers(1));
						object.put("NUM3", lottoList.get(i).getLottoNumbers(2));
						object.put("NUM4", lottoList.get(i).getLottoNumbers(3));
						object.put("NUM5", lottoList.get(i).getLottoNumbers(4));
						object.put("MEGA", lottoList.get(i).getMegaNumber());
						object.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if( e == null){
									Toast.makeText(getApplicationContext(), "Data Sent To Server", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(), "ERROR:" + e.toString(), Toast.LENGTH_SHORT).show();
									Log.i("ERROR: ",  e.toString());
								}
							}
						});
						ParseAnalytics.trackAppOpenedInBackground(null);
					}
				}
			}
		});
	}


	void insertLottoNumberToServer(final LotteryNumbersHolder data) throws IOException {
		//reads the parse server configuration details from a file into an inputstream
		InputStream is = getApplicationContext().getAssets().open("mysignonstuff.txt");
		int ch;
		StringBuilder sb = new StringBuilder();
		while((ch = is.read()) != -1) {//reads inputstream chars into a stringbuilder object
			sb.append((char) ch);
		}
		String dataString = sb.toString(); // converts strinbuilderobject to a string
		final String DELIMITER = "#";
		String[] getMeIn = dataString.split(DELIMITER); // splits the files details into

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

		ParseUser user = new ParseUser();
		user.setUsername(getMeIn[3]);
		user.setPassword(getMeIn[2]);

		ParseUser currentUser = ParseUser.getCurrentUser();

		if(currentUser!=null){
			Toast.makeText(getApplicationContext(), "Connected To Server", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(getApplicationContext(), "No Connection to Server", Toast.LENGTH_SHORT).show();
		}

		ParseUser.logInInBackground(getMeIn[3], getMeIn[4], new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				ParseObject object = new ParseObject("LotteryNumbers");
				object.put("DATE", data.getDate());
				object.put("NUM1", data.getLottoNumbers(0));
				object.put("NUM2", data.getLottoNumbers(1));
				object.put("NUM3", data.getLottoNumbers(2));
				object.put("NUM4", data.getLottoNumbers(3));
				object.put("NUM5", data.getLottoNumbers(4));
				object.put("MEGA", data.getMegaNumber());

				object.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Toast.makeText(getApplicationContext(), "Data Sent To Server", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "ERROR:" + e.toString(), Toast.LENGTH_SHORT).show();
							Log.i("ERROR: ", e.toString());
						}

					}
				});//object.saveInBackground
			}
		});//end ParseUser.logInInBackground

		ParseAnalytics.trackAppOpenedInBackground(null);

	}


	public void changeActionBarTitle(String title) {
		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(title);
		}
	}





}
