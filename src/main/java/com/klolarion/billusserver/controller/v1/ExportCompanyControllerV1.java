package com.klolarion.billusserver.controller.v1;

import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.klolarion.billusserver.security.CustomCompanyDetails;
import com.klolarion.billusserver.service.BillService;
import com.klolarion.billusserver.service.CompanyService;
import com.klolarion.billusserver.service.ExcelExportService;
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
public class ExportCompanyControllerV1 {

    private final ExcelExportService excelExportService;
    private final BillService billService;
    private final CompanyService companyService;

    /**
     * 회사 직원 목록 엑셀 다운로드 API
     * @param customCompanyDetails 인증된 회사 정보
     * @return 직원 목록 엑셀 파일
     */
    @GetMapping("/company/employee/list/excel")
    public ResponseEntity<byte[]> exportEmployeeListExcel(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails){
        List<MemberResponseDto> empList = companyService.findMyEmp(customCompanyDetails.getCompany());
        return excelExportService.companyEmployeeList(empList);
    }

    /**
     * 회사 월별 직원별 매출 합계 엑셀 다운로드 API
     * @param month 조회할 월
     * @return 직원별 월 매출 합계 엑셀 파일
     */
    @GetMapping("/company/month/employee/total/excel")
    public ResponseEntity<byte[]> companyMonthMemberTotal(@RequestParam String month){
        List<BillResponseDto> list = billService.monthlyEmployeeBillTotal(month);
        return excelExportService.companyMonthlyEmpTotalBillExcel(list);
    }

    /**
     * 회사 월별 직원별 상세 매출 엑셀 다운로드 API
     * @param month 조회할 월
     * @param id 직원 ID
     * @return 직원별 월 상세 매출 엑셀 파일
     */
    @GetMapping("/company/month/employee/detail/excel")
    public ResponseEntity<byte[]> companyMonthMemberDetail(@RequestParam String month, String id){
        List<BillResponseDto> list = billService.monthlyEmployeeBillList(month, id);
        return excelExportService.companyMonthlyEmpDetailBillExcel(list);
    }

    /**
     * 회사 월별 매장별 매출 합계 엑셀 다운로드 API
     * @param month 조회할 월
     * @return 매장별 월 매출 합계 엑셀 파일
     */
    @GetMapping("/company/month/store/total/excel")
    public ResponseEntity<byte[]> companyMonthStoreTotal(@RequestParam String month){
        List<BillResponseDto> list = billService.monthlyStoreBillTotalList(month);
        return excelExportService.companyMonthlyStoreTotalExcel(list);
    }

    /**
     * 회사 월별 매장별 상세 매출 엑셀 다운로드 API
     * @param month 조회할 월
     * @param id 매장 ID
     * @return 매장별 월 상세 매출 엑셀 파일
     */
    @GetMapping("/company/month/store/detail/excel")
    public ResponseEntity<byte[]> companyMonthStoreDetail(@RequestParam String month, String id){
        List<BillResponseDto> list = billService.monthlyStoreBillDetailList(month, id);
        return excelExportService.companyMonthlyStoreDetailBillExcel(list);
    }

    /**
     * 회사 월별 상세 매출 엑셀 다운로드 API
     * @param month 조회할 월
     * @param id 회사 ID
     * @return 회사 월 상세 매출 엑셀 파일
     */
    @GetMapping("/company/month/detail/excel")
    public ResponseEntity<byte[]> companyMonthDetail(@RequestParam String month, String storeId, @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails){
        List<Object[]> list = billService.monthlyCompanyBillDetail(month, storeId);
        return excelExportService.companyMonthlyDetailBillExcel(list, customCompanyDetails.getCompany().getCompanyName(), storeId, month);
    }

    /**
     * 회사 월별 전체 매장 일별 장부 누적수 피벗 엑셀 다운로드 API
     * @param month 조회할 월
     * @param customCompanyDetails 인증된 회사 정보
     * @return 전체 매장 일별 장부수 피벗 엑셀 파일
     */
    @GetMapping("/company/month/all-store/detail/excel")
    public ResponseEntity<byte[]> companyMonthAllStoreDetail(@RequestParam String month, @AuthenticationPrincipal CustomCompanyDetails customCompanyDetails) {
        return excelExportService.companyMonthlyAllStoreDetailExcel(month, customCompanyDetails.getCompany().getId().toString());
    }
}
