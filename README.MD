# MBAMS

MBAMS is a Mobile-based Attendance Management System.

  - Web Apllication for professors (server) <br />
     The web application is written in `Java` using `Play Framework`.
  - Android Application for students (client)
 
##Screenshots
![Web Application](http://www.pictureshack.us/images/28094_students-db.png)

![Android Applicaton signup](http://www.pictureshack.us/images/74122_Screenshot_2015-05-03-17-42-26.png)

![Android Applicaton signin](http://www.pictureshack.us/images/23380_Screenshot_2015-05-03-17-44-39.png)

## Version
1.0

##Features:
1. Web application consists of a MySQL database, with full CRUD operations.
2. The professor uses the web app to mange his students attendances by selsecting the lecture number every lecture.
3. The Android Appliction can connect to the web app.
4. The first time, the student has to sign up in the system just by entering his ID.
5. Every Lecture the student can submit his attendance just by one click!
6. The web app responds to the android app, so that the student can make sure he had submitted his attendance successfully.
7. The android app can't submit attendance to more than one student. ONLY ONE!
## How to build MBAMS

###For the web application:
1. You need `Play Framework` installed: 
Follow the instructions [here](https://www.playframework.com/documentation/2.0/Installing)
2. You need mysql installed
3. Create a database with the name `students`
4. Clone the repo: <br />
    ```sh
       $ git clone [git@github.com:AhmadElMelegy/Absence-Checker.git] MBAMS
    ```
	
5. Change the configuration to match yours: <br />
	```sh
       $ nano MBAMS/server/conf/application.cong
    ```
change `db.default.user` and `db.default.pass` to your user & password for the db.
6. Run the app using: <br />
    ```sh
       $ ./activator run
    ```
###For the Android application:

1. Install the following software: <br />
    Android SDK: http://developer.android.com/sdk/index.html <br />
    Gradle: http://www.gradle.org/downloads  <br />
    Android Studio: http://developer.android.com/sdk/installing/studio.html  <br />

2. Import the project in Android Studio
3. Change the congigurations strings values:

	String | Value
	------------ | -------------
	`serverURL` |  The IP of your machine that runs the web app follwing with the port number `9000`
	`wifiName` | The name of the wifi that you will create for the students.
	`wifiPassword` | The password of above wifi
	`siginURL` | The route of the sign in in the web app.<br /> _Don't change it if you didn't change it in the web app._
	`submitURL` | The route of the submit attenance in the web app. <br /> _Don't change it if you didn't change it in the web app._

##License:

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
