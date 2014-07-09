package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ImageSelector extends Activity {
    final int NUMBER_OF_GRID_COLUMNS = 4;
    final int GRID_PADDING = 16;
    final int GRID_SPACING = 4;
    final int PICTURE_SCALE = 200;
    final int PICTURE_PADDING = 8;

    HashMap mSelectedImages = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);
        ImageAdapter imageAdapter = new ImageAdapter(this);

        GridView gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setNumColumns(NUMBER_OF_GRID_COLUMNS);
        gridView.setPadding(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING);
        gridView.setHorizontalSpacing(GRID_SPACING);
        gridView.setVerticalSpacing(GRID_SPACING);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(mSelectedImages.containsKey(position)){
                    ((ImageViewWithPath) v).deselect();
                    mSelectedImages.remove(position);
                    Toast.makeText(getApplicationContext(), "Picture Nr." + String.valueOf(position) + " removed.", Toast.LENGTH_SHORT).show();
                }else {
                    ((ImageViewWithPath) v).select();
                    mSelectedImages.put(position, ((ImageViewWithPath) v).getPath());
                    Toast.makeText(getApplicationContext(), "Picture Nr." + String.valueOf(position) + " added.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_selector, menu);
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

    public void processSelectedImages(View view) {
        Intent rIntent = new Intent(this, MainActivity.class);
        ArrayList<ImagePathParcelable> pathParcelables = new ArrayList<ImagePathParcelable>();
        Iterator it = mSelectedImages.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry)it.next();
            pathParcelables.add(new ImagePathParcelable(entry.getValue().toString()));
            it.remove();
        }
        rIntent.putParcelableArrayListExtra("SELECTED_IMAGES_LIST", pathParcelables);
        setResult(RESULT_OK, rIntent);
        //ri.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(rIntent);
        finish();
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        AllImagesPathsProvider mAllImagesPathsProvider;
        int mImageCount;

        public ImageAdapter(Context c) {
            mContext = c;
            mAllImagesPathsProvider = new AllImagesPathsProvider(mContext);
            mImageCount = mAllImagesPathsProvider.getCount();
        }

        public int getCount() {
            return mImageCount;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        //@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageViewWithPath imageView;

            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageViewWithPath(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(PICTURE_SCALE, PICTURE_SCALE));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING);
            } else {
                imageView = (ImageViewWithPath) convertView;
            }
            if (mAllImagesPathsProvider.nextOrClose()) {
                String picturePath = mAllImagesPathsProvider.getPicturePath();
                imageView.fillView(picturePath);
//                imageView.setPath(picturePath);
//                imageView.setImageBitmap(pictureObject);
            }
            return imageView;
        }
    }

    public class AllImagesPathsProvider {
        Context mContext;
        Cursor mCursor;
        String[] mImagesList;
        String[] mFileColumn;
        int mImgCount;
        Boolean mClosed;

        public AllImagesPathsProvider(Context c){
            mContext = c;

            ContentResolver resolver = getContentResolver();
            mImagesList = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            mCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mImagesList, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            mFileColumn = new String[]{MediaStore.Images.Media.DATA};
            mImgCount = mCursor.getCount();
            mCursor.moveToFirst();
            if (mCursor.getCount() > 0 ){
                mClosed = false;
            }
            else{
                mClosed = true;
            }
        }

        public int getCount(){
            return mImgCount;
        }

        public Boolean nextOrClose(){
            if(mClosed) {
                return false;
            }
            else{
                if(mCursor.isLast()) {
                    close();
                    return false;
                }else{
                    mCursor.moveToNext();
                    return true;
                }
            }
        }

        public void close(){
            mCursor.close();
            mClosed = true;
        }

        public String getPicturePath(){
            return  mCursor.getString(mCursor.getColumnIndex(mFileColumn[0]));
        }

    }

    public class ImageViewWithPath extends ImageView {
        String mPath;
        int mAlpha;
        Boolean mSelected;
        Drawable[] mLayers = new Drawable[2];

        public ImageViewWithPath(Context context) {
            super(context);
            mPath = "";
            mSelected = false;
        }

        public ImageViewWithPath(Context context, String path) {
            super(context);
            mPath = path;
            mSelected = false;
        }

        public void setPath(String path){
            mPath = path;
        }

        public void select(){
            mSelected = true;
            mAlpha = 255;
            redrawView();
        }

       public void deselect(){
           mSelected = false;
           mAlpha = 0;
           redrawView();
        }

        public String getPath(){
            return mPath;
        }

        public void redrawView(){
            if(mSelected){
                mLayers[1].setAlpha(255);
            }else{
                mLayers[1].setAlpha(0);
            }
            setImageDrawable(new LayerDrawable(mLayers));
        }

        public void fillView(String path){
            mLayers[1] =  getResources().getDrawable(R.drawable.picture_cross);
            mAlpha = 0;
            setPath(path);
            mLayers[0] = new BitmapDrawable(mPath);
            redrawView();
        }
    }
}