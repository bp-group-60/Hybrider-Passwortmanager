import {createPassword, getPasswords, passwords, updatePassword, deletePassword} from "./database/passwordOperations.js"
import {saveUrlList, deleteUrlList, getUrlList} from "./database/websiteOperations.js"

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

export function addUrlOnclick(page) {
    return () => {
        let newItem = ons.createElement('<ons-list-item data-unsaved="true" data-removed="false" modifier="nodivider">')
        let urlInput = ons.createElement('<ons-input modifier="underbar" placeholder="Url" float>')

        let rightContent = ons.createElement('<div class="right">')
        let removeIcon = ons.createElement('<ons-icon class="removeIcon" icon="md-minus-circle">')
            removeIcon.style.color = 'red'
            removeIcon.onclick = () => {
                newItem.remove()
            }

        rightContent.append(removeIcon)

        newItem.append(urlInput)
        newItem.append(rightContent)
        page.querySelector('#urlItems').append(newItem)
    };
}

export function editButtonOnclick(page) {
    return () => {
        page.querySelector('#editButton').style.display = 'none'
        page.querySelector('#abortButton').style.display = ''
        page.querySelector('#commitButton').style.display = ''
        page.querySelector('#deleteWrapper').style.display = ''

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
        page.querySelector('#deleteWrapper').style.display = 'none'

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
    if(element.getAttribute('data-unsaved')==='true'){
        if(element.getAttribute('data-removed')==='true'){
            element.setAttribute('data-unsaved', 'false')
            element.setAttribute('data-removed', 'false')
            element.style.display = ''
        }else{
            element.remove()
        }
    }
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

export function createUrlItem(url){
    let listItem = ons.createElement('<ons-list-item data-unsaved="false" data-removed="false" modifier="nodivider">')
    listItem.innerText = url
    let rightContent = ons.createElement('<div class="right">')
    let removeIcon = ons.createElement('<ons-icon class="removeIcon" icon="md-minus-circle">')
    removeIcon.style.display = 'none'
    removeIcon.style.color = 'red'
    removeIcon.onclick = () => {
        listItem.style.display = 'none'
        listItem.setAttribute('data-unsaved','true')
        listItem.setAttribute('data-removed','true')
    }

    rightContent.append(removeIcon)
    listItem.append(rightContent)
    return listItem
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

function getAddedUrls(page) {
    let a = []
    page.querySelector('#urlItems').childNodes.forEach(item => {
        if (item.getAttribute('data-unsaved') === 'true' &&
            item.getAttribute('data-removed') === 'false') {
            a.push(item.querySelector('ons-input').value)
        }
    })
    return a.filter(elm => elm !== '')
}

function getRemovedUrls(page) {
    let a = []
    page.querySelector('#urlItems').childNodes.forEach(item => {
        if (item.getAttribute('data-unsaved') === 'true' &&
            item.getAttribute('data-removed') === 'true') {
            a.push(item.innerText)
        }
    })
    return a
}

export function onclickDelete(page){
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

export function onclickMoreButton(page){
    return () => {
        page.querySelector('#popover').show(page.querySelector('#moreButton'))
    }
}
