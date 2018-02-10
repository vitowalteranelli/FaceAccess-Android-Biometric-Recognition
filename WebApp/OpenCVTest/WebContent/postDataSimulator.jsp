<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
    <HEAD>
        <TITLE>Login using jsp</TITLE>
    </HEAD>
 
    <BODY>
        <H1>LOGIN FORM</H1>


                    <form action="op" method="post">
                <table>
                    <tr>
                        <td> Username  : </td><td> <input type="text" name="username" size=15 /> </td> 
                    </tr>
                    <tr>
                        <td> Password  : </td><td> <input type="password" name="password" size=15 /> </td> 
                    </tr>
                </table>
                <input type="submit" value="op" />
            </form>
           
         
    </BODY>
</HTML>