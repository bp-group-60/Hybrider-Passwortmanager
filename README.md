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
		<li><a href="#features">Features</a></li>
		<li><a href="#license">License</a></li>
		<li><a href="#acknowledgments">Acknowledgments</a></li>
	</ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

Hybrider-Passwortmanager is intended to show a modern representation of an Android app which uses as many different languages as possible.
This is just an academic example and is created for data flow analysis with newly developed tools.
To illustrate the possible security-critical scenarios, a possible modern representation of a password manager was chosen.

A dataflow-diagram is shwon below:<br>
[![](App_Schnittstellen-Graph.png)](https://raw.githubusercontent.com/bp-group-60/Hybrider-Passwortmanager/dev/App_Schnittstellen-Graph.png)

### Built With

* [OnsenUI](https://onsen.io/)
* [emscripten](https://emscripten.org/docs/)
* [prebuilt openSSL-Library](https://github.com/PurpleI2P/OpenSSL-for-Android-Prebuilt)
* [Room Database](https://developer.android.com/reference/android/arch/persistence/room/RoomDatabase)

<!-- GETTING STARTED -->
## Getting Started
For editing and building the project [Android Studio](https://developer.android.com/studio) comes with almost all reqirements and is therefore recommended.
Additional for compiling any c-compiler with make-command is nessesary.

### Installation
1. Clone the repo
	 ```sh
	 git clone https://github.com/bp-group-60/Hybrider-Passwortmanager.git
	 ```
2. Import in IDE (Android Studio)

or just use the build in MS Visual Studio Code tools

### Building
For building this project use gradle's default build configuration.


### Running
To get proper functionality at least SDK 24 (Android 7) and a WebView version that supports ES6 and webassembly is required.



### _For more and detailed explaination of each part of every section, please visit the [Wiki](https://github.com/bp-group-60/Hybrider-Passwortmanager/wiki)_

<!-- FEATURES -->
## Features
After starting the app on the phone you can:
* create an account and login into the app
* create and view login information for a website including:
  * url items (saved in plain)
  * login name (saved in plain)
  * login password (saved encrypted)
* manage all saved data

<!-- LICENSE -->
## License

Distributed under the MIT License. See [LICENSE](https://github.com/bp-group-60/Hybrider-Passwortmanager/blob/main/LICENSE) for more information.

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
