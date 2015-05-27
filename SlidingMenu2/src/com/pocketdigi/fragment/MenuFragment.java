package com.pocketdigi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.pocketdigi.slidingmenudemo.MainActivity;
import com.pocketdigi.slidingmenudemo.R;

public class MenuFragment extends Fragment {
	Button btn_fragmentA,btn_fragmentB;
    LinearLayout linearLayout;
	MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, null);

        mainActivity = (MainActivity)getActivity();

        linearLayout = (LinearLayout) view;
        if (linearLayout != null) {
            btn_fragmentA = (Button) linearLayout.findViewById(R.id.btn_fragmentA);
            btn_fragmentA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.showFragmentA();
                }
            });

            btn_fragmentB = (Button) linearLayout.findViewById(R.id.btn_fragmentB);
            btn_fragmentB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.showFragmentB();
                }
            });
        }

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
