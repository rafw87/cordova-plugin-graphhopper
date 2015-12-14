package org.rafw87.cordova.graphhopper;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.BBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.util.shapes.GHPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphhopperFactory {
    public GHRequest createRequestFromJSON(JSONObject request) throws JSONException {
        GHRequest ghRequest = new GHRequest();
        JSONArray points = request.getJSONArray("points");
        for (int i = 0; i < points.length(); i++) {
            JSONObject point = points.getJSONObject(i);
            GHPoint ghPoint = new GHPoint();
            ghPoint.lat = point.getDouble("lat");
            ghPoint.lon = point.getDouble("lng");
            ghRequest.addPoint(ghPoint);
        }
        ghRequest.setVehicle(request.getString("vehicle"));
        return ghRequest;
    }

    public JSONObject createJsonFromResponse(GHResponse ghResponse, Translation translation) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("hints", createJsonHintsFromResponse(ghResponse.getHints()));
        response.put("paths", createJsonPathsFromResponse(ghResponse, translation));
        return response;
    }

    private JSONObject createJsonHintsFromResponse(PMap ghHints) throws JSONException {
        JSONObject hints = new JSONObject();
        for (Map.Entry<String, String> ghHint : ghHints.toMap().entrySet())
        {
            hints.put(ghHint.getKey(), ghHint.getValue());
        }
        return hints;
    }

    private JSONArray createJsonPathsFromResponse(GHResponse ghResponse, Translation translation) throws JSONException {
        JSONObject path = new JSONObject();
        path.put("instructions", createJsonInstructions(ghResponse.getInstructions(), translation));
        path.put("descend", ghResponse.getDescend());
        path.put("ascend", ghResponse.getAscend());
        path.put("distance", ghResponse.getDistance());
        path.put("bbox", createJsonBBox(ghResponse.calcRouteBBox(new BBox(0, 0, 0, 0))));
        path.put("weight", ghResponse.getRouteWeight());
        path.put("time", ghResponse.getTime());
        path.put("points_encoded", false);
        path.put("points", createJsonPoints(ghResponse.getPoints()));
        return new JSONArray(new Object[] { path });
    }

    private JSONArray createJsonInstructions(InstructionList ghInstructions, Translation translation) throws JSONException {
        JSONArray instructions = new JSONArray();
        for (int i = 0; i < ghInstructions.getSize(); i++) {
            instructions.put(createJsonInstruction(ghInstructions.get(i), translation));
        }
        return instructions;
    }

    private JSONObject createJsonInstruction(Instruction ghInstruction, Translation translation) throws JSONException {
        JSONObject instruction = new JSONObject();
        instruction.put("distance", ghInstruction.getDistance());
        instruction.put("sign", ghInstruction.getSign());
        //instruction.put("interval", ...);
        instruction.put("text", ghInstruction.getTurnDescription(translation));
        instruction.put("time", ghInstruction.getTime());

        return instruction;
    }

    private JSONArray createJsonBBox(BBox ghBBox) {
        return new JSONArray(ghBBox.toGeoJson());
    }

    private JSONObject createJsonPoints(PointList ghPoints) throws JSONException {
        JSONObject points = new JSONObject();
        points.put("coordinates", new JSONArray(ghPoints.toGeoJson(true)));
        points.put("type", "LineString");
        return points;
    }
}
