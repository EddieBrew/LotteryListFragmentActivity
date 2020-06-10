package com.example.lotterylistfragmentactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.lotterylistfragmentactivity.data.LotteryNumbersHolder;

public class MyLotteryDatabase extends SQLiteOpenHelper {

	public static final String TAG = "Database Class";
	public static final String DATABASE_NAME = "LotteryDatabase.db";
	public static final String TABLE_NAME = "lotteryNumbers";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_NUM1 = "num1";
	public static final String COLUMN_NUM2 = "num2";
	public static final String COLUMN_NUM3 = "num3";
	public static final String COLUMN_NUM4 = "num4";
	public static final String COLUMN_NUM5 = "num5";
	public static final String COLUMN_MEGA = "mega";
	final String SPACE_DELIMITER = " ";

	public MyLotteryDatabase(@Nullable Context context) {
		super(context, DATABASE_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_LOTTERY_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME +"("
				+ COLUMN_ID + " INTEGER PRIMARY KEY,"
				+ COLUMN_DATE + " TEXT, "
				+ COLUMN_NUM1 + " INTEGER, "
				+ COLUMN_NUM2 + " INTEGER, "
				+ COLUMN_NUM3 + " INTEGER, "
				+ COLUMN_NUM4 + " INTEGER, "
				+ COLUMN_NUM5 + " INTEGER, "
				+ COLUMN_MEGA + " INTEGER );";



		db.execSQL(CREATE_LOTTERY_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void insertLotteryNumbers(LotteryNumbersHolder lotteryData){
		Log.i(TAG, "ENTERING INSERTION");
		SQLiteDatabase db = this.getWritableDatabase();
		Log.i(TAG, lotteryData.getDate());
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_DATE, lotteryData.getDate());
		contentValues.put(COLUMN_NUM1, lotteryData.getLottoNumbers(0));
		contentValues.put(COLUMN_NUM2, lotteryData.getLottoNumbers(1));
		contentValues.put(COLUMN_NUM3, lotteryData.getLottoNumbers(2));
		contentValues.put(COLUMN_NUM4,lotteryData.getLottoNumbers(3) );
		contentValues.put(COLUMN_NUM5, lotteryData.getLottoNumbers(4));
		contentValues.put(COLUMN_MEGA, lotteryData.getMegaNumber());

		// db.insert(TABLE_NAME, null, contentValues);
		db.insert(TABLE_NAME, null, contentValues);
		db.close();

	}

	public Integer numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();

		return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
	}

	public boolean checkForDuplicateDate(String queryValue){
		SQLiteDatabase db = this.getWritableDatabase();
		LotteryNumbersHolder entry;
		int indexSelect = 1;//selects the date field
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst()){
			do {
				String info ;
				//index = 0 is the tables ID and is not used as a varaibale in the DailyInfoModel class
				for(int index = 1; index < 8; index++  ) {
					if(index == indexSelect) {
						info = cursor.getString(index); //retrieves the date string
						if(info.equals(queryValue)) {
							return true;
						}
					}
				}
			} while (cursor.moveToNext());

		}
		return false;
	}

	public void deleteEntry(String date){
		SQLiteDatabase db = this.getWritableDatabase();
		String queryItems= COLUMN_DATE ;

		db.delete(TABLE_NAME,  queryItems  , new String[]{date});
	}

	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME); //delete all rows in a table
		db.close();
	}

	public Cursor getAllLotteryNumbers() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
		return res;
	}

	public void printDatabase(){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = getAllLotteryNumbers();
		while (res.moveToNext()) {//retrieves lotto info from the database and populates the
			//list which is used to display lottery numbers
			String dateString = res.getString(1);
			StringBuffer buffer = new StringBuffer();
			buffer.append(res.getInt(2) + " ");
			buffer.append(res.getInt(3) + " ");
			buffer.append(res.getInt(4) + " ");
			buffer.append(res.getInt(5) + " ");
			buffer.append(res.getInt(6) + " ");
			buffer.append(res.getInt(7));
            Log.d(TAG, dateString + "::" + buffer.toString());
		}

	}





}
