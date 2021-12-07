var errorCount = 0;

function setFormMessage(formElement, type, message) {
	const messageElement = formElement.querySelector(".form__message");

	messageElement.textContent = message;
	messageElement.classList.remove("form__message--success", "form__message--error");
	messageElement.classList.add(`form__message--${type}`);
}

function setInputError(inputElement, message) {
	inputElement.classList.add("form__input--error");
	inputElement.parentElement.querySelector(".form__input-error-message").textContent = message;
	//errorCount++;
}

function clearInputError(inputElement) {
	inputElement.classList.remove("form__input--error");
	inputElement.parentElement.querySelector(".form__input-error-message").textContent = "";
}

function clearAllErrorMessages(formElement) {
	//form__message--error
	//form__input-error-message
	formElement.querySelector(".form__message--error").textContent = "";

	formElement.querySelectorAll(".form__input").forEach(inputElement => {
		inputElement.value = "";
		clearInputError(inputElement);
	});

}

//Schnittstellen
function existUser(user){
	var euser = Java.existUser(user);
	if (euser){
		var e = document.getElementById("signupUsername");
		setInputError(e, "Benutzername bereits vergeben");
	}
}

function checkUserPassswd(user, passwd){
	sessionStorage.setItem("user", user);
	sessionStorage.setItem("passwd", hashPasswd(passwd));

	const loginForm = document.getElementById('Anmeldung');
    console.log(user);
    console.log(passwd);

	//ZUGRIFF AUF JAVA
	var login = Java.checkUser(user, passwd);
	if(login){
		window.location.href = "./app.html";
	}
	else{
		setFormMessage(loginForm, "error", "Ungültiger Benutzername oder falsches Passwort");
	}
}

function createUserPasswd(user, email, passwd){
	const createAccountForm = document.getElementById("KontoErstellen");

    console.log(user);
    console.log(passwd);
    //ZUGRIFF AUF JAVA
    var regist = Java.createUser(user, email, passwd);
    if (regist){
        sessionStorage.setItem("regist", true);
        window.location.href = "./index.html";;
    }
    else {
        setFormMessage(createAccountForm, "error", "Konto konnte nicht erstellt werden");
    }
}

function hashPasswd(passwd){
	//Anfrage an C
	//hashedPasswd = C.hashPasswd(passwd);
	return passwd;
}

var li = false;
document.addEventListener("DOMContentLoaded", () => {
	//const loginForm = document.querySelector("#Anmeldung");
	const loginForm = document.getElementById('Anmeldung');
	const createAccountForm = document.getElementById("KontoErstellen");

	/*document.getElementById("linkKontoErstellen").addEventListener("click", e => {
	   e.preventDefault();
	   loginForm.classList.add("form--hidden");
	   createAccountForm.classList.remove("form--hidden");
	   clearAllErrorMessages(loginForm);
	});

	document.getElementById("linkAnmeldung").addEventListener("click", e => {
		e.preventDefault();
		loginForm.classList.remove("form--hidden");
		createAccountForm.classList.add("form--hidden");
		clearAllErrorMessages(createAccountForm);
	});*/

	if (loginForm) {
	    var ereg = sessionStorage.getItem("regist");
	    if(sessionStorage.getItem("regist") === "true"){
	        setFormMessage(loginForm, "error", "Benutzerkonto erfolgreich erstellt.");
	    }
		loginForm.addEventListener("submit", e => {
			e.preventDefault();
            var user = document.getElementById('user').value;
            var passwd = document.getElementById('password').value;
            checkUserPassswd(user, passwd);

			/* Demo erster Versuch Error, dann Login...
			if(!li){
				setFormMessage(loginForm, "error", "Ungültiger Benutzername oder falsches Passwort");
				li = !li;
			}
			else {
				window.location.href = "./app.html";
			}*/
		});
	}

	if (createAccountForm) {
		createAccountForm.addEventListener("submit", e => {
			e.preventDefault();
            if(errorCount == 0){
                var user = document.getElementById('signupUsername').value;
                var email = document.getElementById('signupEmail').value;
                var passwd = document.getElementById('signupPasswort').value;
                console.log(user);
                console.log(passwd);
                createUserPasswd(user, email, passwd);
            }
            else {
                setFormMessage(createAccountForm, "error", "Konto konnte nicht erstellt werden");
            }
		});
	}

	document.querySelectorAll(".form__input").forEach(inputElement => {
		inputElement.addEventListener("blur", e => {
			//Mindestlänge Benutzername
			if (e.target.id === "signupUsername") {
			    if (e.target.value.length > 0 && e.target.value.length < 3) {
				    setInputError(inputElement, "Benutzername muss mindestens 3 Zeichen haben");
			    }
			    else {
			        existUser(e.target.value);
			    }
			}
			//E-mail-Kriterien (TEXT @ TEXT . DOMAIN)
			if (e.target.id === "signupEmail" && e.target.value.length > 0) {
				const re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,5})+$/;
				if (!e.target.value.match(re)) {
					setInputError(inputElement, "Bitte gib eine gültige E-Mail-Adresse ein.");
				}
			}
			//Passwortfelder aktiv
			if (e.target.id === "signupPasswort" || e.target.id === "signupPasswortConfirm") {
				const pwd1 = document.getElementById("signupPasswort");
				const pwd2 = document.getElementById("signupPasswortConfirm");
				//Passwortlänge
				if (pwd1.value.length > 0 && pwd1.value.length < 8) {
					setInputError(pwd1, "Passwort muss mindestens 8 Zeichen enthalten");
				}
				// Groß-/Kleinschreibung + Zahlen + Sonderzeichen

				//Passwortabgleich
				if (pwd2.value.length > 0 && pwd1.value != pwd2.value){
					setInputError(pwd2, "Passwörter stimmen nicht überein");
				}
			}
		});

		inputElement.addEventListener("input", e => {
			if(e.target.id === "signupPasswort"){
				clearInputError(document.getElementById("signupPasswortConfirm"));
			}
			clearInputError(inputElement);
		});
	});
});