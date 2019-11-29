package nl.onrequest.snow2liftie;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.reconinstruments.ui.carousel.CarouselItem;
import com.reconinstruments.ui.carousel.StandardCarouselItem;
import com.reconinstruments.ui.dialog.CarouselDialog;
import com.reconinstruments.ui.dialog.DialogBuilder;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.StandardListItem;

import java.util.Arrays;

public class SettingsActivity extends SimpleListActivity {

    private int RefreshRateSelection;
    private int RangeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RefreshRateSelection = Helper.getInstance().getRefreshRate();
        RangeSelection = Helper.getInstance().getRange();
        setContents(
                new ListItem("Refresh rate", new OnClickCallback() {
                    public void onClick(ListItem item) {
                        CreateRefreshRateDialog(item);
                    }
                }, ((StandardCarouselItem) Helper.getInstance().refreshrates[RefreshRateSelection]).getTitle()),
                new ListItem("Show resorts", new OnClickCallback() {
                    public void onClick(ListItem item) {
                        CreateRangeDialog(item);
                    }
                }, ((StandardCarouselItem) Helper.getInstance().ranges[RangeSelection]).getTitle())
        );
    }

    public class ListItem extends StandardListItem {
        String subtitle;
        OnClickCallback callback;
        public ListItem(String text, OnClickCallback callback, String subtitle){
            super(text);
            this.callback = callback;
            this.subtitle = subtitle;
        }
        public void onClick(Context context) {
            callback.onClick(this);
        }
        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            TextView subtitleView = (TextView)getView().findViewById(R.id.subtitle);
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(subtitle);
        }
        public String getSubtitle() {
            return subtitle;
        }
    }
    public interface OnClickCallback {
        void onClick(ListItem item);
    }


    public void CreateRefreshRateDialog(final ListItem listItem) {

        DialogBuilder builder = new DialogBuilder(this).setTitle("Select refreshrate");
        builder.createSelectionDialog(Arrays.asList(Helper.getInstance().refreshrates), RefreshRateSelection, new CarouselDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelected(CarouselDialog dialog, CarouselItem item, int position) {
                listItem.setSubtitle(((StandardCarouselItem) item).getTitle());
                RefreshRateSelection = position;
                Helper.getInstance().setRefreshRate(RefreshRateSelection);
                dialog.dismiss();
            }
        }).show();
    }
    public void CreateRangeDialog(final ListItem listItem) {

        DialogBuilder builder = new DialogBuilder(this).setTitle("Select range");
        builder.createSelectionDialog(Arrays.asList(Helper.getInstance().ranges), RangeSelection, new CarouselDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelected(CarouselDialog dialog, CarouselItem item, int position) {
                listItem.setSubtitle(((StandardCarouselItem) item).getTitle());
                RangeSelection = position;
                Helper.getInstance().setRange(RangeSelection);
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BACK:
            {
                finish();
                break;
            }
        }
        return true;
    }


}
