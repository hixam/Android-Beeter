package edu.upc.eetac.dsa.jjerez.beeter.api;

/**
 * Created by root on 09/04/15.
 */
import java.util.HashMap;
import java.util.Map;

public class BeeterRootAPI {

    private Map<String, Link> links;

    public BeeterRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}
