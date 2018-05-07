MyTestScript = function(){};
MyTestScript.prototype.onGWTReady = function() {
	var foo = new jwave.acme.Foo();
	foo.x = 40;
	foo.y = 2;
                                       // will return 42!                    //
	var z = foo.sum();
	console.log("foo.sum=" + z);

	var test = new jwave.acme.Test("With arg!");
	console.log(test.made);
	console.log(test.make());
	test.set("Yes! By Setting.");
	console.log(test.made);
	
	
	//test.setWaveArray(arr);
	//console.log(test.waveAsBytes);
	//console.log(test.waveAsDoubles);
	//test.decompress();
	//console.log(test.waveAsDoubles);

	
	zip.workerScriptsPath = "zip.js-master/zip.js-master/WebContent/";
	var url = "https://storage.googleapis.com/rd-site-resources/wavelets/pipe.zip";
	zip.createReader(new zip.HttpReader(url), function(reader) {
	  	// get all entries from the zip
	 	reader.getEntries(function(entries) {
			if (entries.length) {
		  		// get first entry content as text
		  		entries[0].getData(new zip.TextWriter(), function(text) {
					// text contains the entry data as a String
					test.setWaveWithString(text);
					console.log(test.waveAsBytes);
					console.log(test.waveAsDoubles);
					test.decompress();
					console.log(test.waveAsDoubles);
					// close the zip reader
					reader.close(function() {
			  			// onclose callback
					});
		  		}, function(current, total) {
					// onprogress callback
		  		});
			}
	  });
	}, function(error) {
	  	// onerror callback
	});
};