import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {WindowRef} from "./WindowRef";
import {s} from "@angular/core/src/render3";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  songs: Array<Array<string>> = Array();
  selectedSong: string;
  sampleRates: number;


  constructor(private http: HttpClient, private winRef: WindowRef) { }

  ngOnInit() {
    this.songs.push(["Staying Alive", "first20"]);
    this.songs.push(["Spooopy", "spoopy"]);
    this.songs.push(["No Tears Left to Cry", "No Tears Left To Cry"]);

      if (!this.winRef.nativeWindow.AudioContext) {
        if (!this.winRef.nativeWindow.webkitAudioContext) {
          alert("Your browser does not support any AudioContext and cannot play back this audio.");
          return;
        }
        this.winRef.nativeWindow.window.AudioContext = this.winRef.nativeWindow.window.webkitAudioContext;
      }

      context = new AudioContext();

    this.winRef.nativeWindow.MyTestScript = function(){};
    this.winRef.nativeWindow.MyTestScript.prototype.onGWTReady = function () {
        /*var foo = new jwave.acme.Foo();
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
        this.wave = test;*/
    };
  }

  playSelected() {
    console.log(this.sampleRates);
    var nName = this.selectedSong.split(" ").join("%20");
    var path = "https://storage.googleapis.com/rd-site-resources/wavelets/" + nName +".zip"

    var arraybuffer;

    var test = new this.winRef.nativeWindow.jwave.acme.Wavi();
    var zip = this.winRef.nativeWindow.zip;

    zip.workerScriptsPath = "assets/zip.js-master/zip.js-master/WebContent/";

    zip.createReader(new zip.HttpReader(path), reader => {
      // get all entries from the zip
      reader.getEntries(entries => {
        if (entries.length) {
          // get first entry content as text
          entries[0].getData(new zip.BlobWriter(), blob => {
            // text contains the entry data as a String
            var fileReader = new FileReader();
            var that = this;
            fileReader.onload  = function () {
              arraybuffer = this.result;
              console.log(arraybuffer);
              test.setWaveArray(new Int8Array(arraybuffer));
              arraybuffer = null;
              //console.log(test.waveAsBytes);
              //console.log(test.waveAsDoubles);
              test.decompress();
              //console.log(test.waveAsBytes);
              //console.log(test.waveAsDoubles);
              that.playDoubles(test.waveAsDoubles, test.sampleRate);
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
  }

  playDoubles(d, r) {
    var floats = new Float32Array(d);
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
    buf = context.createBuffer(1, floats.length, r);
    var source = context.createBufferSource();
    buf.getChannelData(0).set(floats);
    source.buffer = buf;
    source.connect( context.destination );
    source.start(0);
    console.log("Playing!");
  }
}

var context;    // Audio context
var buf;

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
  buf = context.createBuffer(1, floats.length, this.sampleRate);
  var source = context.createBufferSource();
  buf.getChannelData(0).set(floats);
  source.buffer = buf;
  source.connect( context.destination );
  source.start(0);
  console.log("Playing!");
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
