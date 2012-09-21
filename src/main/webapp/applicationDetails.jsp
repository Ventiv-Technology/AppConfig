<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
    		var contextRoot = '<%= request.getContextPath() %>/';
    		var activeApplicationId = '${application.id}';
    		var activeApplicationName = '${application.name}';
    		var activeEnvironmentName = '';
    	</script>
    	
		<script src="<c:url value="/static/js/jquery-1.8.1.min.js" />"></script>
		<script src="<c:url value="/static/js/bootstrap.min.js" />"></script>
		<script src="<c:url value="/static/js/application.js" />" ></script>
		<title>Application Configuration</title>
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
              				<li class="nav-header">Applications</li>
              				<c:forEach items="${applicationList}" var="anApplication">
              					<li id="application-selector-${anApplication.id}"><a href="<c:url value="/application/${anApplication.name}"/>">${anApplication.name}</a></li>
              				</c:forEach>
              				<li class="divider"></li>
              				<li class="nav-header">${application.name} Environments</li>
              				<c:forEach items="${application.environments}" var="environment">
             					<li class="application-environment" applicationName="${application.name}" environmentName="${environment.name}"><a href="#">${environment.name}</a></li>
             				</c:forEach>
              			</ul>
              		</div>
              		
              		<a href="#addApplicationModal" role="button" class="btn" data-toggle="modal">+ App</a>
              		<a href="#addEnvironmentModal" role="button" class="btn" data-toggle="modal">+ Env</a>
              	</div><!-- End Sidebar -->
              	
              	<div class="span10" id="application-contents">
              		<form>
						<legend>${application.name} Settings</legend>
						<label>Application Name</label>
						<input type="text" value="${application.name}" name="name"/>
						<br>
					
						<button type="submit" class="btn btn-primary">Submit</button>
					</form>
              	</div>
              	
			</div>
		</div>
		
		<!-- Add application modal dialog -->
		<div class="modal hide fadein" id="addApplicationModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel">Add Application</h3>
		  </div>
		  <div class="modal-body">
		    <form>
		    	<label>Application Name</label>
		    	<input id="addApplicationName" type="text" />
		    </form>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		    <button class="btn btn-primary">Save changes</button>
		  </div>
		</div>
		
		<!-- Add environment modal dialog -->
		<div class="modal hide fadein" id="addEnvironmentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel">Add Environment</h3>
		  </div>
		  <div class="modal-body">
		    <form>
		    	<input type="hidden" name="applicationName" id="applicationName" value="${application.name}"/>
		    	<label>Environment Name</label>
		    	<input id="addEnvironmentName" type="text" />
		    	
		    	<label>Extends</label>
		    	<select name="environment-parent" id="environment-parent">
		    		<c:forEach items="${application.environments}" var="environment">
						<option value="${environment.id}">${environment.name}</option>
					</c:forEach>
		    	</select>
		    </form>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		    <button class="btn btn-primary">Save changes</button>
		  </div>
		</div>
		
		<!-- Confirm Change Keys Dialog -->
		<div class="modal hide fadein" id="confirmChangeKeys" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel">Confirm Key Change</h3>
		  </div>
		  <div class="modal-body">
		    <p>WARNING: Changing the keys will re-encrypt all encrypted values in the system, but if these have been exported to a
		    file system for a running application, they will no longer work unless the private key is stored along side.  Please
		    ensure you really know what you're doing before finishing.</p>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		    <button class="btn btn-warning">Change Keys</button>
		  </div>
		</div>
		
	</body>
</html>