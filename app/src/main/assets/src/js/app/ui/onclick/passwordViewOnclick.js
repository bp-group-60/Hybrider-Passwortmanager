import {deletePassword, getPasswords, passwords, updatePassword} from "../../database/passwordOperations.js"
import {getSessionPassword, getSessionUser} from "../../sessionHandler.js"
import {deleteUrlList, saveUrlList} from "../../database/websiteOperations.js"
import {getAddedUrls, getRemovedUrls} from "../urlHandler.js"
import {updatePasswordView} from "../passwordViewHandler.js";

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

    }
}

export function editAbortOnclick(page) {
    return () => {
        updatePasswordView(page)
    }
}

export function onclickEditSave(page) {
    return () => {
        let name = passwords[parseInt(page.data.id)][0]
        let loginName = document.getElementById('username').value
        let password = document.getElementById('password').value
        let successful = updatePassword(getSessionUser(), name, loginName, password)

        if (successful) {
            saveUrlList(getSessionUser(), name, getAddedUrls(page))
            deleteUrlList(getSessionUser(), name, getRemovedUrls(page))
            ons.notification.toast('Änderungen gespeichert!', {timeout: 3000})

            getPasswords(getSessionUser(), getSessionPassword())

            updatePasswordView(page)

        } else {
            ons.notification.toast('Fehler beim speichern!', {timeout: 3000})
        }
    }
}

export function onclickDeletePassword(page) {
    return () => {
        ons.notification.confirm('Passwort wirklich löschen?')
            .then(function (input) {
                if (input === 1) {
                    let name = passwords[parseInt(page.data.id)][0]
                    deletePassword(getSessionUser(), name)
                    document.querySelector('#myNavigator').popPage()
                    ons.notification.toast('Passwort wurde gelöscht!', {timeout: 3000})
                }
            })
    }
}

export function showPasswordOnclick(page) {
    return () => {
        if (page.querySelector('#passwordCheckbox').checked) {
            page.querySelector('#password').children[0].type = 'text'
        } else {
            page.querySelector('#password').children[0].type = 'password'
        }
    }
}