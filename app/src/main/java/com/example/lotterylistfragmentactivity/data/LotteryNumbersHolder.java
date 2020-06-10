package com.example.lotterylistfragmentactivity.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class LotteryNumbersHolder implements Parcelable {

	private Integer NUMBER = 5;
	//private Integer dateInteger;
	private String date;
	private Integer num[];
	private Integer megaNumber;


	public LotteryNumbersHolder(String date, String numberString){
		num = new Integer[NUMBER];
		this.date = date;
		//this.dateInteger = convertDateStringToInt(date);
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

/*
		if (in.readByte() == 0) {
			dateInteger = null;
		} else {
			dateInteger = in.readInt();
		}
*/
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
	//public Integer getDateInteger(){return dateInteger;}

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

	/*********************************************************************************
	 *  convertDateStringToInt() converts a date in String format to an integer value t
	 * @pre none
	 * @parameter String
	 * @post returns int reprentation of the date
	 **********************************************************************************/
	private  Integer convertDateStringToInt(String dateString) {
		String delimStr = "";  //date format is mm/dd/yyyy
		String[] words = dateString.split(delimStr);

		return ((Integer.parseInt(words[0]) * 100) + (Integer.parseInt(words[1])) +
				Integer.parseInt(words[2]) * 10000);
	} //end method


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

	@Override
	public String toString() {
		return "LotteryNumbersHolder{" +
				 date  +
				", " + Arrays.toString(num) +
				", " + megaNumber +
				'}' + "\n";
	}
}
