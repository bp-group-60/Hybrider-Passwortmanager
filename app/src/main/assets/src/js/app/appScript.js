import {updateList} from "./listviewHandler.js";
import {createPassword, passwords} from "./database/passwordOperations.js";

const user = sessionStorage.getItem("user")
const password = sessionStorage.getItem("password")

document.addEventListener('show', function(event) {
	let page = event.target

	if (page.id === 'page1') {
		updateList(user, password)
	}

	if (page.id === 'passwordDetailed') {
	  	page.querySelector('ons-toolbar .center').innerHTML = page.data.title
		page.querySelector('#name').innerText = passwords[parseInt(page.data.id)][0]
		page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
		page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]
	}

	if (page.id === 'addPassword') {
		page.querySelector('#addUrl').onclick = () => {
			let newItem = ons.createElement('<ons-list-item modifier="nodivider">')
			let	urlInput = ons.createElement('<ons-input modifier="underbar" placeholder="Url" float>')

			newItem.append(urlInput)
			page.querySelector('#urlItems').append(newItem)
		}

		page.querySelector('#commitButton').onclick = () => {

		    console.log('TODO: validate data')

			let name = document.getElementById('name').value
			let loginName = document.getElementById('username').value
			let password = document.getElementById('password').value
			let successful = createPassword(user, name, loginName, password)

			if(successful){
			    document.querySelector('#myNavigator').popPage()
			    ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000})
			} else {
				//TODO: error message
			    console.log('TODO: error message')
			}

		}

	}
  })
