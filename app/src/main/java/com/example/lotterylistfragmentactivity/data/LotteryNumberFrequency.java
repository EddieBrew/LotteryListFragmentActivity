package com.example.lotterylistfragmentactivity.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LotteryNumberFrequency implements Parcelable {

	private Integer lottoNumber;
	private Integer numberOfTimesDrawn;

	public LotteryNumberFrequency(Integer lottoNumber, Integer numberOfTimesDrawn){
		this.lottoNumber = lottoNumber;
		this.numberOfTimesDrawn = numberOfTimesDrawn;
	}

	protected LotteryNumberFrequency(Parcel in) {
		lottoNumber = in.readInt();
		numberOfTimesDrawn = in.readInt();
	}

	public static final Creator<LotteryNumberFrequency> CREATOR = new Creator<LotteryNumberFrequency>() {
		@Override
		public LotteryNumberFrequency createFromParcel(Parcel in) {
			return new LotteryNumberFrequency(in);
		}

		@Override
		public LotteryNumberFrequency[] newArray(int size) {
			return new LotteryNumberFrequency[size];
		}
	};

	public Integer getLottoNumber(){ return lottoNumber;}
	public Integer getNumberOfTimesDrawn(){ return numberOfTimesDrawn; };

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(lottoNumber);
		dest.writeInt(numberOfTimesDrawn);
	}
}
