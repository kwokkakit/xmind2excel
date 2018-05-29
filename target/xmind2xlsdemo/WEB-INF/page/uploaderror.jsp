<%--
  Created by IntelliJ IDEA.
  User: huahuaxu
  Date: 2018/5/29
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>xmind转换成Excel案例</title>
</head>
<body>
<p style="font-weight:bold">上传的文件不是xmind文件，请上传xmind文件</p>
<form action="/upload.do" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>请选择需要转换成excel的xmind文件</legend>
        <input type="file" name="file"><input type="submit">
    </fieldset>
</form>
</body>
</html>
