package com.socialapp.eventmanager;

import net.callumtaylor.asynchttp.AsyncHttpClient;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.*;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.socialapp.eventmanager.Models.User;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.JsonResponseHandler;
import org.apache.http.Header;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.socialapp.eventmanager.Models.Event;

/**
 * API Connection class.
 *
 * Note all methods are static-- this class requires no state information to function.
 *
 * Note: calling class is retained if its a destroyed activity or fragment.
 */
public class Backend {
    private static final String TAG = "Sujith";
    //private static final String SERVER_URL = "http://manflowyoga.herokuapp.com/";
    private static final String SERVER_URL = "https://powerful-retreat-9824.herokuapp.com/";


    //Callback interface: how calling objects receive responses asynchronously without delegation
    public interface BackendCallback {
        public void onRequestCompleted(String message);
        public void onRequestFailed(String message);
    }


    public interface CreateEventCallback {
        public void onRequestCompleted(String message);
        public void onRequestFailed(String message);
    }



    public static void signUp(String email, String password, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        //headers.add(new BasicHeader("email", email));
        //headers.add(new BasicHeader("password", password));

        //client.post("users/sign_up", jsonParams, headers, new JsonResponseHandler() {

        String string_to_get = "users/sign_up.json?email="+email+"&password="+password;
        Log.d(TAG,"String sent = "+string_to_get);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email",email));
        params.add(new BasicNameValuePair("password",password));

        client.get("users/sign_up.json", params, headers, new JsonResponseHandler() {
            //client.post("users/sign_up.json", jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {

                JsonObject result = getContent().getAsJsonObject();
                String server_msg = result.get("msg").toString();
                Log.d(TAG, "Server Signup success, message = " + server_msg);
                Log.d(TAG, "Received Success from Server");

                /*
                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");

                Log.d(TAG, "Login returned: " + result);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                User user = gson.fromJson(result, User.class);
                */

                callback.onRequestCompleted("SignUp Success... Now you can Login!");

            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }


    private static String handleFailure(JsonElement response) {
        String errorMessage = "Did not get any response from server";

        if (response == null)
            return errorMessage;



        JsonObject result = response.getAsJsonObject();

        //Server will return all error messages (except in the case of a crash) as a single level JSON
        //with one key called "message". This is a convention for this server.
        try {
            errorMessage = result.get("message").toString();
        }
        catch (Exception e) {
            Log.d(TAG, "Unable to parse server error message");
        }

        return errorMessage;
    }

    public static void respondToInvite(String email, String eventId, String response, final BackendCallback callback)
    {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email",email));
        params.add(new BasicNameValuePair("eventId", eventId));
        params.add(new BasicNameValuePair("status", response));

        client.get("events/respond.json", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();
                callback.onRequestCompleted(result.toString());
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }



    public static void logIn(String email, String password, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email",email));
        params.add(new BasicNameValuePair("password",password));

        client.get("users/sign_in.json", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


               // result.addProperty("backendId", result.get("id").toString());
                //result.remove("id");

                Log.d(TAG, "Login returned: " + result.get("msg").toString());
               // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
              //  User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted("Login Success");
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }




    public static void createEvent(Event event, final CreateEventCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            //json.put("email", email);
            //json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name",event.name));
        params.add(new BasicNameValuePair("description",event.description));
        params.add(new BasicNameValuePair("location",event.location));
        params.add(new BasicNameValuePair("email",event.owner));
        params.add(new BasicNameValuePair("startTime", "" + event.start_time));
        params.add(new BasicNameValuePair("endTime", "" + event.end_time));


        client.get("events/create", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


                // result.addProperty("backendId", result.get("id").toString());
                //result.remove("id");

                Log.d(TAG, "Login returned: " + result.get("msg").toString());
                // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                //  User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted(result.toString());
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }


    public static void editEvent(Event event, final CreateEventCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            //json.put("email", email);
            //json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name",event.name));
        params.add(new BasicNameValuePair("description",event.description));
        params.add(new BasicNameValuePair("location",event.location));
        params.add(new BasicNameValuePair("email",event.owner));
        params.add(new BasicNameValuePair("startTime", "" + event.start_time));
        params.add(new BasicNameValuePair("endTime", "" + event.end_time));
        params.add(new BasicNameValuePair("eventId", "" + event.event_id));


        client.get("events/edit", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


                // result.addProperty("backendId", result.get("id").toString());
                //result.remove("id");

                Log.d(TAG, "Edit event returned: " + result.get("msg").toString());
                // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                //  User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted(result.toString());
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }


    public static void deleteEvent(Event event, final CreateEventCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            //json.put("email", email);
            //json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email",event.owner));
        params.add(new BasicNameValuePair("eventId", "" + event.event_id));


        client.get("events/delete", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


                // result.addProperty("backendId", result.get("id").toString());
                //result.remove("id");

                Log.d(TAG, "Delete event returned: " + result.get("msg").toString());
                // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                //  User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted(result.toString());
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }












    public static void sendRegistrationIdToBackend(String email, String regId, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            //json.put("email", email);
            //json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("regid", regId));


        client.get("users/gcm_register", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();
                callback.onRequestCompleted("GCM registration id sent succesfully");
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

    public static void saveImage(Event event, final BackendCallback callback)
    {
        //Header[] headers = new Header[2];
        //headers[0] = new BasicHeader("Content-Type", "")

        RequestParams params = new RequestParams();
        params.put("email",event.owner);
        params.put("eventId", event.event_id);
        params.put("image", event.image_url);

        try {
            params.put("image", new File(event.image_url));
        }
        catch (Exception e)
        {
            Log.d("Error :", e.toString());
            return;
        }

        com.loopj.android.http.AsyncHttpClient client = new com.loopj.android.http.AsyncHttpClient();

        client.post(SERVER_URL + "events/save_image.json", params, new com.loopj.android.http.AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "Image successfully uploaded");
                callback.onRequestCompleted("Image successfully uploaded");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d(TAG, "Error in uploading image");
            }
        });
    }


    public static void getEventFromServer(String eventId, String email, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;


        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("eventId", eventId));


        client.get("events/info", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                String result = getContent().toString();
                callback.onRequestCompleted(result);
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }



    public static void InviteFriends(Event event, String friends_to_invite, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email",event.owner));
        params.add(new BasicNameValuePair("eventId",event.event_id));
        params.add(new BasicNameValuePair("list",friends_to_invite));

        client.get("/events/invite", params, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


                // result.addProperty("backendId", result.get("id").toString());
                //result.remove("id");

                Log.d(TAG, "Login returned: " + result.get("msg").toString());
                // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                //  User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted("Invited Friends");
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }








    /**
     * Load the poses from the backend. Requires a user with a valid auth token-- if the token has expired
     * you must call login again, passing credentials again. In other words, if Date.now > user.tokenExpration,
     * the server will reject the request.
     *
     * @param user The user requesting the resources
     */
/*
    public static void loadPoses(User user, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("X-USER-ID", Integer.toString(user.backendId)));
        headers.add(new BasicHeader("X-AUTHENTICATION-TOKEN", user.authToken));

        client.get("poses", null, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                JsonArray result = getContent().getAsJsonArray();

                //Sugar and GSON don't play nice, need to ensure the ID property is mapped correctly
                for (JsonElement element: result) {
                    JsonObject casted = element.getAsJsonObject();
                    casted.addProperty("backendId", casted.get("id").toString());
                    casted.remove("id");
                }

                Log.d(TAG, "Load returned: " + result);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                Pose[] poses = gson.fromJson(result, Pose[].class);

                callback.onRequestCompleted(new ArrayList<>(Arrays.asList(poses)));
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

*/


/*
    public static void loadRoutines(User user, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("X-USER-ID", Integer.toString(user.backendId)));
        headers.add(new BasicHeader("X-AUTHENTICATION-TOKEN", user.authToken));

        client.get("routines", null, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                JsonArray result = getContent().getAsJsonArray();

                //Sugar and GSON don't play nice, need to ensure the ID property is mapped correctly
                for (JsonElement element: result) {
                    JsonObject casted = element.getAsJsonObject();
                    casted.addProperty("backendId", casted.get("id").toString());
                    casted.remove("id");
                }

                Log.d(TAG, "Load returned: " + result);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                Routine[] routines = gson.fromJson(result, Routine[].class);

                callback.onRequestCompleted(new ArrayList<>(Arrays.asList(routines)));
            }

            @Override
            public void onFailure() {
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

*/
    /* Convenience methods */
    /**
     * Convenience method for parsing server error responses, since most of the handling is similar.
     * @param response the raw response from a server failure.
     * @return a string with an appropriate error message.
     */






}
