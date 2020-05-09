package com.example.lotterylistfragmentactivity;


/*

		Created by Robert Brewer on 5/9/2020.

		The LotteryListFragmentActivity app  generates the California Lottery Super Lotto numbers.
		The app uses the California State Lottery  and Mega numbers, up to the number 47 and 27
		respectively. The app retrieves the past 52 using webscraping methods with the help of
		Volley and Jsoup libraries and  and stores the values. The apps stores previous lotto
		numbers, in a Hashmap key-value pair, where the key is the number and
		the value is the number of times the number has been drawn.


		The apps consists of one activity using 3 fragments that assists the user in generating
		quickpick lottery numbers:

	    GeneratorFragment: Allows the user to input the min and max frequency ranges the lotto
                           and mega  numbers have been drawn, and from that pool of number, ten(10)
                           lottery ticket quick picts are generated and displayed on the UI

	    PastLottoNumbers: Display the past 52 week drawn lottery numbers

	    FrequencyFragment: Display the number of time each SuperLotto and Mega numbers have been
	                       drawn the past 52 weeks.

		*/



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    public static   List<LotteryNumbersHolder> lottoNumbersPastDrawings = new ArrayList<>();;
	private RequestQueue mRequestQueue; //requestqueue varaiable from Vollei library
	private StringRequest stringRequest;   //the variable type of the requestqueue
	FragmentManager fm;
	Fragment fragment;
	Bundle args;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			sendRequestAndPrintResponse();
			Thread.sleep(1000);
			Log.i("SIZE OF LIST", String.valueOf(lottoNumbersPastDrawings.size()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		fm = this.getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.fragment);


			if(fragment == null){
				fragment = GeneratorFragment.newInstance(lottoNumbersPastDrawings, null);
				//fragment.setArguments(args);
				fm.beginTransaction()
						.add(R.id.myContainer, fragment)
						.addToBackStack(null)
						.commit();
			}

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
				//Log.i( "NUMBERS", lottoDrawingDates.get(i + 1) + " ----- " + weeklyLottoNumbers.get(i));
				lottoNumbersPastDrawings.add(new LotteryNumbersHolder(lottoDrawingDates.get(i + 1), weeklyLottoNumbers.get(i)));
			}
			Log.i(TAG, String.valueOf(lottoNumbersPastDrawings.size()));

			//Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();

		}






	}

	/*********************************************************************************
	 *  getListForNumbers() populates a list that will hold the lotto and mega numbers, within the
	 *  user defined min,max frequency in which the number has been drawn.
	 *
	 * @pre none
	 * @parameter List<LotteryNumberHolder>  list : contains the past 52 weeks daily drwan lottry numbers
	 *             Integer number: determines whether the regular lotto numbers(5) or mega number(1)
	 *                              will be generated
	 *             String minMax: a string containing the user defined min and max frequency that will
	 *                            be used to vreate a list of numbers used to generate the new
	 *                            lottery tickets
	 *
	 * @post List<Integer> a list containing the the past lottery numbers occurances during the past
	 *                     52 weeks
	 * **********************************************************************************/
	public List<Integer> getListForNumbers(List<LotteryNumbersHolder> list, Integer number,  String minMax) {

		//creates a HashMap where the keys are numbers between the lotto (1-47) and mega number(1-27) ranges. The values are initialized to
		// zero and will be incremented to reflect the number of times the key number have been
		// drawn in the lottery( key = lottery number, value = # of times number has beeen drawn)
		HashMap<Integer, Integer> myHashLotteryNumbers = new HashMap<>();
		for (int i = 1; i < number +1; i++) { //number is the max numbers that can be selected for lotto (47) and mega (27) numbers
			myHashLotteryNumbers.put(i, 0);
		}

		List<Integer> popularLotteryNumbers = new ArrayList<>();
		//Integer minRange = null;//minimun number of times a number has been selected
		//Integer maxRange = null;////maximun number of times a number has been selected

		try {
			String minNMaxValues[] = minMax.split(",");
			Integer minRangeForLotto = Integer.parseInt(minNMaxValues[0]);
			Integer maxRangeForLotto = Integer.parseInt(minNMaxValues[1]);
			Integer minRangeForMega = Integer.parseInt(minNMaxValues[2]);
			Integer maxRangeForMega = Integer.parseInt(minNMaxValues[3]);
			final int TOTAL_NUMBERS = 5;
			Boolean minNumberRequiredInPool = false;

			switch(number) {
				case 27:  //selects Mega number.
					//Integer megaMin = 5; // pre-defined min occurrence that the mega number has been drawn
					final Integer megaMax = 10;//
					for (int j = 0; j < list.size(); j++) {//pre-defined min occurrence that the mega number has been drawn
						upDateHashTable(myHashLotteryNumbers, list.get(j).getMegaNumber());
					}
					printHashMap(" HASH MEGA NUMBERS: :", myHashLotteryNumbers);
					while (!minNumberRequiredInPool) {
						//printHashMapUsingLoop(" HASH MEGA NUMBERS: :", myHashLotteryNumbers);
						popularLotteryNumbers = getCommonLotteryNumbers(myHashLotteryNumbers, minRangeForMega, maxRangeForMega);
						if (popularLotteryNumbers.size() < 5) {// Adjusts the min Range if the popularLotteryNumberlist is less than the five numbers
							//required to make up the five lottery number.
							minRangeForMega--;
							minNumberRequiredInPool = false;
						} else {
							minNumberRequiredInPool = true;
						}
					}
					//Log.i("MLOTTERYNUMBERS = ", String.valueOf(popularLotteryNumbers.size()));
					break;

				case 47: //selects regular lotto numbers
					for (int j = 0; j < list.size(); j++) {
						for (int k = 0; k < TOTAL_NUMBERS; k++) {
							upDateHashTable(myHashLotteryNumbers, list.get(j).getLottoNumbers(k));
						}
					}
					//printHashMapUsingLoop("LottoNumbers", myHashLotteryNumbers);
					while (!minNumberRequiredInPool) { // Adjusts the min Range if the popularLotteryNumberlist is less than the five numbers
						//required to make up the five lottery number.
						//printHashMapUsingLoop("HASH LOTTO NUMBERS: :", myHashLotteryNumbers);
						popularLotteryNumbers = getCommonLotteryNumbers(myHashLotteryNumbers, minRangeForLotto, maxRangeForLotto);
						if (popularLotteryNumbers.size() <= 5) {
							minRangeForLotto--;
							if (minRangeForLotto < 0) {// Adjusts the max Range if the min range is already at 0
								minRangeForLotto = 0;
								maxRangeForLotto++;
								//Log.i("POPULARLOTTERYNUMBERS = ", String.valueOf(popularLotteryNumbers.size()));
								minNumberRequiredInPool = false;
							}
						} else {
							//Log.i("POPULARLOTTERYNUMBERS = ", String.valueOf(popularLotteryNumbers.size()));
							minNumberRequiredInPool = true;
						}
					}
					break;
				default:
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			Toast.makeText(this, "Min,Max INPUT ERROR: Verify min,max input( for ex. 5,10)", Toast.LENGTH_LONG).show();
		}
		//Log.i("POPULARLOTTERYNUMBERS = ", String.valueOf(popularLotteryNumbers.size()));
		return popularLotteryNumbers;


	}


	/*********************************************************************************
	 *  updateHashTable() updates the hashmap by incrementing the number of times the lottery
	 *  number(key) has been drawn in previous lottery drawings
	 *
	 * @pre none
	 * @parameter HashMap myHashLotteryNumbers, Integer key
	 * @post
	 **********************************************************************************/
	static void upDateHashTable(HashMap<Integer, Integer> myHashLotteryNumbers, Integer key) {

		if (myHashLotteryNumbers.containsKey(key)) {

			Integer oldValue = myHashLotteryNumbers.get(key);
			oldValue++; //increments the hash key values

			myHashLotteryNumbers.put(key, oldValue);
		}
	}


	/*********************************************************************************
	 *  getCommonLotteryNumbers() searched the HashMap and search for values
	 *  between the user defined min,max values. The method returns a list key integers(i.e. lottery numbers)
	 *
	 *
	 * @pre none
	 * @parameter HashMap<Integer, Integer> myHashLotteryNumbers
	 * @post List<Integer> : list of lottery numbers within the specified min,max ranges
	 **********************************************************************************/
	private List<Integer> getCommonLotteryNumbers(HashMap<Integer, Integer> myHashLotteryNumbers, Integer minRange, Integer maxRange) {

		List<Integer> commonNumbers = new ArrayList<>();
		int totalItems = myHashLotteryNumbers.size() + 1;//offset by 1 to include the last key

		int listIndex = 1;

		while (listIndex < totalItems) {

			// since HashMap minimum key is 1, list is offset to represent the first item in map
			if (myHashLotteryNumbers.containsKey(listIndex)) {

				if (myHashLotteryNumbers.get(listIndex) >= minRange && myHashLotteryNumbers.get(listIndex) <= maxRange) {
					commonNumbers.add(listIndex);
				}
				listIndex++;
			}
		}

		return commonNumbers;
	}


	/*********************************************************************************
	 *  printHashMap() prints out the hashmap key,value pai, using an iterator to traverse
	 *  through the hashmap.
	 * @pre none
	 * @parameter String title: Designates whther the hash table contains Lotto or Mega number info
	 *            HashMap<Integer, Integer> myHashLotteryNumbers
	 * @post none
	 **********************************************************************************/
	static void printHashMap(String title,	HashMap<Integer, Integer> map){
		System.out.println(title + " HASH OUTPUT");
		Iterator<Integer> itr = map.keySet().iterator();
		while(itr.hasNext())
		{
			Integer key = itr.next();
			Integer value = map.get(key);
			System.out.println("HASHMAP: Key = " + key + ", Value = " + value);
		}
	}


	/*********************************************************************************
	 *  printHashMapUsingLoop() prints out the hashmap key,value pai, using a For Loop to traverse
	 *  through the hashmap.
	 * @pre none
	 * @parameter String title: Designates whther the hash table contains Lotto or Mega number info
	 *            HashMap<Integer, Integer> myHashLotteryNumbers
	 * @post none
	 **********************************************************************************/
	private void printHashMapUsingLoop(String title, HashMap<Integer, Integer> map){
		System.out.println(title + " HASH OUTPUT");
		for(int i = 1; i < map.size() +1 ; i++){
			Log.i("HASHMAP:", "Key = " + i + ", Value = " + map.get(i).toString());
		}
	}

}
