<%-- 
    Document   : login
    Created on : May 31, 2017, 8:46:29 AM
    Author     : Karlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <h1>Login</h1>
        <form action="j_security_check" method=post>
                <div id="loginBox">
                    <p><strong>Korisničko ime:</strong>
                        <input type="text" size="20" name="j_username"></p>

                    <p><strong>Lozinka:</strong>
                        <input type="password" size="20" name="j_password"></p>

                    <p><input type="submit" value="submit"></p>
                </div>
            </form>
    </body>
</html>
