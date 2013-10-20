package trip.main;

import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		final ListPreference language = (ListPreference) findPreference("defaultLanguage");	
		language.setSummary(language.getEntry().toString());
		
		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				for(int i=0 ; i < language.getEntryValues().length ; i++)
				{
					if (language.getEntryValues()[i].equals((newValue)))
					{
						preference.setSummary(language.getEntries()[i]);
						break;
					}
				}
				
				//preference.setSummary(language.getEntry());
				//preference.setSummary((String)newValue);
				
				 Resources res = getApplication().getResources();
				 // Change local settings in the app.
				    DisplayMetrics dm = res.getDisplayMetrics();
				    android.content.res.Configuration conf = res.getConfiguration();
				    conf.locale = new Locale((String)newValue);
				    getApplication().getResources().getConfiguration();
				    res.updateConfiguration(conf, dm);
				
//				
//				//	getResources().getConfiguration().locale = getResources(). 
//				Log.d("itay", "### i'm here!!!!!!!!!!!!!!!!!");
//				// creating locale
//				Locale locale2 = new Locale((String)newValue); 
//				Locale.setDefault(locale2);
//				Configuration config2 = new Configuration();
//				
//				config2.locale = locale2;
//		
//				// updating locale
//				//getApplication().getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
//				getApplication().getBaseContext().getResources().updateConfiguration(config2,null);
//				
//				Log.d("itay", "### new value = " + (String)newValue);
//				Log.d("itay", "### new value = " + getApplication());
				return true;
			}
		});
		
//		if (language.getValue().equals("he")) {
//
//				
//		}
//		
//		//locale = getResources().getConfiguration().locale.getDisplayName();
//		
//		
//		CharSequence[] entries = { "NIS", "USD", "EURO" };
//		CharSequence[] entryValues = { "0", "1", "2" };
		final ListPreference currency = (ListPreference) findPreference("defaultCurrency");
//		currency.setEntries(entries);
//		currency.setEntryValues(entryValues);
//		currency.setSummary(currency.getEntry());
	}

}
