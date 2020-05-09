package com.example.lotterylistfragmentactivity.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LotteryNumbersHolder implements Parcelable {

	private Integer NUMBER = 5;
	private String date;
	private Integer num[];
	private Integer megaNumber;


	public LotteryNumbersHolder(String date, String numberString){
		num = new Integer[NUMBER];
		this.date = date;
		parseStringToGetNumbers(numberString);

	}

	protected LotteryNumbersHolder(Parcel in) {
		if (in.readByte() == 0) {
			NUMBER = null;
		} else {
			NUMBER = in.readInt();
		}

		date = in.readString();

		if (in.readByte() == 0) {
			megaNumber = null;
		} else {
			megaNumber = in.readInt();
		}

		Object[] object = in.readArray(null);
		num = new Integer[object.length];
		for( int i = 0; i < object.length; i++){
			this.num[i] = (Integer) object[i];
		}
	}

	public static final Creator<LotteryNumbersHolder> CREATOR = new Creator<LotteryNumbersHolder>() {
		@Override
		public LotteryNumbersHolder createFromParcel(Parcel in) {
			return new LotteryNumbersHolder(in);
		}

		@Override
		public LotteryNumbersHolder[] newArray(int size) {
			return new LotteryNumbersHolder[size];
		}
	};

	public Integer getMegaNumber(){return megaNumber;}
	public Integer getLottoNumbers(int index){return num[index];}
	public String getDate(){return date;}
	public Integer getNUMBER(){return NUMBER;}

	private void parseStringToGetNumbers(String numberString){

		final String SPACE_DELIMITER = " ";
		String[] lNumberInput = numberString.split(SPACE_DELIMITER);


		for (int i = 0; i < lNumberInput.length; i++) {

			switch(i){
				case 5: this.megaNumber = Integer.parseInt(lNumberInput[i]);
				break;
				default: this.num[i]= Integer.parseInt(lNumberInput[i]);
			}
		}
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (NUMBER == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(NUMBER);
		}
		dest.writeString(date);
		if (megaNumber == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeInt(megaNumber);
		}

		dest.writeArray(num);
	}
}
