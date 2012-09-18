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
				//$('.result').html(data);
				$("#addApplicationModal").modal('hide');
			}
		});
		
		
	});

});