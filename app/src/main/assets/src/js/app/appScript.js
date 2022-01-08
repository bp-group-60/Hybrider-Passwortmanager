import {updateList} from "./listviewHandler.js";
import {passwords} from "./database/passwordOperations.js";
import {
	addPasswordCommitButtonOnclick,
	addUrlOnclick,
	editAbortOnclick,
	editButtonOnclick,
	showPasswordOnclick
} from "./onclick.js";

const user = sessionStorage.getItem("user")
const password = sessionStorage.getItem("password")

document.addEventListener('show', function(event) {
	let page = event.target

	if (page.id === 'page1') {
		updateList(user, password)
	}

	if (page.id === 'passwordDetailed') {
	  	page.querySelector('ons-toolbar .center').innerHTML = passwords[parseInt(page.data.id)][0]
		page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
		page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]

		page.querySelector('#editButton').onclick = editButtonOnclick(page)
		page.querySelector('#abortButton').onclick = editAbortOnclick(page)
		page.querySelector('#passwordCheckbox').onclick = showPasswordOnclick(page)
	}

	if (page.id === 'addPassword') {
		page.querySelector('#addUrl').onclick = addUrlOnclick(page)

		page.querySelector('#commitButton').onclick = addPasswordCommitButtonOnclick()

	}
  })
