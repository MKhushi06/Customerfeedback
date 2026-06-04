package com.feedback.app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public class QRCodeGenerator {
    public static ImageIcon generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Failed to generate QR code locally: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
