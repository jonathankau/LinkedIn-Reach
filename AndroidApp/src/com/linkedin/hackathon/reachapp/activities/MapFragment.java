package com.linkedin.hackathon.reachapp.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linkedin.hackathon.reachapp.R;

public class MapFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map,
				container, false);
		

		Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/helvetica.otf");
		((TextView) view.findViewById(R.id.checkin)).setTypeface(face);
		
		return view;
	}

}
