/*
 * Copyright (c) 2012, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</div>
 */

package com.groupon.roboremote.roboremoteclientcommon;

import com.android.ddmlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class DebugBridge {
    public static final Logger logger = LoggerFactory.getLogger(DebugBridge.class);

    private AndroidDebugBridge bridge;
    private static DebugBridge _debugBridge = null;

    private IDevice currentDevice = null;

    private LogThread loggerThread = null;

    public static DebugBridge get() throws Exception {
        if (_debugBridge == null) {
            _debugBridge = new DebugBridge();
        }

        return _debugBridge;
    }

    private DebugBridge() throws Exception {
        AndroidDebugBridge.init(false);
        bridge = AndroidDebugBridge.createBridge("adb", true);

        waitForConnected();
        waitForDevices();

        // select first device by default
        if (bridge.getDevices().length == 0)
            throw new Exception("There are no attached devices");

        currentDevice = bridge.getDevices()[0];
    }

    public boolean isEmulator() throws Exception {
        return currentDevice.isEmulator();
    }

    public void runShellCommand(String command) throws Exception {
        
        currentDevice.executeShellCommand(command, new NullOutputReceiver());
    }

    public void runShellCommand(String command, int timeout) throws Exception {

        currentDevice.executeShellCommand(command, new NullOutputReceiver(), timeout);
    }

    public void runShellCommand(String command, IShellOutputReceiver receiver, int timeout) throws Exception {

        currentDevice.executeShellCommand(command, receiver, timeout);
    }

    public void createTunnel(int fromPort, int toPort) throws Exception {
        currentDevice.createForward(fromPort, toPort);
    }

    public void waitForConnected() throws Exception {
        int x = 0;
        while (bridge.isConnected() == false) {
            Thread.sleep(100);
            x++;

            if (x > 50) {
                throw new Exception("Could not get adb connection");
            }
        }
    }

    public void waitForDevices() throws Exception {
        int x = 0;
        while (bridge.hasInitialDeviceList() == false) {
            Thread.sleep(100);
            x++;

            if (x > 50) {
                throw new Exception("Could not get device list");
            }
        }
    }

    public String[] getDevices() throws Exception {
        ArrayList<String> deviceNames = new ArrayList<String>();
        for (IDevice device : bridge.getDevices()) {
            deviceNames.add(device.getAvdName());
        }

        return deviceNames.toArray(new String[0]);
    }

    public void selectDevice(String avdName) throws Exception {
        boolean found = false;
        for (IDevice device : bridge.getDevices()) {
            if (device.getAvdName().equals(avdName)) {
                found = true;
                currentDevice = device;
                break;
            }

            if (!found)
                throw new Exception("Could not find specified device");
        }
    }

    public void close() {
        AndroidDebugBridge.terminate();
    }

    public void getScreenShot(String filename) throws Exception {
        RawImage raw = null;

        try {
            raw = currentDevice.getScreenshot();
        } catch (Exception e) {
            throw new Exception("Error getting screenshot from device: " + e.getMessage());
        }

        BufferedImage image = new BufferedImage(raw.width, raw.height, BufferedImage.TYPE_INT_ARGB);

        int idx = 0;
        int inc = raw.bpp >> 3;
        for (int y = 0; y < raw.height; y++) {
            for (int x = 0; x < raw.width; x++) {
                int val = raw.getARGB(idx);
                idx += inc;
                image.setRGB(x, y, val);
            }
        }

        ImageIO.write(image, "png", new File(filename));
    }

    public void push(String sourcefile, String destination) throws Exception {
        SyncService service = currentDevice.getSyncService();
        service.pushFile(sourcefile, destination, SyncService.getNullProgressMonitor());
    }

    public void pull(String sourcefile, String destination) throws Exception {
        SyncService service = currentDevice.getSyncService();
        service.pullFile(sourcefile, destination, SyncService.getNullProgressMonitor());
    }
    
    public void installPackage(String filename) throws Exception {
        currentDevice.installPackage(filename, false);
    }
    
    public void uninstallPackage(String packageName) throws Exception {
        currentDevice.uninstallPackage(packageName);
    }
    
    public void setLatLon(double lat, double lon) throws Exception {
        EmulatorConsole emu = EmulatorConsole.getConsole(currentDevice);
        emu.sendLocation(lon, lat, 0);
    }

    /**
     * This is the equivalent of adb logcat -c
     * @throws Exception
     */
    public void clearLogCat() throws Exception {
        DebugBridge.get().runShellCommand("logcat -c");
    }

    public void startLogListener(String filename) throws Exception {
        loggerThread = new LogThread(filename);
        loggerThread.start();
    }

    public void stopLogListener() throws Exception {
        if (loggerThread != null) {
            loggerThread.close();
            loggerThread.interrupt();
        }
    }

    public static class MultiReceiver extends MultiLineReceiver {
        boolean closed = false;
        String _fileName = null;
        FileWriter fstream = null;
        BufferedWriter ostream = null;
        Boolean writeToConsole = false;

        public MultiReceiver(String outfile) throws Exception {
            new MultiReceiver(outfile, false);
        }

        public MultiReceiver(String outfile, Boolean writeToConsole) throws Exception {
            _fileName = outfile;
            fstream = new FileWriter(_fileName);
            ostream = new BufferedWriter(fstream);
            this.writeToConsole = writeToConsole;
        }

        public MultiReceiver(Boolean writeToConsole) {
            this.writeToConsole = writeToConsole;
        }

        public MultiReceiver() {
            
        }

        public void processNewLines(java.lang.String[] lines) {
            try {
                for (String line: lines) {
                    if (ostream != null) {
                        ostream.write(line + "\n");
                    }
                    if (writeToConsole)
                        System.out.println(line);
                }
            } catch (Exception e) {
                
            }
        }

        public boolean isCancelled() {
            return closed;
        }

        public void close() {
            closed = true;
        }
    }

    /**
     * This thread contains the running the log listener
     * DebugBridge does not return until the logcat finishes(never) so we have to run it in its own thread
     */
    private static class LogThread extends Thread {
        String _filename = "";
        MultiReceiver _receiver = null;

        public LogThread(String filename) {
            _filename = filename;
        }

        public void run() {
            try {
                _receiver = new MultiReceiver(_filename);
                DebugBridge.get().runShellCommand("logcat -v time", _receiver, 0);
            } catch (Exception e) {
                System.out.println("INTERRUPTED!!!");
            }
        }

        public void close() {
            // cause an exception in the receiver to kill the command
            _receiver.close();
        }
    }
}