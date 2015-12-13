package idlycyme.practice.apps.twitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.activeandroid.query.Select;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

import idlycyme.practice.apps.twitter.R;
import idlycyme.practice.apps.twitter.libraries.TwitterClient;
import idlycyme.practice.apps.twitter.models.User;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
    private User loggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        List<User> loggedInUsers = new Select().from(User.class).where("isLoggedIn = ?", true).execute();
        if (loggedInUsers != null && loggedInUsers.size() > 0) {
            loggedInUser = loggedInUsers.get(0);
            //Log.i("--------------------", loggedInUser.getScreenname().toString());
            goToHomePage();
            return;
        }

        // if loggedInUser is null, reterive it first
        getClient().getLoggedInCredential(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loggedInUser = User.fromJSON(response);
                loggedInUser.setIsLoogedIn(true);
                loggedInUser.save();

                goToHomePage();
            }
        });
	}

    private void goToHomePage() {
        Intent i = new Intent(getBaseContext(), TimelineActivity.class);
        Bundle params = new Bundle();
        params.putSerializable("loggedInUser", loggedInUser);
        i.putExtra("loggedInUser", loggedInUser);
        startActivity(i);
    }

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
