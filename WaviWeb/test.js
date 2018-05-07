console.log("wtf");
      MyTestScript = function(){};
      MyTestScript.prototype.onGWTReady = function()
      {
         var foo = new com.acme.Foo();
         foo.x = 40;
         foo.y = 2;
                                       // will return 42!                     //
         var z = foo.sum();
         console.log("foo.sum=" + z);
         var doubleArray = [1.6, 4, 32, 16];
         var ht = new com.acme.HaarTransform(doubleArray);
         var newArray = ht.transform(doubleArray,1);
          console.log(newArray);
          var inverseTransformVal = ht.inverseTransform(newArray, ht.levels);
          console.log(inverseTransformVal);
		  
		 var wavi = new com.acme.StaticWave("https://storage.googleapis.com/rd-site-resources/wavelets/pipe.zip");
			wavi.decompress();
		  	console.log(wavi.waveAsBytes);
		  
	  };