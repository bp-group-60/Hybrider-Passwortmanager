import {updateList} from "./listviewHandler.js";
import {passwords} from "./database/passwordOperations.js";
import {
	addPasswordCommitButtonOnclick,
	addUrlOnclick,
	editAbortOnclick,
	editButtonOnclick,
	showPasswordOnclick, createUrlItem
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
	  	//TODO: load Url from DB
	  	        page.querySelector('#urlItems').append(createUrlItem('url 1'))
	  	        page.querySelector('#urlItems').append(createUrlItem('url 2'))
	  	        page.querySelector('#urlItems').append(createUrlItem('url 3'))
		page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
		page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]
        page.querySelector('#passwordCheckbox').onclick = showPasswordOnclick(page)

		page.querySelector('#editButton').onclick = editButtonOnclick(page)

		page.querySelector('#addUrl').onclick = addUrlOnclick(page)
		page.querySelector('#abortButton').onclick = editAbortOnclick(page)

	}

	if (page.id === 'addPassword') {
		page.querySelector('#addUrl').onclick = addUrlOnclick(page)
		page.querySelector('#commitButton').onclick = addPasswordCommitButtonOnclick()

	}
  })

function getAddedUrls(page){
    let a = []
    page.querySelector('#urlItems').childNodes.forEach(item => {
        if(item.getAttribute('data-unsaved') === 'true' &&
           item.getAttribute('data-removed') === 'false')
            a.push(item.querySelector('ons-input').value)})
    return a.filter(elm => elm !== '')
}