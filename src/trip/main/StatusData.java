package trip.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();

	public static final String C_ID = BaseColumns._ID;
	public static final String C_DATE = "calc_date";
	public static final String C_DESCRIPTION = "calc_description";
	public static final String C_PRICE = "calc_price";
	public static final String C_CURRENCY = "calc_currency";

	Context context;
	DbHelper dbHelper;

	double[][] currencyTable = { { 1, 3.66, 5.02 }, { 0.27, 1, 1.37 },
			{ 0.19, 0.72, 1 } };

	// Constructor
	public StatusData(Context context) {
		Log.d(TAG, "### StatusData - constructor");
		this.context = context;
		dbHelper = new DbHelper();
	}

	double getValueInDiffCurrency(double value, int srcCurrency, int destCurreny) {
		return value * currencyTable[destCurreny][srcCurrency];
	}

	private ContentValues packValues(String description, Double price,
			String date, int currency) {
		ContentValues values = new ContentValues();
		values.put(StatusData.C_DESCRIPTION, description);
		values.put(StatusData.C_PRICE, price);
		values.put(StatusData.C_DATE, date);
		values.put(StatusData.C_CURRENCY, currency);

		return values;
	}

	public void insert(String description, Double price, String date,
			int currency) {
		// Create content values
		ContentValues values = packValues(description, price, date, currency);

		this.insert(values);
	}

	private void insert(ContentValues values) {
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Insert into database
		long rowID = db.insertWithOnConflict(DbHelper.TABLE, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);

		Log.d(TAG, "### StatusData - insert, rowID:" + rowID);

		// Close database
		db.close();
	}

	public void update(int rowId, String description, double price,
			String date, int currency) {
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Create content values
		ContentValues values = packValues(description, price, date, currency);

		db.update(DbHelper.TABLE, values, StatusData.C_ID + " == " + rowId,
				null);

		// Close database
		db.close();
	}

	public void delete(int rowId) {
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(DbHelper.TABLE, StatusData.C_ID + " == " + rowId, null);

		// Close database
		db.close();
	}

	public ContentValues getRow(int rowId) {
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query(DbHelper.TABLE, null, StatusData.C_ID + " == "
				+ rowId, null, null, null, null);

		if (!cursor.moveToNext())
			return null;

		String description = cursor.getString(cursor
				.getColumnIndex(StatusData.C_DESCRIPTION));

		double price = cursor.getDouble(cursor
				.getColumnIndex(StatusData.C_PRICE));

		String date = cursor
				.getString(cursor.getColumnIndex(StatusData.C_DATE));

		int currency = cursor.getInt(cursor
				.getColumnIndex(StatusData.C_CURRENCY));

		// Create content values
		ContentValues values = packValues(description, price, date, currency);

		// Close database
		db.close();

		return values;
	}

	// Delete all data
	public void delete() {
		// Open database
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// Delete the data
		db.delete(DbHelper.TABLE, null, null);

		// Close database
		db.close();
	}

	public void close() {

	}

	public Cursor query() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(DbHelper.TABLE, null, null, null, null, null, C_DATE
				+ " DESC");
	}

	public Cursor sumQuery(int currency) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(DbHelper.TABLE, new String[] { "SUM("
				+ StatusData.C_PRICE + ")" }, StatusData.C_CURRENCY + " == "
				+ currency, null, null, null, null);
	}

	private class DbHelper extends SQLiteOpenHelper {

		public static final String DB_NAME = "expensesCalc.db";
		public static final int DB_VERSION = 11;
		public static final String TABLE = "expenses";

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "### DbHelper - on create");
			String sql = String
					.format("create table %s (%s integer primary key autoincrement, %s INT, %s TEXT, %s MONEY, %s INT)",
							TABLE, C_ID, C_DATE, C_DESCRIPTION, C_PRICE,
							C_CURRENCY);

			Log.d(TAG, "### on create sql: " + sql);

			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Typically you do ALTER TABLE here...
			db.execSQL("drop table if exists " + TABLE);
			Log.d(TAG, "### onUpgrade dropped table " + TABLE);
			this.onCreate(db);
		}

	}

}
