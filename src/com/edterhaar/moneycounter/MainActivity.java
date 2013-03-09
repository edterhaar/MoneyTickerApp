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
	double secondRateTaxed;
	double secondRateGross;
	
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
			double salary = Double.parseDouble(editText.getText().toString());
			double taxedRate = TaxedRate(salary);
			
			secondRateTaxed = (((double)taxedRate/52)/37)/3600;
			secondRateGross = (((double)salary/52)/37)/3600;
		
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
			TextView taxCounterText = (TextView)findViewById(R.id.TaxCounter);
			TextView grossCounterText = (TextView)findViewById(R.id.GrossCounter);
			
			time ++;
			double amountGross = time*secondRateGross/10;
			double amountTax = time*secondRateTaxed/10;
			
			NumberFormat fmt = NumberFormat.getCurrencyInstance();
			taxCounterText.setText(fmt.format(amountTax));
			grossCounterText.setText(fmt.format(amountGross));
		}
	};
	
	private double TaxedRate(double salary)
	{
		double incomeTax = 0;
		double allowance = 8105;
		double nationalInscurance = 0;
		double minusAllowance = salary - allowance;
				
		if( minusAllowance > 0)
		{
			if(minusAllowance > 34370)
			{
				if(minusAllowance > 150000)
				{
					incomeTax = (34370 * 0.2) + (150000 * 0.40) + ((minusAllowance - 150000) * 0.50);
				}
				else
				{
					incomeTax = (34370 * 0.2) + ((minusAllowance - 34370) * 0.40);					
				}
			}
			else
			{
				incomeTax = minusAllowance * 0.20;
			}
		}
		
		double nationalInsuranceZeroBand = 7592;
		double topBand = 42484;
		if(salary > nationalInsuranceZeroBand)
		{
			
			if(salary > topBand)
			{
				double firstBand = ((topBand - nationalInsuranceZeroBand) * 0.12);
				double secondBand = ((salary - topBand) * 0.02);
				nationalInscurance = firstBand + secondBand;
			}
			else
			{
				nationalInscurance = (salary-nationalInsuranceZeroBand) * 0.12;
				
			}
		}
		
		 		
		return salary - (incomeTax + nationalInscurance);
	}
}
