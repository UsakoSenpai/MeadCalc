package net.phoenixlair.MeadCalc;

import net.phoenixlair.MeadCalc.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MeadCalcActivity extends Activity {
	
	private static final int HONEY = 1;
	private static final int WATER = 2;
	private static final int TARGET_SG = 3;
	private static final int SG_HONEY_DIALOG = 0;
	private static final int GREEN = android.graphics.Color.GREEN;
	
	private static float sg_of_honey = 0.042f;
	
	private Drawable defaultBackground;
	private EditText txt_sg_honey = null;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use defined XML layout
        setContentView(R.layout.main);
        
        // Set 'default' background for later use
        defaultBackground = ((EditText)findViewById(R.id.honey)).getBackground();
    }
    
    /** Override to create an options menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	// Add/Create button to change SG of Honey
    	menu.add(0, 0, 0, R.string.sg_honey_btn);
        return true;
    }
    
    /** Handle item selection from options menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
            case 0:
            	// Show Custom Dialog
            	showDialog(SG_HONEY_DIALOG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /** Define Custom Dialog **/ 
    @Override
	protected Dialog onCreateDialog(int id) {
    	
    	switch (id) {
		case SG_HONEY_DIALOG:

			// Use defined layout XML for the dialog view
			LayoutInflater factory = LayoutInflater.from(this);
			final View honeyDialogView = factory.inflate(R.layout.honey_sg_dialog, null);

			// Grab EditText so we can modify the value and later retrieve it
			txt_sg_honey = (EditText)honeyDialogView.findViewById(R.id.sg_honey);
			txt_sg_honey.setText(""+sg_of_honey);
			
			// Return AlertDialog that uses the view defined above
			return new AlertDialog.Builder(MeadCalcActivity.this)
					.setIcon(R.drawable.mead_calc)
					.setTitle(R.string.sg_honey_dialog)
					.setView(honeyDialogView)
					// Set 'OK' button 
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							// Change SG of Honey used in calculations to entered value 
							MeadCalcActivity.setHoneySG(Float.parseFloat(
									txt_sg_honey.getText().toString()));
						}
					})
					//Set 'Cancel' button
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							//Do nothing...
						}
					}).create();
		}
		return null;
	}
    
    /** Set SG of honey used in calculation **/
    public static void setHoneySG(float honeySG) {
    	sg_of_honey = honeySG;
    }
    
    /** Called onClick of 'Calculate' button **/
    public void onCalc(View calcButton) {
    	
    	// Grab EditText views
    	EditText txt_honey = (EditText) findViewById(R.id.honey);
    	EditText txt_water = (EditText) findViewById(R.id.water);
    	EditText txt_target_sg = (EditText) findViewById(R.id.target_sg);
    	
    	// Initialize counter, entered values, and solution case ID
    	int variablesEntered = 0;
    	float honey = 0f, water = 0f, target_sg = 0f;
    	int solveFor = 0;
    	
    	// Check which fields are empty/filled
    	if (txt_honey.getText().toString().trim().equals("")) {
    		solveFor = HONEY;
    	} else {
    		variablesEntered++;
    		honey = Float.parseFloat(txt_honey.getText().toString());
    	}
    	if (txt_water.getText().toString().trim().equals("")) {
    		solveFor = WATER;
    	} else {
    		variablesEntered++;
    		water = Float.parseFloat(txt_water.getText().toString());
    	}
    	if (txt_target_sg.getText().toString().trim().equals("")) {
    		solveFor = TARGET_SG;
    	} else {
    		variablesEntered++;
    		target_sg = Float.parseFloat(txt_target_sg.getText().toString());
    	}
    	
    	// Check how many values were entered
    	if (variablesEntered != 2) {
    		
    		// Display user error message
    		Toast.makeText(this, R.string.variable_entry_error, Toast.LENGTH_SHORT).show();
    		
    	} else {
    		// Do calculation, set solved value and highlight with green, 
    		// then disable other fields
    		Drawable bg = defaultBackground.getCurrent();
			bg.setColorFilter(GREEN, PorterDuff.Mode.SRC_ATOP);
    		switch (solveFor) {
    			case HONEY:
    				honey = (water*((target_sg-1)*1000)) / (sg_of_honey*1000);
    				txt_honey.setBackgroundDrawable(bg);
    				txt_honey.setText(""+honey);
    				txt_water.setEnabled(false);
    				txt_target_sg.setEnabled(false);
    				break;
    			case WATER:
    				water =((sg_of_honey*1000)*honey) / ( (target_sg-1)*1000);
    				txt_water.setBackgroundDrawable(bg);
    				txt_water.setText(""+water);
    				txt_honey.setEnabled(false);
    				txt_target_sg.setEnabled(false);
    				break;
    			case TARGET_SG:
    				target_sg = (((sg_of_honey*1000)*honey) / (1000*water)) + 1;
    				txt_target_sg.setBackgroundDrawable(bg);
    				txt_target_sg.setText(""+target_sg);
    				txt_honey.setEnabled(false);
    				txt_water.setEnabled(false);
    				break;
    		}
    		// Disable 'Calculate' button
			calcButton.setEnabled(false);
    	}
    }
    
    /** Called onClick of 'Clear' button **/
    public void onClear(View clearButton) {
    	
    	// Grab EditText views and 'Calculate' button
    	EditText txt_honey = (EditText) findViewById(R.id.honey);
    	EditText txt_water = (EditText) findViewById(R.id.water);
    	EditText txt_target_sg = (EditText) findViewById(R.id.target_sg);
    	Button calcButton = (Button) findViewById(R.id.calc_button);

    	// Enable EditText views, clear values, set backgrounds back to default
    	txt_honey.setEnabled(true);
		txt_water.setEnabled(true);
		txt_target_sg.setEnabled(true);
		txt_honey.setBackgroundDrawable(defaultBackground);
		txt_water.setBackgroundDrawable(defaultBackground);
		txt_target_sg.setBackgroundDrawable(defaultBackground);
		txt_honey.setText("");
		txt_water.setText("");
		txt_target_sg.setText("");
		
		// Enable 'Calculate' button
		calcButton.setEnabled(true);
    }
}