package gridentertainment.net.fridgeit.UI;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;

import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;

public class EditViewActivity extends AppCompatActivity {

    private InventoryItem inventoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        if(savedInstanceState != null)
        {
            inventoryItem = savedInstanceState.getParcelable("model");
        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            inventoryItem = bundle.getParcelable("model");
        }


        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentFirebaseUser.getUid();

        final InventoryItem inventoryItem1 = new InventoryItem();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference(userID);
        final String key = databaseReference.push().getKey();

        final EditText name = findViewById(R.id.ed_namedit);
        final EditText quantity = findViewById(R.id.ed_quantityedit);
        final EditText date = findViewById(R.id.ed_dateedit);
        final EditText price = findViewById(R.id.ed_pricedit);

        name.setText(inventoryItem.getName());
        quantity.setText(inventoryItem.getQuantity());
        date.setText(inventoryItem.getExpiryDate());
        price.setText(inventoryItem.getPrice());

        FloatingActionButton floatingActionButton = findViewById(R.id.flt_add2);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = databaseReference.child("items")
                        .orderByChild("name")
                        .equalTo(name.getText().toString());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }

                        inventoryItem1.setQuantity(quantity.getText().toString());
                        inventoryItem1.setName(name.getText().toString());
                        inventoryItem1.setExpiryDate(date.getText().toString());
                        inventoryItem1.setPrice(price.getText().toString());

                        /*Map<String, Object> updates = new HashMap<String,Object>();
                        updates.put("name", name.getText().toString());
                        updates.put("quantity", quantity.getText().toString());
                        updates.put("expiryDate", date.getText().toString());
                        updates.put("price", price.getText().toString());

                        databaseReference.child("items").updateChildren(updates);
                        finish();*/
                        databaseReference.child("items")
                                .child(key)
                                .setValue(inventoryItem1);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("model", inventoryItem);
    }
}
