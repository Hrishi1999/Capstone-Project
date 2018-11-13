package gridentertainment.net.fridgeit.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import gridentertainment.net.fridgeit.Adapter.InvAdapter;
import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<InventoryItem> inventoryItemList;
    RecyclerView recyclerView;
    boolean doubleBackToExitPressedOnce = false;
    InvAdapter recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentFirebaseUser.getUid();

        database = FirebaseDatabase.getInstance();
        if(savedInstanceState==null)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        databaseReference = database.getReference(userID).child("items");
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Get Data");
        nDialog.show();

        recyclerView = findViewById(R.id.recyclerViewMain);

        FloatingActionButton addItem = findViewById(R.id.btn_add);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddItemActivity.class));
            }
        });
        //I don't think it is necessary to use onSavedInstance here,
        //Firebase handles persistence. Is it fine?
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inventoryItemList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    InventoryItem inventoryItem = dataSnapshot1.getValue(InventoryItem.class);
                    InventoryItem listdata = new InventoryItem();
                    String name = inventoryItem.getName();
                    String quantity = inventoryItem.getQuantity();
                    String address=inventoryItem.getExpiryDate();
                    String price=inventoryItem.getPrice();
                    listdata.setName(name);
                    listdata.setQuantity(quantity);
                    listdata.setExpiryDate(address);
                    listdata.setPrice(price);
                    inventoryItemList.add(listdata);
                }

                recycler = new InvAdapter(inventoryItemList);
                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(layoutmanager);
                recyclerView.setItemAnimator( new DefaultItemAnimator());
                recyclerView.setAdapter(recycler);
                nDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,  ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //awesome code when user grabs recycler card to reorder
                return true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //awesome code to run when user drops card and completes reorder
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    final String name = inventoryItemList.get(viewHolder.getAdapterPosition()).getName();
                    Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                    Query query = databaseReference
                            .orderByChild("name")
                            .equalTo(name);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            final java.util.Timer timer = new Timer();
                            Snackbar snackbar = Snackbar
                                    .make(recyclerView, "Remove Item?", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            recycler.notifyDataSetChanged();
                                        }
                                    })
                                    .setCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);
                                                if (event != DISMISS_EVENT_ACTION) {
                                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                                        appleSnapshot.getRef().removeValue();
                                                    }
                                            }
                                        }
                                    });
                            snackbar.show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if (direction == ItemTouchHelper.LEFT) {
                    Intent i = new Intent(MainActivity.this, EditViewActivity.class);
                    InventoryItem inventoryItem = new InventoryItem();
                    int pos = viewHolder.getAdapterPosition();
                    inventoryItem.setPrice(inventoryItemList.get(pos).getPrice());
                    inventoryItem.setExpiryDate(inventoryItemList.get(pos).getExpiryDate());
                    inventoryItem.setName(inventoryItemList.get(pos).getName());
                    inventoryItem.setQuantity(inventoryItemList.get(pos).getQuantity());
                    i.putExtra("model", inventoryItem);
                    startActivity(i);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
