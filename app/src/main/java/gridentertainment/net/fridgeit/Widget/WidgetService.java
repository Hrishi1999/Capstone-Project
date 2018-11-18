package gridentertainment.net.fridgeit.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.widget.RemoteViewsService;

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

public class WidgetService extends RemoteViewsService {

    private ArrayList<InventoryItem> iv = new ArrayList<>();
    private final static String KEY_ITEM = "items";

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

        final WidgetDataProvider[] widgetDataProvider = new WidgetDataProvider[1];

        try {

            iv.clear();
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userID = currentFirebaseUser.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database
                    .getReference(userID)
                    .child(KEY_ITEM);

            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            final int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), FridgeWidget.class));

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    iv.clear();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

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

                        iv.add(listdata);
                    }
                    appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.listViewWidget);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        intent.putParcelableArrayListExtra(getString(R.string.KEY_LIST), iv);
        widgetDataProvider[0] = new WidgetDataProvider(getApplicationContext(), intent);
        return widgetDataProvider[0];
    }
}