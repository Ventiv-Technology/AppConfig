$(function() {
	$('.tooltip-holder').tooltip();
	
	// Handle Adding an Application
	$('#addApplicationModal .btn-primary').click(function() {
		var applicationName = $("#addApplicationName").val();
		$.ajax({
			url: contextRoot + 'application/' + applicationName,
			type: "PUT",
			dataType: "JSON",
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
			success: function(data) {
				window.location.href = contextRoot + 'application/' + applicationName;
			}
		});
	});
	
	
	
	// Highlight the active application
	$("#application-selector-" + activeApplicationId).addClass("active");
	
	// Handle the environments selector
	$("#applicationList .application-environment").click(function() {
		var clickedEnvironment = $(this);
		var environmentName = clickedEnvironment.attr("environmentName");
		var applicationName =clickedEnvironment.attr("applicationName");
		
		$("#application-contents").load(contextRoot + "application/" + applicationName + "/environment/" + environmentName, function() {
			$("#applicationList .active").removeClass("active");
			clickedEnvironment.addClass("active");
			activeEnvironmentName = environmentName;
			
			initializeNewEnvironment();			
		});
	});
	
	// Handle pressing escape to reset forms
	$("body").keyup(function() {
		if (event.which == 27) {
			$(".property-row").off('click');
			$("#properties-body input").replaceWith(function() {
				return $(this).attr("originalValue");
			});
			$(".property-row").click(onPropertyRowClick);
		}
	});
});

function initializeNewEnvironment() {
	$('.tooltip-holder').tooltip();
	attachAddPropertyListener();
	$(".property-row").click(onPropertyRowClick);
}


function attachAddPropertyListener() {
	$('#addProperty').click(function() {
		var newRow = $("<tr class='property-row' propertyName='__adding_new_key__'><td></td><td></td><td></td></tr>").appendTo("#properties-body");
		newRow.click(onPropertyRowClick);
	});
}

function onPropertyRowClick() {
	$(this).off('click');
	var keyTableCell = $(this).children()[0];
	var valueTableCell = $(this).children()[1];
	
	var newKey = $("<td><input type='text' originalValue='" + $(keyTableCell).text() + "' value='" + $(keyTableCell).text() + "'/></td>");
	$(keyTableCell).replaceWith(newKey);
	
	var newValue = $("<td><input type='text' originalValue='" + $(valueTableCell).text() + "' value='" + $(valueTableCell).text() + "'/></td>");
	$(valueTableCell).replaceWith(newValue);

	newKey.find("input").keyup(onPropertySubmission);
	newValue.find("input").keyup(onPropertySubmission);
}

function onPropertySubmission(event) {
	var tableRow = $(this).parent().parent();
	var key = tableRow.find("input:first").val();
	var value = tableRow.find("input:last").val();
	var originalKey = tableRow.find("input:first").attr("originalValue");
	var originalValue = tableRow.find("input:last").attr("originalValue");
	
	if (event.which == 13) {
		$.ajax({
			url: contextRoot + 'application/' + activeApplicationName + "/environment/" + activeEnvironmentName + "/variable/" + originalKey + "?key=" + key + "&value=" + value,
			type: "POST",
			dataType: "JSON",
			success: function(data) {
				tableRow.find("input:first").replaceWith(key);
				tableRow.find("input:last").replaceWith(value);
				
				tableRow.click(onPropertyRowClick);
			}
		});
	}
}