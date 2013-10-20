package trip.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CalcApplication extends Application {
	private static final String TAG = CalcApplication.class.getSimpleName();
	StatusData statusData;
	SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "### calcApplication - on create");
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		statusData = new StatusData(this);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// statusData.close();
	}

	public String currencyIdToMark(int id) {
		String[] marks = getResources().getStringArray(R.array.currency_marks);
		return marks[id];
	}

}
