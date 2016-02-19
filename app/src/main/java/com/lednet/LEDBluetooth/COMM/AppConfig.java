package com.lednet.LEDBluetooth.COMM;

public class AppConfig {

    public static boolean getCheckIsRFStarDevice(String localName) {

        if (localName.startsWith("LEDBlue")
                || localName.startsWith("LEDBLE")) {
            return false;
        } else {
            return true;
        }
    }

}