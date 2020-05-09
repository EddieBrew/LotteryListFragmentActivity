package com.example.lotterylistfragmentactivity.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.data.LotteryNumberFrequency;


import java.util.List;

public class LottoFrequencyArrayAdapter extends ArrayAdapter<LotteryNumberFrequency> {

	public static class LottoNumberFrequencyViewHolder {
		LottoNumberFrequencyViewHolder(){}
		TextView txtViewNumberH;
		TextView txtViewFrequencyH;
		TextView txtViewTimesH;

	}

	public LottoFrequencyArrayAdapter(Context context, int resource, List<LotteryNumberFrequency> objects) {
		super(context, resource, objects);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//return super.getView(position, convertView, parent);

		// Get the data item for this position
		LotteryNumberFrequency user = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		LottoNumberFrequencyViewHolder holder; // view lookup cache stored in tag

		if(convertView == null) {

			holder = new LottoNumberFrequencyViewHolder();

			LayoutInflater inflater = LayoutInflater.from(getContext());
			//convertView uses the custom layout containing each list element
			convertView = inflater.inflate(R.layout.fragment_frequency_itemlist, parent, false);

			holder.txtViewNumberH = convertView.findViewById(R.id.txtViewNumber);
			holder.txtViewFrequencyH = convertView.findViewById(R.id.txtViewFrequency);
			holder.txtViewTimesH = convertView.findViewById(R.id.txtViewTimes);
			convertView.setTag(holder);
		}
		else{

			holder = (LottoNumberFrequencyViewHolder) convertView.getTag();
		}

		assert user != null;
		holder.txtViewNumberH.setText((user.getLottoNumber()).toString());
		holder.txtViewFrequencyH.setText((user.getNumberOfTimesDrawn()).toString());
		holder.txtViewTimesH.setText("X");

		return convertView;

	}
}
