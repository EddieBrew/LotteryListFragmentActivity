package com.example.lotterylistfragmentactivity.myFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lotterylistfragmentactivity.MainActivity;
import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.adapters.LottoFrequencyArrayAdapter;
import com.example.lotterylistfragmentactivity.data.LotteryNumberFrequency;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.lotterylistfragmentactivity.R.layout.fragment_frequency_itemlist;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FrequencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FrequencyFragment extends ListFragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static String TAG = "Frequency Fragment";
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	ListView listView;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private LottoFrequencyArrayAdapter lottoFrequencyArrayAdapter;
	private List<LotteryNumbersHolder> myList;
	private int numSelector;
	private CheckBox chbxTypeOfNumbers;
	public FrequencyFragment() {
		// Required empty public constructor
	}


	 /*
	 NOTE THAT YOU DO NOT NEED TO INSTANTIATE A LISTVIEW OBJECT WHEN EXTENDING A CLASS WITH  A LISTFRAGMENT
	  */
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment FrequencyFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static FrequencyFragment newInstance(List<LotteryNumbersHolder> param1, String param2) {
		FrequencyFragment fragment = new FrequencyFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("pastLottoNumbers", (ArrayList<? extends Parcelable>) param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myList = MainActivity.getDatabaseContent();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {


		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.fragment_frequency, container, false);
		//listView = view.findViewById(R.id.myContainer);
		chbxTypeOfNumbers = view.findViewById(R.id.chbxTypeOfNumbers);

		final int megaNumberTotal = 27;
		final int superLottoTotal = 47;
		 //int numSelector;
		if(chbxTypeOfNumbers.isChecked()){
			numSelector = megaNumberTotal;
		} else {
			numSelector = superLottoTotal;
		}
		createList(myList, numSelector);


		chbxTypeOfNumbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(chbxTypeOfNumbers.isChecked()){
					chbxTypeOfNumbers.setChecked(true);
					numSelector = megaNumberTotal;
					createList(myList, numSelector);
					Toast.makeText(getContext(), "Results For Frequently Used Mega Numbers", Toast.LENGTH_LONG).show();

				} else{
					chbxTypeOfNumbers.setChecked(false)  ;
					numSelector = superLottoTotal;
					createList(myList, numSelector);
					Toast.makeText(getContext(), "Results For Frequently Used Pick 5 Lotto Numbers", Toast.LENGTH_LONG).show();
				}
			}
		});

		return view;
	}

	public  void createList( List<LotteryNumbersHolder> data, Integer num){

		HashMap<Integer, Integer>  	table  = getLottoHashTable(data, numSelector);;
		List<LotteryNumberFrequency> mylottoData = fillLotteyNumberFrequencyClass(table);
		sortListByDescendingFrequency(mylottoData);
		try {
			lottoFrequencyArrayAdapter = new LottoFrequencyArrayAdapter(getContext(), R.layout.fragment_frequency_itemlist, mylottoData);
			setListAdapter(lottoFrequencyArrayAdapter);
			lottoFrequencyArrayAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static  List<LotteryNumberFrequency>  fillLotteyNumberFrequencyClass(HashMap<Integer, Integer> data){
		List<LotteryNumberFrequency> myData = new ArrayList<>();
		Iterator<Integer> itr = data.keySet().iterator();
		while(itr.hasNext()){
			Integer key = itr.next();
			Integer value = data.get(key);
			myData.add(new LotteryNumberFrequency(key, value ));
		}

		return myData;
	}

	/*********************************************************************************
	 *  getLottoHashTable() populates a list that will hold the lotto and mega numbers, within the
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
	private HashMap<Integer, Integer> getLottoHashTable(List<LotteryNumbersHolder> list, Integer number) {

		//creates a HashMap where the keys are numbers between the lotto (1-47) and mega number(1-27) ranges. The values are initialized to
		// zero and will be incremented to reflect the number of times the key number have been
		// drawn in the lottery( key = lottery number, value = # of times number has beeen drawn)
		HashMap<Integer, Integer> myHashLotteryNumbers = new HashMap<>();
		for (int i = 1; i < number +1; i++) { //number is the max numbers that can be selected for lotto (47) and mega (27) numbers
			myHashLotteryNumbers.put(i, 0);
		}

		try {
			switch(number) {
				case 27:  //selects Mega number.
					//Integer megaMin = 5; // pre-defined min occurrence that the mega number has been drawn
					for (int j = 0; j < list.size(); j++) {//pre-defined min occurrence that the mega number has been drawn
						upDateHashTable(myHashLotteryNumbers, list.get(j).getMegaNumber());
					}
					//printHashMap(" HASH LOTTO NUMBERS: :", myHashLotteryNumbers);
					break;

				case 47: //selects regular lotto numbers
					final int TOTAL_NUMBERS = 5;
					for (int j = 0; j < list.size(); j++) {
						for (int k = 0; k < TOTAL_NUMBERS; k++) {
							upDateHashTable(myHashLotteryNumbers, list.get(j).getLottoNumbers(k));
						}
					}
					//printHashMap(" HASH LOTTO NUMBERS: :", myHashLotteryNumbers);
					break;
				default:
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Toast.makeText(getContext(), "Min,Max INPUT ERROR: Verify min,max input( for ex. 5,10)", Toast.LENGTH_LONG).show();
		}

		return myHashLotteryNumbers;
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

	/***********************************************************************************************
	 * sortListByDescendingDate() sorts a List, by their dates, from lowest to highest
	 * @pre none
	 * @parameter List<> : a list of DailyInfoModel objects
	 * @post none
	 * ********************************************************************************************/
	private void sortListByDescendingFrequency(List<LotteryNumberFrequency> dataArray) {
		if (dataArray == null) {
			return;
		}

		boolean swap = true;
		int j = 0;
		while (swap) {
			swap = false;
			j++;
			for (int i = 0; i < dataArray.size() - j; i++) {
				int item1 = dataArray.get(i).getLottoNumber();
				int item2 = dataArray.get(i + 1).getLottoNumber();

				if (item1 > item2) {//swap list item
					LotteryNumberFrequency s = dataArray.get(i);
					dataArray.set(i, dataArray.get(i + 1));
					dataArray.set(i + 1, s);
					swap = true;
				}
			}
		}
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
}
