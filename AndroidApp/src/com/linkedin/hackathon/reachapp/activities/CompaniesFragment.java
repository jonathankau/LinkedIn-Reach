package com.linkedin.hackathon.reachapp.activities;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.linkedin.hackathon.reachapp.R;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class CompaniesFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_companies,
				container, false);
		
		final ListView companies = (ListView) view.findViewById(R.id.companies);
		//companies.setDivider(null);
		
		

		// Query companies based on event ID
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
		query.getInBackground("TVf5FxCPUJ", new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, com.parse.ParseException e) {
				if (e == null) {
					Log.d("JKAU", object.get("companyNames").toString());
					CompaniesAdapter adapter = new CompaniesAdapter(CompaniesFragment.this.getActivity(), "hXZ1N8geTK", (List<String>) object.get("companyNames"));
					companies.setAdapter(adapter);
					
				} else {
					// something went wrong
				}

			}
		});

		return view;
	}

}
