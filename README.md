# Hybrider Passwortmanager

<!-- Shift to Wiki -->
Introduction:

OpenSSL Library:		cpp/include + cpp/libs
OnsenUI:				assests/onsenui
Webassembly:			assets/src/webAssembly
Room-Database:			


<div id="top"></div>

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
	<a href="https://github.com/bp-group-60/Hybrider-Passwortmanager">
		<img src="images/logo.png" alt="Logo" width="80" height="80">
	</a>

<h3 align="center">Hybrider Passwortmanager</h3>
	<p align="center">
		is created for the data flow analysis
		<br />
		<a href="https://github.com/bp-group-60/Hybrider-Passwortmanager/wiki"><strong>Explore the wiki »</strong></a>
		<br />
	</p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
	<summary>Table of Contents</summary>
	<ol>
		<li>
			<a href="#about-the-project">About The Project</a>
			<ul>
				<li><a href="#built-with">Built With</a></li>
			</ul>
		</li>
		<li>
			<a href="#getting-started">Getting Started</a>
			<ul>
				<li><a href="#prerequisites">Prerequisites</a></li>
				<li><a href="#installation">Installation</a></li>
			</ul>
		</li>
		<li><a href="#usage">Usage</a></li>
		<li><a href="#roadmap">Roadmap</a></li>
		<li><a href="#license">License</a></li>
		<li><a href="#acknowledgments">Acknowledgments</a></li>
	</ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

The App is devided into 2 sections: the frontend and the backend. The frontend has a login/register site and the password-overview page to see, edit and add passwords. On the backend side, we have a `Java Dataflowmanager` to get the data from the `SQLite Datbase` and encrypt & decrypt the data in the `C-Cryption` section, so the requested informations can be forwarded to the frontend and been shown there.

A dataflow-diagram is shwon below:<br>
[![Dataflow Diagram][dataflow-diagram]](https://example.com)

<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

* [OnsenUI](https://nextjs.org/)
* [Python](https://www.python.org/)
* [emscripten](https://emscripten.org/docs/)
* [prebuilt openSSL-Library](https://github.com/PurpleI2P/OpenSSL-for-Android-Prebuilt)
* [Room Database](https://developer.android.com/reference/android/arch/persistence/room/RoomDatabase)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started
Open this Project in an IDE of your choice (Android Stuido preferred) and install the app (e.g. android emulator).

### Prerequisites
For editing the project, Android Sdk is required! If there are pending changes to Webassembly, you also need emscripten to be runnable and therefore Python to be installed already.
* Android Studio
	```
	[https://developer.android.com/studio]
	```

### Installation
1. Clone the repo
	 ```sh
	 git clone https://github.com/bp-group-60/Hybrider-Passwortmanager.git
	 ```
2. Import in IDE (Android Studio or MS Visual Studio Code tested)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage
After starting the app on the phone, there is a login screen. If you dont have any account, you can create one using the register site and insert your username, email and masterpassword. The masterpassword will be hashed and used for verification during the login process. After a successful login, the user can see his password list with the application name and the username, which has been used there. The user is capable to add new passwords or select an entry. Adding a password requires an application name, a username and password. The app supports generating passwords to you. Optional the user can add URLs to the password entry.<br>
When the user selects an password entry, he can copy the password into clipboard or edit or even delete the entry. While editing the entry, the user now can only change username, password and URLs. The password generation is also usable in the edit screen.

_For more and detailed explaination of each part of every section, please visit the [Wiki](https://github.com/bp-group-60/Hybrider-Passwortmanager/wiki)_

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] login & register page
	- [x] add multiple users
		- [x] hashing masterpassword
- [x] show passwords
	- [x] proper cryption during saving
	- [x] username/password generation
	- [x] add multiple URLs
	- [x] edit/remove entrys

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [emscripten Tutorial](https://emscripten.org/docs/getting_started/Tutorial.html)



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/bp-group-60/Hybrider-Passwortmanager.svg?style=for-the-badge
[contributors-url]: https://github.com/bp-group-60/Hybrider-Passwortmanager/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/bp-group-60/Hybrider-Passwortmanager.svg?style=for-the-badge
[forks-url]: https://github.com/bp-group-60/Hybrider-Passwortmanager/network/members
[stars-shield]: https://img.shields.io/github/stars/bp-group-60/Hybrider-Passwortmanager.svg?style=for-the-badge
[stars-url]: https://github.com/bp-group-60/Hybrider-Passwortmanager/stargazers
[issues-shield]: https://img.shields.io/github/issues/bp-group-60/Hybrider-Passwortmanager.svg?style=for-the-badge
[issues-url]: https://github.com/bp-group-60/Hybrider-Passwortmanager/issues
[license-shield]: https://img.shields.io/github/license/bp-group-60/Hybrider-Passwortmanager.svg?style=for-the-badge
[license-url]: https://github.com/bp-group-60/Hybrider-Passwortmanager/blob/master/LICENSE.txt
[dataflow-diagram]: images/diagram.png