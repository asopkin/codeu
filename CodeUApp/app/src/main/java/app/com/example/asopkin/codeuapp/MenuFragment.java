package app.com.example.asopkin.codeuapp;

/**
 * Created by asopkin on 7/12/2015.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuFragment extends Fragment {

    public MenuFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_version, container,
                false);
        // Get position of argument
        String name = getArguments().getString("name");

        TextView textView = (TextView) rootView.findViewById(R.id.textView);
        textView.setText(name + " is selected");
        getActivity().setTitle(name);
        return rootView;
    }
}
