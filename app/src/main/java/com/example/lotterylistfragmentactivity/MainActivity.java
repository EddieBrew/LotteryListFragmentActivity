package com.example.lotterylistfragmentactivity;


/*

		Created by Robert Brewer on 5/9/2020.

		The LotteryListFragmentActivity app  generates the California Lottery Super Lotto numbers.
		The app retrieves the bi-weekly lottery drawings using webscraping methods with the help of
		Volley and Jsoup libraries and  and stores the values. The lotto numbers are stored im a
		local database and on the server.


		The apps consists of one activity using 3 fragments that assists the user in generating
		quickpick lottery numbers:

	    GeneratorFragment: Allows the user to input the min and max frequency ranges the lotto
                           and mega  numbers have been drawn, and from that pool of number, ten(10)
                           lottery quick picks are generated and displayed on the UI

	    PastLottoNumbers: Display the past lottery numbers, stored in the database

	    FrequencyFragment: Display the number of time each SuperLotto and Mega numbers have been
	                       drawn the past 52 weeks.

		*/



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;
import com.example.lotterylistfragmentactivity.myFragments.GeneratorFragment;
import com.example.lotterylistfragmentactivity.myFragments.PastLottoNumbersFragment;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.lang.Thread.sleep;
import static org.jsoup.Jsoup.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Jsoup.parse;

public class MainActivity extends CustomMenuActivity {
	public static final String TAG = "MainActivity";
	static  ParseUser user;
	public static   List<LotteryNumbersHolder> lottoList = new ArrayList<>();;
	FragmentManager fm;
	Fragment fragment;
	private RequestQueue mRequestQueue; //requestqueue varaiable from Vollei library
	private StringRequest stringRequest;   //the variable type of the requestqueue
	static String iD;
	static String key;
	static String server;
	static String add1;
	static String add2;
	public static MyLotteryDatabase myDatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		InputStream is = null;
		try {
			is = getApplicationContext().getAssets().open("mysignonstuff.txt");
			int ch;
			StringBuilder sb = new StringBuilder();
			while((ch = is.read()) != -1) {//reads inputstream chars into a stringbuilder object
				sb.append((char) ch);
			}
			String dataString = sb.toString(); // converts strinbuilderobject to a string
			final String DELIMITER = "#";
			String[] getMeIn = dataString.split(DELIMITER); // splits the files details into
			iD = getMeIn[0];
			key  = getMeIn[1];
			server = getMeIn[2];
			add1 = getMeIn[3];
			add2 = getMeIn[4];

		} catch (IOException e) {
			e.printStackTrace();
		}

		if((myDatabase == null) ){ //instantiate database when app is initially imstalled
			myDatabase = new  MyLotteryDatabase(this);
		}

		if(myDatabase.numberOfRows() == 0){ //downloads previous lottonnumbers from server when app
			                                //is initially installed
			try {
				downloadFromServer();
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		checkForNewLottoNumbers(); //retrieves lotto numbers from webpage and add new drawing to the
		                          //to the database and server

		//Instantiate default Generator Fragment on app startup
		fm = this.getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.fragment);
			if(fragment == null){
				fragment =  new GeneratorFragment();
				fm.beginTransaction()
						.add(R.id.myContainer, fragment)
						.addToBackStack(null)
						.commit();
			}

		Toast.makeText(this, "Total database entries are "+ myDatabase.numberOfRows(), Toast.LENGTH_LONG).show();
	}




	/*********************************************************************************
	 *  checkForNewLottoNumbers() is the main function that produces the lottery numbers by
	 *  abstracting the data from the network using Volley for networking and Jsoup for webscraping.
	 *  The drawing datesare compared to the database dates. If the drawing dates are not found in the database,
	 *  the date and numbers are added to the database and the server.
	 *
	 * @pre none
	 * @parameter none
	 * @post Populates the UI lottery number displays
	 **********************************************************************************/
	private void checkForNewLottoNumbers() {

		final String TAG = "sendRequest()";
		String website = "https://www.lotteryusa.com/california/super-lotto-plus/year";
		mRequestQueue = Volley.newRequestQueue(this); //used for simple queue requests
		stringRequest = new StringRequest(Request.Method.GET, website, new Response.Listener<String>() {
			@Override
			public void onResponse(final String response) {

				produceLottoNMegaNumbers(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "ERROR200: " + error.toString(), Toast.LENGTH_SHORT).show();
				Log.i(TAG, error.toString());
			}
		});

		mRequestQueue.add(stringRequest);
	}


	/*********************************************************************************
	 *  produceLottoNMegaNumbers() produced a list containing LotteryNumberHolder objects.
	 *  The LotteryHolderNumber class contains the daily drawings of the CA SuperLotto
	 *  numbers
	 *
	 * @pre String response: the HTML transmitted data received from the CA Lottery webpagepage.
	 *
	 * @parameter none
	 * @post Populates the UI lottery number displays
	 **********************************************************************************/
	private void produceLottoNMegaNumbers(final String response){

		if( response == "")return ;
		try {
			parseHTMLPage(response);// List of the daily lottery numbers
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("produce", e.toString());
		}
	}


	/*********************************************************************************
	 *  parseHTMLPage() parses the CA Lottery HTML website data and extract the daily lotto numbers
	 *                  and populates a List with the lotto numbers
	 *
	 * @pre String response: the HTML transmitted data received from the CA Lottery webpagepage.
	 *
	 * @parameter none
	 * @post  adds new entries into the database and server
	 *
	 **********************************************************************************/

