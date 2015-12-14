package org.rafw87.cordova.graphhopper;

import android.content.res.Resources;
import android.util.Log;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Translation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GraphhopperProxy {
    private GraphHopper _graphhopper;
    private GraphhopperFactory _factory = new GraphhopperFactory();

    public interface GetRoutesSuccessCallback {
        void execute(JSONObject routes);
    }

    public interface GetRoutesErrorCallback {
        void execute(JSONObject err);
    }

    public GraphhopperProxy(JSONObject settings) throws Exception {
        Log.i(GraphhopperPlugin.TAG, "Initializing plugin.");
        Log.i(GraphhopperPlugin.TAG, "Settings: \n" + settings);
        String mapFilePath = settings.getString("mapFilePath");
        _graphhopper = new GraphHopper().forMobile();
        File dir = new File(mapFilePath);
        if(!dir.exists()) {
            throw new Resources.NotFoundException(mapFilePath);
        }
        _graphhopper.load(dir.getAbsolutePath());
        Log.i(GraphhopperPlugin.TAG, "found graph " + _graphhopper.getGraphHopperStorage().toString() + ", nodes:" + _graphhopper.getGraphHopperStorage().getNodes());
        Log.i(GraphhopperPlugin.TAG, "Plugin initialized.");
    }

    public void destroy() {
        _graphhopper.clean();
        _graphhopper.close();
        _graphhopper = null;
        Log.i(GraphhopperPlugin.TAG, "Plugin destroyed.");
    }

    public void setMapFilePath(String mapFilePath) {
        _graphhopper.setOSMFile(mapFilePath);
        Log.i(GraphhopperPlugin.TAG, "Map File Path: '" + mapFilePath + "'.");
    }

    public void setApiUrl(String apiUrl) {
        Log.i(GraphhopperPlugin.TAG, "API URL: '" + apiUrl + "' (unused).");
    }

    public void setApiKey(String apiKey) {
        Log.i(GraphhopperPlugin.TAG, "API Key: '" + apiKey + "' (unused).");
    }

    public void getRoutes(JSONObject request, GetRoutesSuccessCallback success, GetRoutesErrorCallback error) throws JSONException {
        Log.i(GraphhopperPlugin.TAG, "Generating routes.");
        Log.i(GraphhopperPlugin.TAG, "Request: \n" + request);

        Translation translation = _graphhopper.getTranslationMap().getWithFallBack(new Locale("en", "US"));
        GHRequest ghRequest = _factory.createRequestFromJSON(request);
        GHResponse ghResponse = _graphhopper.route(ghRequest);
        if(ghResponse.hasErrors()) {
            error.execute(createJsonFromErrors(ghResponse.getErrors()));
        } else {
            success.execute(_factory.createJsonFromResponse(ghResponse, translation));
        }
    }



    public JSONObject createJsonFromErrorSafe(Throwable error) {
        try {
            List<Throwable> errors = new LinkedList<Throwable>();
            errors.add(error);
            return createJsonFromErrors(errors);
        } catch (JSONException ex) {
            return new JSONObject();
        }
    }

    public JSONObject createJsonFromError(Throwable error) throws JSONException {
        List<Throwable> errors = new LinkedList<Throwable>();
        errors.add(error);
        return createJsonFromErrors(errors);
    }

    public JSONObject createJsonFromErrors(List<Throwable> errors) throws JSONException {
        JSONObject result = new JSONObject();
        if (errors.size() == 1) {
            result.put("message", errors.get(0).getMessage());
        } else {
            result.put("message", "Multiple errors occured.");
        }
        JSONArray errorsJson = new JSONArray();
        for (Throwable error : errors) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("message", error.getMessage());
            errorJson.put("localizedMessage", error.getLocalizedMessage());
            errorJson.put("cause", error.getCause());
            errorJson.put("stackTrace", error.getStackTrace());
            errorsJson.put(errorJson);
        }
        result.put("errors", errorsJson);
        return result;
    }
}
