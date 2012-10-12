<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
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
    	
    	<!--[if lt IE 9]>
      		<script src="<c:url value="/static/js/html5.js" />"></script>
    	<![endif]-->
    	
    	<script type="text/javascript">
    		var contextRoot = '<%= request.getContextPath() %>/';
    		var activeApplicationId = '${application.id}';
    		var activeApplicationName = '${application.name}';
    		var activeEnvironmentName = '';
    		
    		// Labels
    		var labelDataSaved = '<s:message code="labels.common.dataSaved"/>';
    		var labelInvalidName = '<s:message code="labels.common.invalidName"/>';
    	</script>
    	
		<script src="<c:url value="/static/js/jquery-1.8.1.min.js" />"></script>
		<script src="<c:url value="/static/js/bootstrap.min.js" />"></script>
		<script src="<c:url value="/static/js/application.js" />" ></script>
		<script src="<c:url value="/static/js/jquery.form.js" />"></script> 
		<title><s:message code="labels.common.applicationConfiguration"/></title>
	</head>
	<body>
		<jsp:include page="includes/header.jsp" />
	    
	    <div class="container-fluid">
	    	<div class="row-fluid">
	    		<div class="span12" id="alert-holder"></div>
	    	</div>
	    	
      		<div class="row-fluid">
	
				<!-- Side Navigation - The Applications -->
        		<div class="span2">
          			<div class="well sidebar-nav">
            			<ul class="nav nav-list" id="applicationList">
              				<li class="nav-header"><s:message code="labels.common.applications"/></li>
              				<c:forEach items="${applicationList}" var="anApplication">
              					<li id="application-selector-${anApplication.id}"><a href="<c:url value="/application/${anApplication.name}"/>">${anApplication.name}</a></li>
              				</c:forEach>
              				<li class="divider"></li>
              				<li class="nav-header">${application.name} <s:message code="labels.common.environments"/></li>
              				<c:forEach items="${application.environments}" var="environment">
             					<li class="application-environment" applicationName="${application.name}" environmentName="${environment.name}"><a href="#">${environment.name}</a></li>
             				</c:forEach>
              			</ul>
              		</div>
              		
              		<a href="#addApplicationModal" role="button" class="btn" data-toggle="modal"><s:message code="labels.application.addApplicationShort"/></a>
              		<a href="#addEnvironmentModal" role="button" class="btn" data-toggle="modal"><s:message code="labels.application.addEnvironmentShort"/></a>
              	</div><!-- End Sidebar -->
              	
              	<div class="span10" id="application-contents">
              		<form action="<c:url value="/application/${application.name}"/>" method="POST">
						<legend>${application.name} <s:message code="labels.common.settings"/></legend>
						<label><s:message code="labels.application.applicationName"/></label>
						<input type="text" value="${application.name}" name="name"/>
						<br>
					
						<button type="submit" class="btn btn-primary"><s:message code="labels.common.save"/></button>
					</form>
              	</div>
              	
			</div>
		</div>
		
		<!-- Add application modal dialog -->
		<div class="modal hide fadein" id="addApplicationModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="alert-holder"></div>
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel"><s:message code="labels.application.addApplication"/></h3>
		  </div>
		  <form action="<c:url value="/application/"/>" method="PUT">
			  <div class="modal-body">
		    	<label>Application Name</label>
		    	<input id="name" name="name" type="text" />
			  </div>
			  <div class="modal-footer">
			    <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><s:message code="labels.common.close"/></a>
			    <button class="btn btn-primary" type="submit"><s:message code="labels.common.save"/></button>
			  </div>
		  </form>
		</div>
		
		<!-- Add environment modal dialog -->
		<div class="modal hide fadein" id="addEnvironmentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="alert-holder"></div>
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel"><s:message code="labels.application.addEnvironment"/></h3>
		  </div>
		  <form action="<c:url value="/application/${application.name}/environment/"/>" method="PUT">
		  	<div class="modal-body">
		    	<label><s:message code="labels.application.environmentName"/></label>
		    	<input id="name" name="name" type="text" />
		    	
		    	<label><s:message code="labels.common.inheritsFrom"/></label>
		    	<select name="parentId" id="parentId">
		    		<c:forEach items="${application.environments}" var="environment">
						<option value="${environment.id}">${environment.name}</option>
					</c:forEach>
		    	</select>
			  </div>
			  <div class="modal-footer">
			    <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><s:message code="labels.common.close"/></a>
			    <button class="btn btn-primary" type="submit"><s:message code="labels.common.save"/></button>
			  </div>
		  </form>
		</div>
		
		<!-- Confirm Change Keys Dialog -->
		<div class="modal hide fadein" id="confirmChangeKeys" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel"><s:message code="labels.application.confirmKeyChange"/></h3>
		  </div>
		  <div class="modal-body">
		    <p><s:message code="labels.application.keyChangeWarning"/></p>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true"><s:message code="labels.common.close"/></button>
		    <button class="btn btn-warning"><s:message code="labels.application.changeKeys"/></button>
		  </div>
		</div>
		
		<!-- Confirm Delete Environment Modal -->
		<div class="modal hide fadein" id="confirmDelete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel"><s:message code="labels.common.confirmDelete"/></h3>
		  </div>
		  <div class="modal-body">
		    <p><s:message code="labels.application.deleteEnvironmentWarning"/></p>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true"><s:message code="labels.common.close"/></button>
		    <button class="btn btn-warning"><s:message code="labels.common.delete"/></button>
		  </div>
		</div>
		
	</body>
</html>