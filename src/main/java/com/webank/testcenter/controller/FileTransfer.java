package com.webank.testcenter.controller;

import com.webank.testcenter.model.ExcelHandler;
import com.webank.testcenter.model.XmindHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.xmind.core.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Package: com.webank.testcenter.controller
 * @Author: huahuaxu
 * @CreateDate: 2018/5/29
 * @Description:
 * @History:
 */

@Controller
public class FileTransfer {
    @RequestMapping(value = "/")
    public String redirect()
    {
        System.out.println("run redirect");
        return "redirect:xmind2xls";//将主页重定向到上传xmind页面
    }

    @RequestMapping(value = "xmind2xls")
    public ModelAndView homePage()
    {
        System.out.println("run xmind2xls");
        ModelAndView mav = new ModelAndView("xmind2xls");
        //返回指就是一个逻辑视图名, 它遵循springmvc-web.xml视图解析器去寻找物理视图名
        //即去/WEB-INF/page/菜单下找.jsp结尾的param文件
        return mav;
    }

    @RequestMapping(value = "upload.do")
    public ModelAndView dealFileUpload(@RequestParam(value = "file") CommonsMultipartFile file,HttpServletRequest request)  {
        //@RequestParam(value="file")与upload.jsp里面的name="file"标签对应，该标签里提交的是file类型内容
        System.out.println("run upload.do");
        //获取当前时间戳作为文件另存的后缀
        long timestamp = System.currentTimeMillis();
        //获取上传的文件名字
        String uploadFileName = file.getOriginalFilename();
        String fileType = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
        System.out.println("上传的文件格式："+fileType);
        //上传的文件格式必须为xmind
        if (!fileType.equals("xmind")){
            //如果上传的文件后缀不为xmind则返回上传文件格式错误的页面
            ModelAndView mav = new ModelAndView("uploaderror");
            return mav;
        }

        String savePath = request.getSession().getServletContext().getRealPath("/myXmind");
        System.out.println("当前程序运行路径：" + request.getSession().getServletContext().getRealPath("/myXmind"));
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
        }
        //将xmind转换成excel
        //结果excel文件
        String caseFileName = uploadFileName.substring(0, uploadFileName.lastIndexOf("."))+"_"+timestamp+".xls";
        String caseResultFile = savePath + "/" + uploadFileName.substring(0, uploadFileName.lastIndexOf(".")) + "_" + timestamp + ".xls";
        ExcelHandler resultExcelHandler = new ExcelHandler(caseResultFile);
        resultExcelHandler.ExcelHandler();

        String uploadXmindFile = uploadFile;
        System.out.println("上传的xmind文件是："+uploadXmindFile);

        XmindHandler xmindHandler = new XmindHandler(uploadXmindFile);
        xmindHandler.xmind2Excel(resultExcelHandler);

        //设置返回页面和返回信息
        ModelAndView mav = new ModelAndView("download");
        mav.addObject("filename", caseFileName);
        return mav;
    }

    @RequestMapping(value = "sampledownload")
    public ResponseEntity<byte[]> sampleDownload(HttpServletRequest request, @RequestParam(value = "filename") String filename){
        System.out.println("run sample download.do");

        //设置下载文件的绝对路径
        String absoluteFile = request.getSession().getServletContext().getRealPath("/myXmind") + "/" + filename;
        File file = new File(absoluteFile);
        //设置下载信息
        HttpHeaders headers = new HttpHeaders();
        String downloadFielName="";
        try {
            downloadFielName = new String(filename.getBytes("UTF-8"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.setContentDispositionFormData("attachment",downloadFielName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes = new byte[]{};
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(bytes,headers,HttpStatus.CREATED);
    }

    @RequestMapping(value = "download.do")
    public ResponseEntity<byte[]> download(HttpServletRequest request, @RequestParam(value = "filename") String filename){
        System.out.println("run download.do");

        //设置下载文件的绝对路径
        String absoluteFile = request.getSession().getServletContext().getRealPath("/myXmind") + "/" + filename;
        File file = new File(absoluteFile);
        //设置下载信息
        HttpHeaders headers = new HttpHeaders();
        String downloadFielName="";
        try {
            downloadFielName = new String(filename.getBytes("UTF-8"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.setContentDispositionFormData("attachment",downloadFielName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes = new byte[]{};
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(bytes,headers,HttpStatus.CREATED);
    }
}
