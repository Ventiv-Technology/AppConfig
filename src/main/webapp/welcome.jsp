<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link href="static/css/bootstrap.min.css" rel="stylesheet">
		<style type="text/css">
	      body {
	        padding-top: 60px;
	        padding-bottom: 40px;
	      }
	      .sidebar-nav {
	        padding: 9px 0;
	      }
	    </style>
    	<link href="static/css/bootstrap-responsive.min.css" rel="stylesheet">
    	
		<script src="static/js/jquery-1.8.1.min.js"></script>
		<script src="static/js/bootstrap.min.js"></script>
		<script>
			$(function() {
				$('.tooltip-holder').tooltip();
			});
		</script>
		<title>Application Configuration</title>
	</head>
	<body>
		<div class="navbar navbar-fixed-top">
	      <div class="navbar-inner">
	        <div class="container-fluid">
	          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	          </a>
	          <a class="brand" href="<c:url value="/" />">Application Configuration</a>
	          <div class="nav-collapse collapse">
	            <ul class="nav">
	              <li class="active"><a href="<c:url value="/applications" />">Applications</a></li>
	              <li><a href="<c:url value="/environments" />">Environments</a></li>
	            </ul>
	            	<sec:authorize access="isAnonymous()">
			            <form class="navbar-form pull-right" action="j_spring_security_check" method="post">
			              <input class="span2" type="text" placeholder="Login" name="j_username">
			              <input class="span2" type="password" placeholder="Password" name="j_password">
			              <button type="submit" class="btn">Sign in</button>
			            </form>
		            </sec:authorize>
		            <sec:authorize access="isAuthenticated()">
		            	<p class="navbar-text pull-right">
		            		<a href="<c:url value="/j_spring_security_logout" />">Logout: </a> <sec:authentication property="principal.username" />
		            	</p>
	            	</sec:authorize>
	          </div><!--/.nav-collapse -->
	        </div>
	      </div>
	    </div>
	    
	    <div class="container-fluid">
      		<div class="row-fluid">
        		<div class="span3">
          			<div class="well sidebar-nav">
            			<ul class="nav nav-list">
              				<li class="nav-header">Applications</li>
              				<li class="active"><a href="#">iVOS</a></li>
              				<li><a href="#">Risk Console</a></li>
              				<li><a href="#">Site Management</a></li>
              			</ul>
              		</div>
              		
              		<div>
              			<button class="btn">Add Application</button>
              		</div>
              	</div><!-- End Sidebar -->
              	
              	<div class="span9">
              		<div class="tabbable tabs-top">
              			<ul class="nav nav-tabs">
              				<li><a href="#settings" data-toggle="tab">Settings</a></li>
              				<li class="active"><a href="#tab1" data-toggle="tab">Default</a></li>
              				<li><a href="#tab2" data-toggle="tab">Development</a></li>
              				<li class="pull-right"><a href="addNewEnvironment">Add +</a></li>
              			</ul>
              			
              			<div class="tab-content">
              				<div class="tab-pane active" id="tab1">
	              				<table class="table table-hover">
			              			<thead>
				              			<tr>
				              				<th>Key</th>
				              				<th>Value</th>
				              				<th width="30" class="tooltip-holder" rel="tooltip" data-placement="left" title="Indication if this value is inherited from a parent environment"><i class="icon-arrow-up"></i> ?</th>
				              			</tr>
				              		</thead>
				              		<tbody>
					              		<tr>
					              			<td>application.setting.1</td>
					              			<td>Hello</td>
					              			<td>&nbsp;</td>
					              		</tr>
					              		<tr>
					              			<td>application.setting.2</td>
					              			<td>World</td>
					              			<td>&nbsp;</td>
					              		</tr>
				              		</tbody>
								</table>
								
								<button class="btn">Add Property</button>
								<button class="btn btn-danger">Delete Environment</button>
              				</div>
              				
              				<div class="tab-pane" id="tab2">
              					<p>Inherits from <a href="#tab1" data-toggle="tab">Default</a></p>
              					<table class="table table-hover">
			              			<thead>
				              			<tr>
				              				<th>Key</th>
				              				<th>Value</th>
				              				<th width="30" class="tooltip-holder" rel="tooltip" data-placement="left" title="Indication if this value is inherited from a parent environment"><i class="icon-arrow-up"></i> ?</th>
				              			</tr>
				              		</thead>
				              		<tbody>
					              		<tr>
					              			<td>application.setting.1</td>
					              			<td>Hello</td>
					              			<td><i class="icon-arrow-up tooltip-holder" rel="tooltip" data-placement="left" title="Inherits from Default"></i></td>
					              		</tr>
					              		<tr>
					              			<td>application.setting.2</td>
					              			<td>World</td>
					              			<td><i class="icon-arrow-up tooltip-holder" rel="tooltip" data-placement="left" title="Inherits from Default"></i></td>
					              		</tr>
					              		<tr>
					              			<td>application.setting.3</td>
					              			<td>I'm New!</td>
					              			<td>&nbsp;</td>
					              		</tr>
				              		</tbody>
								</table>
								
								<button class="btn">Add Property</button>
								<button class="btn btn-danger">Delete Environment</button>
              				</div>
              				
              				<div class="tab-pane" id="settings">
              					<form>
              						<legend>iVOS Settings</legend>
              						<label>Application Name</label>
              						<input type="text" value="iVOS"/>
              						
              						<label>Application Owner</label>
              						<select>
              							<option>jcrygier</option>
              						</select>
              						
              						<label>---- Private Key ----</label>
              						<pre>jO1O1v2ftXMsawM90tnXwc6xhOAT1gDBC9S8DKeca..JZNUgYYwNS0dP2UK
tmyN+XqVcAKw4HqVmChXy5b5msu8eIq3uc2NqNVtR..2ksSLukP8pxXcHyb
+sEwvM4uf8qbnHAqwnOnP9+KV9vds6BaH1eRA4CHz..n+NVZlzBsTxTlS16
/Umr7wJzVrMqK5sDiSu4WuaaBdqMGfL5hLsTjcBFD..Da2iyQmSKuVD4lIZ
yPQqjHKT70kEuSz+vdKuAzoIGNCvgQxXyqKSSX7td..1r7GBbjlIT7xgo8B
LvNaqyvLW5qKCMfWSVJr7xnP1xUU3MVoahhUPxOKX..sEvVM+tkeSPh7GxF</pre>
              						
              						<label>---- Public Key ----</label>
              						<pre>7QQjk4VpzzxuHx9XKPnYMOE9p8EEJiAyMW+Ms6blh..t3P9GPUJ9aRaH7yl
uUwJ2JXIZu1us4oObAi2mAmSWBebKiWQYBzuNDryK..iNAcY/7kndVqcxV2
PCFMM9TwsiJq6r38+CfvdIkol7sQcPf4us1fpVJSc..EB9U7obrrgX6s2PG</pre>
              						<br>
              						<button type="submit" class="btn btn-primary">Save</button>
              						<button type="submit" class="btn btn-warning">Regenerate Keys</button>
              					</form>
              				</div>
              				
              			</div>
              		</div>
              	</div>
              	
			</div>
		</div>
		              
	</body>
</html>