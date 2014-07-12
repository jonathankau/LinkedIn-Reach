package com.linkedin.hackathon.reachapp.activities;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkedin.hackathon.reachapp.R;
import com.parse.Parse;
import com.parse.ParseObject;

public class CompaniesAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	Context context;
	List<String> data;
	String studentId;
	Typeface face;
	
	public CompaniesAdapter(Context context, String studentId, List<String> list) {
		Parse.initialize(context, "Xy6al4MOCdX3wGyP5oOPNpCj5Zasv6oYcvNtr0CW", "64tmMzQIfJJODfy6I8aOCOkKsgFfnOlboO7IzKUg");
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = list;
		this.studentId = studentId;
		
		face = Typeface.createFromAsset(context.getAssets(), "fonts/helvetica.otf");
	}


	@Override
	public boolean isEnabled(int position) {
		return false; // false to disable click
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.company_list_item, parent, false);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if(arg0.findViewById(R.id.checked_in) != null && arg0.findViewById(R.id.checked_in).getVisibility() == View.GONE) {
						arg0.findViewById(R.id.checked_in).setVisibility(View.VISIBLE);
						
						// Send check in request to Parse
						ParseObject checkIn = new ParseObject("CheckIn");
						checkIn.put("studentId", studentId);
						checkIn.put("hasBeenReviewed", false);
						checkIn.put("companyName", ((TextView) arg0.findViewById(R.id.company_name)).getText().toString());
						checkIn.put("eventId", "TVf5FxCPUJ");
						
						checkIn.saveInBackground();
					}
					
				}
				
			});
			
			
			holder = new ViewHolder();
			
			// Grab views
			holder.company = (TextView) view.findViewById(R.id.company_name);
			holder.booth = (TextView) view.findViewById(R.id.booth_number);
			
			((TextView) view.findViewById(R.id.B)).setTypeface(face);
			holder.company.setTypeface(face);
			holder.booth.setTypeface(face);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.color_box);
		if(position % 2 == 0) {
			layout.setBackgroundColor(Color.parseColor("#24B0E2"));
		} else {
			layout.setBackgroundColor(Color.TRANSPARENT);
		}
		
		
		holder.company.setText(getItem(position));
		holder.booth.setText(Integer.toString(position + 10));
	
		return view;
	}

	static class ViewHolder {
		TextView company;
		TextView booth;
	}

}
