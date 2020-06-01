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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lotterylistfragmentactivity.MainActivity;
import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.adapters.LottoArrayAdapter;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.example.lotterylistfragmentactivity.MainActivity.getDatabaseContent;
import static com.example.lotterylistfragmentactivity.myFragments.FrequencyFragment.upDateHashTable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneratorFragment extends ListFragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String TAG = "Generator Fragment";
	private static final  String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private Button btnGenerateLotteryTickets;
	private TextView editTextMinMax;
	List<LotteryNumbersHolder> myList = new ArrayList<>();

	public GeneratorFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment BlankFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static GeneratorFragment newInstance(String param1, String param2) {
		GeneratorFragment fragment = new GeneratorFragment();
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
		View view =  inflater.inflate(R.layout.fragment_generator, container, false);
		btnGenerateLotteryTickets =  view.findViewById(R.id.btnGenerateLotteryTickets);
		editTextMinMax = view.findViewById(R.id.editTextMinMax);
		editTextMinMax.setMaxLines(1);

		btnGenerateLotteryTickets.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					final int LOTTO_MAX = 47;
					final int MEGA_MAX = 27;
					final int MAX_NUM_OF_TICKETS = 10;
					//List<Integer> lottoNumbers = null; //
					//List<Integer> megaNumbers = null; //
					String minNMax = editTextMinMax.getText().toString();
                    //Code to create lists with the most lotto and mega numbers
					List<Integer> lottoNumbers = getListForNumbers(myList, LOTTO_MAX, minNMax);
					List<Integer> megaNumbers = getListForNumbers(myList, MEGA_MAX, minNMax);
					List<LotteryNumbersHolder> lotteryTicketsProduced = new ArrayList<>();
					for(int i = 0; i < MAX_NUM_OF_TICKETS; i++ ){

						lotteryTicketsProduced.add(generateLotteryNumbers(lottoNumbers, megaNumbers));
					}

					LottoArrayAdapter adapter = new LottoArrayAdapter(getActivity(), R.layout.fragment_past_lotto_listitem, lotteryTicketsProduced);
					setListAdapter(adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		return view;
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
	public List<Integer> getListForNumbers(List<LotteryNumbersHolder> list, Integer number, String minMax) {


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
					//printHashMap(" HASH MEGA NUMBERS: :", myHashLotteryNumbers);
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
			Toast.makeText(getContext(), "Min,Max INPUT ERROR: Verify min,max input( for ex. 5,10,3,8)", Toast.LENGTH_LONG).show();
		}
		//Log.i("POPULARLOTTERYNUMBERS = ", String.valueOf(popularLotteryNumbers.size()));
		Log.i(TAG, String.valueOf(popularLotteryNumbers.size()));
		return popularLotteryNumbers;
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
	 *  generateLottryNumbers() generates lottery numbers using the user's defined min,max
	 *  values. The values are stored in a list. A random number between 1 and the list size
	 *  is generated and the valus is used as the list item index. The value at that index will
	 *  be chosen as the number
	 *
	 * @pre none
	 * @parameter List<Integer> myLottoList: list containing the user defined min,max values
	 * @post
	 **********************************************************************************/
	public LotteryNumbersHolder generateLotteryNumbers(List<Integer> myLottoList, List<Integer> myMegaList){


		//rand is used to determined the list index to extract the integer values from myLottoList and myMegaList ,
		// to filthat will be used to select the number
		// i.e myLottoList has 13 items and rand will generate a number(newNum) between 0 and 12  and then
		//will use that number as the index(myLottoList.get(newNum))  to select the randow
		// lottey number and place it a new list
		Random rand = new Random(); //
		int[] lotteryNumbers = new int[5];
		int loopIterator = 100;//the algorithm generates the numbers after the 100 loop
		int minNum = 1; //
		int maxLottoNum = myLottoList.size() -1;
		int maxMegaNum = myMegaList.size() - 1;
		int num = 5; //total number of daily regular lottery numbers


		try {
			for(int i = 0; i < loopIterator; i++) {

				int index = 0;
				while (index < num) {
					//rand is used to determined the list index, that will be used to select the number
					// from myLottoList and myMegaList i.e myLottoList has 13 items and rand will generate a number(newNum) between 0 and 12  and then
					//will use that number as the index(myLottoList.get(newNum))  to select the randow
					// lottey number and place it a new list
					int newNum = rand.nextInt((maxLottoNum - minNum) + 1) + minNum;
					if (index == 0) {
						lotteryNumbers[index] = myLottoList.get(newNum);
						Log.i(TAG, String.valueOf(lotteryNumbers[index]));
					} else {
						while (isDuplicateNum(myLottoList.get(newNum), lotteryNumbers, index)) {
							newNum = rand.nextInt((maxLottoNum - minNum) + 1) + minNum;
						}
						lotteryNumbers[index] = myLottoList.get(newNum);
						//Log.i("MyNumbers4: ", String.valueOf(lotteryNumbers[index]));
					}
					index++;
				}// end while
			}
			sortAscending(lotteryNumbers);

		}catch(Exception e){
			// Log.i( "ERROR10000", e.toString());
		}


		StringBuilder lottoString = new StringBuilder();
		for(int i = 0; i < num; i++){
			lottoString.append(lotteryNumbers[i]).append(" ");
		}

		int megaNumber = rand.nextInt((maxMegaNum - minNum) + 1) + minNum;
		lottoString.append(megaNumber);
		return new LotteryNumbersHolder(null, lottoString.toString());
	}

	/*********************************************************************************
	 *  isDuplicateNum() checks to see if the current generated number has already been selected
	 *
	 * @pre none
	 * @parameter int newNum: random generated num
	 *            int []lotteryNumbers: numbers currently stored in the array
	 *            int index current number of generated numbers in the array
	 * @post return true if number exist in the arry, false if the number does not exist in the array
	 **********************************************************************************/
	public boolean isDuplicateNum(int newNum, int[]lotteryNumbers, int index){
		Log.i("TESTING", "Entering isDuplicate");
		boolean duplicate = false;
		for (int i = 0; i < index; i++){

			if (newNum == lotteryNumbers[i]){
				duplicate = true;
				break;
				//return duplicate;
			}
		}
		Log.i("TESTING", "Exiting isDuplicate");
		return duplicate;
	}

	/*********************************************************************************
	 *  sortAscending() sorts the generated lottery  numbers in ascending order in the array
	 *
	 * @pre none
	 * @parameter int array
	 * @post  updates the array
	 **********************************************************************************/
	public void sortAscending(int[] array){
		boolean swap = true;
		int j = 0;
		while (swap) {
			swap = false;
			j++;
			for (int i = 0; i < array.length - j; i++) {
				if (array[i] > array[i+1]) {//swap code

					int temp = array[i];
					array[i] = array[i+1];
					array[i+1] = temp;
					swap = true;
				}
			}
		}//end while
	}
}
