import {createPassword, deletePassword, getPasswords, passwords, updatePassword} from "./database/passwordOperations.js"
import {deleteUrlList, getUrlList, saveUrlList} from "./database/websiteOperations.js"
import {createUrlItem, getAddedUrls, getRemovedUrls, restoreUrl} from "./onclick/onclickUrl.js";
import {deleteUser} from "./database/userOperations.js";

export function addPasswordCommitButtonOnclick(page) {
    return () => {

        console.log('TODO: validate data')

        let name = document.getElementById('name').value
        let loginName = document.getElementById('username').value
        let password = document.getElementById('password').value
        let successful = createPassword(sessionStorage.getItem("user"), name, loginName, password)

        if (successful) {
            saveUrlList(sessionStorage.getItem("user"), name, getAddedUrls(page))
            document.querySelector('#myNavigator').popPage()
            ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000})
        } else {
            //TODO: error message
            console.log('TODO: error message')
        }

    };
}

export function editButtonOnclick(page) {
    return () => {
        page.querySelector('#editButton').style.display = 'none'
        page.querySelector('#abortButton').style.display = ''
        page.querySelector('#commitButton').style.display = ''

        page.querySelector('#addUrl').style.display = ''
        page.querySelectorAll('.removeIcon').forEach(icon => icon.style.display = '')

        page.querySelector('#username').children[0].readOnly = false
        page.querySelector('#password').children[0].readOnly = false

        page.querySelector('ons-toolbar .center').innerHTML = "Bearbeiten"

    };
}

export function editAbortOnclick(page) {
    return () => {
        page.querySelector('#editButton').style.display = ''
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

export function showPasswordOnclick(page){
    return () => {
        if(page.querySelector('#passwordCheckbox').checked){
            page.querySelector('#password').children[0].type = 'text'
        }else{
            page.querySelector('#password').children[0].type = 'password'
        }
    };
}

export function onclickEditSave(page){
    return () => {
        let name = passwords[parseInt(page.data.id)][0]
        let loginName = document.getElementById('username').value
        let password = document.getElementById('password').value
        let successful = updatePassword(sessionStorage.getItem("user"), name, loginName, password)

        if (successful) {
            saveUrlList(sessionStorage.getItem("user"), name, getAddedUrls(page))
            deleteUrlList(sessionStorage.getItem("user"), name, getRemovedUrls(page))
            ons.notification.toast('Änderungen gespeichert!', {timeout: 3000})

            getPasswords(sessionStorage.getItem("user"), sessionStorage.getItem("password"))

            page.querySelector('#urlItems').innerHTML = ''
            getUrlList(sessionStorage.getItem("user"), passwords[parseInt(page.data.id)][0]).forEach(url =>{
                page.querySelector('#urlItems').append(createUrlItem(url))
            })

            editAbortOnclick(page)()

        } else {
            ons.notification.toast('Fehler beim speichern!', {timeout: 3000})
            //TODO: error message
            console.log('TODO: error message')
        }
    };
}

export function onclickDeletePassword(page){
    return () => {
        ons.notification.confirm('Passwort wirklich löschen?')
            .then(function (input) {
                if (input===1){
                    let name = passwords[parseInt(page.data.id)][0]
                    deletePassword(sessionStorage.getItem("user"), name)
                    document.querySelector('#myNavigator').popPage()
                    ons.notification.toast('Passwort wurde gelöscht!', {timeout: 3000})
                }
            })
    }
}

export function onclickDeleteUser(page){
    return () => {
        ons.notification.confirm('Benutzer wirklich löschen?')
            .then(function (input) {
                if (input===1){
                    deleteUser(sessionStorage.getItem("user"), sessionStorage.getItem("password"))
                    history.back()
                }
            })
    }
}

export function onclickMoreButton(page){
    return () => {
        page.querySelector('#popover').show(page.querySelector('#moreButton'))
    }
}
