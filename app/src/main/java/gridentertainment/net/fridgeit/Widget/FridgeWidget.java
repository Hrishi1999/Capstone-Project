package gridentertainment.net.fridgeit.Widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;
import gridentertainment.net.fridgeit.UI.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FridgeWidget extends AppWidgetProvider {

    private ArrayList<InventoryItem> iv = new ArrayList<>();

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fridge_widget);
        CharSequence title = context.getString(R.string.widget_title);
        views.setTextViewText(R.id.widget_title, title);
        setRemoteAdapter(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);

         /*Intent intentUpdate = new Intent(context, WidgetService.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_title, pendingUpdate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        ComponentName thisWidget = new ComponentName( context, WidgetService.class);
        AppWidgetManager.getInstance( context ).updateAppWidget(thisWidget, views);*/

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        getData(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "This is called", Toast.LENGTH_SHORT).show();
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.putParcelableArrayListExtra("list", iv);
        views.setRemoteAdapter(R.id.listViewWidget, intent);
    }

    private int getData(final Context context)
    {
        try {

            iv.clear();
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userID = currentFirebaseUser.getUid();
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
        return iv.size();
    }
}

