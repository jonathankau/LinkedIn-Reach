package com.linkedin.hackathon.reachapp.activities;

import java.util.EnumSet;
import java.util.Hashtable;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Skill;
import com.linkedin.hackathon.reachapp.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginSplashActivity extends Activity {
	public static final String CONSUMER_KEY = "75neykgf25j1hz";
	public static final String CONSUMER_SECRET = "sbUz7sxgqvAZNxyU";
	public static final String OAUTH_CALLBACK_SCHEME = "x-oauth-linkedin";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(CONSUMER_KEY, CONSUMER_SECRET);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(CONSUMER_KEY, CONSUMER_SECRET);
	LinkedInRequestToken liToken;
	LinkedInApiClient client;
	String token;
	String tokenSecret;
	Person profile;
	Typeface face;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "Xy6al4MOCdX3wGyP5oOPNpCj5Zasv6oYcvNtr0CW", "64tmMzQIfJJODfy6I8aOCOkKsgFfnOlboO7IzKUg");

		setContentView(R.layout.activity_login_splash);

		face = Typeface.createFromAsset(this.getAssets(), "fonts/helvetica.otf");
		((Button) findViewById(R.id.linkedin_login_button)).setTypeface(face);
		((Button) findViewById(R.id.signup_button)).setTypeface(face);
		((Button) findViewById(R.id.login_button)).setTypeface(face);
		((TextView) findViewById(R.id.student)).setTypeface(face);
		((EditText) findViewById(R.id.email)).setTypeface(face);
		((EditText) findViewById(R.id.password)).setTypeface(face);


		if( Build.VERSION.SDK_INT >= 9){
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

	}

	public void onLoginPressed(View v) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		token = preferences.getString("OAUTH-TOKEN", null);
		tokenSecret = preferences.getString("OAUTH-TOKEN-SECRET", null);

		if(token == null || tokenSecret == null) {
			liToken = oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(liToken.getAuthorizationUrl()));
			startActivity(i);
		} else {
			onStartLoginFlow();
		}
	}

	public void onSignUp(View v) {
		// Check if company has company
		//		ParseQuery<ParseObject> query = ParseQuery.getQuery("Company");
		//		query.whereEqualTo("name", profile.getPositions().getPositionList().get(0).getCompany().getName());
		//		query.getInBackground("xWMyZ4YEGZ", new GetCallback<ParseObject>() {
		//
		//			@Override
		//			public void done(ParseObject po, com.parse.ParseException e) {
		//				// TODO Auto-generated method stub
		//				if(e == null) {
		//					if(po.get)
		//				}
		//			}
		//		});

		final ParseObject data = new ParseObject("LinkedInData");
		data.put("firstName", profile.getFirstName());
		data.put("lastName", profile.getLastName());
		data.put("email", ((TextView) findViewById(R.id.email)).getText().toString());
		data.put("pictureUrl", profile.getPictureUrl());
		if(profile.getSummary() == null)
			profile.setSummary("");
		data.put("summary", profile.getSummary());
		data.put("industry", profile.getIndustry());
		data.put("headline", profile.getHeadline());
		data.put("companyName", profile.getPositions().getPositionList().get(0).getCompany().getName());
		convertSkills(data, profile.getSkills().getSkillList());

		data.saveInBackground(new SaveCallback() {
			@Override
			public void done(com.parse.ParseException e) {
				if (e == null) {
					// Store data in parse database for new user
					ParseUser user = new ParseUser();
					user.setUsername(((TextView) findViewById(R.id.email)).getText().toString());
					user.setPassword(((TextView) findViewById(R.id.password)).getText().toString());
					user.setEmail(((TextView) findViewById(R.id.email)).getText().toString());
					user.put("type", 2);
					user.put("companyName", profile.getPositions().getPositionList().get(0).getCompany().getName());
					user.put("token", token);
					user.put("linkedInData", data);

					user.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(com.parse.ParseException e) {
							if (e == null) {
								// Hooray! Let them use the app now.
								Log.d("JKAU", "success");

								Toast.makeText(LoginSplashActivity.this, "Sign up successful!", 0).show();

								Intent intent = new Intent(LoginSplashActivity.this, MainActivity.class);
								startActivity(intent);
							} else {
								// Sign up didn't succeed. Look at the ParseException
								// to figure out what went wrong
								Log.d("JKAU", e.getLocalizedMessage());

								Toast.makeText(LoginSplashActivity.this, "Invalid sign up request", 0).show();
							}


						}
					});
				} else {

				}
			}
		});

	}

	public void onLogIn(View v) {
		ParseUser user = new ParseUser();
		try {
			user.logIn(((TextView) findViewById(R.id.email)).getText().toString(), ((TextView) findViewById(R.id.password)).getText().toString());
			Intent intent = new Intent(LoginSplashActivity.this, MainActivity.class);
			startActivity(intent);
		} catch (com.parse.ParseException e) {
			Toast.makeText(this, "Invalid login credentials", 0).show();
		}


	}

	private void convertSkills(ParseObject data, List<Skill> skills) {
		for(Skill s: skills) {
			data.add("skills", s.getSkill().getName());
		}
	}

	public void onStartLoginFlow() {
		LinkedInAccessToken accessToken = new LinkedInAccessToken(token, tokenSecret);
		client = factory.createLinkedInApiClient(accessToken);
		profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.FIRST_NAME, ProfileField.LAST_NAME, 
				ProfileField.HEADLINE, ProfileField.PICTURE_URL, ProfileField.SUMMARY, ProfileField.INDUSTRY, ProfileField.SKILLS, ProfileField.ID, ProfileField.POSITIONS, ProfileField.EMAIL_ADDRESS));

		EditText emailView = (EditText) findViewById(R.id.email);
		emailView.setText(profile.getEmailAddress());

		emailView.setVisibility(View.VISIBLE);
		findViewById(R.id.signup_button).setVisibility(View.VISIBLE);
		findViewById(R.id.login_box).setVisibility(View.VISIBLE);
		findViewById(R.id.password).setVisibility(View.VISIBLE);
		findViewById(R.id.login_button).setVisibility(View.VISIBLE);
		findViewById(R.id.linkedin_login_button).setVisibility(View.GONE);
		Log.d("JKAU", profile.getPositions().getPositionList().get(0).getCompany().getName());
		Log.d("JKAU", profile.getSkills().getSkillList().get(0).getSkill().getName());
		Log.d("JKAU", profile.getPictureUrl().toString());
		Log.d("JKAU", profile.getHeadline().toString());
		Log.d("JKAU", profile.getHeadline().toString());
	}

	public void onNewIntent(Intent intent) {
		String verifier = intent.getData().getQueryParameter("oauth_verifier");

		LinkedInAccessToken accessToken = oAuthService.getOAuthAccessToken(liToken, verifier);
		client = factory.createLinkedInApiClient(accessToken);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("OAUTH-TOKEN", accessToken.getToken());
		editor.putString("OAUTH-TOKEN-SECRET", accessToken.getTokenSecret());
		editor.apply();

		onStartLoginFlow();
	}
}
