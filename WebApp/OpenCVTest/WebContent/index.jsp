<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
<img src="/OpenCVTest/desk/result.jpg" />
<h1>JSP Page - 2 Forms Test</h1>
<h2>GET FORM</h2>
<form name="getform" action="Opencv" method="get">
<input type="text" name="form_field_get" />

<input type="submit" value="SUBMIT" />
</form>


</p>
<h2>POST FORM</h2>
<form name="postform" action="Opencv" method="post">
<input type="text" name="form_field_post" />

<input type="submit" value="SUBMIT" />
</form> 
</body>
</html>