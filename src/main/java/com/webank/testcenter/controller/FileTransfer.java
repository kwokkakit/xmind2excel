package com.webank.testcenter.controller;

import com.alibaba.fastjson.JSON;
import com.webank.testcenter.model.ExcelHandler;
import com.webank.testcenter.model.XmindHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.xmind.core.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.webank.testcenter.controller
 * @Author: huahuaxu
 * @CreateDate: 2018/5/29
 * @Description:
 * @History:
 */

@Controller
public class FileTransfer {

    //public static String savePath = "D:\\00Work\\upload";
    public static String savePath = "/tmp/xmind2xls";
    private org.slf4j.Logger logger=LoggerFactory.getLogger(getClass());

    /**
     * 处理xmind文件上传的方法
     * @param file 上传的xmind文件
     * @param request 请求
     * @return 返回解析结果，如果文件格式或者文件保存出错status返回F,status=S是成功的
     */
    @RequestMapping(value = "upload")
    @ResponseBody
    public String  dealFileUpload(@RequestParam(value = "file") CommonsMultipartFile file,HttpServletRequest request)  {
        //@RequestParam(value="file")与upload.jsp里面的name="file"标签对应，该标签里提交的是file类型内容
        logger.info("run upload");
        logger.info("选择的转换模板是："+request.getParameter("selection"));
        String selection = request.getParameter("selection");
        Map<String,String> resultMap = new HashMap<>();
        boolean flag = true;
        //获取当前时间戳作为文件另存的后缀
        long timestamp = System.currentTimeMillis();
        //获取上传的文件名字
        String uploadFileName = file.getOriginalFilename();
        String fileType = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
        logger.info("上传的文件是："+uploadFileName);
        //上传的文件格式必须为xmind
        if (!fileType.equals("xmind")){
            flag = false;
        }
        if ((!selection.equals("1")) && (!selection.equals("2")))
        {
            //模板类型不为"1"且不为"2"
            flag = false;
        }
        //String savePath = request.getSession().getServletContext().getRealPath("/myXmind");
        //System.out.println("当前程序运行路径：" + request.getSession().getServletContext().getRealPath("/myXmind"));
        String saveFileName = uploadFileName.substring(0, uploadFileName.lastIndexOf(".")) + "_" + timestamp + "." + fileType;
        String uploadFile = savePath + "/" + saveFileName;
        File dirs = new File(savePath);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        //解析文件上传的IO流，同时完成将文件保存到服务器的操作
        File fileHandler = new File(dirs, saveFileName);
        try {
            file.transferTo(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }
        if (!flag){
            resultMap.put("status", "F");
            return JSON.toJSONString(resultMap);
        }
        //将xmind转换成excel
        //结果excel文件
        String caseFileName = uploadFileName.substring(0, uploadFileName.lastIndexOf("."))+"_"+timestamp+".xls";
        String caseResultFile = savePath + "/" + uploadFileName.substring(0, uploadFileName.lastIndexOf(".")) + "_" + timestamp + ".xls";
        ExcelHandler resultExcelHandler = new ExcelHandler(caseResultFile);
        resultExcelHandler.ExcelHandler();

        String uploadXmindFile = uploadFile;
        XmindHandler xmindHandler = new XmindHandler(uploadXmindFile);
        xmindHandler.xmind2Excel(resultExcelHandler,selection);

        //设置返回信息
        resultMap.put("status", "S");
        resultMap.put("filename", caseFileName);
        return JSON.toJSONString(resultMap);
    }

    /**
     * 处理下载文件的接口
     * @param request 请求
     * @param filename 下载的文件名
     * @return
     */
    @RequestMapping(value = "download")
    public ResponseEntity<byte[]> sampleDownload(HttpServletRequest request, @RequestParam(value = "filename") String filename){
        logger.info("run download");
        String fileType = request.getParameter("fileType");
        logger.info("文件类型为："+fileType);
        String absoluteFile;
        if (fileType.equals("sample")){
            //设置下载文件的绝对路径
            absoluteFile = request.getSession().getServletContext().getRealPath("/myXmind") + "/" + filename;
        }
        else {
            //设置下载文件的绝对路径
            absoluteFile = savePath + "/" + filename;
        }
        logger.info("下载文件为：" + absoluteFile);
        File file = new File(absoluteFile);
        //设置下载信息
        HttpHeaders headers = new HttpHeaders();
        String downloadFileName="";
        try {
            downloadFileName = new String(filename.getBytes("UTF-8"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.setContentDispositionFormData("attachment",downloadFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes = new byte[]{};
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(bytes,headers,HttpStatus.CREATED);
    }
}
