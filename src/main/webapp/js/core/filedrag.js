(function(){

	var filedrag = {};

	app.filedrag = filedrag;

	//output information
	function Output(msg) {
		var m = jQuery("#messages");
		m.innerHTML = msg + m.innerHTML;
	};


	// file drag hover
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}


	// file selection
	function FileSelectHandler(e, callback) {

		// cancel event and hover styling
		FileDragHover(e);

		console.log("FileSelectHandler")

		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;

		console.log(files);

	
	}

	function getFileContent(file, callback) {

			var reader = new FileReader();
			
			reader.onload = function(e) {
				callback(e.target.result);
			}
			
			reader.readAsText(file);
	}

	// output file information
	function ParseFile(file) {

		Output(
			"<p>File information: <strong>" + file.name +
			"</strong> type: <strong>" + file.type +
			"</strong> size: <strong>" + file.size +
			"</strong> bytes</p>"
		);

		// display an image
		if (file.type.indexOf("image") == 0) {
			var reader = new FileReader();
			reader.onload = function(e) {
				Output(
					"<p><strong>" + file.name + ":</strong><br />" +
					'<img src="' + e.target.result + '" /></p>'
				);
			}
			reader.readAsDataURL(file);
		}

		// display text
		if (file.type.indexOf("text") == 0) {
			var reader = new FileReader();
			reader.onload = function(e) {
				Output(
					"<p><strong>" + file.name + ":</strong></p><pre>" +
					e.target.result.replace(/</g, "&lt;").replace(/>/g, "&gt;") +
					"</pre>"
				);
			}
			reader.readAsText(file);
		}

	}


	filedrag.init_ = function(fileselect, filedrag)
	{

		fileselect.addEventListener("change", FileSelectHandler(), false);
		filedrag.addEventListener("dragover", FileDragHover, false);
		filedrag.addEventListener("dragleave", FileDragHover, false);
		filedrag.addEventListener("drop", FileSelectHandler(), false);

	};


	// initialize
	filedrag.init = function() {

		var fileselect = jQuery("#fileselect")[0],
			filedrag = jQuery("#filedrag")[0],
			submitbutton = jQuery("#submitbutton")[0];

		console.log(fileselect);
			
		// file select
		fileselect.addEventListener("change", FileSelectHandler, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		if (xhr.upload) {

			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";

			// remove submit button
			submitbutton.style.display = "none";
		}

	}

})();