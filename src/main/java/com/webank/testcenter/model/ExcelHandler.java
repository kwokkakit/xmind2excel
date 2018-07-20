package com.webank.testcenter.model;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;

/**
 * @Package: com.webank.testcenter.model
 * @Author: huahuaxu
 * @CreateDate: 2018/5/29
 * @Description:
 * @History:
 */
public class ExcelHandler {
    private String fileName; //文件名，需要绝对路径
    /**
     * @Description: 类构造函数
     * @param fileName:文件名，需要绝对路径
     */
    public ExcelHandler(String fileName){
        this.fileName = fileName;
    }

    /**
     * @Description: 获取类对象的文件名
     */
    public String getFileName(){
        return this.fileName;
    }

    /**
     * @Description: 根据文件名初始化一个测试案例的excel，且包含表头内容
     */
    public boolean ExcelHandler(){
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建sheet页
        HSSFSheet sheet = workbook.createSheet("cases");
        //创建表头
        HSSFRow row = sheet.createRow(0);
        String[] titleNameList = {"用例目录","用例名称","前置条件","用例步骤","预期结果","用例类型","用例状态","用例等级","创建人","需求ID"};
        for (int columnIndex=0;columnIndex<10;columnIndex++)
        {
            HSSFCell cell = row.createCell(columnIndex);
            cell.setCellValue(titleNameList[columnIndex]);
        }
        //创建excel文件
        File file = new File(this.fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @Description: 将案例附在案例excel的最后一行
     * @param casePath: 用例目录
     * @param caseName: 用例名称
     * @param caseCondition: 前置条件
     * @param caseStep: 用例步骤
     * @param caseResult: 预期结果
     */
    public void writeExcel(String casePath,String caseName,String caseCondition,String caseStep,String caseResult) throws IOException, InvalidFormatException {
        FileInputStream fileInputStream = new FileInputStream(this.fileName);
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fileInputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);
        //获取sheet页
        HSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        //RowNum从0开始的
        //System.out.println("lastRowNum:"+lastRowNum);
        //在已有的行数上新增一行案例
        HSSFRow currentRow = sheet.createRow(lastRowNum+1);
        String[] caseInfoList = {casePath,caseName,caseCondition,caseStep,caseResult};
        //把该行案例写进最后一行的每个cell里
        for (int columnIndex=0;columnIndex<5;columnIndex++)
        {
            HSSFCell cell = currentRow.createCell(columnIndex);
            cell.setCellValue(caseInfoList[columnIndex]);
        }
        //写Excel
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(this.fileName));
            outputStream.flush();
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
