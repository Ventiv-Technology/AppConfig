<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</a> <a class="brand" href="<c:url value="/" />"><s:message code="labels.common.applicationConfiguration"/></a>
			<div class="nav-collapse collapse">
				<sec:authorize access="isAnonymous()">
					<form class="navbar-form pull-right" action="<c:url value="/j_spring_security_check" />" method="post">
						<input class="span2" type="text" placeholder="<s:message code="labels.common.login"/>" name="j_username"> <input class="span2" type="password" placeholder="<s:message code="labels.common.password"/>" name="j_password">
						<button type="submit" class="btn"><s:message code="labels.common.signIn"/></button>
					</form>
				</sec:authorize>
				<sec:authorize access="isAuthenticated()">
					<p class="navbar-text pull-right">
						<a href="<c:url value="/j_spring_security_logout" />"><s:message code="labels.common.logout"/>: </a>
						<sec:authentication property="principal.username" />
					</p>
				</sec:authorize>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
</div>