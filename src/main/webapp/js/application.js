$(function() {
	$('.tooltip-holder').tooltip();
	
	// Handle Adding an Application
	$('#addApplicationModal .btn-primary').click(function() {
		var applicationName = $("#addApplicationName").val();
		$.ajax({
			url: contextRoot + 'application/' + applicationName,
			type: "PUT",
			dataType: "JSON",
			error: handleAjaxError,
			success: function(data) {
				window.location.href = contextRoot + 'application/' + applicationName;
			}
		});
	});
	
	// Handle Adding an Environment
	$('#addEnvironmentModal .btn-primary').click(function() {
		var environmentName = $("#addEnvironmentName").val();
		var applicationName = $("#applicationName").val();
		var parent = $("#environment-parent option:selected").val();
		
		$.ajax({
			url: contextRoot + 'application/' + applicationName + "/environment/" + environmentName + "?parentId=" + parent,
			type: "PUT",
			dataType: "JSON",
			error: handleAjaxError,
			success: function(data) {
				window.location.href = contextRoot + 'application/' + applicationName;
			}
		});
	});
	
	
	
	// Highlight the active application
	$("#application-selector-" + activeApplicationId).addClass("active");
	
	// Handle the environments selector
	$("#applicationList .application-environment").click(function() {
		loadNewEnvironment($(this).attr("environmentName"));
	});
	
	// Handle pressing escape to reset forms
	$("body").keyup(function() {
		if (event.which == 27) {
			$("#properties-body input").replaceWith(function() {
				return $(this).attr("originalValue");
			});
			resetPropertyRowClickHandlers();
		}
	});
	
	
});

/**
 * Loads new environment in the right side pane (ID = application-contents).  This will load via an environment name (for RESTful purposes)
 * for the current, active application.
 * 
 * @param environmentName
 */
function loadNewEnvironment(environmentName) {
	$('.tooltip-holder').tooltip('hide');
	$("#application-contents").load(contextRoot + "application/" + activeApplicationName + "/environment/" + environmentName, function() {		
		$("#applicationList .active").removeClass("active");
		$("#applicationList .application-environment[environmentName=" + environmentName + "]").addClass("active");
		activeEnvironmentName = environmentName;
		
		initializeNewEnvironment();			
	});
}

function initializeNewEnvironment() {
	$('.tooltip-holder').tooltip();
	attachAddPropertyListener();
	$(".property-delete").click(onPropertyDelete);
	$(".property-encrypt").click(onPropertyEncrypt);
	$(".property-decrypt").click(onPropertyDecrypt);
	resetPropertyRowClickHandlers();
	$("#confirmChangeKeys .btn-warning").click(regenerateKeysForCurrentEnvironment);
}

function resetPropertyRowClickHandlers() {
	$(".property-key").off('click');
	$(".property-value").off('click');
	$(".property-key").click(onPropertyRowClick);
	$(".property-value").click(onPropertyRowClick);
}


function attachAddPropertyListener() {
	$('#addProperty').click(function() {
		var newRow = $("<tr class='property-row'><td class='property-key'><input type='text' originalValue='__adding_new_property__' value=''/></td><td class='property-value'><input type='text' originalValue='' value=''/></td><td></td><td></td><td></td></tr>").appendTo("#properties-body");
		newRow.find(".property-key").keyup(onPropertySubmission);
		newRow.find(".property-value").keyup(onPropertySubmission);
	});
}

function onPropertyRowClick() {
	var propertyRow = $(this).parents(".property-row");
	var keyTableCell = propertyRow.find(".property-key");
	var valueTableCell = propertyRow.find(".property-value");
	
	keyTableCell.off('click');
	valueTableCell.off('click');
	
	var newKey = $("<input type='text' originalValue='" + $(keyTableCell).text() + "' value='" + $(keyTableCell).text() + "'/>");
	keyTableCell.html(newKey);
	
	var newValue = $("<input type='text' originalValue='" + $(valueTableCell).text() + "' value='" + $(valueTableCell).text() + "'/>");
	valueTableCell.html(newValue);

	newKey.keyup(onPropertySubmission);
	newValue.keyup(onPropertySubmission);
}

function onPropertySubmission(event) {
	var tableRow = $(this).parents(".property-row");
	var key = tableRow.find("input:first").val();
	var value = tableRow.find("input:last").val();
	var originalKey = tableRow.find("input:first").attr("originalValue");
	var originalValue = tableRow.find("input:last").attr("originalValue");
	
	if (event.which == 13) {
		$.ajax({
			url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/variable/" + originalKey + "?key=" + encodeURIComponent(key) + "&value=" + encodeURIComponent(value),
			type: "POST",
			dataType: "JSON",
			error: handleAjaxError,
			success: function(data) {
				tableRow.find("input:first").replaceWith(key);
				tableRow.find("input:last").replaceWith(value);
				
				resetPropertyRowClickHandlers();
			}			
		});
	}
}

function onPropertyDelete() {
	var tableRow = $(this).parents(".property-row");
	var originalKey = tableRow.find("input:first").attr("originalValue");
	if (originalKey == null)
		originalKey = tableRow.find(".property-key").text();
	
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/variable/" + originalKey,
		type: "DELETE",
		dataType: "JSON",
		error: handleAjaxError,
		success: function(data) {
			loadNewEnvironment(activeEnvironmentName);
		}
	});
}

function onPropertyEncrypt() {
	var tableRow = $(this).parents(".property-row");
	var originalKey = tableRow.find("input:first").attr("originalValue");
	if (originalKey == null)
		originalKey = tableRow.find(".property-key").text();
	
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/variable/" + originalKey + "/encrypt",
		type: "POST",
		dataType: "JSON",
		error: handleAjaxError,
		success: function(data) {
			loadNewEnvironment(activeEnvironmentName);
		}
	});
}

function onPropertyDecrypt() {
	var tableRow = $(this).parents(".property-row");
	var originalKey = tableRow.find("input:first").attr("originalValue");
	if (originalKey == null)
		originalKey = tableRow.find(".property-key").text();
	
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/variable/" + originalKey + "/decrypt",
		type: "POST",
		dataType: "JSON",
		error: handleAjaxError,
		success: function(data) {
			loadNewEnvironment(activeEnvironmentName);
		}
	});
}

function regenerateKeysForCurrentEnvironment() {
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/keys",
		type: "POST",
		dataType: "JSON",
		error: handleAjaxError,
		success: function(data) {
			$("#settings-privateKey").text(data.privateKey);
			$("#settings-publicKey").text(data.publicKey);
			
			$("#confirmChangeKeys").modal('hide');
		}
	});
}

function handleAjaxError(jqXHR, textStatus) {
	if (jqXHR.status == 403)
		showAlert("Unauthorized Action")
}


function showAlert(message) {
	$("#alert-holder").append("<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>×</button>" + message + "</div>")
}
