<%--
  Created by IntelliJ IDEA.
  User: huahuaxu
  Date: 2018/5/29
  Time: 10:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>xmind转换成Excel案例</title>
</head>
<body>
<h3>请注意，上传的文件必须xmind文件</h3>
<form action="/upload.do" method="post" enctype="multipart/form-data" target="_blank">
    <fieldset>
        <legend>请选择需要转换成excel的xmind文件</legend>
        <input type="file" name="file" value="选择文件"><input type="submit" value="提交">
        <br>
        <a href="sampledownload?filename=sample.xmind" target="_blank">
            下载xmind样例
        </a>
    </fieldset>
</form>
</body>
</html>
