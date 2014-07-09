package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    final int MULTI_IMAGE_REQUEST = 111;

    HashMap mSelectedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectedImages = new HashMap();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mSelectedImages.isEmpty()) {
            Toast.makeText(getApplicationContext(), mSelectedImages.toString(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Currently no Images selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent rIntent){
        super.onActivityResult(requestCode, resultCode, rIntent);
        switch(requestCode) {
            case(MULTI_IMAGE_REQUEST) : {
                if (resultCode == Activity.RESULT_OK) {
                    //Intent i = getIntent();
                    ArrayList<ImagePathParcelable> pathParcelables = rIntent.getParcelableArrayListExtra("SELECTED_IMAGES_LIST");

                    //Cycle throug recivec list and fill hashmap
                    int iter=0;
                    for (ImagePathParcelable iPP : pathParcelables){
                        mSelectedImages.put(iter++, iPP.getImagePath());
                    }
                }
                break;
            }
        }
    }

    public void openImageSelector(View view){
        Intent intent = new Intent(this, ImageSelector.class);
        startActivityForResult(intent, MULTI_IMAGE_REQUEST);
    }
}
