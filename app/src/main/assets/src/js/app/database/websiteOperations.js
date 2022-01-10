export function getUrlList(user, website){
    return JSON.parse(Java.getUrlList(user, website)).dataArray
}

export function saveUrlList(user, website, list){
    list.forEach(elm => saveUrl(user, website, elm))
}

export function saveUrl(user, website, url){
    Java.saveUrl(user, website, url)
}

export function deleteUrlList(user, website, list){
    list.forEach(elm => deleteUrl(user, website, elm))
}

export function deleteUrl(user, website, url){
    Java.deleteUrl(user, website, url)
}