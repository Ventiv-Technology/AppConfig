<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="<c:url value="/static/css/bootstrap.min.css" />" rel="stylesheet">
<style type="text/css">
body {
	padding-top: 60px;
	padding-bottom: 40px;
}

.sidebar-nav {
	padding: 9px 0;
}
</style>
<link href="<c:url value="/static/css/bootstrap-responsive.min.css" />" rel="stylesheet">

<script src="<c:url value="/static/js/jquery-1.8.1.min.js" />"></script>
<script src="<c:url value="/static/js/jquery.form.js" />"></script>
<script src="<c:url value="/static/js/bootstrap.min.js" />"></script>
<script type="text/javascript">
	var contextRoot = '<%=request.getContextPath()%>/';
	
	$("#addApplicationModal form").ajaxForm({
		dataType: 'JSON', 
		error: function() {
			alert("Error Saving, Please Check Logs.");
		},
		resetForm: true,
		beforeSubmit: function(dataArray, form, options) { 
			this.url = this.url + $("#addApplicationModal #name").val();
			return true;
		},
		success: function(data) {
			window.location.href = this.url;
		}
	});	
</script>

<title>Application Configuration</title>
</head>
<body>
	<jsp:include page="includes/header.jsp" />

	<div class="container-fluid">
		<div class="" id="addApplicationModal" >
			<form action="<c:url value="/application/"/>" method="PUT">
				<legend>Add Application</legend>
				<label>Application Name</label> 
				<input id="name" type="text" />
				
				<br>
				
				<button type="submit" class="btn btn-primary" id="save" >Save</a>
			</form>

		</div>
	</div>


</body>
</html>