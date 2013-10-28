package trip.main;

import java.util.Locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.style.UpdateLayout;
import android.util.DisplayMetrics;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final ListPreference language = (ListPreference) findPreference("defaultLanguage");

		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				/* get the language code: "en", "iw" etc. */
				for (int i = 0; i < language.getEntryValues().length; i++) {
					if (language.getEntryValues()[i].equals((newValue))) {
						preference.setSummary(language.getEntries()[i]);
						break;
					}
				}

				/* change take affect only after restarting the appt */
				Resources res = getApplication().getResources();
				DisplayMetrics dm = res.getDisplayMetrics();
				android.content.res.Configuration conf = res.getConfiguration();
				conf.locale = new Locale((String) newValue);
				res.updateConfiguration(conf, dm);

				/* restarting the app so language change take affect */
				Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(
								getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

				return true;
			}
		});
	}

}
