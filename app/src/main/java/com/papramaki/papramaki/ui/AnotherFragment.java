package com.papramaki.papramaki.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papramaki.papramaki.R;

public class AnotherFragment extends Fragment {

    private static final String TAG = AnotherFragment.class.getSimpleName();

    protected TextView mTextView;

    public AnotherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_another, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mTextView.setText("Papramaki rocks!!!!!");

        return rootView;
    }

}
