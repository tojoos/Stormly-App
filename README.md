[![CircleCI](https://circleci.com/gh/tojoos/Stormly-App/tree/master.svg?style=svg)](https://circleci.com/gh/tojoos/Stormly-App/tree/master)

## Stormly-App

Stormly is an app designed to solve low-quality weather measurements in smaller areas. 
We decided to create a device with a connection to a personal database, which stores weather information. 
This data can be easily accessed by logging into the website. Each user has a unique account with weather information from his device. Stormly is divided into hardware and software parts. Microprocessor - Arduino UNO is responsible for the hardware part, which collects and sends data from sensors. The software part is represented by a Java language application, which processes received data and displays them on the web service.

### Overview of the project
Core of the project is based on MVC design pattern using Java programming language and HTML views. Main technologies used were Spring Framework, Thymeleaf, JUnit, Bootstrap and embedded H2 database.
Application communicates with microprocessor using serial port, transferring data directly into file and updating database with this information. This process is being handled by simple Python script.

<p align="center">
    <img width="300" src="https://user-images.githubusercontent.com/79639840/146686359-fdc6307f-603e-47fc-a7af-1535a40522b9.png">
</p>


#### CircleCI
To avoid building whole project every time change is commited, there is CircleCI's continues integration tool that triggers automated tests and builds. If these fail, they can be repaired quickly - within minutes.

![About-Stormly](https://user-images.githubusercontent.com/79639840/146685882-89100747-b87f-4116-8591-3a759da722da.png)

### Preview
![Main Page](https://user-images.githubusercontent.com/79639840/146686356-d514ff1c-2707-4c5a-ad22-2abc94d4776f.png)
![Archive Page](https://user-images.githubusercontent.com/79639840/146686365-c0480cc3-1dee-41ee-b8fb-57da55d40915.png)
![Login Form](https://user-images.githubusercontent.com/79639840/146686364-755e3b79-e341-4f2c-86d1-68be526c6bef.png)
![Register Form](https://user-images.githubusercontent.com/79639840/146686380-caca3e7e-9117-44f7-943a-b4d0e13cde6f.png)
