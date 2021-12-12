const user = sessionStorage.getItem("user")
const password = sessionStorage.getItem("password")
var passwords = getPasswords()

//Schnittstellen
function getPasswords() {
    // array structure: [[website, loginName, password], ...]
	return JSON.parse(Java.getPasswordList(user, password)).dataArray
}

function createPassword(user, website, loginName,  password) {
    return Java.createPassword(user, website, loginName,  password)
}

function editPassword(i) {
	//Anfrage an Java, um Eintrag i zu bearbeiten
}

function hashPassword(password) {
	//Anfrage an C
	//hashedPassword = C.hashPassword(password)
}

function createList(){
	var array = getPasswords()

	for(var i = 0; i < array.length; i++){
		addToList(`${array[i][0]} / ${array[i][1]}`,i)
	}
}
function addToList(name, id) {
	let listItem = ons.createElement('<ons-list-item modifier="chevron" tappable>')

	listItem.setAttribute("id", id)
	listItem.setAttribute("onClick", "UpdatePassword(this.id)")

	listItem.innerText = name

	document.getElementById('overview').append(listItem)
}

ons.ready(function() {
	// Cordova APIs are ready
})

document.addEventListener('init', function(event) {
	var page = event.target

	if (page.id === 'page1') {
		createList()
	}
  
	if (page.id === 'page2') {
	  	page.querySelector('ons-toolbar .center').innerHTML = page.data.title
		var v = document.getElementById(page.data.id).textContent
		page.querySelector('#website').value = passwords[parseInt(page.data.id)][0]
		page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
		page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]
	}

	if (page.id === 'addPassword') {
		page.querySelector('#commitButton').onclick = () => {

		    console.log('TODO: validate data')

			let website = document.getElementById('website').value
			console.log(website)
			let loginName = document.getElementById('username').value
			console.log(loginName)
			let password = document.getElementById('password').value
			console.log(password)
			let successful = createPassword(user, website, loginName, password)

			if(successful){
			    document.querySelector('#myNavigator').popPage()
			    ons.notification.toast('Passwort hinzugefügt!', {
                    timeout: 2000
                  })
			} else {
			    console.log('TODO: error message')
			}

		}

	}
  })

function UpdatePassword(EintragID){
	document.querySelector('#myNavigator').pushPage('page2.html', 
						{data: {title: 'Passwort ändern', id: EintragID}})
}

document.addEventListener('destroy', function(event) {
    console.log('TODO: update view')

})