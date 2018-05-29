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
<p style="font-weight:bold">请注意，上传的文件必须xmind文件</p>
<form action="/upload.do" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>请选择需要转换成excel的xmind文件</legend>
        <input type="file" name="file"><input type="submit">
    </fieldset>
</form>
</body>
</html>
