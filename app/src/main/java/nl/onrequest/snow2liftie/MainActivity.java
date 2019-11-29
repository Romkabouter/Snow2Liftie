package nl.onrequest.snow2liftie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.ui.carousel.CarouselActivity;
import com.reconinstruments.ui.carousel.StandardCarouselItem;


public class MainActivity extends CarouselActivity {

    static class ImageCarouselItem extends StandardCarouselItem {
        public ImageCarouselItem(String title, Integer icon) {
            super(title, icon);
        }
        @Override
        public int getLayoutId() {
            return R.layout.carousel_item_title_icon_column;
        }
        @Override
        public void onClick(Context context) {
            if (this.getTitle().equals("Lifts")) {
                context.startActivity(new Intent(context, LiftsActivity.class));
            }
            if (this.getTitle().equals("Settings")) {
                context.startActivity(new Intent(context, SettingsActivity.class));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.getInstance().setActivity(this);
        Helper.getInstance().setSharedPreferences(this.getSharedPreferences("nl.onrequest.snow2liftie", Context.MODE_PRIVATE));
        Helper.getInstance().setHUDConnectivityManager((HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE));
        Helper.getInstance().UpdateResortFile();
        setContentView(R.layout.activity_main);
        getCarousel().setPageMargin(30);
        getCarousel().setContents(
                new ImageCarouselItem("Lifts", R.drawable.carousel_icon_lifts),
                new ImageCarouselItem("Settings", R.drawable.carousel_icon_settings));
        System.load("/system/lib/libreconinstruments_jni.so");
    }
}
