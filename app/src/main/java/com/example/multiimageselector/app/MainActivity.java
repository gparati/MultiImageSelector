package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    final int MULTI_IMAGE_REQUEST = 111;

    HashMap mSelectedImages;
    RelativeLayout doubleImage;
    ImageView logo, overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        doubleImage = new RelativeLayout(this);
//        logo = new ImageView(this);
//        overlay = new ImageView(this);
//
//        logo.setImageResource(R.drawable.logo_imageselector);
//        overlay.setImageResource(R.drawable.picture_cross_border);
//        doubleImage.addView(logo);
//        doubleImage.addView(overlay);
//
//        this.addContentView(doubleImage, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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
                    ArrayList<ImagePathParcelable> pathParcelables = rIntent.getParcelableArrayListExtra("SELECTED_IMAGES_LIST");
                    int iter=0;
                    for (ImagePathParcelable iPP : pathParcelables){
                        mSelectedImages.put(iter++, iPP.get_imagePath());
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
