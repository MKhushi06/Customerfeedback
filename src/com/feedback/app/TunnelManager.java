package com.feedback.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TunnelManager {
    private static Process sshProcess;
    private static String tunnelUrl = null;
    private static Runnable onUrlReadyListener;

    public static void startTunnel(Runnable onUrlReady) {
        onUrlReadyListener = onUrlReady;
        new Thread(() -> {
            try {
                System.out.println("Starting secure public tunnel via localhost.run...");
                ProcessBuilder pb = new ProcessBuilder(
                    "ssh", 
                    "-o", "StrictHostKeyChecking=no", 
                    "-o", "UserKnownHostsFile=NUL", 
                    "-R", "80:127.0.0.1:8085", 
                    "nokey@localhost.run"
                );
                pb.redirectErrorStream(true);
                sshProcess = pb.start();
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(sshProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Tunnel Output] " + line);
                        if (line.contains("lhr.life") && line.contains("https://")) {
                            int httpsIdx = line.indexOf("https://");
                            if (httpsIdx != -1) {
                                String url = line.substring(httpsIdx).trim();
                                // Clean up any trailing space or escape characters
                                url = url.replaceAll("\\u001B\\[[;\\d]*m", "");
                                tunnelUrl = url;
                                System.out.println("Tunnel URL Established: " + tunnelUrl);
                                if (onUrlReadyListener != null) {
                                    onUrlReadyListener.run();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Tunnel failed: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static String getTunnelUrl() {
        return tunnelUrl;
    }

    public static void stopTunnel() {
        if (sshProcess != null) {
            sshProcess.destroy();
        }
    }
}
