package gridentertainment.net.fridgeit.Widget;

import android.content.Intent;

import android.widget.RemoteViewsService;
import android.widget.Toast;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}