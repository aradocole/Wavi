/*jshint esversion: 6 */
window.onGoogleYoloLoad = (googleyolo) => {
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
    	useGoogleIdTokenForAuth(credential.idToken);
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
					useGoogleIdTokenForAuth(credential.idToken);
				 }
			}, (error) => {
				switch (error.type) {
					case "userCanceled":
						// The user closed the hint selector. Depending on the desired UX,
					  	// request manual sign up or do nothing.
					  	break;
					case "noCredentialsAvailable":
					  	// No hint available for the session. Depending on the desired UX,
					  	// request manual sign up or do nothing.
					  	break;
					case "requestFailed":
						  // The request failed, most likely because of a timeout.
						  // You can retry another time if necessary.
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
		disableAutoSignIn().then(() => {
  			// Auto sign-in disabled.
		});
	}
	
	function useGoogleIdTokenForAuth(id_token) {
		console.log(id_token);
		var xhr = new XMLHttpRequest();
		xhr.open('POST', 'https://radovandesign.com/waviauth/');
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xhr.onload = function() {
			console.log('Signed in as: ' + xhr.responseText);
		};
		xhr.send('idtoken=' + id_token);
	}
};
