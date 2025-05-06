package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.security.CustomStoreDetails;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.service.BillService;
import com.klolarion.billusserver.service.ExcelExportService;
import com.klolarion.billusserver.service.StoreService;
import com.klolarion.billusserver.util.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billus/a2/v1/export")
public class ExportStoreControllerV1 {

    private final ExcelExportService excelExportService;
    private final QRService qrService;
    private final BillService billService;
    private final StoreService storeService;

    /**
     * 매장 QR코드 생성 API
     * @param customStoreDetails 인증된 매장 정보
     * @return QR코드 이미지
     */
    @GetMapping("/qr")
    public ResponseEntity<byte[]> createStoreQr(@AuthenticationPrincipal CustomStoreDetails customStoreDetails) {
        return qrService.publishStoreQr(customStoreDetails.getStore());
    }

    /**
     * 매장 월간 장부 엑셀 다운로드 API
     * @param month 조회할 월
     * @return 월간 장부 엑셀 파일
     */
    @GetMapping("/store/month/detail/excel")
    public ResponseEntity<byte[]> companyMonthDetail(@RequestParam String month, @AuthenticationPrincipal CustomStoreDetails customStoreDetails){
        List<Object[]> list = billService.monthlyStoreBillDetail(month, customStoreDetails.getStore());
        return excelExportService.storeMonthlyDetailBillExcel(list, customStoreDetails.getStore(), month);
    }

    /**
     * 매장 일별 장부 엑셀 다운로드 API
     * @param date 조회할 날짜
     * @return 일별 장부 엑셀 파일
     */
    @GetMapping("/store/daily/excel")
    public ResponseEntity<byte[]> exportStoreDailyBillExcel(@RequestParam String date, @AuthenticationPrincipal CustomStoreDetails customStoreDetails){
        List<BillResponseDto> billList = billService.dailyCompanyBillTotalList(date, customStoreDetails.getStore());
        return excelExportService.storeDailyBillExcel(billList);
    }

    /**
     * 매장 월별 장부 엑셀 다운로드 API
     * @param month 조회할 월
     * @return 월별 장부 엑셀 파일
     */
    @GetMapping("/store/monthly/excel")
    public ResponseEntity<byte[]> exportStoreMonthlyBillExcel(@RequestParam String month, @AuthenticationPrincipal CustomStoreDetails customStoreDetails){
        List<BillResponseDto> billList = billService.monthlyCompanyBillTotalList(month, customStoreDetails.getStore());
        return excelExportService.storeMonthlyCompanyTotalExcel(billList);
    }
}
