package solutions.doubts;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class NewDoubtActivity extends ActionBarActivity implements OnPictureTakenListener {

    private CameraViewFragment _cameraViewFragment;
    private Bitmap _bitmap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_doubt);
        if (savedInstanceState == null) {
            _cameraViewFragment = new CameraViewFragment();
            _cameraViewFragment.setOnPictureTakenListener(this);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, _cameraViewFragment)
                    .addToBackStack("cameraView")
                    .commit();

            getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    PicturePreviewFragment fragment = (PicturePreviewFragment)getFragmentManager().findFragmentByTag("picturePreviewFragment");
                    if (fragment != null)
                        fragment.setPicture(_bitmap);
                }
            });
        }

        /*Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MaterialEditText title = (MaterialEditText)findViewById(R.id.title);
        title.setHorizontallyScrolling(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        findViewById(R.id.overlayCameraView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (_cameraViewFragment.isAdded()) {
                    getFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .remove(_overlayCameraViewFragment)
                            .show(_cameraViewFragment)
                            .commit();
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .remove(_overlayCameraViewFragment)
                            .replace(R.id.container, _cameraViewFragment)
                            .commit();
                }
                _cameraViewVisible = true;
                MaterialEditText text = (MaterialEditText)findViewById(R.id.title);
                text.setEnabled(false);
            }
        }); */
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    /*@Override
    public void onBackPressed() {
        if (_cameraViewVisible) {
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(_cameraViewFragment)
                    .remove(_cameraViewFragment)
                    .replace(R.id.overlayCameraView, _overlayCameraViewFragment)
                    .commit();
            _cameraViewVisible = false;
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newdoubt_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void pictureTaken(Bitmap bitmap) {
        _bitmap = bitmap;
        if (getFragmentManager().findFragmentByTag("picturePreviewFragment") == null) {
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.container, new PicturePreviewFragment(),
                            "picturePreviewFragment")
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.container, getFragmentManager().findFragmentByTag("picturePreviewFragment"),
                            "picturePreviewFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

}
