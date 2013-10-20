package trip.main;

import java.util.Calendar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends BaseActivity {
	private static final String TAG = AddActivity.class.getSimpleName();
	private static final int DIALOG_DATE_PICKER = 0;
	private static final int DIALOG_CURRENCY_LIST = 1;
	EditText textDescription;
	EditText textPrice;
	EditText textDate;
	TextView textCurrency;
	Button buttonChangeCurrency;
	Button buttonSave;
	Button buttonCancel;
	int currency = 0; // should change later on to default value from settings
	int mYear = 2011;
	int mMonth = 1;
	int mDay = 1;
	int rowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);

		textDescription = (EditText) findViewById(R.id.editTextDescription);
		textPrice = (EditText) findViewById(R.id.editTextPrice);
		textDate = (EditText) findViewById(R.id.editTextDate);
		textCurrency = (TextView) findViewById(R.id.textCurrency);
		buttonChangeCurrency = (Button) findViewById(R.id.buttonChangeCurrency);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowId = extras.getInt("rowId");
			
			// get row data from DB
			ContentValues values = calc.statusData.getRow(rowId);
			
			textDescription.setText(values.getAsString(StatusData.C_DESCRIPTION));
			textPrice.setText("" + values.getAsDouble(StatusData.C_PRICE));
			textCurrency.setText(calc.currencyIdToMark(values.getAsInteger(StatusData.C_CURRENCY)));
			textDate.setText(values.getAsString(StatusData.C_DATE));
			currency = values.getAsInteger(StatusData.C_CURRENCY);
		}
		else {
			rowId = -1;
			
			// set default currency mark
			textCurrency.setText(calc.currencyIdToMark(currency));
			
		}
		
		buttonChangeCurrency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_CURRENCY_LIST);
			}

		});

		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String description = textDescription.getText().toString();
				String price = textPrice.getText().toString();
				String date = textDate.getText().toString();
				
				//String currency = textCurrency.getText().toString();
				
				Log.d(TAG, "### description: " + description);
				Log.d(TAG, "### price: " + price);
				Log.d(TAG, "### date: " + date);
				Log.d(TAG, "### currency: " + currency);

				if (description.equals("")) {
					Toast.makeText(AddActivity.this,
							R.string.errorMsgDescription, Toast.LENGTH_LONG)
							.show();
					return;
				}

				if (price.equals("")) {
					Toast.makeText(AddActivity.this, R.string.errorMsgPrice,
							Toast.LENGTH_LONG).show();
					return;
				}

				if (rowId == -1) // new expense
					calc.statusData.insert(description, Double.parseDouble(price),
						date, currency);
				else
					calc.statusData.update(rowId,description, Double.parseDouble(price),
							date, currency);

				startActivity(new Intent(AddActivity.this,
						ExpensesActivity.class).addFlags(
						Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
						Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});

		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(AddActivity.this,
				//		ExpensesActivity.class).addFlags(
				//		Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
				//		Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				finish();
			}
		});

		textDate.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				Calendar rightNow = Calendar.getInstance();
				Log.d("itay",
						"### " + rightNow.get(Calendar.YEAR) + " "
								+ rightNow.get(Calendar.MONTH) + " "
								+ rightNow.get(Calendar.DAY_OF_MONTH));

				showDialog(DIALOG_DATE_PICKER);
				return true;
			}
		});

	}

	DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DATE_PICKER:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case DIALOG_CURRENCY_LIST:
			return new AlertDialog.Builder(AddActivity.this)
					.setTitle(R.string.changeCurrency)
					.setItems(R.array.select_dialog_items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									/* User clicked so do some stuff */
									currency = which;
									
									String mark = calc.currencyIdToMark(currency);
									textCurrency.setText(mark);
									
									//String[] items = getResources()
									//		.getStringArray(
									//				R.array.select_dialog_items);
									//new AlertDialog.Builder(AddActivity.this)
									//		.setMessage(
									//				"You selected: " + which
									//						+ " , "
									//						+ items[which])
									//		.show();
								}
							}).create();
		}

		return null;
	}

	protected void updateDisplay() {
		textDate.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_DATE_PICKER) {
			Calendar rightNow = Calendar.getInstance();
			mYear = rightNow.get(Calendar.YEAR);
			mMonth = rightNow.get(Calendar.MONTH);
			mDay = rightNow.get(Calendar.DAY_OF_MONTH);
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
		}
	}

}
