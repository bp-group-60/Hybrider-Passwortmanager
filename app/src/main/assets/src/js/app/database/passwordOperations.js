export let passwords

export function getPasswords(user, password) {
    // array structure: [[website, loginName, password], ...]
    passwords = JSON.parse(Java.getPasswordList(user, password)).dataArray
    return passwords
}

export function createPassword(user, website, loginName, password) {
    return Java.createPassword(user, website, loginName, password)
}

export function editPassword(i) {
    //Anfrage an Java, um Eintrag i zu bearbeiten
}

export function hashPassword(password) {
    //Anfrage an C
    //hashedPassword = C.hashPassword(password)
}