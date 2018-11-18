package gridentertainment.net.fridgeit.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import gridentertainment.net.fridgeit.BuildConfig;
import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.Utils.HTTPHandler;
import gridentertainment.net.fridgeit.R;

public class AddItemActivity extends AppCompatActivity {

    private EditText name;
    private EditText quantity;
    private EditText date;
    private EditText price;
    private static int RESULT_LOAD_IMAGE = 1;
    private String bc;
    private ProgressDialog pDialog;

    private final static String KEY_NAME = "name";
    private final static String KEY_quantity = "quantity";
    private final static String KEY_PRICE = "price";
    private final static String KEY_DATE = "date";
    private final static String KEY_ITEM = "items";
    private final static String KEY_DATA = "data";
    private final static String KEY_PRODUCT = "productname";
    private final static String ACCESS_KEY = BuildConfig.ACCESS_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.add_item_txt);

        String userID = currentFirebaseUser.getUid();

        final FirebaseDatabase database;
        DatabaseReference databaseReference;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(userID);

        name = findViewById(R.id.ed_name);
        quantity = findViewById(R.id.ed_quantity);
        date = findViewById(R.id.ed_date);
        price = findViewById(R.id.ed_price);

        FloatingActionButton fab1 = findViewById(R.id.flt_add);
        final DatabaseReference finalDatabaseReference = databaseReference;

        if (savedInstanceState != null)
        {
            name.setText(savedInstanceState.getString(KEY_NAME));
            quantity.setText(savedInstanceState.getString(KEY_quantity));
            price.setText(savedInstanceState.getString(KEY_PRICE));
            date.setText(savedInstanceState.getString(KEY_DATE));
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inv_name = name.getText().toString();
                String inv_quantity = quantity.getText().toString();
                String inv_date = date.getText().toString();
                String inv_price = price.getText().toString();

                final String key = finalDatabaseReference.push().getKey();
                final InventoryItem inventoryItem = new InventoryItem();

                inventoryItem.setName(inv_name);
                inventoryItem.setExpiryDate(inv_date);
                inventoryItem.setQuantity(inv_quantity);
                inventoryItem.setPrice(inv_price);

                Query query = finalDatabaseReference.child(KEY_ITEM)
                        .orderByChild(KEY_NAME)
                        .equalTo(inv_name);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                Toast.makeText(AddItemActivity.this, getString(R.string.itm_exists), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                finalDatabaseReference.child(KEY_ITEM)
                                        .child(key)
                                        .setValue(inventoryItem);
                                finish();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Button btn_barcode = findViewById(R.id.btn_bcd);
        btn_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkForPermissions()) {
                        ActivityCompat.requestPermissions(AddItemActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                    Intent[] intentArray = {cameraIntent};
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooser, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(this, getString(R.string.txt_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == FragmentActivity.RESULT_OK) {
            if (data.getData() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    processBarcode(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap bitmap = null;
                Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED,
                        null, "date_added DESC");
                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    String photoPath = uri.toString();
                    cursor.close();
                    if (photoPath != null) {
                        bitmap = BitmapFactory.decodeFile(photoPath);
                        processBarcode(bitmap);
                    }
                }

                if (bitmap == null) {

                    bitmap = (Bitmap) data.getExtras().get(KEY_DATA);
                    processBarcode(bitmap);
                }

            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void processBarcode(Bitmap bitmap)
    {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        if(!barcodeDetector.isOperational()){
            Toast.makeText(this, getString(R.string.err_bcd), Toast.LENGTH_SHORT).show();
            return;
        }

        Frame myFrame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);

        if (barcodes.size() != 0) {
            Barcode thisCode = barcodes.valueAt(0);
            bc = thisCode.rawValue.replaceAll("\\D+","");
            //Toast.makeText(this, bc, Toast.LENGTH_SHORT).show();
            new barcodeInfoTask().execute();
        }
        else
        {
            Toast.makeText(this, getString(R.string.err_nobcd), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_NAME, name.getText().toString());
        savedInstanceState.putString(KEY_quantity, quantity.getText().toString());
        savedInstanceState.putString(KEY_DATE, date.getText().toString());
        savedInstanceState.putString(KEY_PRICE, price.getText().toString());
    }

    @SuppressLint("StaticFieldLeak")
    private class barcodeInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddItemActivity.this);
            pDialog.setMessage(getString(R.string.dialog_msg));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPHandler sh = new HTTPHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.searchupc.com")
                    .appendPath("handlers")
                    .appendPath("upcsearch.ashx")
                    .appendQueryParameter("request_type", "3")
                    .appendQueryParameter("access_token", ACCESS_KEY)
                    .appendQueryParameter("upc", bc);

            String jsonStr = sh.makeServiceCall(builder.build().toString());

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject query = jsonObj.getJSONObject("0");
                    final String bc_name = query.getString(KEY_PRODUCT);
                    final String bc_price = query.getString(KEY_PRICE);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            name.setText(bc_name);
                            price.setText(bc_price);
                        }
                    });

                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    private boolean checkForPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
