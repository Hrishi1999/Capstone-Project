package gridentertainment.net.fridgeit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;
import gridentertainment.net.fridgeit.UI.EditViewActivity;

public class InvAdapter extends RecyclerView.Adapter<InvAdapter.InvHolder>{

    private List<InventoryItem> listdata;
    private Context context;

    public InvAdapter(List<InventoryItem> listdata, Context context)
    {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public InvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item2,parent,false);
        return new InvHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void onBindViewHolder(@NonNull InvHolder holder, int position) {

        final InventoryItem data = listdata.get(position);
        final String quantity = data.getQuantity();
        final String price = data.getPrice();
        final String name = data.getName();
        final String date = data.getExpiryDate();
        Date date1 = null;
        Date date2 = null;
        String  currentDate = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            date2 = new SimpleDateFormat("dd-MM-yyyy").parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long days = getDifferenceDays(date2, date1);
        Long posDays = Math.abs(days);
        String inDays = posDays.toString();

        if(days != 0)
        {
            if(days <= 10)
            {
                holder.date.setTextColor(context.getColor(R.color.ada_red));
                holder.date.setText(context.getString(R.string.ada_expiring) + inDays
                        + context.getString(R.string.ada_date));
            }
            if(days <= 30 && days >= 11)
            {
                holder.date.setTextColor(context.getColor(R.color.ada_yellow));
                holder.date.setText(context.getString(R.string.ada_expiring) + inDays
                        + context.getString(R.string.ada_date));            }
            if(days >= 30)
            {
                holder.date.setTextColor(context.getColor(R.color.ada_green));
                holder.date.setText(context.getString(R.string.ada_expiring) + inDays
                        + context.getString(R.string.ada_date));            }
            if(days<0)
            {
                holder.date.setText(context.getString(R.string.ada_expiring) + inDays
                        + context.getString(R.string.ada_date));
            }
        }
        else
        {
            holder.date.setVisibility(View.GONE);
        }

        holder.name.setText(name);
        holder.quantity.setText(context.getString(R.string.ada_quantity) + quantity);
        holder.price.setText(context.getString(R.string.ada_price) + price);

        if(name.isEmpty())
        {
            holder.name.setVisibility(View.GONE);
        }
        if(quantity.isEmpty())
        {
            holder.quantity.setVisibility(View.GONE);
        }if(date.isEmpty())
        {
            holder.date.setVisibility(View.GONE);
        }if(price.isEmpty())
        {
            holder.price.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditViewActivity.class);
                InventoryItem inventoryItem = new InventoryItem();
                inventoryItem.setPrice(price);
                inventoryItem.setExpiryDate(date);
                inventoryItem.setName(name);
                inventoryItem.setQuantity(quantity);
                i.putExtra(context.getString(R.string.KEY_MODEL), inventoryItem);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    class InvHolder extends RecyclerView.ViewHolder{
        TextView name, quantity, date, price;

        InvHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.inv_name);
            quantity = itemView.findViewById(R.id.inv_quantity);
            date = itemView.findViewById(R.id.inv_date);
            price = itemView.findViewById(R.id.inv_price);
        }
    }

    private static long getDifferenceDays(Date d1, Date d2) {
        long ret;
        if(d1 != null && d2 != null)
        {
            long diff = d2.getTime() - d1.getTime();
            ret = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }
        else
        {
            ret = 0;
        }
        return ret;
    }
}