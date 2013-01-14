package com.edterhaar.moneycounter;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	int time = 0;
	Timer timer = null;
	double secondRate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		int defaultValue = Integer.parseInt(getString(R.string.Salary_Amount));
		int Count = sharedPref.getInt("salery", defaultValue);
		
		EditText editText = (EditText)findViewById(R.id.SaleryField);
		editText.setText(String.valueOf(Count));
	}

	protected void onStop()
	{
		super.onStop();
		
		SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
		EditText editText = (EditText)findViewById(R.id.SaleryField);
		preferences.edit().putInt("salery", Integer.parseInt(editText.getText().toString())).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void startCounter(View view)
	{
		if(timer == null)
		{
			timer = new Timer();
		
			time=0;
		
			EditText editText = (EditText)findViewById(R.id.SaleryField);
			int salary = Integer.parseInt(editText.getText().toString());
			secondRate = (((double)salary/52)/37)/3600;
		
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					timerMethod();
				}
			
			},0,100);
		}
	}
	
	public void stopCounter(View view)
	{
		if(timer!=null)
		{
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}
	
	public void timerMethod()
	{
		this.runOnUiThread(IncrementCounter);
	}
	
	private Runnable IncrementCounter = new Runnable() 
	{	
		@Override
		public void run() 
		{
			time ++;
			double amount = time*secondRate/10;
			TextView counterText = (TextView)findViewById(R.id.CounterText);
			NumberFormat fmt = NumberFormat.getCurrencyInstance();
			counterText.setText(fmt.format(amount));
		}
	};
	

}
