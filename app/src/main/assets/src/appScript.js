const user = sessionStorage.getItem("user");
const passwd = sessionStorage.getItem("passwd");
var BreakException = {};

//Schnittstellen
function getPasswds(user, password) {
	//Anfrage an Java
	//List Arraylist(website, user, passwd) = Java.getPasswds(user, password)

	/*  input
	"{\"length\":," + 1 +
				"\"websites\":[" + data + "]," +
				"\"loginnames\":[" + data + "]," +
				"\"passwords\":[" + data + "]}";
	*/
	//var json = '{"website":http://....123, "user":"T3C abon", "passwort":123456}';
	//console.log(JSON.parse(json).website);
	//console.log(JSON.parse(json).user);
	//console.log(JSON.parse(json).passwort);

	return array = [
		["https://....01", "User01", "Passwort01"],
		["https://....02", "User02", "Passwort02"],
		["https://...loooooooooooooooooongTest.03", "User03", "Passwort03"],
		["https://....04","UserLoooooooooooooooooooooooooooooongNamee04","Passwort04"],
		["https://....05","User01","Passwort05looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"]
	]
}

function createPasswd(passwd) {

}

function editPasswd(i) {
	//Anfrage an Java, um Eintrag i zu bearbeiten
}

function hashPasswd(passwd) {
	//Anfrage an C
	//hashedPasswd = C.hashPasswd(passwd);
}

function createList(){
	var list = document.getElementById('page1');
	var list2 = document.getElementsByTagName('div');
	var i = 1;
	var array = getPasswds(user, passwd);

	array.forEach(function (rowData) {
		var row = document.createElement('ons-list-item');
		row.setAttribute("modifier", "chevron");
		row.setAttribute("tappable");
		row.setAttribute("id", i);
		row.setAttribute("onClick", "UpdatePasswd(this.id)");

		var txt = `${rowData[0]} / ${rowData[1]}`;
		
		row.innerText = txt;
		list2[2].appendChild(row);

		i++;
	});
}

function createTable(array){
	var table = document.getElementById('Tabelle');
	var tableBody = document.createElement('tbody');

	var i = 0;

	array.forEach(function (rowData) {
		var row = document.createElement('tr');
		row.setAttribute("id", i);

		var j = 0;
		rowData.forEach(function (cellData) {
			var cell = document.createElement('td');
			cell.setAttribute("id", j);
			cell.appendChild(document.createTextNode(cellData));
			row.appendChild(cell);
			j++;
		});

		tableBody.appendChild(row);
		i++;
	});

	table.appendChild(tableBody);
}

document.addEventListener("DOMContentLoaded", () => {
	const passForm = document.getElementById('Tabelle');
	const user = sessionStorage.getItem("user");
	const passwd = sessionStorage.getItem("passwd");

	if(document.getElementById('Tabelle'))createTable(getPasswds(user, passwd));
	else createList(getPasswds(user, passwd));
});

ons.ready(function() {
	// Cordova APIs are ready
	console.log(window.device);
});

document.addEventListener('init', function(event) {
	var page = event.target;
  
	if (page.id === 'page2') {
	  	page.querySelector('ons-toolbar .center').innerHTML = page.data.title;
		var v = document.getElementById(page.data.id).textContent;	
		document.getElementById('page2').querySelector('#Webseite').value = v;
		document.getElementById('page2').querySelector('#username').value = v;
		document.getElementById('page2').querySelector('#passwort').value = v;
	}
  });

function UpdatePasswd(EintragID){
	document.querySelector('#myNavigator').pushPage('page2.html', 
						{data: {title: 'Passwort ändern', id: EintragID}});
}
