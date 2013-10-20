package trip.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class ExpensesActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = ExpensesActivity.class.getSimpleName();
	ListView listExpenses;
	TextView textTotalPrice;
	SimpleCursorAdapter adapter;
	Cursor cursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "### ExpensesActivity - onCreate");
		// Debug.startMethodTracing("itay.trace");

		// Setup UI
		setContentView(R.layout.expenses);
		listExpenses = (ListView) findViewById(R.id.listViewExpenses);
		textTotalPrice = (TextView) findViewById(R.id.textTotalPrice);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "### ExpensesActivity - onResume");

		// Get the data
		cursor = calc.statusData.query();
		startManagingCursor(cursor);
		Log.d(TAG, "### cursor number is: " + cursor.getCount());

		// Setup Adapter
		String[] from = { StatusData.C_ID, StatusData.C_DATE,
				StatusData.C_DESCRIPTION, StatusData.C_PRICE,
				StatusData.C_CURRENCY };
		int[] to = { R.id.textIndex, R.id.textDate, R.id.textText,
				R.id.textPrice, R.id.textCurrency };
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		adapter.setViewBinder(VIEW_BINDER);
		listExpenses.setAdapter(adapter);
		Log.d(TAG, "### finished Setup Adapter");

		listExpenses.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// • Arg0:the listview, notice that it is of type AdapterView.
				// • Arg1: the view that represents the selected item
				// • Arg2: the position of the selected item.
				// • Arg3: the id of the selected item.

				TextView textIndex = ((TextView) arg1
						.findViewById(R.id.textIndex));

				final int rowId;
				try {
					rowId = Integer.parseInt(textIndex.getText().toString());
				} catch (Exception e) {
					return false;
				}

				// get row data from DB
				ContentValues values = calc.statusData.getRow(rowId);

				String strPrice = attachPriceAndCurrency(
						values.getAsDouble(StatusData.C_PRICE),
						values.getAsInteger(StatusData.C_CURRENCY));

				new AlertDialog.Builder(ExpensesActivity.this)
						//.setIcon(R.drawable.alert_dialog_icon)
						.setTitle(strPrice)
						.setMessage(
								values.getAsString(StatusData.C_DESCRIPTION))
						.setPositiveButton("Edit",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent i = new Intent(ExpensesActivity.this, AddActivity.class);
										i.putExtra("rowId",rowId);
										startActivity( i );
									}
								})
						.setNeutralButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										
										calc.statusData.delete(rowId);
										startActivity(new Intent(ExpensesActivity.this,
												ExpensesActivity.class).addFlags(
												Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
												Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										/* User clicked Cancel so do some stuff */
									}
								}).create().show();
				return false;
			}
		});

		listExpenses.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});

		// Toast.makeText(this, "itay is here", Toast.LENGTH_LONG).show();
		Log.d(TAG, "### listExpenses number is: " + listExpenses.getCount());

		// Calculate and display the total expense
		double sum = 0;
		int destCurreny = Integer.parseInt(calc.prefs.getString(
				"defaultCurrency", "0"));

		for (int srcCurrency = 0; srcCurrency < 3; srcCurrency++) {
			cursor = calc.statusData.sumQuery(srcCurrency);
			int index = cursor
					.getColumnIndex("SUM(" + StatusData.C_PRICE + ")");
			cursor.moveToNext();
			double value = cursor.getDouble(index);
			value = calc.statusData.getValueInDiffCurrency(value, srcCurrency,
					destCurreny);
			sum = sum + value;
		}

		String currency = currencyMark(destCurreny);
		String total;
		if (currency.equals("¤"))
			total = String.format("%s%10.2f", currency, sum);
		// textTotalPrice.setText(currency + sum);
		else
			total = String.format("%10.2f%s", sum, currency);
		// textTotalPrice.setText(sum + currency);

		textTotalPrice.setText(total);
	}

	protected String attachPriceAndCurrency(Double price, Integer currency) {
		String mark = calc.currencyIdToMark(currency);

		if (currency == 0)
			return "" + mark + price;
		return "" + price + mark;
	}

	private String currencyMark(int destCurreny) {
		String[] items = getResources().getStringArray(R.array.currency_marks);
		return items[destCurreny];
	}

	@Override
	public void onClick(View arg0) {
		Log.d("itay", " bittan!!");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Debug.stopMethodTracing();
	}

	// static final
	ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (cursor.getColumnIndex(StatusData.C_CURRENCY) != columnIndex)
				return false;
			else {
				int currency = cursor.getInt(columnIndex);

				String mark = calc.currencyIdToMark(currency);
				((TextView) view).setText(mark);
				return true;
			}
		}

	};

	// private class PostToTwitter extends AsyncTask<String, String, String> {
	//
	// @Override
	// protected String doInBackground(String... status) {
	// String result = "status succeeded";
	//
	// // post here to twitter
	//
	// Toast.makeText(ExpensesActivity.this,
	// "doInBackground " + status[0], Toast.LENGTH_LONG).show();
	//
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// }
	//
	// }
}