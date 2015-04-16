/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.socialapp.eventmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class SplashFragment extends Fragment {

    private LoginButton loginButton;
    private TextView skipLoginButton;
    private SkipLoginCallback skipLoginCallback;
    private CallbackManager callbackManager;

    public interface SkipLoginCallback {
        void onSkipLoginPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        //loginButton.setReadPermissions(Arrays.asList("user_friends, public_profile, email, user_birthday"));
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Facebook Login successful", Toast.LENGTH_SHORT).show();
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                //TextView welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);
                //welcomeMsg.setText(constructWelcomeMessage(profile));




                // Using Facebook API for getting User details as well as 
                // friends that are using this app.
                JSONArray friendsIDarray = new JSONArray();
                JSONObject user_friend_list;

                GraphRequestBatch batch = new GraphRequestBatch(
                        GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject jsonObject,
                                            GraphResponse response) {
                                        Log.i("Info Msg:", "Response = ." + response.toString());

                                        try {
                                            Log.i("Info Msg:", "Logged in Users FB id = ." + jsonObject.getString("id"));
                                            Log.i("Info Msg:", "Logged in Users FB name = ." + jsonObject.getString("name"));
                                        }
                                        catch (Exception e)
                                        {
                                        }
/*
                                        if (response) {
                                            // handle the result
                                            Log.i("Info Msg:", "Response = ." + response.toString());


                                            for(int i=0; i<response.getJSONArray().length(); i++)
                                            {

                                                JSONObject data = response.getJSONObject(i);
                                                friendsIDarray.push(data[i].id);
                                            }
                                            user_friend_list = friendsIDarray.join();

                                        }
*/
                                       
                                    }
                                }),
                        // FB API for getting users friends that are using this app.
                        GraphRequest.newMyFriendsRequest(
                                accessToken,
                                new GraphRequest.GraphJSONArrayCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONArray jsonArray,
                                            GraphResponse response) {

                                        try {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                Log.i("Info Msg:", "id = ." + jsonArray.getJSONObject(i).getString("id"));
                                                Log.i("Info Msg:", "name = ." + jsonArray.getJSONObject(i).getString("name"));

                                            }

                                            Log.i("Info Msg:", "Response = ." + response.toString());
                                        }
                                        catch (Exception e)
                                        {

                                        }

                                    }
                                })
                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                    }
                });
                batch.executeAsync();

                if (null != profile)
                {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    //editor.putString("id", profile.getId());
                    editor.putString("fbFirstName", profile.getFirstName());
                    editor.putString("fbLastName", profile.getLastName());
                    editor.commit();
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Facebook Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), "Facebook Login error", Toast.LENGTH_SHORT).show();
            }
        });

        skipLoginButton = (TextView) view.findViewById(R.id.skip_login_button);
        skipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skipLoginCallback != null) {
                    skipLoginCallback.onSkipLoginPressed();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setSkipLoginCallback(SkipLoginCallback callback) {
        skipLoginCallback = callback;
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Facebook Login successful. Welcome " + profile.getName());
        }
        return stringBuffer.toString();
    }
}

