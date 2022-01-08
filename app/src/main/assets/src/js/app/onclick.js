import {createPassword, passwords} from "./database/passwordOperations.js";

export function addPasswordCommitButtonOnclick() {
    return () => {

        console.log('TODO: validate data')

        let name = document.getElementById('name').value
        let loginName = document.getElementById('username').value
        let password = document.getElementById('password').value
        let successful = createPassword(sessionStorage.getItem("user"), name, loginName, password)

        if (successful) {
            document.querySelector('#myNavigator').popPage()
            ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000})
        } else {
            //TODO: error message
            console.log('TODO: error message')
        }

    };
}

export function addUrlOnclick(page) {
    return () => {
        let newItem = ons.createElement('<ons-list-item modifier="nodivider">')
        let urlInput = ons.createElement('<ons-input modifier="underbar" placeholder="Url" float>')

        newItem.append(urlInput)
        page.querySelector('#urlItems').append(newItem)
    };
}

export function editButtonOnclick(page) {
    return () => {
        page.querySelector('#editButton').style.display = 'none'
        page.querySelector('#abortButton').style.display = 'inline-block'
        page.querySelector('#commitButton').style.display = 'inline-block'

        page.querySelector('#addUrl').style.display = 'inline-block'
        page.querySelectorAll('.removeIcon').forEach(icon => icon.style.display = '')

        page.querySelector('#username').children[0].readOnly = false
        page.querySelector('#password').children[0].readOnly = false

        page.querySelector('ons-toolbar .center').innerHTML = "Bearbeiten"

    };
}

export function editAbortOnclick(page) {
    return () => {
        page.querySelector('#editButton').style.display = 'inline-block'
        page.querySelector('#abortButton').style.display = 'none'
        page.querySelector('#commitButton').style.display = 'none'

        page.querySelector('#addUrl').style.display = 'none'
        page.querySelector('#username').children[0].readOnly = true
        page.querySelector('#password').children[0].readOnly = true

        page.querySelectorAll('.removeIcon').forEach(icon => icon.style.display = 'none')
        Array.from(page.querySelector('#urlItems').children).forEach(element => restoreUrl(element))

        page.querySelector('ons-toolbar .center').innerHTML = passwords[parseInt(page.data.id)][0]
        page.querySelector('#username').value = passwords[parseInt(page.data.id)][1]
        page.querySelector('#password').value = passwords[parseInt(page.data.id)][2]
    };
}

function restoreUrl (element){
console.log(element.innerHTML)
    if(element.getAttribute('data-unsaved')==='true'){
        console.log(1)
        console.log(element.getAttribute('data-removed'))
        if(element.getAttribute('data-removed')==='true'){
        console.log(2)
            element.style.display = ''
        }else{
        console.log(3)
            element.remove()
        }
    }
}

export function showPasswordOnclick(page){
    return () => {
        console.log(page.querySelector('#passwordCheckbox').checked)
        if(page.querySelector('#passwordCheckbox').checked){
            page.querySelector('#password').children[0].type = 'text'
        }else{
            page.querySelector('#password').children[0].type = 'password'
        }

    };
}