/*
        Copyright (c) 2012, 2013, 2014, Groupon, Inc.
        All rights reserved.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions
        are met:

        Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.

        Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

        Neither the name of GROUPON nor the names of its contributors may be
        used to endorse or promote products derived from this software without
        specific prior written permission.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
        IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
        TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
        PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
        HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
        SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
        TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
        PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
        LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
        NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.groupon.roboremote.roboremoteclientcommon;

import com.android.ddmlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class DebugBridge {
    public static final Logger logger = LoggerFactory.getLogger(DebugBridge.class);

    private AndroidDebugBridge bridge = null;
    private static DebugBridge _debugBridge = null;

    private IDevice currentDevice = null;

    public static DebugBridge get() throws Exception {
        if (_debugBridge == null) {
            _debugBridge = new DebugBridge();
        }

        return _debugBridge;
    }

    public static void destroy() throws Exception {
        if (_debugBridge != null) {
            _debugBridge = null;
        }
    }

    private AndroidDebugBridge createBridge(final String adbPath) {
        AndroidDebugBridge _bridge = null;
        ExecutorService adbExecutor = Executors.newCachedThreadPool();
        Callable<AndroidDebugBridge> adbTask = new Callable<AndroidDebugBridge>() {
            @Override
            public AndroidDebugBridge call() throws Exception {
                return AndroidDebugBridge.createBridge(adbPath, true);
            }
        };
        Future<AndroidDebugBridge> futureBridge = adbExecutor.submit(adbTask);

        try {
            _bridge = futureBridge.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("Creating bridge failed");
        } finally {
            futureBridge.cancel(true);
        }

        return _bridge;
    }

    private DebugBridge() throws Exception {
        AndroidDebugBridge.initIfNeeded(false);

        // find adb on path
        String adbPath = "adb";
        String execPath = System.getenv("PATH");
        String[] execPaths = execPath.split(File.pathSeparator);
        for (String path: execPaths) {
            File adbFile = new File(path + File.separator + "adb");
            if (adbFile.exists()) {
                adbPath = adbFile.getAbsolutePath();
                logger.info("Got adb path: {}", adbPath);
                break;
            }
        }

        logger.info("Creating ADB bridge");
        // this hangs some times so we do it in an executor
        bridge = AndroidDebugBridge.createBridge(adbPath, false);

        if (bridge == null) {
            throw new Exception("Could not create adb bridge");
        }

        waitForConnected();
        waitForDevices();

        // check for serial number
        if (Utils.getEnv("ROBO_SERIAL_NUMBER", null) != null) {
            this.selectDeviceBySerialNumber(Utils.getEnv("ROBO_SERIAL_NUMBER", null));
        } else {
            // select first device by default
            logger.info("Selecting first device");
            if (bridge.getDevices().length == 0)
                throw new Exception("There are no attached devices");

            currentDevice = bridge.getDevices()[0];
        }
        logger.info("Done starting debug bridge");
    }

    public void terminate() {
        bridge.terminate();
    }

    public boolean isEmulator() throws Exception {
        return currentDevice.isEmulator();
    }

    public void runShellCommand(String command) {
        try {
            currentDevice.executeShellCommand(command, new NullOutputReceiver());
        } catch (Exception e) {
            // since this has a NullOutputReceiver it occasionally fails
            // catch the error and print some warning logs
            logger.warn("Error recieved: {}", e);
        }
    }

    public void runShellCommand(String command, int timeout) {
        try {
            currentDevice.executeShellCommand(command, new NullOutputReceiver(), timeout);
        } catch (Exception e) {
            // since this has a NullOutputReceiver it occasionally fails
            // catch the error and print some warning logs
            logger.warn("Error recieved: {}", e);
        }
    }

    public void runShellCommand(String command, IShellOutputReceiver receiver, int timeout) throws Exception {

        currentDevice.executeShellCommand(command, receiver, timeout);
    }

    public void createTunnel(int fromPort, int toPort) throws Exception {


        currentDevice.createForward(fromPort, toPort);
    }

    public void deleteTunnel(int fromPort, int toPort) throws Exception {
        currentDevice.removeForward(fromPort, toPort);
    }

    public void waitForConnected() throws Exception {
        logger.info("Waiting for ADB connection");
        int x = 0;
        while (bridge.isConnected() == false) {
            Thread.sleep(100);
            x++;

            if (x > 50) {
                throw new Exception("Could not get adb connection");
            }
        }
        logger.info("ADB Connected");
    }

    public void waitForDevices() throws Exception {
        logger.info("Waiting for device list");
        int x = 0;
        while (bridge.hasInitialDeviceList() == false) {
            Thread.sleep(1000);
            x++;

            if (x > 30) {
                throw new Exception("Could not get device list");
            }
        }
        logger.info("Got device list");
    }

    public String[] getDevices() throws Exception {
        ArrayList<String> deviceNames = new ArrayList<String>();
        for (IDevice device : bridge.getDevices()) {
            deviceNames.add(device.getAvdName());
        }

        return deviceNames.toArray(new String[0]);
    }

    public void selectDeviceByAVDName(String avdName) throws Exception {
        boolean found = false;
        for (IDevice device : bridge.getDevices()) {
            if (device.getAvdName().equals(avdName)) {
                found = true;
                currentDevice = device;
                break;
            }
        }

        // throw an exception if we did not find the named device
        if (!found)
            throw new Exception("Could not find specified device");

        logger.info("Now using device: {}", avdName);
    }

    public void selectDeviceBySerialNumber(String serialNumber) throws Exception {
        logger.info("Trying to find device: {}", serialNumber);
        boolean found = false;
        for (IDevice device : bridge.getDevices()) {
            if (device.getSerialNumber().equals(serialNumber)) {
                found = true;
                currentDevice = device;
                break;
            }
        }

        // throw an exception if we did not find the named device
        if (!found)
            throw new Exception("Could not find specified device");

        logger.info("Now using device: {}", serialNumber);
    }

    public String getSerialNumber() {
        return currentDevice.getSerialNumber();
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
}