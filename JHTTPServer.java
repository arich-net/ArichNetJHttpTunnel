/* -*-mode:java; c-basic-offset:2; -*- */
/*
 Copyright (c) 2004 ymnk, JCraft,Inc. All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.
 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.jcraft.jhttptunnel;

import java.io.*;
import java.net.*;
import java.util.*;

public class JHttpTunnelServer implements Runnable
{
	static int connections = 0;
	static int client_connections = 0;
	static int source_connections = 0;

	private ServerSocket serverSocket = null;
	static int port = 8888;
	static String myaddress = null;
	static String myURL = null;

	static String forward_host;
	static int forward_port;

	JHttpTunnelServer(int port)	{
		super ();
		connections = 0;
		try	{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			System.out.println("ServerSocket error"+e );
			System.exit (1);
		}
		try	{
			if (myaddress == null)
				myURL = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
			else
				myURL = "http://" + myaddress + ":" + port;
				System.out.println("myURL: "+myURL);
		}
		catch (Exception e)	{
			System.out.println (e);
		}
	}

	JHttpTunnelServer (int lport, String fhost, int fport) {
		this(lport);
		this.forward_host = fhost;
		this.forward_port = fport;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		while(true) {
			try	{
				socket = serverSocket.accept ();
			}
			catch(IOException e) {
				System.out.println ("Socket accept error, probably the port is busy");
				System.exit(1);
			}
			connections++;
			// new Spawn(socket);
			final Socket _socket = socket;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try	{
						(new JHTTPServerConnection(_socket)).doit();
					}
					catch(Exception e)	{ }
				}
			}).start();
		}
	}
}
