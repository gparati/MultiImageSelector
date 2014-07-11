package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class ImageSelector extends Activity {

    final int PICTURE_SCALE = 256;
    final int PICTURE_PADDING = 1;

    ArrayList<ImageCell> _imagesCellList;
    GridView _gridView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //TODO BUTTON im xml mit leiste im code ersetzen
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int number_of_grid_columns = (width) / (PICTURE_SCALE + PICTURE_PADDING);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout _content = new LinearLayout(this);
        _content.setId(R.id.userinput_imageselector);
        _content.setBackgroundColor(0xFFFFFFFF);
        _content.setOrientation(LinearLayout.VERTICAL);
        _content.setLayoutParams(lp);
        setContentView(_content);

        _gridView = new GridView(this);
        //_gridView.setPadding(1,100,1,1);
        _gridView.setId(R.id.gridview_select_images_id);
        _gridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _gridView.setStretchMode(GridView.STRETCH_SPACING);
        _gridView.setNumColumns(number_of_grid_columns);
        _gridView.setColumnWidth(PICTURE_SCALE);


        if(savedInstanceState != null && savedInstanceState.containsKey("IMAGES_LIST_PARCELABLE")){
            _imagesCellList = getImagesList(savedInstanceState);
            //_gridView.invalidateViews();
        }else{
            _imagesCellList = getImagesList();
        }

        ImageAdapter imageAdapter = new ImageAdapter(this, _imagesCellList);
        _gridView.setAdapter(imageAdapter);

        Button buttonSelect = new Button(this);
        buttonSelect.setText("Select");
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                processSelectedImages();
            }
        });

        _content.addView(buttonSelect);
        _content.addView(_gridView);

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (_imagesCellList.get(position).isSelected()) {
                    _imagesCellList.get(position).deselect();
                    _gridView.invalidateViews();
                } else {
                    _imagesCellList.get(position).select();
                    _gridView.invalidateViews();
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

    @Override
    protected void onPause() {
        super.onPause();
        //_imagesCellList.clear();
        _gridView.invalidateViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //_imagesCellList.clear();
        _gridView.invalidateViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<ImageParcelable> oldImages = new ArrayList<ImageParcelable>();
        Intent rIntent = new Intent(this, MainActivity.class);

        for (int i=0; i < _imagesCellList.size(); i++) { //TODO geht sicher besser ???
            oldImages.add(getImageParcelable(_imagesCellList.get(i).getPostion(), _imagesCellList.get(i).getPath(),_imagesCellList.get(i).isSelected()));
        }
        outState.putParcelableArrayList("IMAGES_LIST_PARCELABLE", oldImages);
    }

    public void processSelectedImages() {
        ArrayList<ImagePathParcelable> selectedImages = new ArrayList<ImagePathParcelable>();
        Intent rIntent = new Intent(this, MainActivity.class);

        for (int i=0; i < _imagesCellList.size(); i++) {
            if (_imagesCellList.get(i).isSelected())
            selectedImages.add(getImagePathParcelable(_imagesCellList.get(i).getPath()));
        }

        rIntent.putParcelableArrayListExtra("SELECTED_IMAGES_LIST", selectedImages);
        setResult(RESULT_OK, rIntent);
        finish();
    }

    public ImageParcelable getImageParcelable(int position, String imagePath, Boolean selected){
        return new ImageParcelable(position, imagePath, selected);
    }

    public ImagePathParcelable getImagePathParcelable(String imagePath){
        return new ImagePathParcelable(imagePath);
    }

    public ArrayList<ImageCell> getImagesList() {
        ArrayList<ImageCell> imagesCellList = new ArrayList<ImageCell>();

        ContentResolver resolver = getContentResolver();
        String[] mediaStoreImages = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor c = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaStoreImages, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        String[] fileColumn = new String[]{MediaStore.Images.Media.DATA};

        c.moveToFirst();
        do {
            imagesCellList.add(new ImageCell(c.getPosition(), c.getString(c.getColumnIndex(fileColumn[0])), false));
            c.moveToNext();
        } while (!c.isLast());
        c.close();

        return imagesCellList;
    };

    public ArrayList<ImageCell> getImagesList(Bundle savedInstanceState) {
        ArrayList<ImageCell> imagesCellList = new ArrayList<ImageCell>();
        ArrayList<ImageParcelable> imagesParcelableList = new ArrayList<ImageParcelable>();

        imagesParcelableList = (ArrayList<ImageParcelable>) savedInstanceState.get("IMAGES_LIST_PARCELABLE");

        for(int i=0; i < imagesParcelableList.size(); i++){
            imagesCellList.add(new ImageCell(imagesParcelableList.get(i).get_position(), imagesParcelableList.get(i).get_imagePath(), imagesParcelableList.get(i).get_selected()));
        }
        return imagesCellList;
    };

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
            RelativeLayout doubleImage;
            ImageView overlay, bitmap;

            if (convertView == null) {
                doubleImage = new RelativeLayout(mContext);
                doubleImage.setLayoutParams(new ListView.LayoutParams(PICTURE_SCALE, PICTURE_SCALE));
                doubleImage.setPadding(PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING);

                overlay = new ImageView(mContext);
                overlay.setImageResource(R.drawable.picture_cross_border);
                overlay.setId(R.id.overlay_id);

                bitmap = new ImageView(mContext);
                bitmap.setImageBitmap(decodeSampledBitmapFromPath(mImageCellList.get(position).getPath(), PICTURE_SCALE, PICTURE_SCALE));
                bitmap.setScaleType(ImageView.ScaleType.CENTER_CROP);

                doubleImage.addView(bitmap);
                doubleImage.addView(overlay);

            }else{
                doubleImage = (RelativeLayout) convertView;
            }
            if(mImageCellList.get(position).isSelected()){
                doubleImage.getChildAt(1).setVisibility(View.VISIBLE);
            }else{
                doubleImage.getChildAt(1).setVisibility(View.INVISIBLE);
            }
            return doubleImage;
        }

        private Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
                // Raw height and width of image
                final int height = options.outHeight;
                final int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                }
                return inSampleSize;
            }
        }

    public class ImageCell{
        int mPosition;
        String mImagePath;
        Boolean mSelected;

        public ImageCell(ImageCell imageCell){
            mPosition = imageCell.getPostion();
            mImagePath = imageCell.getPath();
            mSelected = imageCell.isSelected();
        }

        public ImageCell(int position, String path, Boolean selected){
            mPosition = position;
            mImagePath = path;
            mSelected = selected;
        }

        public int getPostion(){
            return mPosition;
        }

        public String getPath(){
            return mImagePath;
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
}