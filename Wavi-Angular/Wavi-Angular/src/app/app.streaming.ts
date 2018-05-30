import {ChangeDetectorRef, Component, EventEmitter, Input, Output} from "@angular/core";
import {WaviUser} from "./WaviUser";
import {WindowRef} from "./WindowRef";

@Component({
  selector: 'app-streaming',
  templateUrl: './app.streaming.html',
  styleUrls: ['./app.streaming.css']
})
export class AppStreaming {
  @Input() user: WaviUser;

  @Output() played = new EventEmitter<string>();

  @Output() uploaded = new EventEmitter<ArrayBuffer>();

  @Output() signedOut = new EventEmitter();

  @Output() signedIn = new EventEmitter();

  @Input() messages: string[];

  @Input() prog: number;

  @Input() cd: ChangeDetectorRef;

  @Input() winRef: WindowRef;

  file: ArrayBuffer;

  fu: File;

  fileName: string = "No File Selected!";

  songName: string = "";

  playSong(url) {
    this.played.emit(url);
  }

  upload() {
    //this.uploaded.emit(this.file);
    this.messages.length = 0;
    var b = true;
    if (this.songName == "") {
      this.winRef.nativeWindow.alert("You Must Enter a Name for Your Song!");
      b = false;
    }
    if (this.fu == null) {
      this.winRef.nativeWindow.alert("You Must Choose a File to Upload!");
      b = false;
    }
    if (this.fu.size > 32000000) {
      this.winRef.nativeWindow.alert("You Must Choose a File Smaller than 32MB!");
      b = false;
    }
    if (this.fu.type != "audio/wav") {
      this.winRef.nativeWindow.alert("File Must be a .wav Audio File!");
      b = false;
    }
    if (b) {
      const body = new Int8Array(this.file);
      this.messages.push('Request body formed!')

      console.log(body);

      var xhr = new XMLHttpRequest();
      xhr.open("POST", 'https://radovandesign.com/songUpload/?songName=' + this.songName + "&id=" + this.user.id);

      xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      this.messages.push('Request headers set!')

      xhr.onload = () => {
        this.messages.push('Upload and conversion complete!');
        this.user = JSON.parse(xhr.response);
        this.messages.push('User updated!');
        this.prog = 100
        this.songName = "";
        this.clearFile();
      }

      xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
          var per = e.loaded / e.total * 90;
          this.prog = per;
          this.cd.detectChanges();
        }
      }

      xhr.upload.onerror = () => {
        this.prog = 0;
        this.messages.push('An error occured! Please try again, and check the console for more details.');
      }

      xhr.send(body);
      this.messages.push('Request sent!');
    }
  }

  onFileChange(event) {
    if(event.target.files.length > 0) {
      this.fu = event.target.files[0];
      console.log(this.fu);
      this.fileName = this.fu.name;
      let f = new FileReader();
      f.onload = e => {
        this.file = f.result;
        console.log(this.file);
      }
      f.readAsArrayBuffer(this.fu);
    }
  }

  clearFile() {
    (<HTMLInputElement>document.getElementById('song')).value = null;
    this.fu = null;
    this.fileName = "No File Selected!";
    this.file = null;
    this.cd.detectChanges();
  }
}
