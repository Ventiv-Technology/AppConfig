$(function() {
	$('.tooltip-holder').tooltip();
	
	// Handle Adding an Application
	$("#addApplicationModal form").ajaxForm({
		dataType: 'JSON', 
		error: handleAjaxError,
		resetForm: true,
		beforeSubmit: function(dataArray, form, options) {
			this.url = this.url + dataArray[0].value;
			return checkName(dataArray[0].value);
		},
		success: function() {
			window.location.href = contextRoot + 'application/' + activeApplicationName;
		}
	});
	
	$("#application-contents form").ajaxForm({
		dataType: 'JSON', 
		error: handleAjaxError,
		beforeSubmit: function(dataArray, form, options) {
			return checkName(dataArray[0].value);
		},
		success: function(responseXml) {
			window.location.href = contextRoot + 'application/' + responseXml.application.name;
		}
	});
	
	$("#addEnvironmentModal form").ajaxForm({
		dataType: 'JSON', 
		error: handleAjaxError,
		resetForm: true,
		beforeSubmit: function(dataArray, form, options) { 
			this.url = this.url + dataArray[0].value + "?parentId=" + dataArray[1].value;
			return checkName(dataArray[0].value);
		},
		success: function() {
			window.location.href = contextRoot + 'application/' + activeApplicationName;
		}
	});	
	
	$("#importEnvironment form").ajaxForm({
		dataType: 'JSON', 
		error: handleAjaxError,
		resetForm: true,
		beforeSubmit: function(dataArray, form, options) { 
			this.url = this.url + activeEnvironmentName + "/import";
			return true;
		},
		success: function() {
			$("#importEnvironment").modal('hide');
			loadNewEnvironment(activeEnvironmentName);
		}
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
			loadNewEnvironment(activeEnvironmentName);
		}
	});
	
	$("#deleteApplicationModal .btn-warning").click(deleteCurrentApplication);
});

/**
 * Loads new environment in the right side pane (ID = application-contents).  This will load via an environment name (for RESTful purposes)
 * for the current, active application.
 * 
 * @param environmentName
 */
function loadNewEnvironment(environmentName) {
	$('.tooltip-holder').tooltip('hide');
	$("#application-contents").load(contextRoot + "application/" + activeApplicationName + "/environment/" + environmentName, function(responseText, textStatus, jqXHR) {
		if (handleAjaxError(jqXHR, textStatus) == false) {
			return;
		}
		
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
	$("#confirmDelete .btn-warning").click(deleteCurrentEnvironment);
	setupEnvironmentAjaxForm();
	attachManualLists();
}

function resetPropertyRowClickHandlers() {
	$(".property-key").off('click');
	$(".property-value").off('click');
	$(".property-key").click(onPropertyRowClick);
	$(".property-value").click(onPropertyRowClick);
}

function setupEnvironmentAjaxForm() {
	$("#environmentDetails-form").ajaxForm({
		dataType: 'JSON', 
		error: handleAjaxError, 
		beforeSerialize : function() {
			$(".manual-list").find("select option").attr('selected', 'selected');
			return true;
		}, 
		beforeSubmit: function(dataArray) { 
			return checkName(dataArray[0].value); 
		},
		success: function() {
			showAlert(labelDataSaved, "success");
		}		
	});
}

function attachManualLists() {
	$(".manual-list").each(function() {
		var manualListContainer = $(this);
		var addingValueInput = manualListContainer.find("input");
		var currentValuesList = manualListContainer.find("select");
		
		manualListContainer.find(".add-manual-list").click(function() {
			if (addingValueInput.val().length > 0) {
				currentValuesList.append("<option value='" + addingValueInput.val() + "'>" + addingValueInput.val() + "</option>");
				addingValueInput.val("");
			}
		});
		
		manualListContainer.find(".remove-manual-list").click(function() {
			currentValuesList.find("option:selected").remove();
		});
	});
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
				loadNewEnvironment(activeEnvironmentName);
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

function deleteCurrentEnvironment() {
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName,
		type: "DELETE",
		dataType: "JSON",
		error: function(jqXHR, textStatus) {
			$("#confirmDelete").modal('hide');
			handleAjaxError(jqXHR, textStatus)
		},
		success: function(data) {
			window.location.href = contextRoot + 'application/' + activeApplicationName;
		}
	});
}

function deleteCurrentApplication() {
	$.ajax({
		url: contextRoot + 'application/' + activeApplicationName,
		type: "DELETE",
		dataType: "JSON",
		error: function(jqXHR, textStatus) {
			$("#deleteApplicationModal").modal('hide');
			handleAjaxError(jqXHR, textStatus)
		},
		success: function(data) {
			window.location.href = contextRoot;
		}
	});
}

function handleAjaxError(jqXHR, textStatus) {
	if (jqXHR.status == 403) {
		showAlert("Unauthorized Action")
		return false;
	} else if (jqXHR.status == 500) {
		showAlert(jqXHR.responseText.trim())
	}
	
	return true;
}

function showAlert(message, alertType) {
	var alertHolder = $(".modal.in .alert-holder")[0];
	alertHolder = typeof alertHolder !== 'undefined' ? $(alertHolder) : $("#alert-holder");
	alertType = typeof alertType !== 'undefined' ? alertType : "error";
	
	alertHolder.empty();
	alertHolder.append("<div class='alert alert-" + alertType + "'><button type='button' class='close' data-dismiss='alert'>×</button>" + message + "</div>")
}

function checkName(name) {
	var validNameRegex = /^[\d\w]*$/
	if (validNameRegex.test(name)) {
		return true;
	} else {
		showAlert(labelInvalidName);
		return false;
	}
	
}