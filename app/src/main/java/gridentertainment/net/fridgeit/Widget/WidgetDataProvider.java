package gridentertainment.net.fridgeit.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import gridentertainment.net.fridgeit.Models.InventoryItem;
import gridentertainment.net.fridgeit.R;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private final Intent intent;
    private List<InventoryItem> iv = new ArrayList<>();
    private Context context;

    private void initializeData() throws NullPointerException {
        iv = intent.getParcelableArrayListExtra(context.getString(R.string.KEY_LIST));
    }

    public WidgetDataProvider(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDataSetChanged() {
        initializeData();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return iv.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position >= getCount()){
            return getLoadingView();
        }
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