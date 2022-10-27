'use strict';
const HTTP_BAD_REQUEST = 400;
$('.delete').on('click', function() {
	let id = this.id;
	$.ajax({
		type: "DELETE",
		url: "../posts/" + id,
		//data: JSON.stringify(newItem),
		//dataType: 'json',
		//contentType: 'application/json'
	}).done(function (data, textStatus, jqXHR){
		location.href = "../posts";
  }).fail(function (jqXHR, textStatus, errorThrown) {
    console.log(jqXHR.status);
    console.log(textStatus);
    console.log(errorThrown);
		console.log(jqXHR.responseText)
		location.href = "../errormessage?message=" + "there was an error";
  });
});

$('.edit').on('click', function() {
	let id = this.id;
	location.href = "../edit-post?id=" + id; 
});