import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {HttpClient, HttpEvent, HttpEventType, HttpHeaders, HttpParams, HttpRequest} from "@angular/common/http";
import {WindowRef} from "./WindowRef";
import {s} from "@angular/core/src/render3";
import {WaviUser} from "./WaviUser";
import {AuthResponse} from "./AuthResponse";
import {callNgModuleLifecycle} from "@angular/core/src/view/ng_module";
import {KeyboardType} from "tns-core-modules/ui/enums";
import number = KeyboardType.number;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  selectedSong: string;
  sampleRates: number;

  googleyolo: any;
  user: WaviUser;
  id: string;

  songName: string = "";
  songURL: string = "";

  payload: FormData;
  a: ArrayBuffer;

  fu: File;

  progress: number = 0;

  msgs: string[] = [];

  constructor(private http: HttpClient, public winRef: WindowRef, public cd: ChangeDetectorRef) { }

  manualLogin() {
    this.winRef.nativeWindow.gapi.load('auth2',() => {
      var auth2 = this.winRef.nativeWindow.gapi.auth2.init();

      // Sign the user in, and then retrieve their ID.
     // if (auth2.isSignedIn.get()){
        auth2.signIn().then(() => {
          var id_token = auth2.currentUser.get().getAuthResponse().id_token;
          this.useGoogleIdTokenForAuth(id_token);
        });
      //}
    });
  }

  onFileChange(event) {
    if(event.target.files.length > 0) {
      this.fu = event.target.files[0];
      console.log(this.fu);
      let f = new FileReader();
      f.onload = e => {
        this.a = f.result;
        console.log(this.a);
      }
      f.readAsArrayBuffer(this.fu);
    }
  }


  onSignIn() {
    //var id_token = googleUser.getAuthResponse().id_token;
    //console.log(id_token);
    console.log("hit");
    //this.useGoogleIdTokenForAuth(id_token);
  }

  clearFile() {
    (<HTMLInputElement>document.getElementById('song')).value = null;
    this.fu = null;
    this.cd.detectChanges();
  }

  onSubmit() {
    var b = true;
    if (this.songName == "") {
      this.winRef.nativeWindow.alert("You Must Enter a Name for Your Song!");
      b = false;
    }
    if (this.fu == null) {
      this.winRef.nativeWindow.alert("You Must Choose a File to Upload!");
      b = false;
    }
    if (this.fu.type != "audio/wav") {
      this.winRef.nativeWindow.alert("File Must be a .wav Audio File!");
      b = false;
    }
    if (b) {
      const body = new Int8Array(this.a);

      console.log(body);

      var xhr = new XMLHttpRequest();
      xhr.open("POST", 'https://radovandesign.com/songUpload/?songName=' + this.songName + "&id=" + this.user.id);

      xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

      xhr.onload = () => {
        this.user = JSON.parse(xhr.response);
        this.progress = 100;
        this.fu = null;
        this.a = null;
        this.cd.detectChanges();
      }

      xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
         var per = e.loaded / e.total * 90;
         this.progress = per;
         this.cd.detectChanges();
        }
      }

      xhr.send(body);
    }
  }

  sendSong() {
    console.log(this.songName + this.songURL);
    const body = ("songName=" + this.songName + "&URL=" + this.songURL + "&id=" + this.user.id);

    this.http.post<AuthResponse>('https://radovandesign.com/songPost/', body, {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')})
      .subscribe(data => {
          this.user = data;
          this.cd.detectChanges();
      })
  }

  ngOnInit() {

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
    this.login();
  }


  playSelected(p) {
    this.progress = 0;
    this.msgs.length = 0;

    console.log(this.sampleRates);
    var path = p;

    const req = new HttpRequest('GET', path, {
      reportProgress: true,
      responseType: 'blob'
    });

    this.http.request(req).subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.Sent:
          console.log('Request sent!');
          this.msgs.push('Request sent!');
          break;
        case HttpEventType.ResponseHeader:
          console.log('Response header received!');
          this.msgs.push('Response header received!');
          break;
        case HttpEventType.DownloadProgress:
          this.progress = event.loaded / event.total * 90;
          this.cd.detectChanges();
          break;
        case HttpEventType.Response:
          this.unzip(event.body);
      }
    });

    /*this.http.get(path, {responseType: 'blob'})
      .subscribe(data => {

      });*/
  }

  unzip(bleb) {
    var arraybuffer;

    let a: any = this.winRef.nativeWindow.jwave;

    var test = new a.acme.Wavi();
    var zip = this.winRef.nativeWindow.zip;

    zip.workerScriptsPath = "assets/zip.js-master/zip.js-master/WebContent/";

    //zip.createReader(new zip.HttpReader(path), reader => {
    zip.createReader(new zip.BlobReader(bleb), reader => {
      // get all entries from the zip
      reader.getEntries(entries => {
        if (entries.length) {
          // get first entry content as text
          entries[0].getData(new zip.BlobWriter(), blob => {
            // text contains the entry data as a String
            var fileReader = new FileReader();
            var that = this;
            fileReader.onload  = (ev: any) => {
              arraybuffer = ev.target.result;
              this.msgs.push('Payload loaded!');
              this.cd.detectChanges();
              console.log(arraybuffer);
              test.setWaveArray(new Int8Array(arraybuffer));
              this.msgs.push('Decompressor created!');
              this.cd.detectChanges();
              arraybuffer = null;
              //console.log(test.waveAsBytes);
              //console.log(test.waveAsDoubles);
              test.decompress();
              this.msgs.push('Decompressed!');
              this.cd.detectChanges();
              //console.log(test.waveAsBytes);
              //console.log(test.waveAsDoubles);
              if (test.channels == 2) {
                that.playDoubles(test.waveAsDoubles, test.sampleRate*2);
              }
              else {
                that.playDoubles(test.waveAsDoubles, test.sampleRate);
              }
              this.msgs.push('Song playing!');
              this.cd.detectChanges();
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
    this.progress = 100;
    source.start(0);
    console.log("Playing!");
  }

  useGoogleIdTokenForAuth(id_token: string) {
    console.log(id_token);
    const body = ("id_token=" + id_token);
    this.http.post<AuthResponse>('https://radovandesign.com/waviauth/', body, {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')})
      .subscribe(data => {
        this.user = data;
        this.cd.detectChanges();
        console.log(this.user.id);
        console.log(this.user);
      })
  }

  login() {
    this.winRef.nativeWindow.onGoogleYoloLoad = (googleyolo) => {
      this.googleyolo = googleyolo;
      const retrievePromise = googleyolo.retrieve({
        supportedAuthMethods: [
          "https://accounts.google.com"
        ],
        supportedIdTokenProviders: [
          {
            uri: "https://accounts.google.com",
            clientId: "380186301704-gmr2ctka1od78md38cogk48bmtrjlpnp.apps.googleusercontent.com"
          }
        ]
      });

      const hintPromise = googleyolo.hint({
        supportedAuthMethods: [
          "https://accounts.google.com"
        ],
        supportedIdTokenProviders: [
          {
            uri: "https://accounts.google.com",
            clientId: "380186301704-gmr2ctka1od78md38cogk48bmtrjlpnp.apps.googleusercontent.com"
          }
        ]
      });

      function cancel() {
        googleyolo.cancelLastOperation().then(() => {
          // Credential selector closed.
        });
      }

      retrievePromise.then((credential) => {
        this.useGoogleIdTokenForAuth(credential.idToken);
      }, (error) => {
        // Credentials could not be retrieved. In general, if the user does not
        // need to be signed in to use the page, you can just fail silently; or,
        // you can also examine the error object to handle specific error cases.

        // If retrieval failed because there were no credentials available, and
        // signing in might be useful or is required to proceed from this page,
        // you can call `hint()` to prompt the user to select an account to sign
        // in or sign up with.
        if (error.type === 'noCredentialsAvailable') {
          hintPromise.then((credential) => {
            if (credential.idToken) {
              // Send the token to your auth backend.
              this.useGoogleIdTokenForAuth(credential.idToken);
            }
          }, (error) => {
            switch (error.type) {
              case "userCanceled":
                // The user closed the hint selector. Depending on the desired UX,
                // request manual sign up or do nothing.
                console.log("Cancelled!");
                this.manualLogin();
                break;
              case "noCredentialsAvailable":
                // No hint available for the session. Depending on the desired UX,
                // request manual sign up or do nothing.
                this.manualLogin();
                break;
              case "requestFailed":
                // The request failed, most likely because of a timeout.
                // You can retry another time if necessary.
                console.log("Failed!");
                break;
              case "operationCanceled":
                // The operation was programmatically canceled, do nothing.
                break;
              case "illegalConcurrentRequest":
                // Another operation is pending, this one was aborted.
                break;
              case "initializationError":
                // Failed to initialize. Refer to error.message for debugging.
                break;
              case "configurationError":
                // Configuration error. Refer to error.message for debugging.
                break;
              default:
              // Unknown error, do nothing.
            }
          });
        }
      });

      function signOut() {
        googleyolo.disableAutoSignIn().then(() => {
          // Auto sign-in disabled.
        });
      }
    };
  }

  signOut() {
    this.googleyolo.disableAutoSignIn().then(() => {
      // Auto sign-in disabled.
      location.reload(true);
    });
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
