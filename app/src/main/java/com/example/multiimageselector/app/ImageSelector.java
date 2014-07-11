package com.example.multiimageselector.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ImageSelector extends Activity {

    final int PICTURE_SCALE = 200;
    final int PICTURE_PADDING = 1;
    int picture_padding_sides;

    ArrayList<ImageCell> _imagesCellList;
    HashMap _selectedImages = new HashMap();
    GridView _gridView;
    ImageAdapter _imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        _content.setLayoutParams(lp);
        setContentView(_content);

        _gridView = new GridView(this);
        _gridView.setId(R.id.gridview_select_images_id);
        _gridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)); //TODO
        ViewGroup.LayoutParams gVlP = new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _gridView.setStretchMode(GridView.STRETCH_SPACING);
        _gridView.setNumColumns(number_of_grid_columns);
        _gridView.setColumnWidth(PICTURE_SCALE);

        _imagesCellList = getImagesList();
        ImageAdapter imageAdapter = new ImageAdapter(this, _imagesCellList);
        _gridView.setAdapter(imageAdapter);

        Button buttonSelect = new Button(this);// findViewById(R.id.button_select_images_id);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                processSelectedImages();
            }
        });

        addContentView(_gridView, gVlP);

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (_selectedImages.containsKey(position)) {
                    _selectedImages.remove(position);
                    _imagesCellList.get(position).deselect();
                    _gridView.setAdapter(_imageAdapter); //TODO eventually not the way to go
                } else {
                    _selectedImages.put(position, _imagesCellList.get(position));
                    _imagesCellList.get(position).select();
                    _gridView.setAdapter(_imageAdapter); //TODO eventually not the way to go
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

    public void processSelectedImages() {
        Intent rIntent = new Intent(this, MainActivity.class);
        ArrayList<ImagePathParcelable> pathParcelables = new ArrayList<ImagePathParcelable>();
        Iterator it = _selectedImages.entrySet().iterator();
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
            LinearLayout doubleImage;
            ImageView overlay, bitmap;

            if (convertView == null) {
                doubleImage = new LinearLayout(mContext);
                doubleImage.setLayoutParams(new ListView.LayoutParams(PICTURE_SCALE, PICTURE_SCALE));
                doubleImage.setPadding(PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING, PICTURE_PADDING);

                overlay = new ImageView(mContext);
                overlay.setImageResource(R.drawable.picture_cross_border);
                overlay.setId(R.id.overlay_id);
                //overlay.setVisibility(View.INVISIBLE);

                bitmap = new ImageView(mContext);
                bitmap.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bitmap.setImageBitmap(BitmapFactory.decodeFile(mImageCellList.get(position).getPath()));

                doubleImage.addView(bitmap);
                doubleImage.addView(overlay);

            }else{
                doubleImage = (LinearLayout) convertView;
            }
            if(_imagesCellList.get(position).isSelected()){
                doubleImage.getChildAt(1).setVisibility(View.VISIBLE);
            }else{
                doubleImage.getChildAt(1).setVisibility(View.INVISIBLE);
            }
            return doubleImage;
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
}