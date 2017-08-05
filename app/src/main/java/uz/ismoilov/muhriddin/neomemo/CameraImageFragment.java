package uz.ismoilov.muhriddin.neomemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraImageFragment extends Fragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Button button;
    ImageView imageView;


    public CameraImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_image, container, false);
    }

}
