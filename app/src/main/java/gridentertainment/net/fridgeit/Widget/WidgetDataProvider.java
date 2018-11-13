package gridentertainment.net.fridgeit.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private final Intent intent;
    private ArrayList<InventoryItem> iv = new ArrayList<>();
    private Context context;

    private void initializeData() throws NullPointerException {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentFirebaseUser.getUid();

        try {
            iv.clear();


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database
                    .getReference(userID)
                    .child("items");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                        InventoryItem inventoryItems = dataSnapshot1.getValue(InventoryItem.class);
                        InventoryItem listdata = new InventoryItem();

                        String name = inventoryItems.getName();
                        String quantity = inventoryItems.getQuantity();
                        String address=inventoryItems.getExpiryDate();
                        String price=inventoryItems.getPrice();

                        listdata.setName(name);
                        listdata.setQuantity(quantity);
                        listdata.setExpiryDate(address);
                        listdata.setPrice(price);

                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                        iv.add(listdata);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public WidgetDataProvider(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        initializeData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        //Log.d("TAG", "Total count is " + cl.size());
        return iv.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.fridge_widget_item);
        remoteViews.setTextViewText(R.id.wd_item, iv.get(position).getName());
        remoteViews.setTextViewText(R.id.wd_measure, iv.get(position).getQuantity());
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}