	private void parseHTMLPage(final String response) throws IOException {
		final String TAG = "parseHTMLPage";
		if (response == "") ;
		Document doc = parse(response);
		try {
			List<String> lottoDrawingDates = new ArrayList<>(); //holds the lotto drawing dates
			Elements lotteryDatesInfo = doc.select("time");
			for (Element lottoDates : lotteryDatesInfo) {
				Elements date = lottoDates.getElementsByAttribute("datetime");
				lottoDrawingDates.add(date.text());
			}
			List<String> weeklyLottoNumbers = new ArrayList<>(); //holds the lotto numbers
			Elements rows = doc.select("tr.c-game-table__item");
			for (Element row : rows) {
				Elements cells = row.children();//get all elements in the result cell
				for (Element cell : cells) {
					Elements numListings = cell.getElementsByAttributeValue("class", "c-result c-result--in-card c-result--has-extra");
					for (Element numListing : numListings) {
						Elements numbers = numListing.getElementsByTag("li ");
						String lottoNumbers = "";
						for (Element number : numbers) {
							String lotteryNumber = number.text();
							lottoNumbers += lotteryNumber + ", ";
						}

						//Number format when scraped from website--: 5, 10, 6, 8, 34, MN: 33,
						String updatedLottoNumbers = lottoNumbers.replace("MN: ", "");////removes mega Number designator (MN: ) from lotto number string
						String updatedLottoNumbers1 = updatedLottoNumbers.replace (",", ""); //removes the comma separator from the numbers string
						weeklyLottoNumbers.add(updatedLottoNumbers1);
					}
				}
			}
			for (int i = 0; i < weeklyLottoNumbers.size(); i++) {
				if (! myDatabase.checkForDuplicateDate(lottoDrawingDates.get(i + 1))) { //checks to see if the lottery drawing date is already in the database
					myDatabase.insertLotteryNumbers(new LotteryNumbersHolder(lottoDrawingDates.get(i + 1), weeklyLottoNumbers.get(i)));//insert drwaing into local database
					addToServer(new LotteryNumbersHolder(lottoDrawingDates.get(i + 1), weeklyLottoNumbers.get(i))); //adds drawing to server
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	/*********************************************************************************
	 *  getDatabaseContent() retrieves the content from the local SQLite database
	 *  and returns the content in a List object
	 *
	 * @pre none
	 * @parameter none
	 * @post returns an ArrayList object of the database content
	 **********************************************************************************/

	public static List<LotteryNumbersHolder> getDatabaseContent(){
		List<LotteryNumbersHolder> list = new ArrayList<>();
		Cursor res = myDatabase.getAllLotteryNumbers();
		while (res.moveToNext()) {//retrieves lotto info from the database and populates the
			//list which is used to display lottery numbers
			String dateString = res.getString(1);
			StringBuffer buffer = new StringBuffer();
			buffer.append(res.getInt(2)+" ");
			buffer.append(res.getInt(3)+" ");
			buffer.append(res.getInt(4)+" ");
			buffer.append(res.getInt(5)+" ");
			buffer.append(res.getInt(6)+" ");
			buffer.append(res.getInt(7));

			list.add(new LotteryNumbersHolder(dateString, buffer.toString()));
		}
		return list;
	}


	/*********************************************************************************
	 *  convertDateStringToInt() converts a date, in String format,  to an integer value
	 * @pre none
	 * @parameter String
	 * @post returns int reprentation of the date
	 **********************************************************************************/
	public static  int convertDateStringToInt(String dateStringWhole) {

		//dateStringWhole format is for example   Wed, May 2, 2020
		String delimStr = " ";

		//Parses the dateStringWhole variable
		String[] dateParsed = dateStringWhole.split(delimStr);
		int month =  getMonthInt(dateParsed[1]);
		int day = Integer.parseInt(dateParsed[2].replace(",", ""));
		int year  = Integer.parseInt(dateParsed[3]);


		// new date format is mm/dd/yyyy
		return  ((month * 100) + (day) +(year * 10000));

	} //end method

	/*********************************************************************************
	 *  getMonthInt() Converts the month into an integer represntation.
	 * @pre none
	 * @parameter String moth
	 * @post returns an integer representation og the month
	 **********************************************************************************/
	private static int getMonthInt(String month){

		switch(month){
			case "Jan" : return 1;
			case "Feb" : return 2;
			case "Mar" : return 3;
			case "Apr" : return 4;
			case "May" : return 5;
			case "Jun" : return 6;
			case "Jul" : return 7;
			case "Aug" : return 8;
			case "Sep" : return 9;
			case "Oct" : return 10;
			case "Nov" : return 11;
			case "Dec" : return 12;
			default:
		}
		return 0;
	}


	/*********************************************************************************
	 *  addToServer() adds the new lottery drawings into the Parse Server
	 * @pre none
	 * @parameter LotteryNumbersHolder data
	 * @post Displays a Tosat message indicating wheyher successful or not
	 **********************************************************************************/
	public void addToServer(LotteryNumbersHolder data) {

		Parse.enableLocalDatastore(this);
		Parse.initialize(new Parse.Configuration.Builder(this)
				.applicationId(iD)
				.clientKey(key)
				.server(server)
				.build()
		);

		ParseObject object = new ParseObject("LotteryNumbers");
		object.put("DateInteger", MainActivity.convertDateStringToInt(data.getDate()));
		object.put("Username", "rbrewer");
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
					Toast.makeText(getApplicationContext(), "Successful: Data Sent To Server", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "ERROR: No Data Sent to Server", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

}//end class
