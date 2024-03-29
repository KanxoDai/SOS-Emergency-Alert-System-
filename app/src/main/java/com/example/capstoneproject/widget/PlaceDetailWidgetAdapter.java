package com.example.capstoneproject.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.capstoneproject.R;
import com.example.capstoneproject.data.PlaceDetailContract;
import com.example.capstoneproject.data.PlaceDetailContract.PlaceDetailEntry;
import com.example.capstoneproject.model.Place;

import java.util.ArrayList;

/**
 * Created by Daniel Ng`andu on 28/01/2020
 */

public class PlaceDetailWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<Place> mFavouritePlaceArrayList = new ArrayList<>();

    //Constructor
    public PlaceDetailWidgetAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        mFavouritePlaceArrayList = getFavouritePlaceListData();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mFavouritePlaceArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_widget);
        remoteViews.setTextViewText(R.id.place_name, mFavouritePlaceArrayList.get(position).getPlaceName());
        remoteViews.setTextViewText(R.id.place_open_status, mFavouritePlaceArrayList.get(position)
                .getPlaceOpeningHourStatus());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private ArrayList<Place> getFavouritePlaceListData() {

        //Uri for requesting data from Database
        Uri placeContentUri = PlaceDetailContract.PlaceDetailEntry.CONTENT_URI;

        //Cursor object to get resultSet from Database

        Cursor placeDetailDataCursor = mContext.getContentResolver().query(
                placeContentUri, null, null, null, null);

        if (placeDetailDataCursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Place singlePlaceDetail = new Place(
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_ID)),
                        placeDetailDataCursor.getDouble(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_LATITUDE)),
                        placeDetailDataCursor.getDouble(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_LONGITUDE)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_NAME)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_OPENING_HOUR_STATUS)),
                        placeDetailDataCursor.getDouble(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_RATING)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_ADDRESS)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_PHONE_NUMBER)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_WEBSITE)),
                        placeDetailDataCursor.getString(placeDetailDataCursor
                                .getColumnIndex(PlaceDetailEntry.COLUMN_PLACE_SHARE_LINK)));
                mFavouritePlaceArrayList.add(singlePlaceDetail);
            } while (placeDetailDataCursor.moveToNext());
        }
        placeDetailDataCursor.close();
        return mFavouritePlaceArrayList;
    }
}
