package be.ehb.koalaexpress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JSONhelper {
    private static ObjectMapper objMapper = getDefaultObjectMapper();

    public static ObjectMapper getDefaultObjectMapper()
    {
        if(objMapper == null) {
            ObjectMapper defmapper = new ObjectMapper();
            defmapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            objMapper = defmapper;
        }
        return objMapper;
    }
    public static JsonNode parseJSON(String jsonstring) throws IOException
    {
        return objMapper.readTree(jsonstring);
    }
    public static <A> A fromJSON(JsonNode node, Class<A>clazz) throws JsonProcessingException {
        return objMapper.treeToValue(node, clazz);
    }
    public static JsonNode toJSON(Object obj)
    {
        return objMapper.valueToTree(obj);
    }
    public static String stringifyJSON(JsonNode node) throws JsonProcessingException {
        ObjectWriter objwr = objMapper.writer();
        return objwr.writeValueAsString(node);
    }
    public static String prettyPrintJSON(JsonNode node) throws JsonProcessingException {
        // print mooiere indentatie voor output debugging
        ObjectWriter objwr = objMapper.writer();
        objwr = objwr.with(SerializationFeature.INDENT_OUTPUT);
        return objwr.writeValueAsString(node);
    }
    public static String DecodeStringComingIn(String content) {
        if(content==null)
            return null;
        String decoded = "";
        try {
            decoded = URLDecoder.decode(content, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }
    public static String EncodeStringGoingOut(String content) {
        if(content==null)
            return null;
        String encoded = "";
        try {
            encoded = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static returnMessage extractmsgFromJSONAnswer(String jsonstring)
    {
        String[] parts = jsonstring.split("\\|");
        returnMessage h = new returnMessage();
        if (parts.length < 2)
        {
            if(jsonstring.equalsIgnoreCase("timeout"))
                h.setSucces(false, jsonstring + ", Server request timed out.");
            else
                h.setSucces(false, jsonstring + " Missing header in response answer from servlet.");
        }
        else {
            ObjectMapper defmapper = new ObjectMapper();
            defmapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            try {
                h.m_header = defmapper.readValue(parts[0], resultHeader.class);
                String content = parts[1];
                h.m_Content = URLDecoder.decode(content, StandardCharsets.UTF_8.toString());
            } catch (JsonProcessingException e) {
                h.setSucces(false, e.getMessage());
            } catch (UnsupportedEncodingException e) {
                h.setSucces(false, e.getMessage());
            } catch (Exception e) {
            }
        }
        return h;
    }

}
