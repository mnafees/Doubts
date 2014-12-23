package solutions.doubts;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class PicturePreviewFragment extends Fragment {

    private String TAG = getClass().getSimpleName();

    private ImageView _imageView;
    private Bitmap _bitmap;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getByteArray("bitmap") != null) {
                byte[] array = savedInstanceState.getByteArray("bitmap");
                _bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                Log.d(TAG, _bitmap.toString());
                _imageView.setImageBitmap(_bitmap);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)  {
        super.onSaveInstanceState(outState);
        if (_bitmap != null) {
            int bytes = _bitmap.getByteCount();

            ByteBuffer buffer = ByteBuffer.allocate(bytes);
            _bitmap.copyPixelsToBuffer(buffer);

            byte[] array = buffer.array();
            outState.putByteArray("bitmap", array);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_new_doubt_picture_preview, container, false);
        Button okayButton = (Button)view.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getName(), "okay button");
            }
        });
        Button notokayButton = (Button)view.findViewById(R.id.notokayButton);
        notokayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        _imageView = (ImageView)view.findViewById(R.id.imageView);
        return view;
    }

    public void setPicture(Bitmap bitmap) {
        _bitmap = bitmap;
        _imageView.setImageBitmap(_bitmap);
    }

}
