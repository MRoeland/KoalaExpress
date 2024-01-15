package be.ehb.koalaexpress;

public class ConnectionInfo {
    public static String ServerUrl()
    {
        //local
        //Tomcat server op localhost testen, enkel mogelijk indien tomcat in debug draait op zelfde machine
        //return "http://192.168.1.13:8080/KoalaExpressServer";

        // binnen wifi
        //return "http://192.168.1.200:7070/KoalaExpressServer"; // run on NASlocal in netwerk -> docker tomcat 10 port 7070

        // buiten wifi !!
        return "http://91.181.60.26:7070/KoalaExpressServer"; // run on NAS -> docker tomcat 10 port 7070
    }
}
