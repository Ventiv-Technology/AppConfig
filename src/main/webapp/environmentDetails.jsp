<%@page import="org.aon.esolutions.appconfig.model.Environment"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="tabbable tabs-top">
	<ul class="nav nav-tabs">
		<li><a href="#settings" data-toggle="tab">Settings</a></li>
		<li class="active"><a href="#properties" data-toggle="tab" id="environmentTab">${environment.name}</a></li>
	</ul>

	<div class="tab-content">
		<div class="tab-pane active" id="properties">
			<c:if test="${environment.parent != null}">
				<p>Inherits From ${environment.parent.name}</p>
			</c:if>
			<table class="table table-hover">
				<thead>
					<tr>
						<th width="46%">Key</th>
						<th width="46%">Value</th>
						<th width="4%" class="tooltip-holder" rel="tooltip" data-placement="left" title="Indicates if this property is overridden or inherited"><i class="icon-info-sign"></i></th>
						<th width="4%" class="tooltip-holder" rel="tooltip" data-placement="left" title="Delete this property (if overridden, you will revert)"><i class="icon-remove-circle"></i></th>
					</tr>
				</thead>
				<tbody id="properties-body">
					<c:forEach items="${allVariables}" var="aVariable">
						<tr class="property-row">
							<td>${aVariable.key}</td>
							<td>${aVariable.value}</td>
							<td>
								<c:if test="${aVariable.inheritedFrom.name != null}">
									<i class="icon-arrow-up tooltip-holder" rel="tooltip" data-placement="left" title="Inherits from ${aVariable.inheritedFrom.name}"></i>
									</td><td></td>
								</c:if>
								<c:if test="${aVariable.overrides.name != null}">
									<i class="icon-share-alt tooltip-holder" rel="tooltip" data-placement="left" title="Overrides original value '${aVariable.overrideValue}' from ${aVariable.overrides.name}"></i>
									</td><td width="4%" class="tooltip-holder" rel="tooltip" data-placement="left" title="Reverts property to original value from ${aVariable.overrides.name}: ${aVariable.overrideValue}"><i class="icon-remove-circle"></i>
								</c:if>
								<c:if test="${aVariable.overrides.name == null && aVariable.inheritedFrom.name == null}">
									</td><td width="4%" class="tooltip-holder" rel="tooltip" data-placement="left" title="Removes property completely"><i class="icon-remove-circle"></i>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<button class="btn" id="addProperty">Add Property</button>
			<button class="btn btn-danger">Delete Environment</button>
		</div>

		<div class="tab-pane" id="settings">
			<form>
				<legend id="settings-label">${environment.name} Settings</legend>
				<label>Environment Name</label> 
				<input type="text" name="name" id="environment-name" value="${environment.name}" />
				<label>Environment Owner</label>
				<select>
					<option>jcrygier</option>
				</select>
				
				<label>---- Private Key ----</label>
				<pre id="settings-privateKey"></pre>

				<label>---- Public Key ----</label>
				<pre id="settings-publicKey"></pre>
				<br>
				<button type="submit" class="btn btn-primary">Save</button>
				<button type="submit" class="btn btn-warning">Regenerate Keys</button>
			</form>
		</div>
	</div>
</div>