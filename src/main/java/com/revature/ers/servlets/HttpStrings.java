package com.revature.ers.servlets;

public class HttpStrings {

    public HttpStrings(){

    }


    public String fourOFour(String url){
        return "<html lang=‘en’> <head> <title>HTTP Status 404 – Not Found</title> <style type=‘text/css’> body { font-family: Tahoma, Arial, sans-serif; } h1, h2, h3, b { color: white; background-color: #525D76; } h1 { font-size: 22px; } h2 { font-size: 16px; } h3 { font-size: 14px; } p { font-size: 12px; } a { color: black; } .line { height: 1px; background-color: #525D76; border: none; } </style> </head> <body> <h1>HTTP Status 404 – Not Found</h1> <hr class='line' /> <p><b>Type</b> Status Report</p> <p><b>Message</b> The requested resource " + url + " is not available</p> <p><b>Description</b> The origin server did not find a current representation for the target resource or is not willing to disclose that one exists.</p> <hr class='line' /> <h3>Apache Tomcat/10.0.21</h3> </body> </html>";
    }

    public String fourOThree(String role){
        return "<html lang=‘en’> <head> <title>HTTP Status 403 – Forbidden</title> <style type=‘text/css’> body { font-family: Tahoma, Arial, sans-serif; } h1, h2, h3, b { color: white; background-color: #525D76; } h1 { font-size: 22px; } h2 { font-size: 16px; } h3 { font-size: 14px; } p { font-size: 12px; } a { color: black; } .line { height: 1px; background-color: #525D76; border: none; } </style> </head> <body> <h1>HTTP Status 403 – Forbidden</h1> <hr class='line' /> <p><b>Type</b> Status Report</p> <p><b>Message</b> " + role + " is forbidden to view this page</p> <hr class='line' /> <h3>Apache Tomcat/10.0.21</h3> </body> </html>";
    }


    public String httpStr(int statusCode, String msg, String desc){
        return "<html lang=‘en’> <head> <title>HTTP Status "+ statusCode +" – "+ msg+"</title> <style type=‘text/css’> body { font-family: Tahoma, Arial, sans-serif; } h1, h2, h3, b { color: white; background-color: #525D76; } h1 { font-size: 22px; } h2 { font-size: 16px; } h3 { font-size: 14px; } p { font-size: 12px; } a { color: black; } .line { height: 1px; background-color: #525D76; border: none; } </style> </head> <body> <h1>HTTP Status "+statusCode+" – "+ msg+"</h1> <hr class='line' /> <p><b>Type</b> Status Report</p> <p><b>Message</b> " + msg + " </p> <p><b>Description</b>" + desc + "</p> <hr class='line' /> <h3>Apache Tomcat/10.0.21</h3> </body> </html>";
    }
}
