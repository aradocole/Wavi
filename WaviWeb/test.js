console.log("wtf");
      MyTestScript = function(){};
      MyTestScript.prototype.onGWTReady = function()
      {
         var foo = new com.acme.Foo();
         foo.x = 40;
         foo.y = 2;
                                       // will return 42!                    //
         var z = foo.sum();
         console.log("foo.sum=" + z);
		  
		 var wavi = new com.acme.StaticWave("https://storage.googleapis.com/rd-site-resources/wavelets/pipe.zip");
			wavi.decompress();
		  	console.log(wavi.waveAsBytes);
		  
	  };