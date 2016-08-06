package com.pk.mdmc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by PavelK on 6/23/2016.
 */
public class JTest2 {


    public static void main(String[] args) throws IOException {
        Socket echoSocket = new Socket("localhost", 6379);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        //out.write("*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$8\r\nmy value\r\n");

        //HSET EBS.MDS.NY.NODES 1 "{"a":1}"
        out.write("*4\r\n" + "$4\r\nHSET\r\n" + "$16\r\nEBS.MDS.NY.NODES\r\n" + "$3\r\n100\r\n" + "$7\r\n{\"x\":7}\r\n");
        out.flush();
        //System.out.println(in.read());
        System.out.println(in.readLine());

        /*
        * For Simple Strings the first byte of the reply is "+"
            For Errors the first byte of the reply is "-"
            For Integers the first byte of the reply is ":"
            For Bulk Strings the first byte of the reply is "$"
            For Arrays the first byte of the reply is "*"
            */


    }

}
