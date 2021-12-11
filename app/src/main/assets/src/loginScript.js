import {clearInputError, setFormMessage, setInputError} from "./loginMessages.js"

function existUser(user){
	if (Java.existUser(user)){
		let e = document.getElementById("signupUsername")
		setInputError(e, "Benutzername bereits vergeben")
	}
}

function checkUserPassword(user, password){
	sessionStorage.setItem("user", user)
	sessionStorage.setItem("password", hashPassword(password))

	if(Java.checkUser(user, password)){
		window.location.href = "./app.html"
	}
	else{
		const loginForm = document.getElementById('Anmeldung')
		setFormMessage(loginForm, "error", "Ungültiger Benutzername oder falsches Passwort")
	}
}

function createUser(user, email, password){
    if (Java.createUser(user, email, password)){
        sessionStorage.setItem("regist", true)
        window.location.href = "./index.html"
    }
    else {
		const createAccountForm = document.getElementById("KontoErstellen")
        setFormMessage(createAccountForm, "error", "Konto konnte nicht erstellt werden")
    }
}

function hashPassword(password){
	//Anfrage an C
	//hashedPassword = C.hashPassword(password)
	return password
}

document.addEventListener("DOMContentLoaded", () => {
	//const loginForm = document.querySelector("#Anmeldung")
	const loginForm = document.getElementById('Anmeldung')
	const createAccountForm = document.getElementById("KontoErstellen")

	if (loginForm) {
	    let ereg = sessionStorage.getItem("regist")
	    if(sessionStorage.getItem("regist") === "true"){
	        setFormMessage(loginForm, "info", "Benutzerkonto erfolgreich erstellt.")
	    }
		loginForm.addEventListener("submit", e => {
			e.preventDefault()
            let user = document.getElementById('user').value
            let password = document.getElementById('password').value
            checkUserPassword(user, password)
		})
	}

	if (createAccountForm) {
		createAccountForm.addEventListener("submit", e => {
			e.preventDefault()

                let user = document.getElementById('signupUsername').value
                let email = document.getElementById('signupEmail').value
                let password = document.getElementById('signupPasswort').value

                createUser(user, email, password)
		})
	}

	document.querySelectorAll(".form__input").forEach(inputElement => {
		inputElement.addEventListener("blur", e => {
			//Mindestlänge Benutzername
			if (e.target.id === "signupUsername") {
			    if (e.target.value.length > 0 && e.target.value.length < 3) {
				    setInputError(inputElement, "Benutzername muss mindestens 3 Zeichen haben")
			    }
			    else {
			        existUser(e.target.value)
			    }
			}
			//E-mail-Kriterien (TEXT @ TEXT . DOMAIN)
			if (e.target.id === "signupEmail" && e.target.value.length > 0) {
				const re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,5})+$/
				if (!e.target.value.match(re)) {
					setInputError(inputElement, "Bitte gib eine gültige E-Mail-Adresse ein.")
				}
			}
			//Passwortfelder aktiv
			if (e.target.id === "signupPasswort" || e.target.id === "signupPasswortConfirm") {
				const pwd1 = document.getElementById("signupPasswort")
				const pwd2 = document.getElementById("signupPasswortConfirm")
				//Passwortlänge
				if (pwd1.value.length > 0 && pwd1.value.length < 8) {
					setInputError(pwd1, "Passwort muss mindestens 8 Zeichen enthalten")
				}
				// Groß-/Kleinschreibung + Zahlen + Sonderzeichen

				//Passwortabgleich
				if (pwd2.value.length > 0 && pwd1.value !== pwd2.value){
					setInputError(pwd2, "Passwörter stimmen nicht überein")
				}
			}
		})

		inputElement.addEventListener("input", e => {
			if(e.target.id === "signupPasswort"){
				clearInputError(document.getElementById("signupPasswortConfirm"))
			}
			clearInputError(inputElement)
		})
	})
})