package org.rafw87.cordova.graphhopper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class GraphhopperPlugin extends CordovaPlugin {
    /**
     * Plugin's tag for log purposes
     */
    public static final String TAG = "graphhopper-cordova";
    private static GraphhopperProxy _proxy = null;



    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        if ("test".equals(action)) {
            callbackContext.success(args);
            return true;
        }
        Boolean handled = false;
        try {
            if ("initialize".equals(action)) {
                handled = true;
                JSONObject settings = args.getJSONObject(0);
                _proxy = new GraphhopperProxy(settings);
                callbackContext.success();
            }
            if ("destroy".equals(action)) {
                handled = true;
                _proxy.destroy();
                _proxy = null;
                callbackContext.success();
            }
            if ("set-map-file-path".equals(action)) {
                handled = true;
                String mapFilePath = args.getString(0);
                _proxy.setMapFilePath(mapFilePath);
                callbackContext.success();
            }
            if ("set-api-url".equals(action)) {
                handled = true;
                String apiUrl = args.getString(0);
                _proxy.setApiUrl(apiUrl);
                callbackContext.success();
            }
            if ("set-api-key".equals(action)) {
                handled = true;
                String apiKey = args.getString(0);
                _proxy.setApiKey(apiKey);
                callbackContext.success();
            }
            if ("get-routes".equals(action)) {
                handled = true;
                JSONObject request = args.getJSONObject(0);
                _proxy.getRoutes(request, new GraphhopperProxy.GetRoutesSuccessCallback() {
                    @Override
                    public void execute(JSONObject routes) {
                        callbackContext.success(routes);
                    }
                }, new GraphhopperProxy.GetRoutesErrorCallback() {
                    @Override
                    public void execute(JSONObject err) {

                        callbackContext.error(err);
                    }
                });
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(_proxy.createJsonFromErrorSafe(e));
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(_proxy.createJsonFromErrorSafe(e));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(_proxy.createJsonFromErrorSafe(e));
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(_proxy.createJsonFromErrorSafe(e));
        }
        return handled;
    }
}