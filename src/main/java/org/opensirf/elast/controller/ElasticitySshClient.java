/*
 * OpenSIRF JAX-RS
 * 
 * Copyright IBM Corporation 2016.
 * All Rights Reserved.
 * 
 * MIT License:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization of the
 * copyright holder.
 */
package org.opensirf.elast.controller;

import java.io.IOException;
import java.io.InputStream;

import org.opensirf.elast.SirfElasticityException;
import org.opensirf.jaxrs.config.SIRFConfiguration;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author pviana
 *
 */
public class ElasticitySshClient {

	public ElasticitySshClient(String username, String host, int port) {
		this.username = username;
		this.host = host;
		this.port = port;
		shell = new JSch();
	}
	
	public void addIdentity(String privateKeyPath, String passphrase) {
		try {
			shell.addIdentity(privateKeyPath, passphrase);
		} catch(JSchException je) {
			throw new SirfElasticityException("JSch exception adding the private key: " +
					je.getMessage());
		}
	}
	
	public void setKnownHosts(String knownHostsPath) {
		try {
			shell.setKnownHosts(knownHostsPath);
		} catch(JSchException je) {
			throw new SirfElasticityException("JSch exception setting known hosts: " +
					je.getMessage());
		}
	}
	
	public String createNewVagrantVM(String vmType, String vmName) {
		String imageName = "null";
		
		if(vmType.equalsIgnoreCase("swift")) {
			imageName = "devstack";
		}
		
		return executeCommand(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY +
				"/elast-actuator/sirf-provision " + imageName + " " + vmName);
	}
	
	public void disconnect() {
		session.disconnect();
	}
	
	private String executeCommand(String command) {
		try {
			StringBuilder outputBuffer = new StringBuilder();
			ChannelExec c = ((ChannelExec) session.openChannel("exec"));
			c.setCommand(command);
	        InputStream commandOutput = c.getInputStream();
	        c.connect();
	        int readByte = commandOutput.read();

	        while(readByte != 0xffffffff) {
	           outputBuffer.append((char)readByte);
	           readByte = commandOutput.read();
	        }
	        
	        c.disconnect();
	        
	        return new String(outputBuffer);
	        
		} catch(JSchException je) {
			throw new SirfElasticityException("JSch exception executing command: " + je.getMessage());
		} catch(IOException ioe) {
			throw new SirfElasticityException("IO exception executing command: " + ioe.getMessage());
		}
		
	}
	
	public void connect() {
		try {
			session = shell.getSession(username, host, port);
			session.connect();
		} catch(JSchException je) {
			throw new SirfElasticityException("JSch exception opening session: " +	je.getMessage());
		}
	}
	
	private Session session;
	private JSch shell;
	private String username;
	private String host;
	private int port;
}
