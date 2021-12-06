//Schnittstellen
function getPasswds(user, password) {
	//Anfrage an Java
	//List Arraylist(website, user, passwd) = Java.getPasswds(user, password)
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

	getPasswds(user, passwd);

	var i = 10;
	//array.forEach(element => {
	//	var tableRow = document.createElement('td');
	//});
	createTable(getPasswds(user, passwd));
});