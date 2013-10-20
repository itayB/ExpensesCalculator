package trip.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity{

	CalcApplication calc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Debug.startMethodTracing("itay.trace");

		calc = (CalcApplication)this.getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemAdd:
			startActivity( new Intent(this, AddActivity.class) );
			break;
		case R.id.itemPrefs:
			startActivity( new Intent(this, SettingsActivity.class) );
			break;
		case R.id.itemAbout:
			
			(new AlertDialog.Builder(this)
            //.setIcon(R.drawable.alert_dialog_icon)
            .setTitle("About")
            .setMessage("This application was created by Itay Bittan")
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       
                    }
                }
            )
            .create()).show();
			
			//Dialog d = new Dialog(this);
			//d.setTitle("This app was created by Itay Bittan");
			//d.show();
			break;			
		case R.id.itemExit:
			finish();
			break;			
			
		}		
		
		return super.onOptionsItemSelected(item);
		//return true;
	}
	

}
