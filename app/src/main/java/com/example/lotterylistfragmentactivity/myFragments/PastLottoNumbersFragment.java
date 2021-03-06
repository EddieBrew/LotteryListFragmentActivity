package com.example.lotterylistfragmentactivity.myFragments;


import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.adapters.LottoArrayAdapter;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.lotterylistfragmentactivity.CustomMenuActivity.sortListByDescendingDate;
import static com.example.lotterylistfragmentactivity.MainActivity.getDatabaseContent;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastLottoNumbersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastLottoNumbersFragment extends ListFragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String TAG = "PastLottoNumbers";
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private List<LotteryNumbersHolder> myList = new ArrayList<>();
	private List<LotteryNumbersHolder> mParam1;
	private String mParam2;
	public PastLottoNumbersFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment newListFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static PastLottoNumbersFragment newInstance(String param1, String param2) {
		PastLottoNumbersFragment fragment = new PastLottoNumbersFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myList = getDatabaseContent();
		sortListByDescendingDate(myList);
		//Log.d(TAG, Arrays.toString(myList.toArray()));


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_past_lotto_numbers_list, container, false);

		LottoArrayAdapter adapter = new LottoArrayAdapter(getActivity(), R.layout.fragment_past_lotto_listitem, myList );
		setListAdapter(adapter);

		return view;
	}



}
