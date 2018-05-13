//window.onload = init;
var context;    // Audio context
var buf;        // Audio buffer
var channels;
var sampleRate;

function init() {
if (!window.AudioContext) {
    if (!window.webkitAudioContext) {
        alert("Your browser does not support any AudioContext and cannot play back this audio.");
        return;
    }
        window.AudioContext = window.webkitAudioContext;
    }

    context = new AudioContext();
}


	  
MyTestScript = function(){};
MyTestScript.prototype.onGWTReady = function() {
	init();
	document.getElementById("playb").addEventListener('click', function() {
  		context.resume().then(() => {
    		console.log('Playback resumed successfully');
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
	var arraybuffer;

	
	zip.workerScriptsPath = "zip.js-master/zip.js-master/WebContent/";
	var url5 = "https://storage.googleapis.com/rd-site-resources/wavelets/pipe.zip";
	var url2 = "https://storage.googleapis.com/rd-site-resources/wavelets/first20.zip";
	var url3 = "https://storage.googleapis.com/rd-site-resources/wavelets/spoopy.zip";
			var url4 = "https://storage.googleapis.com/rd-site-resources/wavelets/All%20Of%20The%20Lights.zip";
	
	var url = document.getElementById("link").value;
	channels = document.getElementById("chan").value;
	sampleRate = document.getElementById("rate").value;
			
	zip.createReader(new zip.HttpReader(url), function(reader) {
	  	// get all entries from the zip
	 	reader.getEntries(function(entries) {
			if (entries.length) {
		  		// get first entry content as text
		  		entries[0].getData(new zip.BlobWriter(), function(blob) {
					// text contains the entry data as a String
					var fileReader = new FileReader();
					fileReader.onload = function() {
						arraybuffer = this.result;
						console.log(arraybuffer);
						test.setWaveArray(new Int8Array(arraybuffer));
						arraybuffer = null;
						console.log(test.waveAsBytes);
						console.log(test.waveAsDoubles);
						test.decompress();
						console.log(test.waveAsBytes);
						console.log(test.waveAsDoubles);
						playBytes(test.waveAsDoubles);
						console.log(context.state);
					};
					fileReader.readAsArrayBuffer(blob);
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
  		});
	});
	
	
};

function playBytes(b) {
	var floats = new Float32Array(b);
	var min = 0;
	var max = 0;
	for (var i = 0; i < floats.length; i++) {
		//var a = b[((i+1) *2) - 2] & 0xFF;
		//var c = (b[((i+1) *2) - 1] << 8) & 0x0000FF00;
		//floats[i] = a | b;
		if (floats[i] > max) {
			max = floats[i];
		}
		if (floats[i] < min) {
			min = floats[i];
		}
	}
	console.log(floats);
	var scale = max;
	if (Math.abs(min) > max) {
		scale = Math.abs(min);
	}
	floats.forEach(function(sample, i) {
		floats[i] = sample/scale;
	});
	console.log(floats);
	buf = context.createBuffer(channels, floats.length, sampleRate);
	var source = context.createBufferSource();
	buf.getChannelData(0).set(floats);
    source.buffer = buf;
   	source.connect( context.destination );
   	source.start(0);
}

function playByteArray( bytes ) {
    var buffer = new Uint8Array( bytes.length );
    buffer.set( new Uint8Array(bytes), 0 );

    context.decodeAudioData(buffer.buffer, play);
}

function play( audioBuffer ) {
		var source = context.createBufferSource();
    	source.buffer = audioBuffer;
    	source.connect( context.destination );
    	source.start(0);
}