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

<script type="text/javascript">
	var contextRoot = '<%=request.getContextPath()%>/';
	$(function() {
		$("#saveApplication").click(function() {
			$.ajax({
				url: contextRoot + 'application/' + $("#addApplicationName").val(),
				type: "PUT",
				dataType: "JSON",
				success: function(data) {
					window.location.href = contextRoot + 'application/' + $("#addApplicationName").val();
				}
			});
		});
	});
</script>

<script src="<c:url value="/static/js/jquery-1.8.1.min.js" />"></script>
<script src="<c:url value="/static/js/bootstrap.min.js" />"></script>
<script src="<c:url value="/static/js/application.js" />"></script>
<title>Application Configuration</title>
</head>
<body>
	<jsp:include page="includes/header.jsp" />

	<div class="container-fluid">
		<div class="" id="addApplicationModal" >
			<form action="<c:url value="/application"/>" method="PUT">
				<legend>Add Application</legend>
				<label>Application Name</label> 
				<input id="addApplicationName" type="text" />
				
				<br>
				
				<a href="#" class="btn btn-primary" id="saveApplication" >Save</a>
			</form>

		</div>
	</div>


</body>
</html>