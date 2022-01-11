import {updateList} from "./listviewHandler.js";
import {passwords} from "./database/passwordOperations.js";
import {
	addPasswordCommitButtonOnclick,
	addUrlOnclick,
	createUrlItem,
	editAbortOnclick,
	onclickEditSave,
	editButtonOnclick,
	onclickDelete,
	showPasswordOnclick,
	onclickMoreButton
} from "./onclick.js";
import {getUrlList} from "./database/websiteOperations.js";

const user = sessionStorage.getItem("user")
const password = sessionStorage.getItem("password")

document.addEventListener('show', function(event) {
	let page = event.target

	if (page.id === 'page1') {
		updateList(user, password)
		page.querySelector('#moreButton').onclick = onclickMoreButton(page)
	}

	if (page.id === 'passwordDetailed') {
	  	page.querySelector('ons-toolbar .center').innerHTML = passwords[parseInt(page.data.id)][0]
	  	page.querySelector('#moreButton').onclick = onclickMoreButton(page)

		getUrlList(user, passwords[parseInt(page.data.id)][0]).forEach(url =>{
			page.querySelector('#urlItems').append(createUrlItem(url))
		})

		page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
		page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]
        page.querySelector('#passwordCheckbox').onclick = showPasswordOnclick(page)

		page.querySelector('#editButton').onclick = editButtonOnclick(page)

		page.querySelector('#addUrl').onclick = addUrlOnclick(page)
		page.querySelector('#abortButton').onclick = editAbortOnclick(page)
		page.querySelector('#commitButton').onclick = onclickEditSave(page)
		page.querySelector('#deleteButton').onclick = onclickDelete(page)
	}

	if (page.id === 'addPassword') {
		page.querySelector('#addUrl').onclick = addUrlOnclick(page)
		page.querySelector('#commitButton').onclick = addPasswordCommitButtonOnclick(page)

	}
  })
