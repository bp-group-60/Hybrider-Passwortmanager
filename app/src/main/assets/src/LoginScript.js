var globalVariable={
	user: '',
	passwd: ''
}

function setFormMessage(formElement, type, message) {
	const messageElement = formElement.querySelector(".form__message");

	messageElement.textContent = message;
	messageElement.classList.remove("form__message--success", "form__message--error");
	messageElement.classList.add(`form__message--${type}`);
}

function setInputError(inputElement, message) {
	inputElement.classList.add("form__input--error");
	inputElement.parentElement.querySelector(".form__input-error-message").textContent = message;
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

}

function checkUserPassswd(user, passwd){
	// Entweder
	globalVariable.user = user;
	globalVariable.passwd = hashPasswd(passwd);
	// Oder
	sessionStorage.setItem("user", user);
	sessionStorage.setItem("passwd", hashPasswd(passwd));
}

function createUserPasswd(user, passwd){

}

function hashPasswd(passwd){
	//Anfrage an C
	//hashedPasswd = C.hashPasswd(passwd);
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
		loginForm.addEventListener("submit", e => {
			e.preventDefault();

			// hier müsste username und password Kombination überprüft werden...
			// hier müsste username und password Kombination überprüft werden...
			if(!li){
				setFormMessage(loginForm, "error", "Ungültiger Benutzername oder falsches Passwort");
				li = !li;
			}
			else {
				window.location.href = "./app.html";
			}
			setFormMessage(loginForm, "error", "Ungültiger Benutzername oder falsches Passwort");
		});
	}

	if (createAccountForm) {
		createAccountForm.addEventListener("submit", e => {
			e.preventDefault();

			// hier müsste man Kontoerstellungskriterien einfügen...

			setFormMessage(createAccountForm, "error", "Konto konnte nicht erstellt werden");
		});
	}

	document.querySelectorAll(".form__input").forEach(inputElement => {
		inputElement.addEventListener("blur", e => {
			//Mindestlänge Benutzername
			if (e.target.id === "signupUsername" && e.target.value.length > 0 && e.target.value.length < 3) {
				setInputError(inputElement, "Benutzername muss mindestens 3 Zeichen haben");
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