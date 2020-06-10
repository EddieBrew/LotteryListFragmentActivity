package com.example.lotterylistfragmentactivity.myFragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.adapters.LottoArrayAdapter;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyLotteryFragment extends ListFragment {
	private List<LotteryNumbersHolder> myList = new ArrayList<>();
	public MyLotteryFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// myList = new ArrayList<>();
		LottoArrayAdapter adapter = new LottoArrayAdapter(getActivity(), R.layout.fragment_past_lotto_listitem, myList );
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.my_lotteryfragment, container, false);




		return view;
	}
}
