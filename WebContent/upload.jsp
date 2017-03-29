<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload the Image</title>
</head>
<body>

<form method="post" action="save" enctype="multipart/form-data">
<p>
We are learning here file upload using 4 ways -
1.  JSPs
2.	Servlets
3.  Struts
4.  Springs
<br>
create table storage (
	id int,
	name varchar(255),
    file blob
);
<br>

</p>
<label>ID:</label>
<input type="text" name="id"> <br>
<label>Name:</label>
<input type="text" name="name"> <br>
<label>File:</label>
<input type="file" name="myFile"/>
<br>
<input type="submit" value="Save">
</form>

</body>
</html>