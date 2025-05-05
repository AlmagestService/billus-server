package com.klolarion.billusserver.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.klolarion.billusserver.domain.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QRService {

    /**
     * 매장 정보를 기반으로 QR코드 이미지를 생성하여 반환합니다.
     * @param store 매장 엔티티
     * @return QR코드 이미지(PNG) 바이너리 ResponseEntity
     */
    public ResponseEntity<byte[]> publishStoreQr(Store store) {
        String storeId = store.getId().toString();
        String storeName = store.getStoreName();
        int width = 300;
        int height = 300;
        String qrCodeData = storeId + "," + storeName;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        } catch (WriterException e) {
            // QR코드 생성 실패
            return ResponseEntity.status(500).body(("QR코드 생성 실패: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // 이미지 변환 등 기타 예외
            return ResponseEntity.status(500).body(("이미지 변환 실패: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
