package edu.upc.eetac.dsa.jjerez.beeter.api;

/**
 * Created by root on 09/04/15.
 */
public class AppException extends Exception {
    public AppException() {
        super();
    }

    public AppException(String detailMessage) {
        super(detailMessage);
    }
}