package gridentertainment.net.fridgeit.Widget;

import android.content.Intent;

import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}