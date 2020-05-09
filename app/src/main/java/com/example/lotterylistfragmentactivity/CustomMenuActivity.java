package com.example.lotterylistfragmentactivity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.example.lotterylistfragmentactivity.myFragments.FrequencyFragment;
import com.example.lotterylistfragmentactivity.myFragments.GeneratorFragment;
import com.example.lotterylistfragmentactivity.myFragments.PastLottoNumbersFragment;

import static com.example.lotterylistfragmentactivity.MainActivity.lottoNumbersPastDrawings;

public class CustomMenuActivity extends AppCompatActivity {
	private Intent mIntent;
	private FragmentManager fm;
	ActionBar actionBar;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getMenuInflater();
		mMenuInflater.inflate(R.menu.menu_activity, menu);
		changeActionBarTitle("PAST LOTTO NUMBERS");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int fragmentSelector;

		switch (item.getItemId()) {
			case (R.id.menu_generateLottoNumbers)://When the Delete Database menu item is clicked, the event below is performed:
				changeActionBarTitle("LOTTO NUMBER GENERATOR");
				fm = getSupportFragmentManager();
				GeneratorFragment generatorFragment = GeneratorFragment.newInstance(lottoNumbersPastDrawings, null);
				fm.beginTransaction()
						.replace(R.id.myContainer, generatorFragment)
						.addToBackStack(null)
						.commit();
				Toast.makeText(this, "LOTTO NUMBER GENERATOR", Toast.LENGTH_LONG).show();
				return true;

			case (R.id.menu_pastLottoNumbers)://
				changeActionBarTitle("PAST LOTTO NUMBERS");
				//FragmentManager fm;
				fm = getSupportFragmentManager();
				PastLottoNumbersFragment pastLottoNumbersFragment = PastLottoNumbersFragment.newInstance(lottoNumbersPastDrawings, null);
				fm.beginTransaction()
						.replace(R.id.myContainer, pastLottoNumbersFragment)
						.addToBackStack(null)
						.commit();


				Toast.makeText(this, "PAST LOTTERY NUMBERS", Toast.LENGTH_LONG).show();
				return true;




			case (R.id.menu_numberQuery)://

				//FragmentManager fm3;;
				fm = getSupportFragmentManager();
				FrequencyFragment frequencyFragment = FrequencyFragment.newInstance(lottoNumbersPastDrawings, null);
				fm.beginTransaction()
						.replace(R.id.myContainer, frequencyFragment)
						.addToBackStack(null)
						.commit();
				changeActionBarTitle("LOTTO NUMBER FREQUENCY");
				Toast.makeText(this, "Lotto Number Frequencies", Toast.LENGTH_LONG).show();
				return true;

			default:
		}//end switch
		return true;
	}

	public void changeActionBarTitle(String title){

		actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setTitle(title);
		}
	}

}
