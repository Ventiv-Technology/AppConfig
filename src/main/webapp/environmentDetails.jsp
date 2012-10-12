<%@page import="org.aon.esolutions.appconfig.model.Environment"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="tabbable tabs-top">
	<ul class="nav nav-tabs">
		<li><a href="#settings" data-toggle="tab"><s:message code="labels.common.settings"/></a></li>
		<li class="active"><a href="#properties" data-toggle="tab" id="environmentTab">${environment.name}</a></li>
	</ul>

	<div class="tab-content">
		<div class="tab-pane active" id="properties">
			<c:if test="${environment.parent != null}">
				<p><s:message code="labels.common.inheritsFrom"/> ${environment.parent.name}</p>
			</c:if>
			<table class="table table-hover">
				<thead>
					<tr>
						<th width="30%">Key</th>
						<th width="66%">Value</th>
						<th width="2%" class="tooltip-holder" rel="tooltip" data-placement="left" title='<s:message code="labels.environment.inheritTooltip"/>'><i class="icon-info-sign"></i></th>
						<th width="2%" class="tooltip-holder" rel="tooltip" data-placement="left" title='<s:message code="labels.environment.deleteTooltip"/>'><i class="icon-trash"></i></th>
						<th width="2%" class="tooltip-holder" rel="tooltip" data-placement="left" title='<s:message code="labels.environment.encryptTooltip"/>'><i class="icon-lock"></i></th>
					</tr>
				</thead>
				<tbody id="properties-body">
					<c:forEach items="${allVariables}" var="aVariable">
						<tr class="property-row">
							<td class="property-key"><c:out value="${aVariable.key}"/></td>
							<td class="property-value"><c:out value="${aVariable.valueDisplay}"/></td>
							
							<%-- Inherited / Overrides Column (Can't be both)  --%>
							<c:if test="${aVariable.inherited}"> <%-- Inherited --%>
								<td><i class="icon-arrow-up tooltip-holder" rel="tooltip" data-placement="left" title="<s:message code="labels.common.inheritsFrom"/> <c:out value="${aVariable.inheritedFrom.name}"/>"></i></td>
							</c:if>
							<c:if test="${aVariable.overridden}"> <%-- Overridden --%>
								<td><i class="icon-share-alt tooltip-holder" rel="tooltip" data-placement="left" title="<s:message code="labels.environment.overrideTooltip" arguments="${fn:escapeXml(aVariable.overrideValueDisplay)},${fn:escapeXml(aVariable.overrides.name)}"/>"></i></td>
							</c:if>
							<c:if test="${aVariable.ownedProperty}"> <%-- Neither Inherited or Overridden - so it's our own  --%>
								<td></td>
							</c:if>
							
							<%-- Delete Column --%>
							<c:if test="${aVariable.overridden}">
								<td class="tooltip-holder property-delete" rel="tooltip" data-placement="left" title="<s:message code="labels.environment.deleteOverriddenTooltip" arguments="${fn:escapeXml(aVariable.overrides.name)},${fn:escapeXml(aVariable.overrideValueDisplay)}"/>"><i class="icon-trash"></i></td>
							</c:if>
							<c:if test="${aVariable.ownedProperty}">
								<td class="tooltip-holder property-delete" rel="tooltip" data-placement="left" title="<s:message code="labels.environment.deletePermanentlyTooltip"/> "><i class="icon-trash"></i></td>
							</c:if>
							<c:if test="${aVariable.inherited}">
								<td></td>
							</c:if>
							
							<%-- Encrypt Column --%>
							<c:if test="${aVariable.encrypted == false && aVariable.inherited == false}">
								<td class="tooltip-holder property-encrypt" rel="tooltip" data-placement="left" title="<s:message code="labels.environment.encryptTooltip"/>"><i class="icon-lock"></i></td>
							</c:if>
							<c:if test="${aVariable.encrypted && aVariable.inherited == false}">
								<td class="tooltip-holder property-decrypt" rel="tooltip" data-placement="left" title="<s:message code="labels.environment.decryptTooltip"/>"><i class="icon-ok-sign"></i></td>
							</c:if>
							<c:if test="${aVariable.inherited}">
								<td></td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<button class="btn" id="addProperty"><s:message code="labels.environment.addProperty"/></button>
			<a href="#confirmDelete" role="button" class="btn btn-danger" data-toggle="modal"><s:message code="labels.environment.deleteEnvironment"/></a>
			<a href="#importEnvironment" role="button" class="btn" data-toggle="modal"><s:message code="labels.environment.import"/></a>
		</div>

		<div class="tab-pane" id="settings">
			<form id="environmentDetails-form" action="<c:url value="/application/${applicationName}/environment/${environment.name}"/>" method="POST">
				<legend id="settings-label">${environment.name} <s:message code="labels.common.settings"/></legend>
				<label><s:message code="labels.environment.enironmentName"/></label> 
				<form:input path="environment.name"  />
				
				<div class="row-fluid">
					<div class="span3">
						<label><s:message code="labels.environment.permittedUsers"/></label>
						<form:select cssStyle="width: 100%" size="6" path="environment.permittedUsers" items="${availableUsers}" multiple="multiple" />
					</div>
					
					<div class="span3">
						<label><s:message code="labels.environment.permittedRoles"/></label>
						<form:select cssStyle="width: 100%" size="6" path="environment.permittedRoles" items="${availableRoles}" multiple="multiple" />
					</div>
					
					<div class="span3 manual-list">
						<label><s:message code="labels.environment.permittedMachines"/></label>
						<div class="input-append">
							<input type="text" style="width: 94%"/><a class="add-on add-manual-list" href="#"><i class="icon-plus"></i></a>
						</div>
						<div class="input-append">
							<form:select cssStyle="width: 100%" size="4" path="environment.permittedMachines" items="${environment.permittedMachines}" multiple="multiple" />
							<a class="add-on remove-manual-list" href="#"><i class="icon-trash"></i></a>
						</div>
					</div>
				</div>
				
				<label class="checkbox">
					<form:checkbox path="environment.visibleToAll"/> <s:message code="labels.environment.visibleToAll"/>
				</label>
				
				<label>---- <s:message code="labels.environment.privateKey"/> ----</label>
				<pre id="settings-privateKey">${environment.privateKeyHolder.privateKey}</pre>

				<label>---- <s:message code="labels.environment.publicKey"/> ----</label>
				<pre id="settings-publicKey">${environment.publicKey}</pre>
				<br>
				<button type="submit" class="btn btn-primary"><s:message code="labels.common.save"/></button>
				<a href="#confirmChangeKeys" role="button" class="btn btn-warning" data-toggle="modal"><s:message code="labels.environment.regenerateKeys"/></a>
			</form>
		</div>
	</div>
</div>