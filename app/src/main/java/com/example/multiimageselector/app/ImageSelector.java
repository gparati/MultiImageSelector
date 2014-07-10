package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
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

    ArrayList<ImageCell> mImagesCellList;
    //ArrayList<String> mImagesList;
    HashMap mSelectedImages = new HashMap();
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        mImagesCellList = getImagesList();
        //mImagesList = getImagesList();

        final ImageAdapter imageAdapter = new ImageAdapter(this, mImagesCellList);

        mGridView = (GridView) findViewById(R.id.gridView1);

        mGridView.setNumColumns(NUMBER_OF_GRID_COLUMNS);
        mGridView.setPadding(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING);
        mGridView.setHorizontalSpacing(GRID_SPACING);
        mGridView.setVerticalSpacing(GRID_SPACING);

        mGridView.setAdapter(imageAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mSelectedImages.containsKey(position)) {
                    deselectImage(position); //TODO eventuell ohne extra funktion ????
                } else {
                    selectImage(position); //TODO eventuell ohne extra funktion ????
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
            HashMap.Entry entry = (HashMap.Entry) it.next();
            pathParcelables.add(new ImagePathParcelable(entry.getValue().toString()));
            it.remove();
        }
        rIntent.putParcelableArrayListExtra("SELECTED_IMAGES_LIST", pathParcelables);
        setResult(RESULT_OK, rIntent);
        finish();
    }

    public ArrayList<ImageCell> getImagesList() {
        ArrayList<ImageCell> imagesCellList = new ArrayList<ImageCell>();

        ContentResolver resolver = getContentResolver();
        String[] mediaStoreImages = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor c = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaStoreImages, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        String[] fileColumn = new String[]{MediaStore.Images.Media.DATA};

        c.moveToFirst();
        do {
            imagesCellList.add(new ImageCell(c.getPosition(), c.getString(c.getColumnIndex(fileColumn[0]))));
            c.moveToNext();
        } while (!c.isLast());
        c.close();

        return imagesCellList;
    };

    public void selectImage(int position) {
        mSelectedImages.put(position, mImagesCellList.get(position));
        mImagesCellList.get(position).select();
    }

    public void deselectImage(int position) {
        mSelectedImages.remove(position);
        mImagesCellList.get(position).deselect();
    }

    ////////////////////
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ImageCell> mImageCellList;

        public ImageAdapter(Context context, ArrayList<ImageCell> imagesCellList) {
            mContext = context;
            mImageCellList = imagesCellList;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getCount() {
            return mImageCellList.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //XViewGroup viewGroup = new XViewGroup(mContext);

            ImageView overlayView = new ImageView(mContext);
            overlayView.setImageResource(R.drawable.picture_cross_border);

            ImageView imageView;
            if (convertView == null) {

                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(PICTURE_SCALE, PICTURE_SCALE));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(BitmapFactory.decodeFile(mImageCellList.get(position).getPath()));

            //parent.addView(overlayView);
            //parent.addView(imageView);

            return imageView;
        }
    }

    public class ImageCell{
        int mPosition;
        String mImagePath;
        Boolean mSelected;

        public ImageCell(int position, String path){
            mPosition = position;
            mImagePath = path;
            mSelected = false;
        }

        public String getPath(){
            return mImagePath;
        }

        public int getPosition(){
            return mPosition;
        }

        public Boolean isSelected(){
            return mSelected;
        }

        public void select(){
            mSelected = true;
        }

        public void deselect(){
            mSelected = false;
        }
    }
//
//    public class XViewGroup extends ViewGroup{
//        Boolean selected;
//
//        public XViewGroup (Context context){
//            super(context);
//            selected = false;
//        }
//
//        @Override
//        protected void onLayout(boolean changed, int l, int t, int r, int b) {
//            //TODO WUT???
//        }
//    }
}