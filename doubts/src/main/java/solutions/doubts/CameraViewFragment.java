package solutions.doubts;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

public class CameraViewFragment extends CameraFragment {

    private final String TAG = getClass().getSimpleName();
    private Button _toggleFlashButton;
    private String _flashMode;
    private PreviewCameraHost _host;
    private OnPictureTakenListener _onPictureTakenListener;
    boolean _toToggleFlashMode;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        _host = new PreviewCameraHost(getActivity());
        _host.setOnPictureTakenListener(_onPictureTakenListener);
        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(_host);
        setHost(builder.useFullBleedPreview(true).build());

        if (state == null) {
            _flashMode = Camera.Parameters.FLASH_MODE_ON;
            _toToggleFlashMode = false;
        } else {
            String flashMode = state.getString("flashMode");
            if (flashMode != null) {
                // hack around ;)
                if (flashMode == Camera.Parameters.FLASH_MODE_ON) {
                    _flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                } else if (flashMode == Camera.Parameters.FLASH_MODE_OFF) {
                    _flashMode = Camera.Parameters.FLASH_MODE_ON;
                } else if (flashMode == Camera.Parameters.FLASH_MODE_AUTO) {
                    _flashMode = Camera.Parameters.FLASH_MODE_OFF;
                }
                _toToggleFlashMode = true;
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState)  {
        super.onSaveInstanceState(outState);
        outState.putString("flashMode", _flashMode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_new_doubt_camera_view, container, false);
        FrameLayout cameraView = (FrameLayout)view.findViewById(R.id.cameraView);
        View cameraPreviewFragment = super.onCreateView(inflater, container, savedInstanceState);
        cameraView.addView(cameraPreviewFragment);
        Button shutterButton = (Button)view.findViewById(R.id.shutter);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click an image
                PictureTransaction transaction = new PictureTransaction(_host);
                transaction.flashMode(_flashMode);
                transaction.needBitmap(true);
                takePicture(transaction);
            }
        });
        _toggleFlashButton = (Button)view.findViewById(R.id.toggleFlashButton);
        _toggleFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlashMode();
            }
        });
        if (_toToggleFlashMode) {
            toggleFlashMode();
            _toToggleFlashMode = false;
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setOnPictureTakenListener(OnPictureTakenListener listener) {
        _onPictureTakenListener = listener;
    }

    void toggleFlashMode() {
        if (_flashMode == Camera.Parameters.FLASH_MODE_ON) {
            // user switches off the flash
            _flashMode = Camera.Parameters.FLASH_MODE_OFF;
            _toggleFlashButton.setBackgroundResource(R.drawable.flash_auto);
        } else if (_flashMode == Camera.Parameters.FLASH_MODE_OFF) {
            _flashMode = Camera.Parameters.FLASH_MODE_AUTO;
            _toggleFlashButton.setBackgroundResource(R.drawable.flash_on);
        } else if (_flashMode == Camera.Parameters.FLASH_MODE_AUTO) {
            // user switches on the flash
            _flashMode = Camera.Parameters.FLASH_MODE_ON;
            _toggleFlashButton.setBackgroundResource(R.drawable.flash_off);
        }
    }

}

class PreviewCameraHost extends SimpleCameraHost {

    private OnPictureTakenListener _onPictureTakenListener;

    public PreviewCameraHost(Context context) {
        super(context);
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
        // do nothing here
    }

    @Override
    public void saveImage(PictureTransaction xact, Bitmap bitmap) {
        _onPictureTakenListener.pictureTaken(bitmap);
    }

    public void setOnPictureTakenListener(OnPictureTakenListener listener) {
        _onPictureTakenListener = listener;
    }
}
