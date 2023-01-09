package com.reconinstruments.camerasample;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.reconinstruments.ui.carousel.CarouselActivity;
import com.reconinstruments.ui.carousel.CarouselItem;
import com.reconinstruments.ui.carousel.StandardCarouselItem;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends CarouselActivity {

    private static final String TAG = "CameraActivity";

    public enum CAM_MODE {
        PHOTO,
        VIDEO
    }

    Camera camera;

    CameraPreview preview;
    TextView recordingTimeView;
    FrameLayout modeSwitchView;

    VideoRecorder activeVideo;

    SimpleDateFormat recordTimeFormatter = new SimpleDateFormat("mm:ss", Locale.getDefault());

    @Override
    public int getLayoutId() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preview = (CameraPreview) findViewById(R.id.preview);
        recordingTimeView = (TextView) findViewById(R.id.recording_time);
        modeSwitchView = (FrameLayout) findViewById(R.id.mode_switcher);
    }


    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override
    protected List<? extends CarouselItem> createContents() {
        return Arrays.asList(
                new StandardCarouselItem(R.drawable.photo_icon),
                new StandardCarouselItem(R.drawable.video_icon));
    }

    public CAM_MODE getCamMode() {
        if(getCarousel().getCurrentItem()==0)
            return CAM_MODE.PHOTO;
        else
            return CAM_MODE.VIDEO;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER) {
            switch (getCamMode()) {
                case PHOTO:
                    camera.takePicture(null, null, jpegSavedCallback);
                    break;
                case VIDEO:
                    if(!isRecording())
                        startRecording();
                    else
                        stopRecording();
                    break;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    PictureCallback jpegSavedCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            StorageUtils.insertJpeg(CameraActivity.this,data,System.currentTimeMillis());
            preview.setCamera(camera);
        }
    };

    private void startRecording() {
        activeVideo = new VideoRecorder(this,camera);

        recordingTimeView.setVisibility(View.VISIBLE);
        modeSwitchView.setVisibility(View.GONE);

        final Handler recordHandler = new Handler();
        final Runnable recordUpdater = new Runnable() {
            int recordTime = VideoRecorder.MAX_DURATION;
            @Override
            public void run() {
                recordTime--;
                String timeLeftString = recordTimeFormatter.format(new Date(recordTime * 1000));
                recordingTimeView.setText(timeLeftString);

                if(recordTime>0&&isRecording())
                    recordHandler.postDelayed(this,1000);
                else if(isRecording())
                    stopRecording();
            }
        };
        recordHandler.postDelayed(recordUpdater, 0);
    }

    public void stopRecording() {
        activeVideo.stopRecording();
        activeVideo = null;

        recordingTimeView.setVisibility(View.GONE);
        modeSwitchView.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Video captured!", Toast.LENGTH_LONG).show();
    }

    public boolean isRecording() {
        return activeVideo!=null;
    }

    public void openCamera() {
        try {
            camera = Camera.open();
        } catch(RuntimeException ex) {
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
        if(camera!=null)
            preview.setCamera(camera);
    }

    public void closeCamera() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}