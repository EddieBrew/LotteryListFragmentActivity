package com.example.lotterylistfragmentactivity;


/*

		Created by Robert Brewer on 5/9/2020.

		The LotteryListFragmentActivity app  generates the California Lottery Super Lotto numbers.
		The app retrieves the past 52 weeks drawings using webscraping methods with the help of
		Volley and Jsoup libraries and  and stores the values.


		The apps consists of one activity using 3 fragments that assists the user in generating
		quickpick lottery numbers:

	    GeneratorFragment: Allows the user to input the min and max frequency ranges the lotto
                           and mega  numbers have been drawn, and from that pool of number, ten(10)
                           lottery quick picks are generated and displayed on the UI

	    PastLottoNumbers: Display the past 52 week drawn lottery numbers

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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.lang.Thread.sleep;
import static org.jsoup.Jsoup.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.jsoup.Jsoup.parse;

public class MainActivity extends CustomMenuActivity {
	public static final String TAG = "MainActivity";
	public static   List<LotteryNumbersHolder> lottoList = new ArrayList<>();;
	private RequestQueue mRequestQueue; //requestqueue varaiable from Vollei library
	private StringRequest stringRequest;   //the variable type of the requestqueue
	FragmentManager fm;
	Fragment fragment;
	Bundle args;
	public static MyLotteryDatabase myDatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if(myDatabase == null){
			myDatabase = new  MyLotteryDatabase(this);
		}

		try {
			sendRequestAndPrintResponse(); //launches and fill the database the first time the app is installed
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		fm = this.getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.fragment);
			if(fragment == null){
				fragment =  new GeneratorFragment();
				//fragment.setArguments(args);
				fm.beginTransaction()
						.add(R.id.myContainer, fragment)
						.addToBackStack(null)
						.commit();
			}

		Toast.makeText(this, "Total database entries are "+ myDatabase.numberOfRows(), Toast.LENGTH_LONG).show();
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
	 *  sendRequestAndPrintResponse() is the main function that produces the lottery numbers by
	 *  abstracting the data from the network using Volley for networking and Jsoup for webscraping.
	 *  Upon analazing the the 52 weeks of the CA State Lottery drawings, lotto and mega numbers
	 *  are populated into the UI
	 *
	 * @pre none
	 * @parameter none
	 * @post Populates the UI lottery number displays
	 **********************************************************************************/
	private void sendRequestAndPrintResponse(){
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
	 * @post List<LotteryNumberHolder> updates global variable "lottoNumberList" which holds the daily
	 *                                  lottoe numbers for the last 52 weeks
	 **********************************************************************************/

	private void parseHTMLPage(final String response) throws IOException {
		final String TAG = "parseHTMLPage";
		if (response == "") ;
		Document doc = parse(response);
		//ist<LotteryNumbersHolder> myDailyLotto = new ArrayList<>();
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
						//Log.i( "NUMBERS", lottoNumbers);
						//Number format when scraped from website--: 5, 10, 6, 8, 34, MN: 33,
						String updatedLottoNumbers = lottoNumbers.replace("MN: ", "");////removes mega Number designator (MN: ) from lotto number string
						String updatedLottoNumbers1 = updatedLottoNumbers.replace (",", ""); //removes the comma separator from the numbers string

						weeklyLottoNumbers.add(updatedLottoNumbers1);
					}
				}
			}
			for (int i = 0; i < weeklyLottoNumbers.size(); i++) {
				if(!myDatabase.checkForDuplicateDate(lottoDrawingDates.get(i + 1))){ //checks to see if the lottery drawing date is already in the database
					myDatabase.insertLotteryNumbers(new LotteryNumbersHolder(lottoDrawingDates.get(i + 1), weeklyLottoNumbers.get(i)));//insert drwaing into local database
					insertLottoNumberToServer(new LotteryNumbersHolder(lottoDrawingDates.get(i + 1), weeklyLottoNumbers.get(i))); //insert drawing into Parse Server
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
