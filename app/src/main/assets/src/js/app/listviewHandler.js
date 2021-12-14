import {getPasswords, passwords} from "./database/passwordOperations.js";

export function updateList(user, password) {
    document.getElementById('overview').innerHTML = ''
    createList(user, password)
}

function createList(user, password) {
    getPasswords(user, password)

    for (let i = 0; i < passwords.length; i++) {
        addToList(`${passwords[i][0]} / ${passwords[i][1]}`, i)
    }
}

function addToList(name, id) {
    let listItem = ons.createElement('<ons-list-item modifier="chevron" tappable>')

    listItem.setAttribute("id", id)
    listItem.setAttribute("onClick",
        "document.querySelector('#myNavigator').pushPage('onsPages/page2.html',{data: {title: 'Passwort ändern', id: "+ id +"}})")

    listItem.innerText = name

    document.getElementById('overview').append(listItem)
}
