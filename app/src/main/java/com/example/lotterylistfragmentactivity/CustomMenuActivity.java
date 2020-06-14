package com.example.lotterylistfragmentactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;
import com.example.lotterylistfragmentactivity.myFragments.FrequencyFragment;
import com.example.lotterylistfragmentactivity.myFragments.GeneratorFragment;
import com.example.lotterylistfragmentactivity.myFragments.PastLottoNumbersFragment;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.lotterylistfragmentactivity.MainActivity.add1;
import static com.example.lotterylistfragmentactivity.MainActivity.add2;
import static com.example.lotterylistfragmentactivity.MainActivity.convertDateStringToInt;
import static com.example.lotterylistfragmentactivity.MainActivity.iD;
import static com.example.lotterylistfragmentactivity.MainActivity.key;
import static com.example.lotterylistfragmentactivity.MainActivity.lottoList;
import static com.example.lotterylistfragmentactivity.MainActivity.myDatabase;
import static com.example.lotterylistfragmentactivity.MainActivity.server;
import static com.example.lotterylistfragmentactivity.MainActivity.user;
import static java.lang.Thread.sleep;

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
				//Toast.makeText(this, "LOTTO NUMBER GENERATOR", Toast.LENGTH_LONG).show();
				Toast.makeText(this, "Total database entries are "+ myDatabase.numberOfRows(), Toast.LENGTH_LONG).show();
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
				//Toast.makeText(this, "PAST LOTTERY NUMBERS TOTAL ENTRIES = ", Toast.LENGTH_LONG).show();
				return true;

			case (R.id.menu_numberQuery)://
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
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("Upload Database to Server?")
						.setMessage("Do You Want to Update the Parse Server's Database? Previous data will be destroyed")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								try {
									uploadLottoNumbersToServer();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						})
						.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//allows entered data to be edited
								Toast.makeText(getApplicationContext(), "Database Update Cancelled", Toast.LENGTH_SHORT).show();
							}
						}).show();
				return true;

			case (R.id.menu_downloadFromServer):
					new AlertDialog.Builder(this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("Update Local Database?")
							.setMessage("Do You Want to Update the Database? Previous Content will be destroyed")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									myDatabase.deleteAll();
									downloadFromServer();
								}
							})
							.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									//allows entered data to be edited
									Toast.makeText(getApplicationContext(), "Database Update Cancelled", Toast.LENGTH_SHORT).show();
								}
							}).show();
			return true;
			case(R.id.menu_deleteDataFromServer):
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("Delete  Server Info?")
						.setMessage("Do You Want to Delete the Parse Server's Content? Previous data will be destroyed")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								deleteParseServerDatabase();
							}
						})
						.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Toast.makeText(getApplicationContext(), "Server Content Deletion Cancelled", Toast.LENGTH_SHORT).show();
							}
						}).show();
				return true;
				default:
		}//end switch
		return true;
	}

	/*******************************************************************************************
	 *  deleteParseServerDatabase() deletes all data located in the Parse Serve TimeCardClass
	 * @pre none
	 * @parameter
	 * @post none
	 ********************************************************************************************/
	public void deleteParseServerDatabase(){

		Parse.enableLocalDatastore(this);
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
				.applicationId(iD)
				.clientKey(key)
				.server(server)
				.build()
		);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("LotteryNumbers");
		query.whereExists("rbrewer");

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> records, ParseException e) {
				if (e == null) {
					// iterate over all messages and delete them
					for (ParseObject record : records) {
						record.deleteInBackground();
					}
					Toast.makeText(getApplicationContext(), "Parse Server Lottery Data Deleted", Toast.LENGTH_SHORT).show();
				} else {
					//Handle condition here
					Toast.makeText(getApplicationContext(), "No Data Deleted in Server", Toast.LENGTH_LONG).show();
				}
			}//end done(List<ParseObject> records, ParseException e)
		});// end query.findInBackground
	}

	private void uploadLottoNumbersToServer() throws IOException {

		Parse.enableLocalDatastore(this);
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
				.applicationId(iD)
				.clientKey(key)
				.server(server)
				.build()
		);

		List<LotteryNumbersHolder> data = MainActivity.getDatabaseContent();


		//Server content installed with updated data
		for(int i = 0; i < data.size(); i++){
			ParseObject object = new ParseObject("LotteryNumbers");
			object.put("Username", "rbrewer");
			object.put("DateInteger", convertDateStringToInt(data.get(i).getDate()));
			object.put("DATE", data.get(i).getDate());
			object.put("NUM1", data.get(i).getLottoNumbers(0));
			object.put("NUM2", data.get(i).getLottoNumbers(1));
			object.put("NUM3", data.get(i).getLottoNumbers(2));
			object.put("NUM4", data.get(i).getLottoNumbers(3));
			object.put("NUM5", data.get(i).getLottoNumbers(4));
			object.put("MEGA", data.get(i).getMegaNumber());
			object.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast.makeText(getApplicationContext(), "Data Sent To Server", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "ERROR:" + e.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
		}//end for
	}//end method


	void downloadFromServer(){

		final  String TAG = "Download";

		//final ArrayList<Object>[] lottoList = new ArrayList<Object>[1];
		Parse.enableLocalDatastore(this);
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
				.applicationId(iD)
				.clientKey(key)
				.server(server)
				.build()
		);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("LotteryNumbers");
		query.whereEqualTo("Username", "rbrewer");
		query.setLimit(500);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e == null){
					if(objects.size() > 0){
						List<LotteryNumbersHolder> lottoList = new ArrayList<>();
						final String space = " ";
						for(ParseObject object : objects){
							String date = object.getString("DATE");
							String data =
									object.getNumber("NUM1") + space +
									object.getNumber("NUM2") + space +
									object.getNumber("NUM3") + space +
									object.getNumber("NUM4") + space +
									object.getNumber("NUM5") + space +
									object.getNumber("MEGA");
							lottoList.add(new LotteryNumbersHolder(date, data));

						}
						sortListByDescendingDate(lottoList);
						for (int i = 0; i < lottoList.size(); i++) {
							myDatabase.insertLotteryNumbers(lottoList.get(i));
						}
					}
				}
			}
		});


	}


	/***********************************************************************************************
	 * sortListByDescendingDate() sorts a List, by their dates, from lowest to highest
	 * @pre none
	 * @parameter List<> : a list of DailyInfoModel objects
	 * @post none
	 * ********************************************************************************************/
	public static void sortListByDescendingDate(List<LotteryNumbersHolder> dataArray) {
		if (dataArray == null) {
			return;
		}

		boolean swap = true;
		int j = 0;
		while (swap) {
			swap = false;
			j++;
			for (int i = 0; i < dataArray.size() - j; i++) {
				String item1 = dataArray.get(i).getDate();
				String item2 = dataArray.get(i + 1).getDate();
				int item1Int =  convertDateStringToInt(item1);
				int item2Int = convertDateStringToInt(item2);
				if (item1Int < item2Int) {//swap list item
					LotteryNumbersHolder s = dataArray.get(i);
					dataArray.set(i, dataArray.get(i + 1));
					dataArray.set(i + 1, s);
					swap = true;
				}
			}
		}

	}



	public void changeActionBarTitle(String title) {
		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(title);
		}
	}


}
