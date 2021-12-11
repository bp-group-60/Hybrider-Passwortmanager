const user = sessionStorage.getItem("user")
const password = sessionStorage.getItem("password")
var BreakException = {}

//Schnittstellen
function getPasswords(user, password) {
    // array structure: [[website, loginname, password], ...]
	return JSON.parse(Java.getPasswordList(user, password)).overview
}

function createPassword(password) {

}

function editPassword(i) {
	//Anfrage an Java, um Eintrag i zu bearbeiten
}

function hashPassword(password) {
	//Anfrage an C
	//hashedPassword = C.hashPassword(password)
}

function createList(){
	var list = document.getElementById('page1')
	var list2 = document.getElementsByTagName('div')
	var i = 1
	var array = getPasswords(user, password)

	array.forEach(function (rowData) {
		var row = document.createElement('ons-list-item')
		row.setAttribute("modifier", "chevron")
		row.setAttribute("tappable")
		row.setAttribute("id", i)
		row.setAttribute("onClick", "UpdatePassword(this.id)")

		var txt = `${rowData[0]} / ${rowData[1]}`
		
		row.innerText = txt
		list2[2].appendChild(row)

		i++
	})
}

function createTable(array){
	var table = document.getElementById('Tabelle')
	var tableBody = document.createElement('tbody')

	var i = 0

	array.forEach(function (rowData) {
		var row = document.createElement('tr')
		row.setAttribute("id", i)

		var j = 0
		rowData.forEach(function (cellData) {
			var cell = document.createElement('td')
			cell.setAttribute("id", j)
			cell.appendChild(document.createTextNode(cellData))
			row.appendChild(cell)
			j++
		})

		tableBody.appendChild(row)
		i++
	})

	table.appendChild(tableBody)
}

document.addEventListener("DOMContentLoaded", () => {
	const passForm = document.getElementById('Tabelle')
	const user = sessionStorage.getItem("user")
	const password = sessionStorage.getItem("password")

	if(document.getElementById('Tabelle'))createTable(getPasswords(user, password))
	else createList(getPasswords(user, password))
})

ons.ready(function() {
	// Cordova APIs are ready
	console.log(window.device)
})

document.addEventListener('init', function(event) {
	var page = event.target
  
	if (page.id === 'page2') {
	  	page.querySelector('ons-toolbar .center').innerHTML = page.data.title
		var v = document.getElementById(page.data.id).textContent
		document.getElementById('page2').querySelector('#Webseite').value = v
		document.getElementById('page2').querySelector('#username').value = v
		document.getElementById('page2').querySelector('#passwort').value = v
	}
  })

function UpdatePassword(EintragID){
	document.querySelector('#myNavigator').pushPage('page2.html', 
						{data: {title: 'Passwort ändern', id: EintragID}})
}
