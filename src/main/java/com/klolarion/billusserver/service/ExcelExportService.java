package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.entity.QStore;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.bill.BillResponseDto;
import com.klolarion.billusserver.dto.member.MemberResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExcelExportService {

    private final JPAQueryFactory query;
    private final EntityManager em;

    /**
     * 매장 - 일일 장부 합계 엑셀 파일을 생성하여 반환합니다.
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> storeDailyBillExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("daily_total_sheet");


            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "회사이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {

                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
//                cell.getCellStyle().setWrapText(true); //자동줄바꿈
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getCompanyName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "daily_total.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 매장 - 월간 회사별 합계 엑셀 파일을 생성하여 반환합니다. (월간 통계)
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> storeMonthlyCompanyTotalExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("monthly_total_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "회사이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getCompanyName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "monthly_total.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 매장 - 월간 회사별 상세 장부 엑셀 파일을 생성하여 반환합니다. *월간 통계
     * @param billList Object[] 형태의 장부 데이터(회사명, 일별 데이터 등)
     * @param store 매장 엔티티
     * @param date 기준월(yyyy-MM)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> storeMonthlyDetailBillExcel(List<Object[]> billList, Store store, String date) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("monthly_detail_sheet");

            int price = store.getPrice();
            String storeName = store.getStoreName();
            

            // 스타일 정의 (생략, 기존 코드 그대로)
            CellStyle centerAlignThinStyle = workbook.createCellStyle();
            centerAlignThinStyle.setAlignment(HorizontalAlignment.CENTER);
            centerAlignThinStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerAlignThinStyle.setBorderTop(BorderStyle.THIN);
            centerAlignThinStyle.setBorderBottom(BorderStyle.THIN);
            centerAlignThinStyle.setBorderLeft(BorderStyle.THIN);
            centerAlignThinStyle.setBorderRight(BorderStyle.THIN);
            centerAlignThinStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리 상단 스타일
            CellStyle dataTopThickStyle = workbook.createCellStyle();
            dataTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataTopThickStyle.setBorderLeft(BorderStyle.THIN);
            dataTopThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 하단 스타일
            CellStyle dataBottomThickStyle = workbook.createCellStyle();
            dataBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            dataBottomThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 우측 스타일
            CellStyle dataRightThickStyle = workbook.createCellStyle();
            dataRightThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightThickStyle.setBorderTop(BorderStyle.THIN);
            dataRightThickStyle.setBorderBottom(BorderStyle.THIN);
            dataRightThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightThickStyle.setBorderRight(BorderStyle.MEDIUM);


            //가운데정렬, 굵은테두리 우상단 스타일
            CellStyle dataRightTopThickStyle = workbook.createCellStyle();
            dataRightTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataRightTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataRightTopThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightTopThickStyle.setBorderRight(BorderStyle.MEDIUM);

            //가운데정렬, 굵은테두리 우하단 스타일
            CellStyle dataRightBottomThickStyle = workbook.createCellStyle();
            dataRightBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataRightBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataRightBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightBottomThickStyle.setBorderRight(BorderStyle.MEDIUM);

            //가운데정렬, 굵은테두리 좌측 스타일
            CellStyle dataLeftThickStyle = workbook.createCellStyle();
            dataLeftThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftThickStyle.setBorderTop(BorderStyle.THIN);
            dataLeftThickStyle.setBorderBottom(BorderStyle.THIN);
            dataLeftThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 좌상단 스타일
            CellStyle dataLeftTopThickStyle = workbook.createCellStyle();
            dataLeftTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataLeftTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataLeftTopThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftTopThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 좌하단 스타일
            CellStyle dataLeftBottomThickStyle = workbook.createCellStyle();
            dataLeftBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataLeftBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataLeftBottomThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftBottomThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorThinStyle = workbook.createCellStyle();
            countColorThinStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorThinStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorThinStyle.setBorderTop(BorderStyle.THIN);
            countColorThinStyle.setBorderBottom(BorderStyle.THIN);
            countColorThinStyle.setBorderLeft(BorderStyle.THIN);
            countColorThinStyle.setBorderRight(BorderStyle.THIN);
            countColorThinStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorThinStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorThinStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorLeftThickStyle = workbook.createCellStyle();
            countColorLeftThickStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorLeftThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorLeftThickStyle.setBorderTop(BorderStyle.THIN);
            countColorLeftThickStyle.setBorderBottom(BorderStyle.THIN);
            countColorLeftThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            countColorLeftThickStyle.setBorderRight(BorderStyle.THIN);
            countColorLeftThickStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorLeftThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorLeftThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorTopThickStyle = workbook.createCellStyle();
            countColorTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            countColorTopThickStyle.setBorderBottom(BorderStyle.THIN);
            countColorTopThickStyle.setBorderLeft(BorderStyle.THIN);
            countColorTopThickStyle.setBorderRight(BorderStyle.THIN);
            countColorTopThickStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorTopThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorTopThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));


            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorBottomThickStyle = workbook.createCellStyle();
            totalColorBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorBottomThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            totalColorBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorBottomThickStyle.setBorderRight(BorderStyle.THIN);
            totalColorBottomThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorRightThickStyle = workbook.createCellStyle();
            totalColorRightThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorRightThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorRightThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderBottom(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderRight(BorderStyle.MEDIUM);
            totalColorRightThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorRightThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorRightThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorLeftBottomThickStyle = workbook.createCellStyle();
            totalColorLeftBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorLeftBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorLeftBottomThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorLeftBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            totalColorLeftBottomThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            totalColorLeftBottomThickStyle.setBorderRight(BorderStyle.THIN);
            totalColorLeftBottomThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorLeftBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorLeftBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle allTotalColorRightBottomThickStyle = workbook.createCellStyle();
            allTotalColorRightBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            allTotalColorRightBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            allTotalColorRightBottomThickStyle.setBorderTop(BorderStyle.THIN);
            allTotalColorRightBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            allTotalColorRightBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            allTotalColorRightBottomThickStyle.setBorderRight(BorderStyle.MEDIUM);
            allTotalColorRightBottomThickStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            allTotalColorRightBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            allTotalColorRightBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle allTotalColorBottomThickStyle = workbook.createCellStyle();
            allTotalColorBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            allTotalColorBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            allTotalColorBottomThickStyle.setBorderTop(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            allTotalColorBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setBorderRight(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            allTotalColorBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            allTotalColorBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));



            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorRightTopThickStyle = workbook.createCellStyle();
            totalColorRightTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorRightTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorRightTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            totalColorRightTopThickStyle.setBorderBottom(BorderStyle.THIN);
            totalColorRightTopThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorRightTopThickStyle.setBorderRight(BorderStyle.MEDIUM);
            totalColorRightTopThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorRightTopThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorRightTopThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));



            //헤더
            final String[] header = {"","", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //첫번째와 마지막셀 넓이 조정
                if(i==0 || i==34){
                    //셀 폭 조정
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+3072);
                }else{
                    //셀 폭 조정
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);
                }
                Cell cell = row.createCell(i);
                cell.setCellValue(header[i]);
            }

            Cell cell = null;

            //-----------------------------------

            row = sheet.createRow(1);

            cell = row.createCell(1);
            cell.setCellValue("매장");
            cell.setCellStyle(dataLeftTopThickStyle);

            cell = row.createCell(2);
            cell.setCellValue(storeName);
            cell.setCellStyle(dataTopThickStyle);

            cell = row.createCell(3);
            cell.setCellStyle(dataRightTopThickStyle);

            // 셀 합치기
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));

            //-----------------------------------


            row = sheet.createRow(2);

            cell = row.createCell(1);
            cell.setCellValue("기준월");
            cell.setCellStyle(dataLeftBottomThickStyle);

            cell = row.createCell(2);
            cell.setCellValue(date);
            cell.setCellStyle(dataBottomThickStyle);

            cell = row.createCell(3);
            cell.setCellStyle(dataRightBottomThickStyle);

            // 셀 합치기
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));

            // 데이터 바디 (회사명, 01~31, 누적횟수, 합계)
            int totalCount = 0;
            int totalPrice = 0;

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 7);
                int count = 0;
                for (int j = 0; j < 32; j++) { // 0:회사명, 1~31:일별
                    cell = row.createCell(j);
                    if(j == 1){
                        String value = billList.get(i)[j].toString();
                        cell = row.createCell(j);
                        cell.setCellStyle(centerAlignThinStyle);
                        cell.setCellValue(value);
                        continue;
                    }
                    String value = billList.get(i)[j].toString();
                    try{
                        if(j > 1) {
                            count += Integer.parseInt(value);
                        }
                    }catch (Exception ignored){

                    }

                    if(j==0){
                        cell.setCellValue(value);
                        cell.setCellStyle(dataLeftThickStyle);
                        continue;
                    }

                    cell.setCellStyle(centerAlignThinStyle);
                    cell.setCellValue(value);

                }


                //회사별 누적횟수
                cell = row.createCell(33);
                cell.setCellValue(count);
                cell.setCellStyle(countColorThinStyle);


                //회사별 합계
                cell = row.createCell(34);
                cell.setCellValue(count * price);
                cell.setCellStyle(totalColorRightThickStyle);

                //전체 누적횟수, 합계
                totalCount += count;
                totalPrice += count * price;
            }



            //-----------------------------------
            row = sheet.createRow(7+billList.size()); //데이터 행
            Row totalRow = sheet.createRow(8+billList.size()); //합계 행

            cell = row.createCell(0);
            cell.setCellValue("누적횟수");
            cell.setCellStyle(countColorLeftThickStyle);

            cell = row.createCell(1);
            cell.setCellStyle(countColorThinStyle);

            cell = row.createCell(33);
            cell.setCellValue(totalCount);
            cell.setCellStyle(countColorThinStyle);

            cell = row.createCell(34);
            cell.setCellStyle(totalColorRightThickStyle);

            cell = totalRow.createCell(0);
            cell.setCellValue("합계");
            cell.setCellStyle(totalColorLeftBottomThickStyle);

            cell = totalRow.createCell(1);
            cell.setCellStyle(totalColorBottomThickStyle);

            cell = totalRow.createCell(33);
            cell.setCellValue("전체합계");
            cell.setCellStyle(allTotalColorBottomThickStyle);

            cell = totalRow.createCell(34);
            cell.setCellValue(totalPrice);
            cell.setCellStyle(allTotalColorRightBottomThickStyle);

            for(int columnIndex=2; columnIndex<33; columnIndex++){
                int dayTotalPrice = 0;
                int dayCount = 0;
                for(int rowIndex = 6; rowIndex < 6+billList.size(); rowIndex++){
                    Row rowTmp = sheet.getRow(rowIndex);
                    cell = rowTmp.getCell(columnIndex);
                    String value = cell.getStringCellValue();
                    int cnt = 0;
                    try {
                        cnt = Integer.parseInt(value);
                    } catch (Exception ignored) {}
                    dayTotalPrice += cnt * price;
                    dayCount += cnt;
                }
                //일별 누적횟수
                cell = row.createCell(columnIndex);
                cell.setCellValue(dayCount);
                cell.setCellStyle(countColorThinStyle);

                //일별 합계
                cell = totalRow.createCell(columnIndex);
                cell.setCellValue(dayTotalPrice);
                cell.setCellStyle(totalColorBottomThickStyle);
            }



            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "monthly_detail.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 회사 - 전체 직원 리스트 엑셀 파일을 생성하여 반환합니다.
     * @param empList 직원 정보 리스트(MemberResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyEmployeeList(List<MemberResponseDto>  empList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("emp_list_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"직원번호", "직원이름", "이메일", "전화번호"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < empList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(empList.get(i).getMemberId());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(empList.get(i).getMemberName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellValue(empList.get(i).getEmail());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellValue(empList.get(i).getTel());
                cell.setCellStyle(centerAlign);


            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "emp_list.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 회사 - 월간 직원별 장부 합계 엑셀 파일을 생성하여 반환합니다.
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyMonthlyEmpTotalBillExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("emp_monthly_total_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "직원이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1


                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getEmployeeName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "emp_monthly_total.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 회사 - 월간 직원별 상세 장부 엑셀 파일을 생성하여 반환합니다.
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyMonthlyEmpDetailBillExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("emp_monthly_detail_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "직원이름", "매장이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getEmployeeName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellValue(billList.get(i).getStoreName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(4);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "emp_monthly_detail.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 회사 - 월간 매장별 합계 엑셀 파일을 생성하여 반환합니다. (월간 통계)
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyMonthlyStoreTotalExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("emp_monthly_total_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "매장이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1


                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getStoreName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "emp_monthly_total.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 회사 - 월간 매장별 상세 장부 엑셀 파일을 생성하여 반환합니다.
     * @param billList 장부 데이터 리스트(BillResponseDto)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyMonthlyStoreDetailBillExcel(List<BillResponseDto> billList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("emp_monthly_detail_sheet");

            //숫자 포맷은 아래 numberCellStyle을 적용시킬 것이다(000,000,000)
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);


            //헤더
            final String[] header = {"Idx", "직원이름", "매장이름", "합계", "날짜"};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //셀 폭 조정
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);

                Cell cell = row.createCell(i);
                cell.setCellStyle(centerAlign);
                cell.setCellValue(header[i]);
            }

            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 1);  //헤더 이후로 데이터가 출력되어야하니 +1

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(billList.get(i).getIndex());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(1);
                cell.setCellValue(billList.get(i).getEmployeeName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(2);
                cell.setCellValue(billList.get(i).getStoreName());
                cell.setCellStyle(centerAlign);

                cell = row.createCell(3);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(Integer.parseInt(billList.get(i).getPrice()));
                cell.setCellStyle(centerAlign);

                cell = row.createCell(4);
                cell.setCellValue(billList.get(i).getDate());
                cell.setCellStyle(centerAlign);

            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "emp_monthly_detail.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 회사 - 매장별 월간 상세 장부 엑셀 파일을 생성하여 반환합니다.
     * 특정 매장에서 어떤 직원이 몇일에 장부를 등록했는지 한달 단위로 출력합니다.
     * @param billList Object[] 형태의 장부 데이터(일별 데이터 등)
     * @param companyName 회사명
     * @param storeId 매장ID
     * @param date 기준월(yyyy-MM)
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     * @throws RuntimeException 엑셀 생성 중 IO 오류 발생 시
     */
    public ResponseEntity<byte[]> companyMonthlyDetailBillExcel(List<Object[]> billList, String companyName, String storeId, String date) {
        try {

            QStore qStore = QStore.store;
            Store store = query.selectFrom(qStore)
                    .where(qStore.id.eq(UUID.fromString(storeId)))
                    .fetchOne();

            String storeName = store.getStoreName();
            int price = store.getPrice();
            


            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("monthly_detail_sheet");

            //숫자 포맷(000,000,000)
            //가운데정렬, 얇은테두리, 숫자 포맷 스타일
            CellStyle centerAlignThinStyle = workbook.createCellStyle();
            centerAlignThinStyle.setAlignment(HorizontalAlignment.CENTER);
            centerAlignThinStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerAlignThinStyle.setBorderTop(BorderStyle.THIN);
            centerAlignThinStyle.setBorderBottom(BorderStyle.THIN);
            centerAlignThinStyle.setBorderLeft(BorderStyle.THIN);
            centerAlignThinStyle.setBorderRight(BorderStyle.THIN);
            centerAlignThinStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리 상단 스타일
            CellStyle dataTopThickStyle = workbook.createCellStyle();
            dataTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataTopThickStyle.setBorderLeft(BorderStyle.THIN);
            dataTopThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 하단 스타일
            CellStyle dataBottomThickStyle = workbook.createCellStyle();
            dataBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            dataBottomThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 우측 스타일
            CellStyle dataRightThickStyle = workbook.createCellStyle();
            dataRightThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightThickStyle.setBorderTop(BorderStyle.THIN);
            dataRightThickStyle.setBorderBottom(BorderStyle.THIN);
            dataRightThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightThickStyle.setBorderRight(BorderStyle.MEDIUM);


            //가운데정렬, 굵은테두리 우상단 스타일
            CellStyle dataRightTopThickStyle = workbook.createCellStyle();
            dataRightTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataRightTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataRightTopThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightTopThickStyle.setBorderRight(BorderStyle.MEDIUM);

            //가운데정렬, 굵은테두리 우하단 스타일
            CellStyle dataRightBottomThickStyle = workbook.createCellStyle();
            dataRightBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataRightBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataRightBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataRightBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataRightBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            dataRightBottomThickStyle.setBorderRight(BorderStyle.MEDIUM);

            //가운데정렬, 굵은테두리 좌측 스타일
            CellStyle dataLeftThickStyle = workbook.createCellStyle();
            dataLeftThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftThickStyle.setBorderTop(BorderStyle.THIN);
            dataLeftThickStyle.setBorderBottom(BorderStyle.THIN);
            dataLeftThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 좌상단 스타일
            CellStyle dataLeftTopThickStyle = workbook.createCellStyle();
            dataLeftTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            dataLeftTopThickStyle.setBorderBottom(BorderStyle.THIN);
            dataLeftTopThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftTopThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리 좌하단 스타일
            CellStyle dataLeftBottomThickStyle = workbook.createCellStyle();
            dataLeftBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            dataLeftBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataLeftBottomThickStyle.setBorderTop(BorderStyle.THIN);
            dataLeftBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            dataLeftBottomThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            dataLeftBottomThickStyle.setBorderRight(BorderStyle.THIN);

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorThinStyle = workbook.createCellStyle();
            countColorThinStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorThinStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorThinStyle.setBorderTop(BorderStyle.THIN);
            countColorThinStyle.setBorderBottom(BorderStyle.THIN);
            countColorThinStyle.setBorderLeft(BorderStyle.THIN);
            countColorThinStyle.setBorderRight(BorderStyle.THIN);
            countColorThinStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorThinStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorThinStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorLeftThickStyle = workbook.createCellStyle();
            countColorLeftThickStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorLeftThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorLeftThickStyle.setBorderTop(BorderStyle.THIN);
            countColorLeftThickStyle.setBorderBottom(BorderStyle.THIN);
            countColorLeftThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            countColorLeftThickStyle.setBorderRight(BorderStyle.THIN);
            countColorLeftThickStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorLeftThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorLeftThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 누적횟수색상 스타일
            CellStyle countColorTopThickStyle = workbook.createCellStyle();
            countColorTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            countColorTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            countColorTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            countColorTopThickStyle.setBorderBottom(BorderStyle.THIN);
            countColorTopThickStyle.setBorderLeft(BorderStyle.THIN);
            countColorTopThickStyle.setBorderRight(BorderStyle.THIN);
            countColorTopThickStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            countColorTopThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            countColorTopThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));


            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorBottomThickStyle = workbook.createCellStyle();
            totalColorBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorBottomThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            totalColorBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorBottomThickStyle.setBorderRight(BorderStyle.THIN);
            totalColorBottomThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorRightThickStyle = workbook.createCellStyle();
            totalColorRightThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorRightThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorRightThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderBottom(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorRightThickStyle.setBorderRight(BorderStyle.MEDIUM);
            totalColorRightThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorRightThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorRightThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorLeftBottomThickStyle = workbook.createCellStyle();
            totalColorLeftBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorLeftBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorLeftBottomThickStyle.setBorderTop(BorderStyle.THIN);
            totalColorLeftBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            totalColorLeftBottomThickStyle.setBorderLeft(BorderStyle.MEDIUM);
            totalColorLeftBottomThickStyle.setBorderRight(BorderStyle.THIN);
            totalColorLeftBottomThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorLeftBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorLeftBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle allTotalColorRightBottomThickStyle = workbook.createCellStyle();
            allTotalColorRightBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            allTotalColorRightBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            allTotalColorRightBottomThickStyle.setBorderTop(BorderStyle.THIN);
            allTotalColorRightBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            allTotalColorRightBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            allTotalColorRightBottomThickStyle.setBorderRight(BorderStyle.MEDIUM);
            allTotalColorRightBottomThickStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            allTotalColorRightBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            allTotalColorRightBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle allTotalColorBottomThickStyle = workbook.createCellStyle();
            allTotalColorBottomThickStyle.setAlignment(HorizontalAlignment.CENTER);
            allTotalColorBottomThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            allTotalColorBottomThickStyle.setBorderTop(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setBorderBottom(BorderStyle.MEDIUM);
            allTotalColorBottomThickStyle.setBorderLeft(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setBorderRight(BorderStyle.THIN);
            allTotalColorBottomThickStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            allTotalColorBottomThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            allTotalColorBottomThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));



            //가운데정렬, 굵은테두리, 숫자 포맷, 합계색상 스타일
            CellStyle totalColorRightTopThickStyle = workbook.createCellStyle();
            totalColorRightTopThickStyle.setAlignment(HorizontalAlignment.CENTER);
            totalColorRightTopThickStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalColorRightTopThickStyle.setBorderTop(BorderStyle.MEDIUM);
            totalColorRightTopThickStyle.setBorderBottom(BorderStyle.THIN);
            totalColorRightTopThickStyle.setBorderLeft(BorderStyle.THIN);
            totalColorRightTopThickStyle.setBorderRight(BorderStyle.MEDIUM);
            totalColorRightTopThickStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            totalColorRightTopThickStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalColorRightTopThickStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));



            //헤더
            final String[] header = {"","", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                //첫번째와 마지막셀 넓이 조정
                if(i==0 || i==34){
                    //셀 폭 조정
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+3072);
                }else{
                    //셀 폭 조정
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024);
                }

                Cell cell = row.createCell(i);
                cell.setCellValue(header[i]);
            }

            Cell cell = null;

            //-----------------------------------

            row = sheet.createRow(1);

            cell = row.createCell(1);
            cell.setCellValue("회사");
            cell.setCellStyle(dataLeftTopThickStyle);

            cell = row.createCell(2);
            cell.setCellValue(companyName);
            cell.setCellStyle(dataTopThickStyle);

            cell = row.createCell(3);
            cell.setCellStyle(dataRightTopThickStyle);

            // 셀 합치기
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));

            //-----------------------------------

            row = sheet.createRow(2);

            cell = row.createCell(1);
            cell.setCellValue("매장");
            cell.setCellStyle(dataLeftThickStyle);

            cell = row.createCell(2);
            cell.setCellValue(storeName);
            cell.setCellStyle(centerAlignThinStyle);

            cell = row.createCell(3);
            cell.setCellStyle(dataRightThickStyle);

            // 셀 합치기
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));

            //-----------------------------------

            row = sheet.createRow(3);

            cell = row.createCell(1);
            cell.setCellValue("기준월");
            cell.setCellStyle(dataLeftBottomThickStyle);

            cell = row.createCell(2);
            cell.setCellValue(date);
            cell.setCellStyle(dataBottomThickStyle);

            cell = row.createCell(3);
            cell.setCellStyle(dataRightBottomThickStyle);

            // 셀 합치기
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));

            //-----------------------------------

            row = sheet.createRow(4);

            cell = row.createCell(1);
            cell.setCellValue("");


            //-----------------------------------


            row = sheet.createRow(5);

            final String[] dateList = {"날짜","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "누적횟수", "합계"};

            for (int i = 0; i < dateList.length; i++) {
                cell = row.createCell(i);

                if(i==0){
                    cell.setCellStyle(dataLeftTopThickStyle);
                    cell.setCellValue(dateList[i]);
                    continue;
                }else if(i==32){
                    cell.setCellStyle(countColorTopThickStyle);
                    cell.setCellValue(dateList[i]);
                    continue;
                }else if(i==33){
                    cell.setCellStyle(totalColorRightTopThickStyle);
                    cell.setCellValue(dateList[i]);
                    continue;
                }

                cell.setCellStyle(dataTopThickStyle);
                cell.setCellValue(dateList[i]);
            }


            //-----------------------------------

            int totalCount = 0;
            int totalPrice = 0;
            //바디
            for (int i = 0; i < billList.size(); i++) {
                row = sheet.createRow(i + 6);
                int count = 0;

                for(int j = 0; j < 32; j++) {
                    cell = row.createCell(j);
                    String value = billList.get(i)[j].toString();
                    try{
                        count += Integer.parseInt(value);
                    }catch (Exception ignored){

                    }
                    if(j==0){
                        cell.setCellValue(value);
                        cell.setCellStyle(dataLeftThickStyle);
                        continue;
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(centerAlignThinStyle);
                }


                //개인별 누적횟수
                cell = row.createCell(33);
                cell.setCellValue(count);
                cell.setCellStyle(countColorThinStyle);


                //개인별 합계
                cell = row.createCell(34);
                cell.setCellValue(count * price);
                cell.setCellStyle(totalColorRightThickStyle);
            }



            //-----------------------------------
            row = sheet.createRow(6+billList.size());
            Row totalRow = sheet.createRow(7+billList.size());

            cell = row.createCell(0);
            cell.setCellValue("누적횟수");
            cell.setCellStyle(countColorLeftThickStyle);

            cell = row.createCell(1);
            cell.setCellStyle(countColorThinStyle);

            cell = row.createCell(33);
            cell.setCellValue(totalCount);
            cell.setCellStyle(countColorThinStyle);

            cell = row.createCell(34);
            cell.setCellStyle(totalColorRightThickStyle);

            cell = totalRow.createCell(0);
            cell.setCellValue("합계");
            cell.setCellStyle(totalColorLeftBottomThickStyle);

            cell = totalRow.createCell(1);
            cell.setCellStyle(totalColorBottomThickStyle);

            cell = totalRow.createCell(33);
            cell.setCellValue("전체합계");
            cell.setCellStyle(allTotalColorBottomThickStyle);

            cell = totalRow.createCell(34);
            cell.setCellValue(totalPrice);
            cell.setCellStyle(allTotalColorRightBottomThickStyle);

            for(int columnIndex=2; columnIndex<33; columnIndex++){
                int dayTotalPrice = 0;
                int dayCount = 0;
                for(int rowIndex = 6; rowIndex < 6+billList.size(); rowIndex++){
                    Row rowTmp = sheet.getRow(rowIndex);
                    cell = rowTmp.getCell(columnIndex);
                    String value = cell.getStringCellValue();
                    int cnt = 0;
                    try {
                        cnt = Integer.parseInt(value);
                    } catch (Exception ignored) {}
                    dayTotalPrice += cnt * price;
                    dayCount += cnt;
                }
                //일별 누적횟수
                cell = row.createCell(columnIndex);
                cell.setCellValue(dayCount);
                cell.setCellStyle(countColorThinStyle);

                //일별 합계
                cell = totalRow.createCell(columnIndex);
                cell.setCellValue(dayTotalPrice);
                cell.setCellStyle(totalColorBottomThickStyle);
            }



            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "monthly_detail.xlsx");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 회사 - 모든 매장 월간 일별 장부 등록수 피벗 엑셀 파일 생성 (DB 직접 조회)
     * @param month 기준월(yyyy-MM)
     * @param companyId 회사 ID
     * @return 엑셀 파일 바이너리(ResponseEntity<byte[]>)
     */
    public ResponseEntity<byte[]> companyMonthlyAllStoreDetailExcel(String month, String companyId) {
        // 기준 날짜 리스트
        List<String> dayList = new ArrayList<>();
        for (int i = 1; i < 32; i++) {
            String tmp = String.format("%02d", i);
            dayList.add(month + tmp);
        }
        // 쿼리문 생성
        StringBuilder sql = new StringBuilder();
        sql.append("select store_name,");
        for (int i = 0; i < dayList.size(); i++) {
            sql.append(" MAX(CASE WHEN date = '").append(dayList.get(i)).append("' THEN row_count ELSE 0 END) AS '").append(String.format("%02d", i+1)).append("',");
        }
        sql.append(" SUM(row_count) as total ");
        sql.append("from (SELECT store_name, date, COUNT(*) AS row_count FROM bill WHERE company_id = '")
           .append(companyId).append("' AND date LIKE '").append(month).append("%'")
           .append(" GROUP BY store_name, date) as subquery group by store_name;");
        // 쿼리 실행
        List<Object[]> billList;
        try {
            jakarta.persistence.Query result = em.createNativeQuery(sql.toString());
            billList = result.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("DB 조회 오류", e);
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("all_store_monthly_detail");
            CellStyle centerAlign = workbook.createCellStyle();
            centerAlign.setAlignment(HorizontalAlignment.CENTER);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);
            centerAlign.setBorderTop(BorderStyle.THIN);
            centerAlign.setBorderBottom(BorderStyle.THIN);
            centerAlign.setBorderLeft(BorderStyle.THIN);
            centerAlign.setBorderRight(BorderStyle.THIN);
            // 헤더
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("매장명");
            for (int i = 1; i <= 31; i++) {
                row.createCell(i).setCellValue(String.format("%02d", i));
            }
            row.createCell(32).setCellValue("누적합계");
            for (int i = 0; i <= 32; i++) {
                row.getCell(i).setCellStyle(centerAlign);
                sheet.setColumnWidth(i, 256*10);
            }
            // 데이터
            for (int i = 0; i < billList.size(); i++) {
                Object[] arr = billList.get(i);
                Row dataRow = sheet.createRow(i+1);
                for (int j = 0; j < arr.length; j++) {
                    Cell cell = dataRow.createCell(j);
                    cell.setCellStyle(centerAlign);
                    cell.setCellValue(arr[j] != null ? arr[j].toString() : "");
                }
            }
            // 엑셀 파일로 출력
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "all_store_monthly_detail.xlsx");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(baos.size())
                    .body(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
