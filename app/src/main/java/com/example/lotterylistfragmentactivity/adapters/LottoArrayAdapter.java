package com.example.lotterylistfragmentactivity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.example.lotterylistfragmentactivity.R;
import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;


import java.util.List;

public class LottoArrayAdapter extends ArrayAdapter<LotteryNumbersHolder> {

	private final List<LotteryNumbersHolder> objects;
	private final Context context;
    private final Integer NUMCOUNT = 5;
	private Object Locale;

	public class LottoNumberViewHolder {
		LottoNumberViewHolder(){}
		TextView textViewDateH;
		TextView textViewNum1H;
		TextView textViewNum2H;
		TextView textViewNum3H;
		TextView textViewNum4H;
		TextView textViewNum5H;
		TextView textViewNumMegaH;
	}

   //place the name of the custom listview layout (lottery_itemlist) in the super
	public LottoArrayAdapter(Context context, @LayoutRes int resource, List<LotteryNumbersHolder> objects) {
		super(context, resource, objects);

		this.context = context;
		this.objects = objects;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get the data item for this position
		LotteryNumbersHolder user = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		LottoNumberViewHolder holder; // view lookup cache stored in tag


		// If there's no view to re-use, inflate a brand new view for row
		if(convertView == null) {
			holder = new LottoNumberViewHolder();

			LayoutInflater inflater = LayoutInflater.from(getContext());

			//convertView uses the custom layout containing each list element
			convertView = inflater.inflate(R.layout.fragment_past_lotto_listitem, parent, false);
			holder.textViewDateH = convertView.findViewById(R.id.txtViewDate);
			holder.textViewNum1H = convertView.findViewById(R.id.num1);
			holder.textViewNum2H = convertView.findViewById(R.id.num2);
			holder.textViewNum3H = convertView.findViewById(R.id.num3);
			holder.textViewNum4H = convertView.findViewById(R.id.num4);
			holder.textViewNum5H = convertView.findViewById(R.id.num5);
			holder.textViewNumMegaH = convertView.findViewById(R.id.numMega);
			convertView.setTag(holder);
		}
		else{
			holder = (LottoNumberViewHolder) convertView.getTag();
		}


		holder.textViewDateH.setText(user.getDate());

		for(int i = 0; i < NUMCOUNT; i++){
			switch(i){

				case 0: holder.textViewNum1H.setText(String.format("%2d",user.getLottoNumbers(i)));
				break;
				case 1: holder.textViewNum2H.setText(String.format("%2d", user.getLottoNumbers(i)));
				break;
				case 2: holder.textViewNum3H.setText(String.format("%2d",user.getLottoNumbers(i)));
				break;
				case 3: holder.textViewNum4H.setText(String.format("%2d", user.getLottoNumbers(i)));
				break;
				case 4: holder.textViewNum5H.setText(String.format("%2d", user.getLottoNumbers(i)));
				break;
				default:
			}

		}//end for

		holder.textViewNumMegaH.setText(String.format("%d", user.getMegaNumber()));

		return convertView;


	}



}
